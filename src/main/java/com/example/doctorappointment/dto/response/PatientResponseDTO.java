package com.example.doctorappointment.dto.response;

import java.time.LocalDate;

public record PatientResponseDTO(
        Long id,
        String fullName,
        String email,
        String phone,
        LocalDate dateOfBirth
) {}
