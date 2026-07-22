package com.example.doctorappointment.service;

import com.example.doctorappointment.dto.request.DoctorRequestDTO;
import com.example.doctorappointment.dto.response.DoctorResponseDTO;

import java.util.List;

public interface DoctorService {
    DoctorResponseDTO create(DoctorRequestDTO request);
    DoctorResponseDTO getById(Long id);
    List<DoctorResponseDTO> getAll();
    List<DoctorResponseDTO> getBySpecialization(String specialization);
    DoctorResponseDTO update(Long id, DoctorRequestDTO request);
    void delete(Long id);
}
