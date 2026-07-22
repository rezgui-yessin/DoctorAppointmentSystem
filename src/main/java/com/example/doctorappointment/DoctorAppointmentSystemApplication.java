package com.example.doctorappointment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DoctorAppointmentSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoctorAppointmentSystemApplication.class, args);
    }

}
