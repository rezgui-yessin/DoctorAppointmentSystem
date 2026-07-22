package com.example.doctorappointment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String fullName;

    @NotBlank
    private String specialization;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    /** e.g. "MON,TUE,WED,THU,FRI" - kept simple; can be normalized into its own table later */
    private String workingDays;

    private String startTime; // "09:00"
    private String endTime;   // "17:00"

    @Builder.Default
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();
}
