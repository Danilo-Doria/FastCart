package com.danilodoria.server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // Esta clase contiene configuración que Spring debe cargar antes de iniciar
@EnableWebSecurity // Le dice a Spring que quieres utilizar Spring Security para controlar las peticiones HTTP
public class SecurityConfig {


    // , Antes de que tu controlador reciba la petición, pasa por diferentes filtros
    // HttpSecurity es el objeto que Spring proporciona para configurar la seguridad HTTP
    // Cross-Site Request Forgery(CSRF) Es un mecanismo de protección especialmente relevante para aplicaciones
    // que utilizan autenticación basada en cookies/sesiones.

    // ¿Por qué STATELESS?
    //Porque con JWT no guardamos la sesión en el servidor. Cada petición lleva su token, y el servidor solo lo verifica
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                // CORS le dice a Spring Security que utilice la configuración CORS disponible.
                .cors(cors -> {
                })
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((request, response, ex) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            response.getWriter().write("""
                                    {"status":403,"error":"Forbidden","message":"No tienes permisos para esta acción"}
                                    """);
                        }))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/productos/**", "/categorias/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/productos/**", "/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/productos/**", "/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/productos/**", "/categorias/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
