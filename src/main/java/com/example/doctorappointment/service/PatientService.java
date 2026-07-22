package com.example.doctorappointment.service;

import com.example.doctorappointment.dto.request.PatientRequestDTO;
import com.example.doctorappointment.dto.response.PatientResponseDTO;

import java.util.List;

public interface PatientService {
    PatientResponseDTO create(PatientRequestDTO request);
    PatientResponseDTO getById(Long id);
    List<PatientResponseDTO> getAll();
    PatientResponseDTO update(Long id, PatientRequestDTO request);
    void delete(Long id);
}
