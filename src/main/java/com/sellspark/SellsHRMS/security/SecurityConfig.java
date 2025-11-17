package com.sellspark.SellsHRMS.security;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        private final CombinedUserDetailsService userDetailsService;
        private final CustomLoginSuccessHandler loginSuccessHandler;

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider authProvider() {
                DaoAuthenticationProvider p = new DaoAuthenticationProvider();
                p.setUserDetailsService(userDetailsService);
                p.setPasswordEncoder(passwordEncoder());
                return p;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/login", "/error", "/css/**", "/js/**").permitAll()
                                                .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                                                .requestMatchers("/sa/**").hasRole("SUPER_ADMIN")
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/hr/**").hasRole("HR")
                                                .requestMatchers("/user/**").hasRole("EMPLOYEE")
                                                .anyRequest().authenticated())

                                .formLogin(login -> login
                                                .loginPage("/login")
                                                .loginProcessingUrl("/authenticate")
                                                .successHandler(loginSuccessHandler)
                                                .failureUrl("/login?error=true")
                                                .permitAll())

                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout=true"))

                                .authenticationProvider(authProvider());

                return http.build();
        }
}
