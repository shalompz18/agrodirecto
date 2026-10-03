package com.upc.agrodirecto.security.config;

import com.upc.agrodirecto.security.filter.JwtAuthenticationFilter;
import com.upc.agrodirecto.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomUserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint apiAuthenticationEntryPoint() {
        return (request, response, ex) -> {
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Se requiere un JWT válido\"}");
        };
    }

    @Bean
    public AccessDeniedHandler apiAccessDeniedHandler() {
        return (request, response, ex) -> {
            response.setStatus(403);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":403,\"error\":\"Forbidden\",\"message\":\"No tienes permisos para este recurso\"}");
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(apiAuthenticationEntryPoint())
                        .accessDeniedHandler(apiAccessDeniedHandler()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/error"
                        ).permitAll()
                        .requestMatchers("/api/v1/cultivos", "/api/v1/precios-mercado/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/cargas").hasRole("PRODUCTOR")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/api/v1/cargas/*/cancelar").hasRole("PRODUCTOR")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/calificaciones").hasRole("PRODUCTOR")
                        .requestMatchers("/api/v1/productores/**", "/api/v1/reportes/ahorro-productor/**").hasRole("PRODUCTOR")
                        .requestMatchers("/api/v1/transportistas/**", "/api/v1/cargas/retorno").hasRole("TRANSPORTISTA")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/api/v1/lotes/*/confirmar-entrega").hasRole("TRANSPORTISTA")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/api/v1/lotes/*/reportar-retraso").hasRole("TRANSPORTISTA")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/api/v1/cargas/*/asignar-retorno").hasRole("TRANSPORTISTA")
                        .requestMatchers("/api/v1/usuarios", "/api/v1/usuarios/*/estado", "/api/v1/usuarios/*/administrador", "/api/v1/chats/solicitudes", "/api/v1/chats/solicitudes/*/estado", "/api/v1/transacciones/**", "/api/v1/reportes/transacciones/**", "/api/v1/cargas").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/v1/reportes/reputacion-transportista/**").authenticated()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
