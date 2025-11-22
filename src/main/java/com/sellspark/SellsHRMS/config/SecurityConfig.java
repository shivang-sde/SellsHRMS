package com.sellspark.SellsHRMS.config;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

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
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())

                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.INCLUDE).permitAll()

                        .requestMatchers("/", "/login", "/register",
                                "/error", "/favicon.ico",
                                "/api/auth/login", "/api/auth/logout", "/api/auth/register-superadmin")
                        .permitAll()

                        // JSP under WEB-INF can be forwarded
                        .requestMatchers("/WEB-INF/**").permitAll()

                        // protected dashboards
                        .requestMatchers("/superadmin/**").hasAuthority("SUPER_ADMIN")
                        .requestMatchers("/orgadmin/**").hasAuthority("ORG_ADMIN")
                        .requestMatchers("/employee/**").hasAuthority("EMPLOYEE")

                        .anyRequest().authenticated())

                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }
}
