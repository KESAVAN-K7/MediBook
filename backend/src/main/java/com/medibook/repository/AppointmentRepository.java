package com.medibook.repository;

import com.medibook.entity.Appointment;
import com.medibook.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientIdOrderByAppointmentDateDescAppointmentTimeAsc(Long patientId);
    List<Appointment> findByDoctorIdOrderByAppointmentDateDescAppointmentTimeAsc(Long doctorId);
    
    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
            Long doctorId, LocalDate appointmentDate, LocalTime appointmentTime, AppointmentStatus status
    );

    long countByDoctorIdAndStatus(Long doctorId, AppointmentStatus status);
    long countByDoctorId(Long doctorId);
    long countByPatientId(Long patientId);
}
