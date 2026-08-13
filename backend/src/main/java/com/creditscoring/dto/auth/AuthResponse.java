package com.creditscoring.dto.auth;

import com.creditscoring.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String fullName;
    private Role role;
}
