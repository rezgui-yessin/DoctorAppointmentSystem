package com.example.doctorappointment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class DoctorAppointmentSystemApplicationTests {

    @Test
    void contextLoads() {
        // Verifies the full Spring context (security, JPA, beans) wires up correctly
    }

}
