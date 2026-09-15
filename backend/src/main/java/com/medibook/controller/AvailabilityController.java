package com.medibook.controller;

import com.medibook.dto.AvailabilityRequest;
import com.medibook.entity.Availability;
import com.medibook.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @GetMapping("/{doctorId}")
    public ResponseEntity<List<Availability>> getAvailability(
            @PathVariable Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date != null) {
            return ResponseEntity.ok(availabilityService.getDoctorAvailabilityForDate(doctorId, date));
        }
        return ResponseEntity.ok(availabilityService.getDoctorAvailability(doctorId));
    }

    @PostMapping
    public ResponseEntity<Availability> addAvailability(@Valid @RequestBody AvailabilityRequest request) {
        return ResponseEntity.ok(availabilityService.addAvailability(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable Long id) {
        availabilityService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }
}
