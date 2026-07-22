package com.example.doctorappointment.service.impl;

import com.example.doctorappointment.dto.request.PatientRequestDTO;
import com.example.doctorappointment.dto.response.PatientResponseDTO;
import com.example.doctorappointment.entity.Patient;
import com.example.doctorappointment.exception.DuplicateResourceException;
import com.example.doctorappointment.exception.ResourceNotFoundException;
import com.example.doctorappointment.mapper.PatientMapper;
import com.example.doctorappointment.repository.PatientRepository;
import com.example.doctorappointment.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Override
    @Transactional
    public PatientResponseDTO create(PatientRequestDTO request) {
        if (patientRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("A patient with this email already exists");
        }
        Patient patient = patientMapper.toEntity(request);
        return patientMapper.toDto(patientRepository.save(patient));
    }

    @Override
    public PatientResponseDTO getById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        return patientMapper.toDto(patient);
    }

    @Override
    public List<PatientResponseDTO> getAll() {
        return patientRepository.findAll().stream()
                .map(patientMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public PatientResponseDTO update(Long id, PatientRequestDTO request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        patient.setFullName(request.fullName());
        patient.setEmail(request.email());
        patient.setPhone(request.phone());
        patient.setDateOfBirth(request.dateOfBirth());

        return patientMapper.toDto(patientRepository.save(patient));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }
}
