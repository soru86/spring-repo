package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeedDataConfig implements CommandLineRunner {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        
      if (userRepository.count() == 0) {

        User admin = User
        		      .builder()
                      .firstName("admin")
                      .lastName("admin")
                      .email("admin@admin.com")
                      .password(passwordEncoder.encode("password"))
                      .role(Role.ROLE_ADMIN)
                      .build();

        userService.save(admin);
        System.out.println("created ADMIN user - {}" + admin);
      }
    }

}
