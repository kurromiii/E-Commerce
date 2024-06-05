package com.mftplus.ecommerce.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
public class SecurityConfig {

    private final JWTRequestFilter jwtRequestFilter;

    public SecurityConfig(JWTRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()).cors(cors -> cors.disable());
        http
                .addFilterBefore(jwtRequestFilter, AuthorizationFilter.class);
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/product","/auth/register","/auth/login","/auth/verify","/test/data","/error")
                        .permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

}
