package com.sagademo.auth.config;

import com.sagademo.auth.domain.UserAccount;
import com.sagademo.auth.domain.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/actuator/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated())
                .httpBasic();
        return http.build();
    }

    @Bean
    public CommandLineRunner seedDefaultUser(UserAccountRepository repository, PasswordEncoder encoder) {
        return args -> repository.findByUsername("demo").orElseGet(() -> {
            UserAccount account = new UserAccount();
            account.setUsername("demo");
            account.setPassword(encoder.encode("demo123"));
            account.setRoles(Set.of("ROLE_USER", "ROLE_ADMIN"));
            return repository.save(account);
        });
    }
}

