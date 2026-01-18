# 병원 EMR 연계 백엔드 서버 포트폴리오 가이드

## Part 4: API 설계 및 외부 시스템 연계

---

## 7. API 설계

### 7.1 RESTful API 설계 원칙

```
1. URI는 명사를 사용 (동사 X)
   ✓ GET /api/patients
   ✗ GET /api/getPatients

2. 복수형 사용
   ✓ /api/patients
   ✗ /api/patient

3. 계층 관계 표현
   ✓ /api/patients/{patientId}/medical-records
   ✓ /api/patients/{patientId}/prescriptions

4. 하이픈(-) 사용, 언더스코어(_) 미사용
   ✓ /api/medical-records
   ✗ /api/medical_records
```

### 7.2 주요 API 명세

#### 7.2.1 인증 API

```yaml
POST /api/auth/login
Request:
  {
    "username": "doctor001",
    "password": "securePassword123!"
  }
Response: 200 OK
  {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 1800,
    "user": {
      "userId": 1,
      "username": "doctor001",
      "name": "김의사",
      "role": "DOCTOR",
      "department": "내과"
    }
  }

POST /api/auth/refresh
Request:
  {
    "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
  }
Response: 200 OK
  {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 1800
  }
```

#### 7.2.2 환자 API

```yaml
# 환자 목록 조회
GET /api/patients
Headers:
  Authorization: Bearer {accessToken}
Query: page, size, sort
Response: 200 OK
  {
    "content": [
      {
        "patientId": 1,
        "patientNo": "P2024-000001",
        "name": "홍길동",
        "birthDate": "1990-01-15",
        "gender": "M",
        "phone": "010-1234-5678"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8
  }

# 환자 상세 조회
GET /api/patients/{patientId}
Response: 200 OK
  {
    "patientId": 1,
    "patientNo": "P2024-000001",
    "name": "홍길동",
    "birthDate": "1990-01-15",
    "gender": "M",
    "phone": "010-1234-5678",
    "address": "서울시 강남구...",
    "bloodType": "A+",
    "insuranceType": "건강보험",
    "allergies": "페니실린",
    "emergencyContact": {
      "name": "홍부인",
      "phone": "010-9876-5432",
      "relationship": "배우자"
    }
  }

# 환자 등록
POST /api/patients
Request:
  {
    "name": "김환자",
    "residentNo": "900115-1234567",
    "birthDate": "1990-01-15",
    "gender": "M",
    "phone": "010-1234-5678",
    "address": "서울시 강남구...",
    "bloodType": "B+",
    "insuranceType": "건강보험"
  }
Response: 201 Created
  {
    "patientId": 2,
    "patientNo": "P2024-000002",
    "message": "환자가 등록되었습니다."
  }
```

#### 7.2.3 진료 기록 API

