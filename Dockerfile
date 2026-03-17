# ============================================================================
# SellsHRMS — Production-Grade Multi-Stage Dockerfile for MVC JSP Application
# ============================================================================

# ---------------------------------------------------------------------------
# Stage 1 — Dependency cache
# ---------------------------------------------------------------------------
FROM eclipse-temurin:21-jdk-jammy AS deps

WORKDIR /build

COPY .mvn/   .mvn/
COPY mvnw    mvnw
COPY pom.xml pom.xml

RUN chmod +x mvnw

# Download all dependencies (offline-ready layer)
RUN ./mvnw dependency:go-offline -B -q

# ---------------------------------------------------------------------------
# Stage 2 — Build the application JAR
# ---------------------------------------------------------------------------
FROM deps AS build

WORKDIR /build

COPY src/ src/

# Build the application
RUN ./mvnw package -B -q \
    -DskipTests \
    -Dspotless.check.skip=true \
    -Dspotless.apply.skip=true \
    && mv target/*.jar target/app.jar

# Extract Spring Boot layered JAR for optimal Docker caching
RUN java -Djarmode=layertools -jar target/app.jar extract --destination /extracted

# ---------------------------------------------------------------------------
# Stage 3 — Production runtime (minimal image, non-root user)
# ---------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-jammy AS runtime

# ── Metadata labels ──────────────────────────────────────────────────────
LABEL org.opencontainers.image.title="SellsHRMS" \
    org.opencontainers.image.description="HRMS solutions of SellsPark for organisations" \
    org.opencontainers.image.vendor="SellsPark" \
    org.opencontainers.image.source="https://github.com/shivang-sde/SellsHRMS"

# ── OS-level hardening ──────────────────────────────────────────────────
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl fontconfig fonts-dejavu-core && \
    rm -rf /var/lib/apt/lists/* && \
    groupadd --system --gid 1001 hrms && \
    useradd  --system --uid 1001 --gid hrms --shell /usr/sbin/nologin hrms

# ── Application directory ───────────────────────────────────────────────
WORKDIR /app

# Create upload directory & give ownership to non-root user
RUN mkdir -p /opt/hrms/uploads && chown -R hrms:hrms /opt/hrms/uploads

# ── Copy Spring Boot layered JAR ────────────────────────────────────────
COPY --from=build /extracted/dependencies/          ./
COPY --from=build /extracted/spring-boot-loader/    ./
COPY --from=build /extracted/snapshot-dependencies/ ./
COPY --from=build /extracted/application/           ./

# ── ✅ FIXED: Copy JSP files to the correct location in the classpath ───
# For Spring Boot with layered JAR, webapp files go into BOOT-INF/classes
COPY --from=build /build/src/main/webapp /app/BOOT-INF/classes/META-INF/resources

# Alternative approach: Copy directly to the application layer
# COPY --from=build /build/src/main/webapp /app/application/BOOT-INF/classes/META-INF/resources

# ── Set proper ownership ────────────────────────────────────────────────
RUN chown -R hrms:hrms /app && \
    chown -R hrms:hrms /opt/hrms/uploads

# ── Switch to non-root user ─────────────────────────────────────────────
USER hrms

# ── JVM tuning for containers ───────────────────────────────────────────
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -Djava.security.egd=file:/dev/./urandom \
    -Dfile.encoding=UTF-8"

# ── Spring Boot defaults for MVC application ────────────────────────────
ENV SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080 \
    UPLOAD_BASE_DIR=/opt/hrms/uploads \
    # MVC specific settings - updated for classpath location
    SPRING_MVC_VIEW_PREFIX=/META-INF/resources/WEB-INF/views/ \
    SPRING_MVC_VIEW_SUFFIX=.jsp

# ── Expose application port ─────────────────────────────────────────────
EXPOSE 8080

# ── Health check via Spring Actuator ────────────────────────────────────
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -sf http://localhost:8080/actuator/health || exit 1

# ── Entrypoint ───────────────────────────────────────────────────────────
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]