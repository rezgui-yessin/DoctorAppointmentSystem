package com.example.doctorappointment.dto.response;

public record AuthResponseDTO(
        String token,
        String email,
        String role
) {}
