package com.creditscoring.controller;

import com.creditscoring.dto.auth.AuthResponse;
import com.creditscoring.dto.auth.LoginRequest;
import com.creditscoring.dto.auth.RegisterRequest;
import com.creditscoring.security.UserPrincipal;
import com.creditscoring.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // Защитен endpoint - полезен за бърза проверка дали токенът работи
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(Map.of(
                "email", principal.getUsername(),
                "fullName", principal.getUser().getFullName(),
                "role", principal.getUser().getRole()
        ));
    }
}
