package com.medibook.service;

import com.medibook.entity.Doctor;
import com.medibook.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public List<Doctor> getAllDoctors(String search, String dept, String sort) {
        String queryParam = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        String deptParam = (dept != null && !dept.trim().isEmpty() && !"All".equalsIgnoreCase(dept.trim())) ? dept.trim() : null;

        List<Doctor> doctors = doctorRepository.searchDoctors(queryParam, deptParam);

        if ("rating".equalsIgnoreCase(sort)) {
            doctors = doctors.stream()
                    .sorted(Comparator.comparing(Doctor::getRating, Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
        } else if ("experience".equalsIgnoreCase(sort)) {
            doctors = doctors.stream()
                    .sorted(Comparator.comparing(Doctor::getExperience, Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
        } else if ("fee_low".equalsIgnoreCase(sort)) {
            doctors = doctors.stream()
                    .sorted(Comparator.comparing(Doctor::getConsultationFee, Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList());
        }

        return doctors;
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
    }

    public Doctor updateDoctorProfile(Long doctorId, Doctor updatedInfo) {
        Doctor doctor = getDoctorById(doctorId);
        if (updatedInfo.getName() != null) doctor.setName(updatedInfo.getName());
        if (updatedInfo.getSpecialization() != null) doctor.setSpecialization(updatedInfo.getSpecialization());
        if (updatedInfo.getExperience() != null) doctor.setExperience(updatedInfo.getExperience());
        if (updatedInfo.getHospital() != null) doctor.setHospital(updatedInfo.getHospital());
        if (updatedInfo.getConsultationFee() != null) doctor.setConsultationFee(updatedInfo.getConsultationFee());
        if (updatedInfo.getBio() != null) doctor.setBio(updatedInfo.getBio());
        if (updatedInfo.getAvailableDays() != null) doctor.setAvailableDays(updatedInfo.getAvailableDays());

        return doctorRepository.save(doctor);
    }
}
