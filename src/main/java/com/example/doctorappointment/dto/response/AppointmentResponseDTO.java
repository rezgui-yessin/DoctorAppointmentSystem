package com.example.doctorappointment.dto.response;

import com.example.doctorappointment.entity.enums.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponseDTO(
        Long id,
        Long doctorId,
        String doctorName,
        Long patientId,
        String patientName,
        LocalDateTime appointmentTime,
        AppointmentStatus status,
        String reason
) {}
