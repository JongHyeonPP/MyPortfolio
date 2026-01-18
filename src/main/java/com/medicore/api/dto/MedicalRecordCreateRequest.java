package com.medicore.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordCreateRequest {
    private Long patientId;
    private LocalDate visitDate;
    private String visitType;
    private String department;
    private String chiefComplaint;
    private String diagnosisCode;
    private String diagnosisName;
}
