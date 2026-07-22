package com.example.doctorappointment.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthRequestDTO {

    public record RegisterRequest(
            @Email @NotBlank String email,
            @NotBlank(message = "Password is required") String password,
            @NotBlank(message = "Role is required (ADMIN, DOCTOR, PATIENT)") String role
    ) {}

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}
}
