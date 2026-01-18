package com.medicore.api.controller;

import com.medicore.api.dto.MedicalRecordCreateRequest;
import com.medicore.api.dto.MedicalRecordResponse;
import com.medicore.api.dto.MedicalRecordUpdateRequest;
import com.medicore.config.Audited;
import com.medicore.config.RequirePermission;
import com.medicore.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping
    @RequirePermission(resource = "MEDICAL_RECORD", action = "CREATE")
    @Audited(resource = "MEDICAL_RECORD", action = "CREATE")
    public ResponseEntity<MedicalRecordResponse> createMedicalRecord(@RequestBody MedicalRecordCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medicalRecordService.createMedicalRecord(request));
    }

    @GetMapping("/{recordId}")
    @RequirePermission(resource = "MEDICAL_RECORD", action = "READ")
    @Audited(resource = "MEDICAL_RECORD", action = "READ")
    public ResponseEntity<MedicalRecordResponse> getMedicalRecord(@PathVariable Long recordId) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecord(recordId));
    }

    @PutMapping("/{recordId}")
    @RequirePermission(resource = "MEDICAL_RECORD", action = "UPDATE")
    @Audited(resource = "MEDICAL_RECORD", action = "UPDATE")
    public ResponseEntity<MedicalRecordResponse> updateMedicalRecord(
            @PathVariable Long recordId,
            @RequestBody MedicalRecordUpdateRequest request) {
        return ResponseEntity.ok(medicalRecordService.updateMedicalRecord(recordId, request));
    }
}
