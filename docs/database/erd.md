# 병원 EMR 연계 백엔드 서버 포트폴리오 가이드

## Part 2: 데이터베이스 설계

---

## 4. 데이터베이스 설계

### 4.1 ERD (Entity-Relationship Diagram)

**핵심 테이블 관계도**

```
┌─────────────────┐       ┌─────────────────┐
│     USERS       │       │     ROLES       │
├─────────────────┤       ├─────────────────┤
│ PK user_id      │       │ PK role_id      │
│    username     │       │    role_name    │
│    password     │       │    description  │
│    name         │◄──────│                 │
│ FK role_id      │       └─────────────────┘
│    department   │
│    license_no   │
│    is_active    │
└────────┬────────┘
         │
         │                ┌─────────────────┐
         │                │    PATIENTS     │
         │                ├─────────────────┤
         │                │ PK patient_id   │
         │                │    patient_no   │ (병원 등록번호)
         │                │    name         │
         │                │    resident_no  │ (주민번호 암호화)
         │                │    birth_date   │
         │                │    gender       │
         │                │    phone        │
         │                └────────┬────────┘
         │                         │
         │         ┌───────────────┼───────────────┐
         │         │               │               │
         │         ▼               ▼               ▼
         │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
         │  │MEDICAL_     │ │PRESCRIPTIONS│ │LAB_RESULTS  │
         │  │RECORDS      │ ├─────────────┤ ├─────────────┤
         │  ├─────────────┤ │PK presc_id  │ │PK result_id │
         │  │PK record_id │ │FK patient_id│ │FK patient_id│
         │  │FK patient_id│ │FK record_id │ │FK order_id  │
         └──│FK doctor_id │ │FK doctor_id │ │   test_code │
            │   visit_date│ │   order_date│ │   result_val│
            │   diagnosis │ │   status    │ │   abnormal  │
            │   treatment │ │             │ │   critical  │
            └─────────────┘ └─────────────┘ └─────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                         AUDIT_LOGS                               │
├─────────────────────────────────────────────────────────────────┤
│ PK log_id          - 로그 고유 ID                                │
│    user_id         - 행위자 ID                                   │
│    username        - 행위자 이름 (비정규화)                        │
│    user_role       - 행위자 역할 (비정규화)                        │
│    action          - 행위 유형 (READ, CREATE, UPDATE, DELETE)    │
│    resource_type   - 대상 리소스 유형                             │
│    resource_id     - 대상 리소스 ID                               │
│    patient_id      - 관련 환자 ID                                 │
│    request_ip      - 요청 IP 주소                                 │
│    result          - 결과 (SUCCESS, FAILURE, DENIED)             │
│    created_at      - 로그 생성 시각                               │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 테이블 정의서

#### 4.2.1 USERS (사용자)

```sql
CREATE TABLE users (
    user_id         BIGSERIAL PRIMARY KEY,
    username        VARCHAR(50) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    name            VARCHAR(100) NOT NULL,
    role_id         BIGINT NOT NULL,
    department      VARCHAR(100),
    license_no      VARCHAR(50),           -- 의사 면허 번호 등
    email           VARCHAR(100),
    phone           VARCHAR(20),
    is_active       BOOLEAN DEFAULT true,
    last_login_at   TIMESTAMP,
    login_fail_cnt  INT DEFAULT 0,
    locked_until    TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by      BIGINT,

    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

-- 인덱스
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role_id);
CREATE INDEX idx_users_active ON users(is_active);

