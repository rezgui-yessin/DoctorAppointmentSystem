package com.example.doctorappointment.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DoctorRequestDTO(
        @NotBlank(message = "Full name is required") String fullName,
        @NotBlank(message = "Specialization is required") String specialization,
        @Email(message = "Valid email is required") @NotBlank String email,
        String phone,
        String workingDays,
        String startTime,
        String endTime
) {}
