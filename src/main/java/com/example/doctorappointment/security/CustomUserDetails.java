package com.example.doctorappointment.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {

    private final Long patientId;
    private final Long doctorId;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Long patientId, Long doctorId) {
        super(username, password, authorities);
        this.patientId = patientId;
        this.doctorId = doctorId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }
}
