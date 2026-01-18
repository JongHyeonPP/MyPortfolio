package com.medicore.domain.repository;

import com.medicore.domain.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatient_PatientId(Long patientId);
    List<MedicalRecord> findByDoctorId(Long doctorId);
}
