package com.medibook.controller;

import com.medibook.dto.AppointmentRequest;
import com.medibook.entity.Appointment;
import com.medibook.entity.AppointmentStatus;
import com.medibook.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.createAppointment(request));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Appointment>> getPatientAppointments() {
        return ResponseEntity.ok(appointmentService.getPatientAppointments());
    }

    @GetMapping("/doctor")
    public ResponseEntity<List<Appointment>> getDoctorAppointments() {
        return ResponseEntity.ok(appointmentService.getDoctorAppointments());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Appointment> cancelAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Appointment> approveAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, AppointmentStatus.CONFIRMED));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Appointment> rejectAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, AppointmentStatus.CANCELLED));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Appointment> completeAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, AppointmentStatus.COMPLETED));
    }

    @GetMapping("/patient-stats")
    public ResponseEntity<Map<String, Object>> getPatientStats() {
        return ResponseEntity.ok(appointmentService.getPatientStats());
    }

    @GetMapping("/doctor-stats")
    public ResponseEntity<Map<String, Object>> getDoctorStats() {
        return ResponseEntity.ok(appointmentService.getDoctorStats());
    }
}