```yaml
# 진료 기록 목록
GET /api/patients/{patientId}/medical-records
Query: startDate, endDate, department, page, size
Response: 200 OK
  {
    "content": [
      {
        "recordId": 1,
        "visitDate": "2024-01-15",
        "visitType": "OUTPATIENT",
        "department": "내과",
        "doctorName": "김의사",
        "diagnosisCode": "J00",
        "diagnosisName": "급성 비인두염",
        "status": "COMPLETED"
      }
    ],
    ...
  }

# 진료 기록 상세 조회
GET /api/medical-records/{recordId}
Response: 200 OK
  {
    "recordId": 1,
    "patient": {
      "patientId": 1,
      "patientNo": "P2024-000001",
      "name": "홍길동"
    },
    "doctor": {
      "userId": 5,
      "name": "김의사",
      "department": "내과"
    },
    "visitDate": "2024-01-15",
    "visitType": "OUTPATIENT",
    "chiefComplaint": "3일 전부터 콧물, 기침, 미열",
    "presentIllness": "3일 전부터 맑은 콧물이...",
    "vitalSigns": {
      "bloodPressure": "120/80",
      "heartRate": 72,
      "temperature": 37.2
    },
    "diagnosisCode": "J00",
    "diagnosisName": "급성 비인두염 (감기)",
    "treatmentPlan": "대증 치료, 경과 관찰",
    "status": "COMPLETED",
    "signedAt": "2024-01-15T11:00:00"
  }

# 진료 기록 생성
POST /api/medical-records
Request:
  {
    "patientId": 1,
    "visitDate": "2024-01-15",
    "visitType": "OUTPATIENT",
    "department": "내과",
    "chiefComplaint": "3일 전부터 콧물, 기침",
    "vitalSigns": {
      "bloodPressure": "120/80",
      "heartRate": 72,
      "temperature": 37.2
    },
    "diagnosisCode": "J00",
    "diagnosisName": "급성 비인두염"
  }
Response: 201 Created
  {
    "recordId": 1,
    "status": "DRAFT",
    "message": "진료 기록이 생성되었습니다."
  }

# 진료 기록 서명 (전자서명)
POST /api/medical-records/{recordId}/sign
Response: 200 OK
  {
    "recordId": 1,
    "status": "COMPLETED",
    "signedAt": "2024-01-15T11:00:00",
    "message": "진료 기록이 서명되었습니다."
  }

# 서명된 진료 기록 수정 (Amendment) - 의료법 준수
POST /api/medical-records/{recordId}/amend
Request:
  {
    "fieldName": "diagnosisCode",
    "newValue": "J01",
    "amendmentReason": "진단명 오기로 인한 수정"  // 필수!
  }
Response: 200 OK
  {
    "recordId": 1,
    "status": "AMENDED",
    "amendmentId": 1,
    "message": "진료 기록 수정이 기록되었습니다."
  }
```

#### 7.2.4 처방 API

```yaml
# 처방 생성
POST /api/prescriptions
Request:
  {
    "patientId": 1,
    "recordId": 1,
    "prescriptionType": "MEDICATION",
    "items": [
      {
        "drugCode": "D001",
        "drugName": "타이레놀정 500mg",
        "dosage": "500mg",
        "frequency": "1일 3회 식후",
        "frequencyCode": "TID",
        "durationDays": 3,
        "totalQuantity": 9,
        "unit": "정",
        "instruction": "식후 30분에 복용"
      }
    ]
  }
Response: 201 Created
  {
    "prescriptionId": 1,
    "prescriptionNo": "RX2024-000001",
    "status": "ORDERED",
    "message": "처방이 생성되었습니다."
  }
```

#### 7.2.5 검사 API

```yaml
# 검사 오더 생성
POST /api/lab-orders
Request:
  {
    "patientId": 1,
    "recordId": 1,
    "orders": [
      {
        "testCode": "L001",
        "testName": "일반혈액검사 (CBC)",
        "testCategory": "혈액검사",
        "priority": "ROUTINE",
        "clinicalInfo": "감기 증상, 빈혈 여부 확인"
      }
    ]
  }
Response: 201 Created
  {
    "orders": [
      {
        "orderId": 1,
        "orderNo": "LO2024-000001",
        "status": "ORDERED"
      }
    ]
  }

# 검사 결과 조회
GET /api/lab-results/{resultId}
Response: 200 OK
  {
    "resultId": 1,
    "order": {
      "orderId": 1,
      "orderNo": "LO2024-000001",
      "testName": "일반혈액검사 (CBC)"
    },
    "testCode": "L001-WBC",
    "testName": "백혈구 (WBC)",
    "resultValue": "8.5",
    "unit": "10^3/μL",
    "referenceRange": "4.0-10.0",
    "abnormalFlag": null,
    "criticalFlag": false,
    "resultStatus": "FINAL"
  }
```

### 7.3 공통 에러 응답

```java
// 400 Bad Request (유효성 검증 실패)
{
    "status": 400,
    "error": "Bad Request",
    "message": "입력 데이터가 유효하지 않습니다.",
    "fieldErrors": [
        {"field": "name", "message": "이름은 필수입니다."}
    ]
}

// 401 Unauthorized
{
    "status": 401,
    "error": "Unauthorized",
    "message": "인증이 필요합니다."
}

// 403 Forbidden
{
    "status": 403,
    "error": "Forbidden",
    "message": "해당 리소스에 대한 접근 권한이 없습니다."
}

// 404 Not Found
{
    "status": 404,
    "error": "Not Found",
    "message": "환자를 찾을 수 없습니다. [id: 999]"
}
```

