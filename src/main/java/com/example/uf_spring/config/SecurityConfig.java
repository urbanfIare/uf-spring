package com.example.uf_spring.config;

import com.example.uf_spring.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(CustomUserDetailsService customUserDetailsService) {
        return customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(
                    "/", // 루트 경로 추가!
                    "/login", "/logout", "/api/auth/**", "/admin-setup.html", "/board.html", "/test.html",
                    "/h2-console/**", "/*.css", "/*.js", "/*.png", "/*.jpg", "/*.jpeg", "/*.gif", "/*.ico"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/board.html", true)
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/api/admin/create-admin")
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            )
            .httpBasic();
        return http.build();
    }
} 