package com.medicore.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private Long doctorId; // Linking to User ID

    @Column(nullable = false)
    private LocalDate visitDate;

    @Column(nullable = false)
    private String visitType; // OUTPATIENT, INPATIENT, EMERGENCY

    @Column(nullable = false)
    private String department;

    @Column(columnDefinition = "TEXT")
    private String chiefComplaint;

    private String diagnosisCode;

    private String diagnosisName;

    @Column(columnDefinition = "TEXT")
    private String treatmentPlan;

    private String status; // DRAFT, COMPLETED, AMENDED

    private LocalDateTime signedAt;

    private Long signedBy;
}
