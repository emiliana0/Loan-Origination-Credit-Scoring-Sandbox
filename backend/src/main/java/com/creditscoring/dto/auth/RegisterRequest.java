package com.creditscoring.dto.auth;

import com.creditscoring.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, message = "Паролата трябва да е поне 6 символа")
    private String password;

    @NotBlank
    private String fullName;

    @NotNull
    private Role role;
}
