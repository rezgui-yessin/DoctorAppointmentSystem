package com.example.doctorappointment.service;

import com.example.doctorappointment.dto.request.AppointmentRequestDTO;
import com.example.doctorappointment.dto.response.AppointmentResponseDTO;
import com.example.doctorappointment.entity.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AppointmentService {
    AppointmentResponseDTO bookAppointment(AppointmentRequestDTO request);
    AppointmentResponseDTO getById(Long id);
    List<AppointmentResponseDTO> getByPatient(Long patientId);
    Page<AppointmentResponseDTO> getByDoctor(Long doctorId, Pageable pageable);
    AppointmentResponseDTO updateStatus(Long id, AppointmentStatus status);
    void cancelAppointment(Long id);
}
