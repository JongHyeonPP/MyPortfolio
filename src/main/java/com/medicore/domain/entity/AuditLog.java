package com.medicore.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    private Long userId;
    private String username;
    private String userRole;
    private String userDepartment;

    private String action; // CREATE, READ, UPDATE, DELETE...
    private String resourceType;
    private String resourceId;

    private Long patientId;
    private String patientNo;
    private String patientName; // Masked

    private String requestIp;
    private String requestUri;
    private String requestMethod;

    @Column(columnDefinition = "TEXT")
    private String requestParams; // Masked

    private String userAgent;

    private String result; // SUCCESS, FAILURE, DENIED
    private Integer responseStatus;
    private String failureReason;

    private Integer executionTimeMs;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
