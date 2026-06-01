package com.minimarket.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.minimarket.security.filter.JwtAuthFilter;
import com.minimarket.security.service.CustomUserDetailsService;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilita la seguridad a nivel de método
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtAuthFilter jwtAuthFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/**").permitAll() // primero las rutas públicas
                    .requestMatchers("/public/**").permitAll()
                    .requestMatchers("/api/usuarios").hasRole("GERENTE")
                    .requestMatchers("/api/carrito").hasRole("CLIENTE")
                    .requestMatchers("/api/inventario", "/api/ventas", "/api/detalle-ventas").hasAnyRole("GERENTE", "EMPLEADO")
                    .requestMatchers("/api/productos", "/api/categorias").hasAnyRole("GERENTE", "EMPLEADO", "CLIENTE")
                    .anyRequest().authenticated())
                        /*
                        Se agregó sessionManagement para configurar la política de creación de sesiones. En este caso, se establece como STATELESS, 
                        lo que significa que no se crearán sesiones y cada solicitud 
                        se autenticarán de forma independiente. 
                        */
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Configuración de encriptación de contraseñas
    }
}