---

## 8. 외부 시스템 연계

### 8.1 연계 유형

```
1. 검사 장비 연계
   - 혈액 검사기 → 검사 결과 수신
   - 영상 장비 → DICOM 이미지 수신

2. 외부 기관 연계
   - 건강보험심사평가원 → 청구/심사
   - 질병관리청 → 감염병 신고

3. 내부 시스템 연계
   - 원무 시스템 → 수납 정보
   - 약국 시스템 → 조제 정보
```

### 8.2 HL7 메시지 이해

**HL7이란?**
- Health Level 7의 약자
- 의료 정보 교환을 위한 국제 표준 프로토콜

**HL7 v2.x 메시지 예시 (검사 결과)**
```
MSH|^~\&|LAB_SYSTEM|LAB|EMR_SYSTEM|HOSPITAL|20240115113000||ORU^R01|MSG00001|P|2.5
PID|1||P2024-000001^^^HOSPITAL||홍길동||19900115|M
OBR|1|LO2024-000001||L001^일반혈액검사^LOCAL|||20240115110000
OBX|1|NM|WBC^백혈구^LOCAL||8.5|10^3/μL|4.0-10.0|N|||F
OBX|2|NM|RBC^적혈구^LOCAL||4.8|10^6/μL|4.2-5.8|N|||F
```

### 8.3 HL7 스타일 JSON 메시지

```java
@Getter
@Builder
public class LabResultMessage {

    private MessageHeader header;
    private PatientIdentifier patient;
    private OrderInfo order;
    private List<ResultItem> results;

    @Getter
    @Builder
    public static class MessageHeader {
        private String sendingApplication;     // LAB_SYSTEM
        private String sendingFacility;        // LAB
        private String receivingApplication;   // EMR_SYSTEM
        private LocalDateTime messageDateTime;
        private String messageType;            // ORU^R01
        private String messageControlId;       // 메시지 고유 ID
    }

    @Getter
    @Builder
    public static class PatientIdentifier {
        private String patientNo;
        private String name;
        private LocalDate birthDate;
    }

    @Getter
    @Builder
    public static class OrderInfo {
        private String orderNo;
        private String testCode;
        private String testName;
        private LocalDateTime collectionDateTime;
    }

    @Getter
    @Builder
    public static class ResultItem {
        private String itemCode;
        private String itemName;
        private String resultValue;
        private Double resultValueNumeric;
        private String unit;
        private String referenceRange;
        private Double referenceLow;
        private Double referenceHigh;
        private String abnormalFlag;       // H, L, A, N
        private boolean criticalFlag;      // 위급 수치
        private String resultStatus;       // P(임시), F(최종)
        private LocalDateTime performedDateTime;
    }
}
```

### 8.4 검사 결과 수신 API

```java
@RestController
@RequestMapping("/api/integration")
@RequiredArgsConstructor
@Slf4j
public class LabIntegrationController {

    private final LabIntegrationService labIntegrationService;

    @PostMapping("/lab-results")
    public ResponseEntity<IntegrationResponse> receiveLabResult(
            @RequestHeader("X-Lab-Device-Id") String deviceId,
            @RequestHeader("X-Message-Id") String messageId,
            @Valid @RequestBody LabResultMessage message) {

        log.info("검사 결과 수신: deviceId={}, messageId={}, orderNo={}",
                deviceId, messageId, message.getOrder().getOrderNo());

        try {
            // 중복 메시지 확인
            if (labIntegrationService.isDuplicateMessage(messageId)) {
                log.warn("중복 메시지 수신: messageId={}", messageId);
                return ResponseEntity.ok(IntegrationResponse.duplicate(messageId));
            }

            // 검사 결과 처리
            IntegrationResult result = labIntegrationService.processLabResult(message);

            // 위급 수치 알림
            if (result.hasCriticalValues()) {
                labIntegrationService.notifyCriticalValues(result);
            }

            return ResponseEntity.ok(IntegrationResponse.success(messageId, result));

        } catch (PatientNotFoundException e) {
            return ResponseEntity.badRequest()
                    .body(IntegrationResponse.error(messageId, "PATIENT_NOT_FOUND", e.getMessage()));

        } catch (OrderNotFoundException e) {
            return ResponseEntity.badRequest()
                    .body(IntegrationResponse.error(messageId, "ORDER_NOT_FOUND", e.getMessage()));
        }
    }
}
```