COMMENT ON TABLE users IS '시스템 사용자 (의사, 간호사, 행정직원, 전산관리자)';
COMMENT ON COLUMN users.license_no IS '의료인의 경우 면허 번호';
COMMENT ON COLUMN users.login_fail_cnt IS '연속 로그인 실패 횟수 (5회 초과 시 계정 잠금)';
```

#### 4.2.2 ROLES (역할)

```sql
CREATE TABLE roles (
    role_id         BIGSERIAL PRIMARY KEY,
    role_name       VARCHAR(50) NOT NULL UNIQUE,
    role_code       VARCHAR(20) NOT NULL UNIQUE,
    description     VARCHAR(255),
    is_system_role  BOOLEAN DEFAULT false,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 기본 역할 데이터
INSERT INTO roles (role_name, role_code, description, is_system_role) VALUES
('의사', 'DOCTOR', '진료 및 처방 권한', true),
('간호사', 'NURSE', '환자 관리 및 진료 보조 권한', true),
('행정직원', 'ADMIN_STAFF', '원무 및 행정 업무 권한', true),
('전산관리자', 'IT_ADMIN', '시스템 관리 및 감사 권한', true),
('수련의', 'RESIDENT', '제한된 진료 권한', true),
('의료기사', 'TECHNICIAN', '검사 수행 및 결과 입력 권한', true);

COMMENT ON TABLE roles IS '사용자 역할 정의';
```

#### 4.2.3 ROLE_PERMISSIONS (역할별 권한)

```sql
CREATE TABLE role_permissions (
    permission_id   BIGSERIAL PRIMARY KEY,
    role_id         BIGINT NOT NULL,
    resource        VARCHAR(50) NOT NULL,   -- 리소스 유형
    action          VARCHAR(20) NOT NULL,   -- 허용 액션
    conditions      JSONB,                  -- 추가 조건
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_permission_role FOREIGN KEY (role_id) REFERENCES roles(role_id),
    CONSTRAINT uk_role_resource_action UNIQUE (role_id, resource, action)
);

-- 의사 권한
INSERT INTO role_permissions (role_id, resource, action) VALUES
((SELECT role_id FROM roles WHERE role_code = 'DOCTOR'), 'PATIENT', 'READ'),
((SELECT role_id FROM roles WHERE role_code = 'DOCTOR'), 'PATIENT', 'CREATE'),
((SELECT role_id FROM roles WHERE role_code = 'DOCTOR'), 'MEDICAL_RECORD', 'READ'),
((SELECT role_id FROM roles WHERE role_code = 'DOCTOR'), 'MEDICAL_RECORD', 'CREATE'),
((SELECT role_id FROM roles WHERE role_code = 'DOCTOR'), 'MEDICAL_RECORD', 'UPDATE'),
((SELECT role_id FROM roles WHERE role_code = 'DOCTOR'), 'PRESCRIPTION', 'READ'),
((SELECT role_id FROM roles WHERE role_code = 'DOCTOR'), 'PRESCRIPTION', 'CREATE'),
((SELECT role_id FROM roles WHERE role_code = 'DOCTOR'), 'LAB_RESULT', 'READ'),
((SELECT role_id FROM roles WHERE role_code = 'DOCTOR'), 'LAB_ORDER', 'CREATE');

-- 간호사 권한 (처방 생성 불가!)
INSERT INTO role_permissions (role_id, resource, action) VALUES
((SELECT role_id FROM roles WHERE role_code = 'NURSE'), 'PATIENT', 'READ'),
((SELECT role_id FROM roles WHERE role_code = 'NURSE'), 'PATIENT', 'UPDATE'),
((SELECT role_id FROM roles WHERE role_code = 'NURSE'), 'MEDICAL_RECORD', 'READ'),
((SELECT role_id FROM roles WHERE role_code = 'NURSE'), 'PRESCRIPTION', 'READ'),
((SELECT role_id FROM roles WHERE role_code = 'NURSE'), 'LAB_RESULT', 'READ');

-- 행정직원 권한 (진료기록 접근 불가!)
INSERT INTO role_permissions (role_id, resource, action) VALUES
((SELECT role_id FROM roles WHERE role_code = 'ADMIN_STAFF'), 'PATIENT', 'READ'),
((SELECT role_id FROM roles WHERE role_code = 'ADMIN_STAFF'), 'PATIENT', 'CREATE'),
((SELECT role_id FROM roles WHERE role_code = 'ADMIN_STAFF'), 'PATIENT', 'UPDATE'),
((SELECT role_id FROM roles WHERE role_code = 'ADMIN_STAFF'), 'APPOINTMENT', 'READ'),
((SELECT role_id FROM roles WHERE role_code = 'ADMIN_STAFF'), 'APPOINTMENT', 'CREATE'),
((SELECT role_id FROM roles WHERE role_code = 'ADMIN_STAFF'), 'BILLING', 'READ'),
((SELECT role_id FROM roles WHERE role_code = 'ADMIN_STAFF'), 'BILLING', 'CREATE');

-- 전산관리자 권한 (환자 진료정보 직접 접근 불가!)
INSERT INTO role_permissions (role_id, resource, action) VALUES
((SELECT role_id FROM roles WHERE role_code = 'IT_ADMIN'), 'USER', 'READ'),
((SELECT role_id FROM roles WHERE role_code = 'IT_ADMIN'), 'USER', 'CREATE'),
((SELECT role_id FROM roles WHERE role_code = 'IT_ADMIN'), 'USER', 'UPDATE'),
((SELECT role_id FROM roles WHERE role_code = 'IT_ADMIN'), 'AUDIT_LOG', 'READ'),
((SELECT role_id FROM roles WHERE role_code = 'IT_ADMIN'), 'SYSTEM_CONFIG', 'READ'),
((SELECT role_id FROM roles WHERE role_code = 'IT_ADMIN'), 'SYSTEM_CONFIG', 'UPDATE');

COMMENT ON TABLE role_permissions IS '역할별 리소스 접근 권한 정의';
COMMENT ON COLUMN role_permissions.resource IS 'PATIENT, MEDICAL_RECORD, PRESCRIPTION, LAB_RESULT, USER, AUDIT_LOG 등';
COMMENT ON COLUMN role_permissions.action IS 'CREATE, READ, UPDATE, DELETE';
```

#### 4.2.4 PATIENTS (환자)

```sql
CREATE TABLE patients (
    patient_id      BIGSERIAL PRIMARY KEY,
    patient_no      VARCHAR(20) NOT NULL UNIQUE,  -- 병원 등록 번호
    name            VARCHAR(100) NOT NULL,
    resident_no     VARCHAR(255) NOT NULL,        -- 암호화 저장
    birth_date      DATE NOT NULL,
    gender          CHAR(1) NOT NULL CHECK (gender IN ('M', 'F')),
    phone           VARCHAR(20),
    mobile          VARCHAR(20),
    email           VARCHAR(100),
    postal_code     VARCHAR(10),
    address         VARCHAR(255),
    address_detail  VARCHAR(255),
    blood_type      VARCHAR(5),                   -- A+, A-, B+, B-, O+, O-, AB+, AB-
    emergency_contact_name  VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    emergency_contact_rel   VARCHAR(50),          -- 관계
    insurance_type  VARCHAR(20),                  -- 건강보험, 의료급여 등
    insurance_no    VARCHAR(50),
    allergies       TEXT,                         -- 알레르기 정보
    note            TEXT,                         -- 특이사항
    is_active       BOOLEAN DEFAULT true,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by      BIGINT,

    CONSTRAINT fk_patient_creator FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- 인덱스
CREATE INDEX idx_patients_patient_no ON patients(patient_no);
CREATE INDEX idx_patients_name ON patients(name);
CREATE INDEX idx_patients_birth ON patients(birth_date);
CREATE UNIQUE INDEX idx_patients_resident_no ON patients(resident_no);

COMMENT ON TABLE patients IS '환자 기본 정보';
COMMENT ON COLUMN patients.patient_no IS '병원 고유 등록 번호 (예: P2024-000001)';
COMMENT ON COLUMN patients.resident_no IS 'AES-256 암호화된 주민등록번호';
```

#### 4.2.5 MEDICAL_RECORDS (진료 기록)

```sql
CREATE TABLE medical_records (
    record_id           BIGSERIAL PRIMARY KEY,
    patient_id          BIGINT NOT NULL,
    doctor_id           BIGINT NOT NULL,
    visit_date          DATE NOT NULL,
    visit_time          TIME NOT NULL,
    visit_type          VARCHAR(20) NOT NULL,     -- OUTPATIENT, INPATIENT, EMERGENCY
    department          VARCHAR(50) NOT NULL,
    chief_complaint     TEXT NOT NULL,            -- 주호소
    present_illness     TEXT,                     -- 현병력
    past_history        TEXT,                     -- 과거력
    family_history      TEXT,                     -- 가족력
    physical_exam       TEXT,                     -- 신체검사 소견
    vital_signs         JSONB,                    -- {"bp": "120/80", "hr": 72, "temp": 36.5}
    diagnosis_code      VARCHAR(20),              -- ICD-10 코드
    diagnosis_name      VARCHAR(255),             -- 진단명
    diagnosis_type      VARCHAR(20),              -- MAIN(주진단), SUB(부진단)
    treatment_plan      TEXT,                     -- 치료 계획
    clinical_note       TEXT,                     -- 임상 소견
    follow_up_date      DATE,                     -- 다음 방문일
    status              VARCHAR(20) DEFAULT 'DRAFT',  -- DRAFT, COMPLETED, AMENDED
    signed_at           TIMESTAMP,                -- 전자서명 시각
    signed_by           BIGINT,                   -- 서명 의사 ID
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_record_patient FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    CONSTRAINT fk_record_doctor FOREIGN KEY (doctor_id) REFERENCES users(user_id),
    CONSTRAINT fk_record_signer FOREIGN KEY (signed_by) REFERENCES users(user_id)
);

-- 인덱스
CREATE INDEX idx_records_patient ON medical_records(patient_id);
CREATE INDEX idx_records_doctor ON medical_records(doctor_id);
CREATE INDEX idx_records_visit_date ON medical_records(visit_date);
CREATE INDEX idx_records_diagnosis ON medical_records(diagnosis_code);

COMMENT ON TABLE medical_records IS '진료 기록 (전자의무기록)';
COMMENT ON COLUMN medical_records.status IS 'DRAFT: 작성중, COMPLETED: 완료(수정불가), AMENDED: 수정됨';
COMMENT ON COLUMN medical_records.signed_at IS '전자서명 시각 - 의료법상 전자서명 필수';
```

#### 4.2.6 MEDICAL_RECORD_AMENDMENTS (진료 기록 수정 이력)

```sql
-- 의료법에 따라 진료기록 수정 시 원본 보존 및 수정 이력 필수
CREATE TABLE medical_record_amendments (
    amendment_id        BIGSERIAL PRIMARY KEY,
    record_id           BIGINT NOT NULL,
    amended_by          BIGINT NOT NULL,
    amendment_reason    TEXT NOT NULL,            -- 수정 사유 (필수!)
    field_name          VARCHAR(100) NOT NULL,    -- 수정된 필드명
    old_value           TEXT,                     -- 수정 전 값
    new_value           TEXT,                     -- 수정 후 값
    amended_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_amendment_record FOREIGN KEY (record_id) REFERENCES medical_records(record_id),
    CONSTRAINT fk_amendment_user FOREIGN KEY (amended_by) REFERENCES users(user_id)
);

CREATE INDEX idx_amendments_record ON medical_record_amendments(record_id);

COMMENT ON TABLE medical_record_amendments IS '진료 기록 수정 이력 - 의료법 준수';
COMMENT ON COLUMN medical_record_amendments.amendment_reason IS '의료법상 수정 사유 기록 필수';
```

#### 4.2.7 PRESCRIPTIONS (처방)

```sql
CREATE TABLE prescriptions (
    prescription_id     BIGSERIAL PRIMARY KEY,
    patient_id          BIGINT NOT NULL,
    record_id           BIGINT,                   -- 연관 진료 기록
    doctor_id           BIGINT NOT NULL,
    prescription_no     VARCHAR(30) NOT NULL UNIQUE,
    prescription_date   DATE NOT NULL,
    prescription_type   VARCHAR(20) NOT NULL,     -- MEDICATION, INJECTION, EXTERNAL
    status              VARCHAR(20) DEFAULT 'ORDERED',  -- ORDERED, DISPENSED, CANCELLED
    dispensed_at        TIMESTAMP,
    dispensed_by        BIGINT,
    pharmacy_note       TEXT,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_presc_patient FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    CONSTRAINT fk_presc_record FOREIGN KEY (record_id) REFERENCES medical_records(record_id),
    CONSTRAINT fk_presc_doctor FOREIGN KEY (doctor_id) REFERENCES users(user_id)
);
```

#### 4.2.8 PRESCRIPTION_ITEMS (처방 상세)

```sql
CREATE TABLE prescription_items (
    item_id             BIGSERIAL PRIMARY KEY,
    prescription_id     BIGINT NOT NULL,
    drug_code           VARCHAR(20) NOT NULL,     -- 약품 코드
    drug_name           VARCHAR(200) NOT NULL,    -- 약품명
    dosage              VARCHAR(50) NOT NULL,     -- 용량 (예: 500mg)
    dosage_form         VARCHAR(50),              -- 제형 (정제, 캡슐 등)
    route               VARCHAR(20),              -- 투여 경로 (PO, IV, IM 등)
    frequency           VARCHAR(50) NOT NULL,     -- 투약 횟수 (예: 1일 3회)
    frequency_code      VARCHAR(20),              -- TID, BID 등
    duration_days       INT NOT NULL,             -- 투약 일수
    total_quantity      DECIMAL(10,2) NOT NULL,   -- 총 수량
    unit                VARCHAR(20) NOT NULL,     -- 단위 (정, 캡슐, ml 등)
    instruction         TEXT,                     -- 복용 지시사항
    caution             TEXT,                     -- 주의사항
    is_prn              BOOLEAN DEFAULT false,    -- 필요시 복용 여부
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_item_prescription FOREIGN KEY (prescription_id) REFERENCES prescriptions(prescription_id)
);

COMMENT ON COLUMN prescription_items.frequency_code IS 'QD(1일1회), BID(1일2회), TID(1일3회), QID(1일4회), PRN(필요시)';
COMMENT ON COLUMN prescription_items.route IS 'PO(경구), IV(정맥), IM(근육), SC(피하), SL(설하)';
```

#### 4.2.9 LAB_ORDERS (검사 오더)

```sql
CREATE TABLE lab_orders (
    order_id            BIGSERIAL PRIMARY KEY,
    patient_id          BIGINT NOT NULL,
    record_id           BIGINT,
    doctor_id           BIGINT NOT NULL,
    order_no            VARCHAR(30) NOT NULL UNIQUE,
    order_date          DATE NOT NULL,
    order_time          TIME NOT NULL,
    test_code           VARCHAR(20) NOT NULL,
    test_name           VARCHAR(200) NOT NULL,
    test_category       VARCHAR(50),              -- 혈액검사, 소변검사 등
    specimen_type       VARCHAR(50),              -- 검체 종류
    priority            VARCHAR(20) DEFAULT 'ROUTINE',  -- ROUTINE, URGENT, STAT
    status              VARCHAR(20) DEFAULT 'ORDERED',  -- ORDERED, COLLECTED, IN_PROGRESS, COMPLETED
    clinical_info       TEXT,                     -- 임상 정보
    collected_at        TIMESTAMP,                -- 검체 채취 시각
    collected_by        BIGINT,
    note                TEXT,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_patient FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    CONSTRAINT fk_order_doctor FOREIGN KEY (doctor_id) REFERENCES users(user_id)
);
```

#### 4.2.10 LAB_RESULTS (검사 결과)

```sql
CREATE TABLE lab_results (
    result_id           BIGSERIAL PRIMARY KEY,
    order_id            BIGINT NOT NULL,
    patient_id          BIGINT NOT NULL,
    test_code           VARCHAR(20) NOT NULL,
    test_name           VARCHAR(200) NOT NULL,
    result_value        VARCHAR(100),             -- 결과값
    result_value_num    DECIMAL(15,5),            -- 수치 결과
    unit                VARCHAR(30),              -- 단위
    reference_range     VARCHAR(100),             -- 참고치 범위
    reference_low       DECIMAL(15,5),            -- 참고치 하한
    reference_high      DECIMAL(15,5),            -- 참고치 상한
    abnormal_flag       VARCHAR(5),               -- H(High), L(Low), A(Abnormal), N(Normal)
    critical_flag       BOOLEAN DEFAULT false,    -- 위급 수치 여부
    result_status       VARCHAR(20) DEFAULT 'PRELIMINARY',  -- PRELIMINARY, FINAL, CORRECTED
    result_comment      TEXT,
    performed_at        TIMESTAMP,                -- 검사 수행 시각
    performed_by        BIGINT,
    verified_at         TIMESTAMP,                -- 결과 확인 시각
    verified_by         BIGINT,
    device_id           VARCHAR(50),              -- 검사 장비 ID
    received_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_result_order FOREIGN KEY (order_id) REFERENCES lab_orders(order_id),
    CONSTRAINT fk_result_patient FOREIGN KEY (patient_id) REFERENCES patients(patient_id)
);

CREATE INDEX idx_lab_results_abnormal ON lab_results(abnormal_flag) WHERE abnormal_flag IS NOT NULL;
CREATE INDEX idx_lab_results_critical ON lab_results(critical_flag) WHERE critical_flag = true;

COMMENT ON COLUMN lab_results.abnormal_flag IS 'H: 높음, L: 낮음, A: 비정상, N: 정상';
COMMENT ON COLUMN lab_results.critical_flag IS '위급 수치 - 즉시 통보 필요';
```

#### 4.2.11 AUDIT_LOGS (감사 로그) - 가장 중요!

```sql
CREATE TABLE audit_logs (
    log_id              BIGSERIAL PRIMARY KEY,
    -- 행위자 정보
    user_id             BIGINT,
    username            VARCHAR(50) NOT NULL,     -- 비정규화 (사용자 삭제되어도 로그 보존)
    user_role           VARCHAR(50) NOT NULL,
    user_department     VARCHAR(100),

    -- 행위 정보
    action              VARCHAR(20) NOT NULL,     -- CREATE, READ, UPDATE, DELETE, LOGIN, LOGOUT, EXPORT
    action_detail       VARCHAR(100),             -- 상세 행위

    -- 대상 정보
    resource_type       VARCHAR(50) NOT NULL,     -- PATIENT, MEDICAL_RECORD 등
    resource_id         VARCHAR(50),
    resource_name       VARCHAR(200),

    -- 환자 관련 정보
    patient_id          BIGINT,
    patient_no          VARCHAR(20),
    patient_name        VARCHAR(100),             -- 마스킹 저장

    -- 요청 정보
    request_ip          VARCHAR(45) NOT NULL,     -- IPv6 대응
    request_uri         VARCHAR(500) NOT NULL,
    request_method      VARCHAR(10) NOT NULL,
    request_params      TEXT,                     -- 민감정보 마스킹 후 저장
    user_agent          VARCHAR(500),

    -- 결과 정보
    response_status     INT,
    result              VARCHAR(20) NOT NULL,     -- SUCCESS, FAILURE, DENIED
    failure_reason      TEXT,
    execution_time_ms   INT,

    -- 세션 정보
    session_id          VARCHAR(100),

    -- 추가 컨텍스트
    additional_info     JSONB,

    -- 메타 정보
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 인덱스
CREATE INDEX idx_audit_user ON audit_logs(user_id);
CREATE INDEX idx_audit_patient ON audit_logs(patient_id);
CREATE INDEX idx_audit_resource ON audit_logs(resource_type, resource_id);
CREATE INDEX idx_audit_action ON audit_logs(action);
CREATE INDEX idx_audit_result ON audit_logs(result);
CREATE INDEX idx_audit_created ON audit_logs(created_at);

-- UPDATE 방지 트리거
CREATE OR REPLACE FUNCTION prevent_audit_log_update()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'Audit logs cannot be modified';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_audit_log_no_update
    BEFORE UPDATE ON audit_logs
    FOR EACH ROW
    EXECUTE FUNCTION prevent_audit_log_update();

-- DELETE 방지 트리거
CREATE OR REPLACE FUNCTION prevent_audit_log_delete()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'Audit logs cannot be deleted';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_audit_log_no_delete
    BEFORE DELETE ON audit_logs
    FOR EACH ROW
    EXECUTE FUNCTION prevent_audit_log_delete();

COMMENT ON TABLE audit_logs IS '시스템 감사 로그 - 삭제 불가, 전산관리자만 조회 가능';
```

### 4.3 데이터 사전

**주요 코드 정의**

| 코드 유형 | 코드 값 | 설명 |
|----------|---------|------|
| 성별 (gender) | M | 남성 |
| 성별 (gender) | F | 여성 |
| 방문 유형 (visit_type) | OUTPATIENT | 외래 |
| 방문 유형 (visit_type) | INPATIENT | 입원 |
| 방문 유형 (visit_type) | EMERGENCY | 응급 |
| 진료기록 상태 (status) | DRAFT | 작성 중 |
| 진료기록 상태 (status) | COMPLETED | 완료 (서명됨) |
| 진료기록 상태 (status) | AMENDED | 수정됨 |
| 검사 우선순위 (priority) | ROUTINE | 일반 |
| 검사 우선순위 (priority) | URGENT | 긴급 |
| 검사 우선순위 (priority) | STAT | 응급 (즉시) |
| 비정상 플래그 (abnormal_flag) | H | 높음 (High) |
| 비정상 플래그 (abnormal_flag) | L | 낮음 (Low) |
| 비정상 플래그 (abnormal_flag) | A | 비정상 (Abnormal) |
| 비정상 플래그 (abnormal_flag) | N | 정상 (Normal) |
| 감사 액션 (action) | CREATE | 생성 |
| 감사 액션 (action) | READ | 조회 |
| 감사 액션 (action) | UPDATE | 수정 |
| 감사 액션 (action) | DELETE | 삭제 |
| 감사 액션 (action) | LOGIN | 로그인 |
| 감사 액션 (action) | LOGOUT | 로그아웃 |
| 감사 액션 (action) | EXPORT | 내보내기 |
| 감사 결과 (result) | SUCCESS | 성공 |
| 감사 결과 (result) | FAILURE | 실패 |
| 감사 결과 (result) | DENIED | 권한 거부 |

### 4.4 민감 데이터 암호화

```java
// 주민등록번호 암호화 예시
@Converter
public class ResidentNoConverter implements AttributeConverter<String, String> {

    private final AESEncryptor encryptor;

    @Override
    public String convertToDatabaseColumn(String residentNo) {
        if (residentNo == null) return null;
        return encryptor.encrypt(residentNo);
    }

    @Override
    public String convertToEntityAttribute(String encrypted) {
        if (encrypted == null) return null;
        return encryptor.decrypt(encrypted);
    }
}

// 엔티티에서 사용
@Entity
public class Patient {

    @Convert(converter = ResidentNoConverter.class)
    @Column(name = "resident_no")
    private String residentNo;
}
```
