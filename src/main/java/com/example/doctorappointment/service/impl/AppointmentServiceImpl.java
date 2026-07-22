package com.example.doctorappointment.service.impl;

import com.example.doctorappointment.dto.request.AppointmentRequestDTO;
import com.example.doctorappointment.dto.response.AppointmentResponseDTO;
import com.example.doctorappointment.entity.Appointment;
import com.example.doctorappointment.entity.Doctor;
import com.example.doctorappointment.entity.Patient;
import com.example.doctorappointment.entity.enums.AppointmentStatus;
import com.example.doctorappointment.exception.AppointmentConflictException;
import com.example.doctorappointment.exception.ResourceNotFoundException;
import com.example.doctorappointment.mapper.AppointmentMapper;
import com.example.doctorappointment.repository.AppointmentRepository;
import com.example.doctorappointment.repository.DoctorRepository;
import com.example.doctorappointment.repository.PatientRepository;
import com.example.doctorappointment.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentMapper appointmentMapper;

    @Override
    @Transactional
    public AppointmentResponseDTO bookAppointment(AppointmentRequestDTO request) {
        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + request.doctorId()));
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.patientId()));

        boolean conflict = appointmentRepository.existsByDoctorIdAndAppointmentTimeAndStatusNot(
                doctor.getId(), request.appointmentTime(), AppointmentStatus.CANCELLED);
        if (conflict) {
            throw new AppointmentConflictException(
                    "Doctor " + doctor.getFullName() + " already has an appointment at this time");
        }

        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .appointmentTime(request.appointmentTime())
                .reason(request.reason())
                .status(AppointmentStatus.PENDING)
                .build();

        return appointmentMapper.toDto(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponseDTO getById(Long id) {
        return appointmentMapper.toDto(findOrThrow(id));
    }

    @Override
    public List<AppointmentResponseDTO> getByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(appointmentMapper::toDto)
                .toList();
    }

    @Override
    public Page<AppointmentResponseDTO> getByDoctor(Long doctorId, Pageable pageable) {
        return appointmentRepository.findByDoctorId(doctorId, pageable)
                .map(appointmentMapper::toDto);
    }

    @Override
    @Transactional
    public AppointmentResponseDTO updateStatus(Long id, AppointmentStatus status) {
        Appointment appointment = findOrThrow(id);
        appointment.setStatus(status);
        return appointmentMapper.toDto(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public void cancelAppointment(Long id) {
        Appointment appointment = findOrThrow(id);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    private Appointment findOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
    }
}
