package com.example.doctorappointment.service.impl;

import com.example.doctorappointment.security.CustomUserDetails;
import com.example.doctorappointment.security.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;
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

        CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        if (currentUser != null && SecurityUtils.hasRole("PATIENT") && !patient.getId().equals(currentUser.getPatientId())) {
            throw new AccessDeniedException("You can only book appointments for yourself");
        }

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
        Appointment appointment = findOrThrow(id);
        verifyOwnership(appointment);
        return appointmentMapper.toDto(appointment);
    }

    @Override
    public List<AppointmentResponseDTO> getByPatient(Long patientId) {
        CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        if (currentUser != null && SecurityUtils.hasRole("PATIENT") && !patientId.equals(currentUser.getPatientId())) {
            throw new AccessDeniedException("You do not have permission to access this patient's appointments");
        }
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(appointmentMapper::toDto)
                .toList();
    }

    @Override
    public Page<AppointmentResponseDTO> getByDoctor(Long doctorId, Pageable pageable) {
        CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        if (currentUser != null && SecurityUtils.hasRole("DOCTOR") && !doctorId.equals(currentUser.getDoctorId())) {
            throw new AccessDeniedException("You do not have permission to access this doctor's appointments");
        }
        return appointmentRepository.findByDoctorId(doctorId, pageable)
                .map(appointmentMapper::toDto);
    }

    @Override
    @Transactional
    public AppointmentResponseDTO updateStatus(Long id, AppointmentStatus status) {
        Appointment appointment = findOrThrow(id);
        verifyOwnership(appointment);
        appointment.setStatus(status);
        return appointmentMapper.toDto(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public void cancelAppointment(Long id) {
        Appointment appointment = findOrThrow(id);
        verifyOwnership(appointment);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    private Appointment findOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
    }

    private void verifyOwnership(Appointment appointment) {
        CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) return;
        if (SecurityUtils.hasRole("ADMIN")) return;

        if (SecurityUtils.hasRole("PATIENT")) {
            if (!appointment.getPatient().getId().equals(currentUser.getPatientId())) {
                throw new AccessDeniedException("You do not have permission to access this appointment");
            }
        } else if (SecurityUtils.hasRole("DOCTOR")) {
            if (!appointment.getDoctor().getId().equals(currentUser.getDoctorId())) {
                throw new AccessDeniedException("You do not have permission to access this appointment");
            }
        }
    }
    @Override
    public List<com.example.doctorappointment.dto.response.AvailableSlotDTO> getAvailableSlots(Long doctorId, java.time.LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        if (doctor.getWorkingDays() == null || !doctor.getWorkingDays().contains(date.getDayOfWeek().name().substring(0, 3))) {
            return java.util.Collections.emptyList();
        }

        java.time.LocalTime start = java.time.LocalTime.parse(doctor.getStartTime());
        java.time.LocalTime end = java.time.LocalTime.parse(doctor.getEndTime());

        List<java.time.LocalTime> generatedSlots = new java.util.ArrayList<>();
        java.time.LocalTime current = start;
        while (current.isBefore(end)) {
            generatedSlots.add(current);
            current = current.plusMinutes(30);
        }

        List<Appointment> bookedAppointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                doctorId, date.atStartOfDay(), date.atTime(java.time.LocalTime.MAX)
        );

        List<java.time.LocalTime> bookedTimes = bookedAppointments.stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .map(a -> a.getAppointmentTime().toLocalTime())
                .toList();

        return generatedSlots.stream()
                .filter(slot -> !bookedTimes.contains(slot))
                .map(slot -> new com.example.doctorappointment.dto.response.AvailableSlotDTO(slot.toString()))
                .toList();
    }
}
