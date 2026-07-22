package com.example.doctorappointment.service.impl;

import com.example.doctorappointment.dto.request.DoctorRequestDTO;
import com.example.doctorappointment.dto.response.DoctorResponseDTO;
import com.example.doctorappointment.entity.Doctor;
import com.example.doctorappointment.exception.DuplicateResourceException;
import com.example.doctorappointment.exception.ResourceNotFoundException;
import com.example.doctorappointment.mapper.DoctorMapper;
import com.example.doctorappointment.repository.DoctorRepository;
import com.example.doctorappointment.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    @Override
    @Transactional
    public DoctorResponseDTO create(DoctorRequestDTO request) {
        if (doctorRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("A doctor with this email already exists");
        }
        Doctor doctor = doctorMapper.toEntity(request);
        return doctorMapper.toDto(doctorRepository.save(doctor));
    }

    @Override
    public DoctorResponseDTO getById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
        return doctorMapper.toDto(doctor);
    }

    @Override
    public List<DoctorResponseDTO> getAll() {
        return doctorRepository.findAll().stream()
                .map(doctorMapper::toDto)
                .toList();
    }

    @Override
    public List<DoctorResponseDTO> getBySpecialization(String specialization) {
        return doctorRepository.findBySpecializationIgnoreCase(specialization).stream()
                .map(doctorMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public DoctorResponseDTO update(Long id, DoctorRequestDTO request) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));

        doctor.setFullName(request.fullName());
        doctor.setSpecialization(request.specialization());
        doctor.setEmail(request.email());
        doctor.setPhone(request.phone());
        doctor.setWorkingDays(request.workingDays());
        doctor.setStartTime(request.startTime());
        doctor.setEndTime(request.endTime());

        return doctorMapper.toDto(doctorRepository.save(doctor));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Doctor not found with id: " + id);
        }
        doctorRepository.deleteById(id);
    }
}
