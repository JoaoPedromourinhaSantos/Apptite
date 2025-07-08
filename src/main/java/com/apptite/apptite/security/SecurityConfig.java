package com.apptite.apptite.security; // Ou seu pacote de configuração

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Usa o BCrypt, que é um algoritmo forte
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desabilita CSRF (comum para APIs stateless)
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/**").permitAll() // Permite todas as requisições por enquanto
                // Você vai querer restringir isso depois, ex:
                // .requestMatchers("/clientes/login", "/clientes").permitAll()
                // .anyRequest().authenticated()
            )
            .httpBasic(withDefaults()); // Pode ser removido se você não quiser HTTP Basic pop-up
        return http.build();
    }
}