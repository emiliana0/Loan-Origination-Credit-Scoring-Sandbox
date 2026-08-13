package com.creditscoring.service;

import com.creditscoring.domain.User;
import com.creditscoring.dto.auth.AuthResponse;
import com.creditscoring.dto.auth.LoginRequest;
import com.creditscoring.dto.auth.RegisterRequest;
import com.creditscoring.exception.EmailAlreadyExistsException;
import com.creditscoring.repository.UserRepository;
import com.creditscoring.security.JwtService;
import com.creditscoring.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(request.getRole())
                .build();

        userRepository.save(user);

        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtService.generateToken(principal);

        return new AuthResponse(token, user.getEmail(), user.getFullName(), user.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        // AuthenticationManager хвърля BadCredentialsException, ако паролата не съвпада
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("Потребителят изчезна след успешна автентикация"));

        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtService.generateToken(principal);

        return new AuthResponse(token, user.getEmail(), user.getFullName(), user.getRole());
    }
}
