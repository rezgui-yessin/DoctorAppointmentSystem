package com.example.doctorappointment.dto.response;

public record DoctorResponseDTO(
        Long id,
        String fullName,
        String specialization,
        String email,
        String phone,
        String workingDays,
        String startTime,
        String endTime
) {}
