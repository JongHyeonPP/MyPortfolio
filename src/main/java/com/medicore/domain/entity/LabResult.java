package com.medicore.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    private String testCode;
    private String testName;
    private String resultValue;
    private BigDecimal resultValueNum;
    private String unit;
    private String referenceRange;
    private BigDecimal referenceLow;
    private BigDecimal referenceHigh;
    private String abnormalFlag;
    private boolean criticalFlag;
    private String resultStatus;

    private LocalDateTime performedAt;
    private String deviceId;
    private LocalDateTime receivedAt;
}
