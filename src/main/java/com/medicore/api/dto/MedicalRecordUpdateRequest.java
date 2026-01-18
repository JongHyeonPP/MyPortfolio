package com.medicore.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordUpdateRequest {
    private String chiefComplaint;
    private String diagnosisCode;
    private String diagnosisName;
    private String treatmentPlan;
    private String amendmentReason; // Required if signed
}
