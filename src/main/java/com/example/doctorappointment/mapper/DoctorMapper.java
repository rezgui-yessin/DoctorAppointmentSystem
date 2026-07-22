package com.example.doctorappointment.mapper;

import com.example.doctorappointment.dto.request.DoctorRequestDTO;
import com.example.doctorappointment.dto.response.DoctorResponseDTO;
import com.example.doctorappointment.entity.Doctor;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper {

    public Doctor toEntity(DoctorRequestDTO dto) {
        return Doctor.builder()
                .fullName(dto.fullName())
                .specialization(dto.specialization())
                .email(dto.email())
                .phone(dto.phone())
                .workingDays(dto.workingDays())
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .build();
    }

    public DoctorResponseDTO toDto(Doctor doctor) {
        return new DoctorResponseDTO(
                doctor.getId(),
                doctor.getFullName(),
                doctor.getSpecialization(),
                doctor.getEmail(),
                doctor.getPhone(),
                doctor.getWorkingDays(),
                doctor.getStartTime(),
                doctor.getEndTime()
        );
    }
}
