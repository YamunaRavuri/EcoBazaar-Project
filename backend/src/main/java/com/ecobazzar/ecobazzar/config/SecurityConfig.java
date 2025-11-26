package com.ecobazzar.ecobazzar.config;

import com.ecobazzar.ecobazzar.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                
                // ⭐ VERY IMPORTANT — allow Angular CORS (OPTIONS preflight)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public auth endpoints
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()

                // Swagger
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                // Seller + Admin product access
                .requestMatchers(HttpMethod.GET, "/api/products/seller")
                    .hasAnyAuthority("ROLE_SELLER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/products/**")
                    .hasAnyAuthority("ROLE_SELLER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**")
                    .hasAnyAuthority("ROLE_SELLER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**")
                    .hasAnyAuthority("ROLE_SELLER", "ROLE_ADMIN")

                // Admin-only routes
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/admin-request/pending").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/admin-request/approve/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/admin-request/reject/**").hasAuthority("ROLE_ADMIN")

                // Public browsing
                .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/*").permitAll()

                // Authenticated user routes
                .requestMatchers("/api/cart/**", "/api/checkout/**", "/api/orders/**").authenticated()
                .requestMatchers("/api/admin-request/request").authenticated()
                .requestMatchers("/api/admin-request/has-pending").authenticated()

                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