### 8.5 검사 결과 처리 서비스

```java
@Service
@RequiredArgsConstructor
@Transactional
public class LabIntegrationService {

    private final LabOrderRepository labOrderRepository;
    private final LabResultRepository labResultRepository;
    private final PatientRepository patientRepository;
    private final IntegrationLogRepository integrationLogRepository;
    private final NotificationService notificationService;

    public IntegrationResult processLabResult(LabResultMessage message) {
        // 1. 환자 매칭
        Patient patient = patientRepository
                .findByPatientNo(message.getPatient().getPatientNo())
                .orElseThrow(() -> new PatientNotFoundException(
                        "환자를 찾을 수 없습니다: " + message.getPatient().getPatientNo()));

        // 2. 오더 매칭
        LabOrder order = labOrderRepository
                .findByOrderNo(message.getOrder().getOrderNo())
                .orElseThrow(() -> new OrderNotFoundException(
                        "검사 오더를 찾을 수 없습니다: " + message.getOrder().getOrderNo()));

        // 3. 오더 상태 업데이트
        order.setStatus("COMPLETED");
        labOrderRepository.save(order);

        // 4. 검사 결과 저장
        List<LabResult> savedResults = new ArrayList<>();
        boolean hasCritical = false;

        for (LabResultMessage.ResultItem item : message.getResults()) {
            LabResult result = LabResult.builder()
                    .order(order)
                    .patient(patient)
                    .testCode(item.getItemCode())
                    .testName(item.getItemName())
                    .resultValue(item.getResultValue())
                    .resultValueNum(item.getResultValueNumeric() != null ?
                            BigDecimal.valueOf(item.getResultValueNumeric()) : null)
                    .unit(item.getUnit())
                    .referenceRange(item.getReferenceRange())
                    .referenceLow(item.getReferenceLow() != null ?
                            BigDecimal.valueOf(item.getReferenceLow()) : null)
                    .referenceHigh(item.getReferenceHigh() != null ?
                            BigDecimal.valueOf(item.getReferenceHigh()) : null)
                    .abnormalFlag(item.getAbnormalFlag())
                    .criticalFlag(item.isCriticalFlag())
                    .resultStatus(item.getResultStatus())
                    .performedAt(item.getPerformedDateTime())
                    .deviceId(message.getHeader().getSendingApplication())
                    .receivedAt(LocalDateTime.now())
                    .build();

            savedResults.add(labResultRepository.save(result));

            if (item.isCriticalFlag()) {
                hasCritical = true;
            }
        }

        // 5. 연계 로그 저장
        IntegrationLog integrationLog = IntegrationLog.builder()
                .messageId(message.getHeader().getMessageControlId())
                .messageType(message.getHeader().getMessageType())
                .sourceSystem(message.getHeader().getSendingApplication())
                .patientId(patient.getPatientId())
                .orderId(order.getOrderId())
                .status("SUCCESS")
                .processedAt(LocalDateTime.now())
                .build();
        integrationLogRepository.save(integrationLog);

        return IntegrationResult.builder()
                .messageId(message.getHeader().getMessageControlId())
                .patientId(patient.getPatientId())
                .orderId(order.getOrderId())
                .resultCount(savedResults.size())
                .hasCriticalValues(hasCritical)
                .criticalItems(savedResults.stream()
                        .filter(LabResult::isCriticalFlag)
                        .map(r -> r.getTestName() + ": " + r.getResultValue())
                        .toList())
                .build();
    }

    @Async
    public void notifyCriticalValues(IntegrationResult result) {
        log.warn("위급 수치 발생! patientId={}, criticalItems={}",
                result.getPatientId(), result.getCriticalItems());

        notificationService.sendCriticalAlert(result);
    }
}
```

