package com.medibook.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String specialization;

    private Integer experience; // in years

    private String hospital;

    private Double consultationFee;

    private Double rating;

    @Column(length = 1000)
    private String bio;

    private String avatarUrl;

    private String availableDays; // e.g. "Monday, Wednesday, Friday"

    private Boolean isAvailableToday;
}
