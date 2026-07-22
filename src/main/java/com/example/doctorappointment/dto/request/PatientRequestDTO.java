package com.example.doctorappointment.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record PatientRequestDTO(
        @NotBlank(message = "Full name is required") String fullName,
        @Email(message = "Valid email is required") @NotBlank String email,
        String phone,
        @Past(message = "Date of birth must be in the past") LocalDate dateOfBirth
) {}
