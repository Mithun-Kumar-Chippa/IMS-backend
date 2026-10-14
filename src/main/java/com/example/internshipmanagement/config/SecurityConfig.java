package com.example.internshipmanagement.config;

import com.example.internshipmanagement.security.JwtAuthenticationEntryPoint;
import com.example.internshipmanagement.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationEntryPoint unauthorizedHandler,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public auth and registration
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // Public internship browse and stats
                        .requestMatchers(HttpMethod.GET, "/api/v1/internships", "/api/v1/internships/{id:[0-9]+}", "/api/v1/internships/stats").permitAll()
                        // Public announcements
                        .requestMatchers(HttpMethod.GET, "/api/v1/announcements/public").permitAll()
                        // Static upload access
                        .requestMatchers("/uploads/**", "/api/v1/files/download/**").permitAll()
                        // Swagger & OpenAPI documentation
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Actuator health & metrics
                        .requestMatchers("/actuator/**").permitAll()
                        // Role-specific endpoints
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/companies/**").hasAnyRole("COMPANY", "ADMIN")
                        .requestMatchers("/api/v1/students/**").hasAnyRole("STUDENT", "ADMIN")
                        // Any other request must be authenticated
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
