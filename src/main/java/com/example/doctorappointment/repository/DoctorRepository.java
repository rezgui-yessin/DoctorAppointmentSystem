package com.example.doctorappointment.repository;

import com.example.doctorappointment.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByEmail(String email);

    List<Doctor> findBySpecializationIgnoreCase(String specialization);

    boolean existsByEmail(String email);
}
