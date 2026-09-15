package com.medibook.service;

import com.medibook.dto.AvailabilityRequest;
import com.medibook.entity.Availability;
import com.medibook.entity.Doctor;
import com.medibook.entity.User;
import com.medibook.repository.AvailabilityRepository;
import com.medibook.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;
    private final AuthService authService;

    public List<Availability> getDoctorAvailability(Long doctorId) {
        return availabilityRepository.findByDoctorId(doctorId);
    }

    public List<Availability> getDoctorAvailabilityForDate(Long doctorId, LocalDate date) {
        return availabilityRepository.findByDoctorIdAndAvailableDate(doctorId, date);
    }

    @Transactional
    public Availability addAvailability(AvailabilityRequest request) {
        User currentUser = authService.getCurrentUser();
        Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        Availability availability = Availability.builder()
                .doctor(doctor)
                .availableDate(request.getAvailableDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isBooked(false)
                .build();

        return availabilityRepository.save(availability);
    }

    @Transactional
    public void deleteAvailability(Long id) {
        User currentUser = authService.getCurrentUser();
        Availability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Availability slot not found"));

        if (!availability.getDoctor().getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized to delete this slot");
        }

        availabilityRepository.delete(availability);
    }
}
