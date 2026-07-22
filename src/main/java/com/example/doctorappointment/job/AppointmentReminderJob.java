package com.example.doctorappointment.job;

import com.example.doctorappointment.entity.Appointment;
import com.example.doctorappointment.entity.enums.AppointmentStatus;
import com.example.doctorappointment.repository.AppointmentRepository;
import com.example.doctorappointment.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentReminderJob {

    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    // Runs every day at 8:00 AM
    @Scheduled(cron = "0 0 8 * * *")
    public void sendReminders() {
        log.info("Starting scheduled appointment reminder job...");
        
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDateTime startOfDay = tomorrow.atStartOfDay();
        LocalDateTime endOfDay = tomorrow.atTime(LocalTime.MAX);

        // Ideally we need a method in repo to find by date range and status. Let's filter in memory for now or update repo if needed.
        // We will fetch all non-cancelled appointments for tomorrow (assuming the system is small enough, but a custom query is better for production).
        // For now, let's fetch all CONFIRMED or PENDING appointments.
        
        // Let's assume we want to remind all PENDING or CONFIRMED.
        List<Appointment> allTomorrow = appointmentRepository.findByStatus(AppointmentStatus.PENDING).stream()
                .filter(a -> a.getAppointmentTime().toLocalDate().equals(tomorrow))
                .toList();

        for (Appointment appointment : allTomorrow) {
            String patientEmail = appointment.getPatient().getEmail();
            String doctorEmail = appointment.getDoctor().getEmail();
            String time = appointment.getAppointmentTime().toLocalTime().toString();

            // Send to Patient
            emailService.sendReminderEmail(
                    patientEmail,
                    "Appointment Reminder",
                    String.format("Dear %s,\n\nThis is a reminder for your upcoming appointment with Dr. %s tomorrow at %s.\n\nThank you,\nDoctor Appointment System",
                            appointment.getPatient().getFullName(),
                            appointment.getDoctor().getFullName(),
                            time)
            );

            // Send to Doctor
            emailService.sendReminderEmail(
                    doctorEmail,
                    "Upcoming Appointment",
                    String.format("Dear Dr. %s,\n\nYou have an appointment with patient %s tomorrow at %s.\n\nThank you.",
                            appointment.getDoctor().getFullName(),
                            appointment.getPatient().getFullName(),
                            time)
            );
        }

        log.info("Finished sending {} reminders.", allTomorrow.size());
    }
}
