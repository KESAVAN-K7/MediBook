package com.medibook.config;

import com.medibook.entity.*;
import com.medibook.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final AvailabilityRepository availabilityRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (departmentRepository.count() > 0) {
            return; // Data already seeded
        }

        // 1. Seed Departments
        Department cardiology = departmentRepository.save(Department.builder()
                .name("Cardiology")
                .icon("Heart")
                .description("Heart and cardiovascular health care by expert cardiologists.")
                .doctorCount(15)
                .build());

        Department neurology = departmentRepository.save(Department.builder()
                .name("Neurology")
                .icon("Brain")
                .description("Specialized treatment for brain, nerve, and spine disorders.")
                .doctorCount(12)
                .build());

        Department dermatology = departmentRepository.save(Department.builder()
                .name("Dermatology")
                .icon("Sparkles")
                .description("Comprehensive skin, hair, and skincare treatments.")
                .doctorCount(18)
                .build());

        Department genMed = departmentRepository.save(Department.builder()
                .name("General Medicine")
                .icon("Stethoscope")
                .description("Primary care, routine checkups, and general diagnosis.")
                .doctorCount(25)
                .build());

        Department dentistry = departmentRepository.save(Department.builder()
                .name("Dentistry")
                .icon("Smile")
                .description("Dental hygiene, cosmetic surgery, and orthodontic care.")
                .doctorCount(10)
                .build());

        Department ophthalmology = departmentRepository.save(Department.builder()
                .name("Ophthalmology")
                .icon("Eye")
                .description("Advanced eye checkups, vision therapy, and laser treatments.")
                .doctorCount(8)
                .build());

        // 2. Seed Demo Users
        User patient1 = userRepository.save(User.builder()
                .name("Rahul Sharma")
                .email("patient@medibook.com")
                .password(passwordEncoder.encode("password123"))
                .phone("+91 98765 43210")
                .role(Role.PATIENT)
                .build());

        User docUser1 = userRepository.save(User.builder()
                .name("Arun Kumar")
                .email("doctor@medibook.com")
                .password(passwordEncoder.encode("password123"))
                .phone("+91 91234 56789")
                .role(Role.DOCTOR)
                .build());

        User docUser2 = userRepository.save(User.builder()
                .name("Priya Sharma")
                .email("priya@medibook.com")
                .password(passwordEncoder.encode("password123"))
                .phone("+91 98111 22233")
                .role(Role.DOCTOR)
                .build());

        User docUser3 = userRepository.save(User.builder()
                .name("Rahul Menon")
                .email("rahul@medibook.com")
                .password(passwordEncoder.encode("password123"))
                .phone("+91 97444 55566")
                .role(Role.DOCTOR)
                .build());

        User docUser4 = userRepository.save(User.builder()
                .name("Ananya Rao")
                .email("ananya@medibook.com")
                .password(passwordEncoder.encode("password123"))
                .phone("+91 96333 44455")
                .role(Role.DOCTOR)
                .build());

        // 3. Seed Doctors
        Doctor doc1 = doctorRepository.save(Doctor.builder()
                .user(docUser1)
                .name("Dr. Arun Kumar")
                .specialization("Cardiology")
                .experience(10)
                .hospital("ABC Medical Center")
                .consultationFee(500.0)
                .rating(4.8)
                .bio("Dr. Arun Kumar is an experienced senior cardiologist specializing in interventional cardiology, heart preventive care, and hypertension management.")
                .avatarUrl("https://images.unsplash.com/photo-1622253692010-333f2da6031d?auto=format&fit=crop&w=400&q=80")
                .availableDays("Monday, Wednesday, Friday")
                .isAvailableToday(true)
                .build());

        Doctor doc2 = doctorRepository.save(Doctor.builder()
                .user(docUser2)
                .name("Dr. Priya Sharma")
                .specialization("Dermatology")
                .experience(8)
                .hospital("Apollo Skin Clinic")
                .consultationFee(400.0)
                .rating(4.9)
                .bio("Dr. Priya Sharma is a certified dermatologist with 8+ years of expertise in clinical skincare, acne therapy, and aesthetic procedures.")
                .avatarUrl("https://images.unsplash.com/photo-1594824813566-88855ce783d1?auto=format&fit=crop&w=400&q=80")
                .availableDays("Tuesday, Thursday, Saturday")
                .isAvailableToday(true)
                .build());

        Doctor doc3 = doctorRepository.save(Doctor.builder()
                .user(docUser3)
                .name("Dr. Rahul Menon")
                .specialization("Neurology")
                .experience(12)
                .hospital("Fortis Healthcare Center")
                .consultationFee(700.0)
                .rating(4.7)
                .bio("Dr. Rahul Menon is a leading neurospecialist focusing on migraine therapies, stroke management, and nerve disorder rehabilitations.")
                .avatarUrl("https://images.unsplash.com/photo-1537368910025-700350fe46c7?auto=format&fit=crop&w=400&q=80")
                .availableDays("Monday, Tuesday, Thursday")
                .isAvailableToday(false)
                .build());

        Doctor doc4 = doctorRepository.save(Doctor.builder()
                .user(docUser4)
                .name("Dr. Ananya Rao")
                .specialization("General Medicine")
                .experience(7)
                .hospital("City Care Hospital")
                .consultationFee(300.0)
                .rating(4.6)
                .bio("Dr. Ananya Rao provides holistic primary healthcare, routine preventative checkups, and diagnostic consultations for families.")
                .avatarUrl("https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=400&q=80")
                .availableDays("Monday, Wednesday, Saturday")
                .isAvailableToday(true)
                .build());

        // 4. Seed Availability Slots (for next 7 days)
        LocalDate today = LocalDate.now();
        List<Doctor> allDoctors = Arrays.asList(doc1, doc2, doc3, doc4);
        List<LocalTime> timeSlots = Arrays.asList(
                LocalTime.of(9, 0),
                LocalTime.of(9, 30),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                LocalTime.of(11, 0),
                LocalTime.of(14, 0),
                LocalTime.of(14, 30),
                LocalTime.of(15, 0)
        );

        for (Doctor doc : allDoctors) {
            for (int i = 0; i < 7; i++) {
                LocalDate slotDate = today.plusDays(i);
                for (LocalTime slotTime : timeSlots) {
                    availabilityRepository.save(Availability.builder()
                            .doctor(doc)
                            .availableDate(slotDate)
                            .startTime(slotTime)
                            .endTime(slotTime.plusMinutes(30))
                            .isBooked(false)
                            .build());
                }
            }
        }

        // 5. Seed Demo Appointments for Patient
        appointmentRepository.save(Appointment.builder()
                .patient(patient1)
                .doctor(doc1)
                .appointmentDate(today.plusDays(1))
                .appointmentTime(LocalTime.of(10, 0))
                .status(AppointmentStatus.CONFIRMED)
                .notes("Routine cardiac wellness checkup.")
                .build());

        appointmentRepository.save(Appointment.builder()
                .patient(patient1)
                .doctor(doc2)
                .appointmentDate(today.plusDays(3))
                .appointmentTime(LocalTime.of(14, 30))
                .status(AppointmentStatus.PENDING)
                .notes("Consultation regarding skin allergy.")
                .build());

        appointmentRepository.save(Appointment.builder()
                .patient(patient1)
                .doctor(doc4)
                .appointmentDate(today.minusDays(5))
                .appointmentTime(LocalTime.of(11, 0))
                .status(AppointmentStatus.COMPLETED)
                .notes("Annual general blood test follow-up.")
                .build());
    }
}
