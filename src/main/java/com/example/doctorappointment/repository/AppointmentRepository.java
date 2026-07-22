package com.example.doctorappointment.repository;

import com.example.doctorappointment.entity.Appointment;
import com.example.doctorappointment.entity.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    Page<Appointment> findByDoctorId(Long doctorId, Pageable pageable);

    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
            Long doctorId, LocalDateTime start, LocalDateTime end);

    boolean existsByDoctorIdAndAppointmentTimeAndStatusNot(
            Long doctorId, LocalDateTime appointmentTime, AppointmentStatus status);

    List<Appointment> findByStatus(AppointmentStatus status);
}
