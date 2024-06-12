package com.example.demo.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.JwtAuthenticationResponse;
import com.example.demo.dto.SignInRequest;
import com.example.demo.dto.SignUpRequest;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private UserRepository userRepository;
  private UserService userService;
  private PasswordEncoder passwordEncoder;
  private JwtService jwtService;
  private AuthenticationManager authenticationManager;

  public JwtAuthenticationResponse signup(SignUpRequest request) {
      var user = User
                  .builder()
                  .firstName(request.getFirstName())
                  .lastName(request.getLastName())
                  .email(request.getEmail())
                  .password(passwordEncoder.encode(request.getPassword()))
                  .role(Role.ROLE_USER)
                  .build();

      user = userService.save(user);
      var jwt = jwtService.generateToken(user);
      return JwtAuthenticationResponse.builder().token(jwt).build();
  }


  public JwtAuthenticationResponse signin(SignInRequest request) {
      authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
      var user = userRepository.findByEmail(request.getEmail())
              .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
      var jwt = jwtService.generateToken(user);
      return JwtAuthenticationResponse.builder().token(jwt).build();
  }
  
}
