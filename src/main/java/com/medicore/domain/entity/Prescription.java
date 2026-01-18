package com.medicore.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prescriptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    private Long recordId;
    private Long doctorId;

    @Column(nullable = false, unique = true)
    private String prescriptionNo;

    private LocalDate prescriptionDate;
    private String prescriptionType;
    private String status; // ORDERED, DISPENSED, CANCELLED

    private LocalDateTime dispensedAt;
    private Long dispensedBy;
}