### 8.6 Mock 검사 장비 시뮬레이터

```java
@Component
@Slf4j
public class MockLabDeviceServer {

    private final RestTemplate restTemplate;

    @Value("${mock.lab.emr-endpoint}")
    private String emrEndpoint;

    public void simulateLabResult(String orderNo, String patientNo) {
        LabResultMessage message = LabResultMessage.builder()
                .header(LabResultMessage.MessageHeader.builder()
                        .sendingApplication("LAB_ANALYZER_01")
                        .sendingFacility("LABORATORY")
                        .receivingApplication("MEDICORE_EMR")
                        .messageDateTime(LocalDateTime.now())
                        .messageType("ORU^R01")
                        .messageControlId(UUID.randomUUID().toString())
                        .build())
                .patient(LabResultMessage.PatientIdentifier.builder()
                        .patientNo(patientNo)
                        .build())
                .order(LabResultMessage.OrderInfo.builder()
                        .orderNo(orderNo)
                        .testCode("L001")
                        .testName("일반혈액검사 (CBC)")
                        .build())
                .results(generateCBCResults())
                .build();

        sendToEMR(message);
    }

    private List<LabResultMessage.ResultItem> generateCBCResults() {
        return List.of(
            createResult("WBC", "백혈구", "8.5", 8.5, "10^3/μL", "4.0-10.0", 4.0, 10.0, "N"),
            createResult("RBC", "적혈구", "4.8", 4.8, "10^6/μL", "4.2-5.8", 4.2, 5.8, "N"),
            createResult("HGB", "헤모글로빈", "14.2", 14.2, "g/dL", "13.5-17.5", 13.5, 17.5, "N"),
            createResult("PLT", "혈소판", "250", 250.0, "10^3/μL", "150-400", 150.0, 400.0, "N")
        );
    }

    private LabResultMessage.ResultItem createResult(
            String code, String name, String value, Double numValue,
            String unit, String refRange, Double refLow, Double refHigh, String flag) {

        String abnormalFlag = flag;
        boolean critical = false;

        if (numValue != null && refLow != null && refHigh != null) {
            if (numValue < refLow) {
                abnormalFlag = "L";
                if (numValue < refLow * 0.5) critical = true;  // 위급 기준
            } else if (numValue > refHigh) {
                abnormalFlag = "H";
                if (numValue > refHigh * 2) critical = true;
            }
        }

        return LabResultMessage.ResultItem.builder()
                .itemCode(code)
                .itemName(name)
                .resultValue(value)
                .resultValueNumeric(numValue)
                .unit(unit)
                .referenceRange(refRange)
                .referenceLow(refLow)
                .referenceHigh(refHigh)
                .abnormalFlag(abnormalFlag)
                .criticalFlag(critical)
                .resultStatus("F")
                .performedDateTime(LocalDateTime.now())
                .build();
    }
}
```

### 8.7 연계 실패 시 재시도 로직

```java
@Service
@RequiredArgsConstructor
public class IntegrationRetryService {

    private final IntegrationLogRepository integrationLogRepository;

    @Scheduled(fixedDelay = 300000)  // 5분마다
    public void retryFailedIntegrations() {
        List<IntegrationLog> failedLogs = integrationLogRepository
            .findByStatusAndRetryCountLessThan("FAILED", 3);

        for (IntegrationLog log : failedLogs) {
            try {
                // 재시도 로직
                retryIntegration(log);
                log.setStatus("SUCCESS");
            } catch (Exception e) {
                log.setRetryCount(log.getRetryCount() + 1);
                log.setLastError(e.getMessage());
                if (log.getRetryCount() >= 3) {
                    log.setStatus("FAILED_FINAL");
                    // 알림 발송
                    sendAlertToAdmin(log);
                }
            }
            integrationLogRepository.save(log);
        }
    }
}
```
