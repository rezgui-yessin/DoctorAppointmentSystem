package com.example.doctorappointment.mapper;

import com.example.doctorappointment.dto.request.PatientRequestDTO;
import com.example.doctorappointment.dto.response.PatientResponseDTO;
import com.example.doctorappointment.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public Patient toEntity(PatientRequestDTO dto) {
        return Patient.builder()
                .fullName(dto.fullName())
                .email(dto.email())
                .phone(dto.phone())
                .dateOfBirth(dto.dateOfBirth())
                .build();
    }

    public PatientResponseDTO toDto(Patient patient) {
        return new PatientResponseDTO(
                patient.getId(),
                patient.getFullName(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getDateOfBirth()
        );
    }
}
