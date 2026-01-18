package com.medicore.service;

import com.medicore.api.dto.MedicalRecordCreateRequest;
import com.medicore.api.dto.MedicalRecordResponse;
import com.medicore.api.dto.MedicalRecordUpdateRequest;
import com.medicore.domain.entity.MedicalRecord;
import com.medicore.domain.entity.Patient;
import com.medicore.domain.repository.MedicalRecordRepository;
import com.medicore.domain.repository.PatientRepository;
import com.medicore.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;

    public MedicalRecordResponse createMedicalRecord(MedicalRecordCreateRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new BusinessException("Patient not found"));

        MedicalRecord record = MedicalRecord.builder()
                .patient(patient)
                .doctorId(1L) // Hardcoded for demo
                .visitDate(request.getVisitDate())
                .visitType(request.getVisitType())
                .department(request.getDepartment())
                .chiefComplaint(request.getChiefComplaint())
                .diagnosisCode(request.getDiagnosisCode())
                .diagnosisName(request.getDiagnosisName())
                .status("DRAFT")
                .build();

        MedicalRecord saved = medicalRecordRepository.save(record);
        return mapToResponse(saved);
    }

    public MedicalRecordResponse updateMedicalRecord(Long recordId, MedicalRecordUpdateRequest request) {
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new BusinessException("Record not found"));

        if ("COMPLETED".equals(record.getStatus())) {
             throw new BusinessException("Cannot update signed record");
        }

        // Update fields
        if (request.getChiefComplaint() != null) record.setChiefComplaint(request.getChiefComplaint());
        // ... other fields

        return mapToResponse(medicalRecordRepository.save(record));
    }

    public MedicalRecordResponse getMedicalRecord(Long recordId) {
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                 .orElseThrow(() -> new BusinessException("Record not found"));
        return mapToResponse(record);
    }

    private MedicalRecordResponse mapToResponse(MedicalRecord record) {
        return MedicalRecordResponse.builder()
                .recordId(record.getRecordId())
                .patientId(record.getPatient().getPatientId())
                .patientName(record.getPatient().getName())
                .doctorId(record.getDoctorId())
                .status(record.getStatus())
                .build();
    }
}
