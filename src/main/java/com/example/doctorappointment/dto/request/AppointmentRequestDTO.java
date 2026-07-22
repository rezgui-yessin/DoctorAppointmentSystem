package com.example.doctorappointment.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentRequestDTO(
        @NotNull(message = "Doctor id is required") Long doctorId,
        @NotNull(message = "Patient id is required") Long patientId,
        @NotNull(message = "Appointment time is required")
        @Future(message = "Appointment time must be in the future") LocalDateTime appointmentTime,
        String reason
) {}
