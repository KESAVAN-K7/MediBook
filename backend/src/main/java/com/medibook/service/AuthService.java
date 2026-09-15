package com.medibook.service;

import com.medibook.dto.AuthRequest;
import com.medibook.dto.AuthResponse;
import com.medibook.dto.RegisterRequest;
import com.medibook.entity.Doctor;
import com.medibook.entity.Role;
import com.medibook.entity.User;
import com.medibook.repository.DoctorRepository;
import com.medibook.repository.UserRepository;
import com.medibook.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long doctorId = null;
        if (user.getRole() == Role.DOCTOR) {
            Optional<Doctor> doctorOpt = doctorRepository.findByUserId(user.getId());
            if (doctorOpt.isPresent()) {
                doctorId = doctorOpt.get().getId();
            }
        }

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .doctorId(doctorId)
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email address is already in use!");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole())
                .build();

        user = userRepository.save(user);

        Long doctorId = null;
        if (request.getRole() == Role.DOCTOR) {
            Doctor doctor = Doctor.builder()
                    .user(user)
                    .name("Dr. " + user.getName())
                    .specialization(request.getSpecialization() != null ? request.getSpecialization() : "General Medicine")
                    .experience(request.getExperience() != null ? request.getExperience() : 5)
                    .hospital(request.getHospital() != null ? request.getHospital() : "MediBook Health Center")
                    .consultationFee(request.getConsultationFee() != null ? request.getConsultationFee() : 500.0)
                    .rating(4.8)
                    .bio(request.getBio() != null ? request.getBio() : "Experienced medical practitioner committed to quality patient care.")
                    .avatarUrl("https://images.unsplash.com/photo-1537368910025-700350fe46c7?auto=format&fit=crop&w=300&q=80")
                    .availableDays("Monday, Wednesday, Friday")
                    .isAvailableToday(true)
                    .build();

            doctor = doctorRepository.save(doctor);
            doctorId = doctor.getId();
        }

        AuthRequest loginReq = new AuthRequest();
        loginReq.setEmail(request.getEmail());
        loginReq.setPassword(request.getPassword());

        return login(loginReq);
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
