package com.medibook.service;

import com.medibook.dto.AppointmentRequest;
import com.medibook.entity.*;
import com.medibook.repository.AppointmentRepository;
import com.medibook.repository.AvailabilityRepository;
import com.medibook.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final AvailabilityRepository availabilityRepository;
    private final AuthService authService;

    @Transactional
    public Appointment createAppointment(AppointmentRequest request) {
        User currentUser = authService.getCurrentUser();

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Double-booking check
        boolean exists = appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                doctor.getId(),
                request.getAppointmentDate(),
                request.getAppointmentTime(),
                AppointmentStatus.CANCELLED
        );

        if (exists) {
            throw new RuntimeException("Selected time slot is already booked for Dr. " + doctor.getName() + ". Please choose another time slot.");
        }

        Appointment appointment = Appointment.builder()
                .patient(currentUser)
                .doctor(doctor)
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .status(AppointmentStatus.CONFIRMED)
                .notes(request.getNotes())
                .build();

        appointment = appointmentRepository.save(appointment);

        // Mark matching availability slot as booked
        Optional<Availability> availOpt = availabilityRepository.findByDoctorIdAndAvailableDateAndStartTime(
                doctor.getId(), request.getAppointmentDate(), request.getAppointmentTime()
        );
        if (availOpt.isPresent()) {
            Availability avail = availOpt.get();
            avail.setIsBooked(true);
            availabilityRepository.save(avail);
        }

        return appointment;
    }

    public List<Appointment> getPatientAppointments() {
        User currentUser = authService.getCurrentUser();
        return appointmentRepository.findByPatientIdOrderByAppointmentDateDescAppointmentTimeAsc(currentUser.getId());
    }

    public List<Appointment> getDoctorAppointments() {
        User currentUser = authService.getCurrentUser();
        Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Doctor profile not found for current user"));
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateDescAppointmentTimeAsc(doctor.getId());
    }

    @Transactional
    public Appointment cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        User currentUser = authService.getCurrentUser();

        // Verify patient or doctor authorization
        boolean isPatient = appointment.getPatient().getId().equals(currentUser.getId());
        boolean isDoctor = appointment.getDoctor().getUser().getId().equals(currentUser.getId());

        if (!isPatient && !isDoctor && currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("You are not authorized to cancel this appointment");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment = appointmentRepository.save(appointment);

        // Un-book availability slot if exists
        Optional<Availability> availOpt = availabilityRepository.findByDoctorIdAndAvailableDateAndStartTime(
                appointment.getDoctor().getId(), appointment.getAppointmentDate(), appointment.getAppointmentTime()
        );
        if (availOpt.isPresent()) {
            Availability avail = availOpt.get();
            avail.setIsBooked(false);
            availabilityRepository.save(avail);
        }

        return appointment;
    }

    @Transactional
    public Appointment updateAppointmentStatus(Long appointmentId, AppointmentStatus newStatus) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        User currentUser = authService.getCurrentUser();
        if (!appointment.getDoctor().getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only the assigned doctor can update appointment status");
        }

        appointment.setStatus(newStatus);
        return appointmentRepository.save(appointment);
    }

    public Map<String, Object> getPatientStats() {
        User currentUser = authService.getCurrentUser();
        List<Appointment> all = appointmentRepository.findByPatientIdOrderByAppointmentDateDescAppointmentTimeAsc(currentUser.getId());
        
        long upcoming = all.stream().filter(a -> a.getStatus() == AppointmentStatus.CONFIRMED || a.getStatus() == AppointmentStatus.PENDING).count();
        long completed = all.stream().filter(a -> a.getStatus() == AppointmentStatus.COMPLETED).count();
        long cancelled = all.stream().filter(a -> a.getStatus() == AppointmentStatus.CANCELLED).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", all.size());
        stats.put("upcoming", upcoming);
        stats.put("completed", completed);
        stats.put("cancelled", cancelled);
        return stats;
    }

    public Map<String, Object> getDoctorStats() {
        User currentUser = authService.getCurrentUser();
        Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
        
        List<Appointment> all = appointmentRepository.findByDoctorIdOrderByAppointmentDateDescAppointmentTimeAsc(doctor.getId());
        
        long pending = all.stream().filter(a -> a.getStatus() == AppointmentStatus.PENDING).count();
        long confirmed = all.stream().filter(a -> a.getStatus() == AppointmentStatus.CONFIRMED).count();
        long completed = all.stream().filter(a -> a.getStatus() == AppointmentStatus.COMPLETED).count();
        long cancelled = all.stream().filter(a -> a.getStatus() == AppointmentStatus.CANCELLED).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPatients", all.stream().map(a -> a.getPatient().getId()).distinct().count());
        stats.put("todayCount", confirmed + pending);
        stats.put("pendingRequests", pending);
        stats.put("completedCount", completed);
        stats.put("cancelledCount", cancelled);
        return stats;
    }
}
