package com.medibook.repository;

import com.medibook.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByDoctorId(Long doctorId);
    List<Availability> findByDoctorIdAndAvailableDate(Long doctorId, LocalDate availableDate);
    Optional<Availability> findByDoctorIdAndAvailableDateAndStartTime(Long doctorId, LocalDate availableDate, LocalTime startTime);
}
