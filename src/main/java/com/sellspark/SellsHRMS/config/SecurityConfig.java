package com.sellspark.SellsHRMS.config;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.sellspark.SellsHRMS.security.OrganisationAccessFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final OrganisationAccessFilter organisationAccessFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false))
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:8080")); // or frontend URL
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))

                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.INCLUDE).permitAll()

                        .requestMatchers("/api/test/payroll/**").permitAll() // <<< add this line

                        // 🔓 Public endpoints
                        .requestMatchers("/", "/login", "/new-login", "/register",
                                "/error/**", "/error", "/favicon.ico",
                                "/api/auth/login", "/api/auth/logout", "/api/auth/register-superadmin",
                                "/actuator/health", "/actuator/info")
                        .permitAll()

                        // 🔓 Static resources
                        .requestMatchers("/css/**", "/js/**", "/images/**",
                                "/bundles/**", "/plugins/**", "/fonts/**",
                                "/audio/**", "/ajax.cloudflare.com/**")
                        .permitAll()

                        // 🔓 JSPs
                        .requestMatchers("/WEB-INF/**").permitAll()

                        // 🧠 Specific role-based routes (order matters!)
                        .requestMatchers("/api/superadmin/**", "/superadmin/**").hasAuthority("SUPER_ADMIN")
                        .requestMatchers("/api/org-admin/**", "/orgadmin/**")
                        .hasAnyAuthority("ORG_ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/api/employee/**", "/employee/**")
                        .hasAnyAuthority("EMPLOYEE", "ORG_ADMIN", "SUPER_ADMIN")

                        // 🧱 Default catch-all for authenticated users
                        .requestMatchers("/api/**").authenticated()

                        // Everything else must be authenticated
                        .anyRequest().authenticated())

                // 🔐 Add org access filter
                .addFilterBefore(organisationAccessFilter, UsernamePasswordAuthenticationFilter.class)

                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }
}
