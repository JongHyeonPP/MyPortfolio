# 병원 EMR 연계 백엔드 서버 포트폴리오 가이드

## Part 5: 운영 문서, 테스트, 배포 및 면접 대비

---

## 9. 장애 대응 및 운영 문서

### 9.1 운영 문서가 왜 중요한가?

```
병원 전산직 면접에서 실제로 받은 질문:
- "새벽 3시에 DB 서버가 다운되면 어떻게 하실 건가요?"
- "진료 중에 EMR이 안 열린다고 전화오면 어떻게 대응하시겠어요?"
- "데이터 백업은 어떻게 구성하시겠어요?"

코드보다 운영 문서가 더 중요한 이유:
→ 병원 전산은 24시간 운영, 장애 시 환자 진료에 직접 영향
→ 실제 업무는 개발보다 운영/장애대응이 대부분
```

### 9.2 장애 대응 매뉴얼 (Incident Response)

**장애 등급 정의**

| 등급 | 설명 | 예시 | 대응 시간 |
|-----|------|------|----------|
| **Critical** | 전체 서비스 중단 | DB 서버 다운, 네트워크 장애 | 즉시 (15분 이내) |
| **High** | 주요 기능 장애 | EMR 조회 불가, 처방 불가 | 30분 이내 |
| **Medium** | 부분 기능 장애 | 검사 결과 지연, 출력 오류 | 2시간 이내 |
| **Low** | 경미한 문제 | UI 깨짐, 속도 저하 | 당일 내 |

**장애 대응 절차**

```
┌─────────────────────────────────────────────────────────────────┐
│                        장애 대응 프로세스                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 장애 감지                                                    │
│     ├── 모니터링 알림                                            │
│     ├── 사용자 신고                                              │
│     └── 정기 점검 중 발견                                         │
│                           │                                     │
│                           ▼                                     │
│  2. 장애 등급 판정 & 에스컬레이션                                  │
│     ├── Critical/High → 즉시 담당자 연락                          │
│     ├── 필요시 벤더사 연락                                        │
│     └── 영향 범위 파악                                           │
│                           │                                     │
│                           ▼                                     │
│  3. 원인 분석                                                    │
│     ├── 로그 확인 (Application, DB, System)                      │
│     ├── 최근 변경 이력 확인                                       │
│     └── 네트워크/인프라 상태 확인                                  │
│                           │                                     │
│                           ▼                                     │
│  4. 조치                                                        │
│     ├── 임시 조치 (서비스 복구 우선)                               │
│     └── 근본 조치 (원인 해결)                                     │
│                           │                                     │
│                           ▼                                     │
│  5. 복구 확인 & 모니터링                                          │
│     ├── 서비스 정상 확인                                          │
│     └── 재발 여부 모니터링                                        │
│                           │                                     │
│                           ▼                                     │
│  6. 사후 조치                                                    │
│     ├── 장애 보고서 작성                                          │
│     ├── 재발 방지 대책 수립                                       │
│     └── 관련 문서 업데이트                                        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

**주요 장애 시나리오별 대응**

```markdown
## DB 연결 실패

### 증상
- 애플리케이션 로그: "Connection refused" 또는 "Connection timeout"
- API 응답: 500 Internal Server Error

### 체크리스트
1. [ ] DB 서버 상태 확인: `systemctl status postgresql`
2. [ ] 포트 확인: `netstat -tlnp | grep 5432`
3. [ ] 연결 수 확인: `SELECT count(*) FROM pg_stat_activity;`
4. [ ] 디스크 용량 확인: `df -h`
5. [ ] 메모리 확인: `free -m`

### 조치
1. DB 서버 재시작: `sudo systemctl restart postgresql`
2. 연결 풀 초기화: 애플리케이션 재시작
3. 필요시 max_connections 조정

---

## EMR 응답 지연

### 증상
- 페이지 로딩 10초 이상
- 타임아웃 발생

### 체크리스트
1. [ ] 슬로우 쿼리 확인
2. [ ] CPU/메모리 사용률 확인
3. [ ] 동시 접속자 수 확인
4. [ ] 네트워크 상태 확인

### 조치
1. 슬로우 쿼리 강제 종료
2. 캐시 초기화
3. 임시 인덱스 추가 (긴급 시)
4. 서버 스케일업 (필요시)

---

## 검사 결과 미수신

### 증상
- 검사 결과가 EMR에 반영되지 않음

### 체크리스트
1. [ ] 연계 로그 확인
2. [ ] 검사 장비 상태 확인
3. [ ] 네트워크 연결 확인
4. [ ] 메시지 큐 상태 확인

### 조치
1. 연계 서비스 재시작
2. 실패 메시지 재처리
3. 검사실에 수동 입력 요청 (긴급 시)
```

### 9.3 백업 및 복구 절차

```markdown
## 백업 정책

### 백업 대상
- 메인 DB (PostgreSQL)
- 감사 로그 DB
- 애플리케이션 설정 파일
- 업로드 파일 (의료 문서 등)

### 백업 주기
| 유형 | 주기 | 보관 기간 | 저장 위치 |
|-----|------|----------|----------|
| 전체 백업 | 매일 02:00 | 30일 | 백업 서버 + 외부 스토리지 |
| 증분 백업 | 매 6시간 | 7일 | 백업 서버 |
| WAL 아카이브 | 실시간 | 7일 | 백업 서버 |
| 감사 로그 | 매일 | 5년 | 장기 보관 스토리지 |

### 백업 스크립트

```bash
#!/bin/bash
# backup.sh - 일일 백업 스크립트

BACKUP_DIR=/backup/daily
DATE=$(date +%Y%m%d)
DB_NAME=medicore
DB_USER=postgres

# DB 전체 백업
pg_dump -U $DB_USER -Fc $DB_NAME > $BACKUP_DIR/db_$DATE.dump

# 압축
gzip $BACKUP_DIR/db_$DATE.dump

# 30일 이전 백업 삭제
find $BACKUP_DIR -name "*.gz" -mtime +30 -delete

# 원격지 복사
rsync -avz $BACKUP_DIR/ backup-server:/backup/medicore/

# 백업 확인 알림
if [ $? -eq 0 ]; then
    echo "백업 성공: $DATE" | mail -s "백업 완료" admin@hospital.com
else
    echo "백업 실패: $DATE" | mail -s "[긴급] 백업 실패" admin@hospital.com
fi
```

### 복구 절차

```bash
#!/bin/bash
# restore.sh - 복구 스크립트

BACKUP_FILE=$1
DB_NAME=medicore

if [ -z "$BACKUP_FILE" ]; then
    echo "Usage: restore.sh <backup_file>"
    exit 1
fi

# 기존 연결 종료
psql -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname='$DB_NAME';"

# DB 삭제 후 재생성
dropdb $DB_NAME
createdb $DB_NAME

# 복구
gunzip -c $BACKUP_FILE | pg_restore -d $DB_NAME

echo "복구 완료: $BACKUP_FILE"
```
```

### 9.4 헬스체크 스크립트

```bash
#!/bin/bash
# health-check.sh

API_URL="http://localhost:8080/api/health"
DB_HOST="localhost"
DB_PORT="5432"
ALERT_EMAIL="admin@hospital.com"

# API 체크
api_status=$(curl -s -o /dev/null -w "%{http_code}" $API_URL)
if [ "$api_status" != "200" ]; then
    echo "[ALERT] API 서버 이상: HTTP $api_status" | mail -s "[긴급] API 장애" $ALERT_EMAIL
fi

# DB 체크
pg_isready -h $DB_HOST -p $DB_PORT > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "[ALERT] DB 서버 연결 불가" | mail -s "[긴급] DB 장애" $ALERT_EMAIL
fi

# 디스크 용량 체크
disk_usage=$(df -h / | awk 'NR==2 {print $5}' | sed 's/%//')
if [ "$disk_usage" -gt 80 ]; then
    echo "[ALERT] 디스크 사용률 $disk_usage%" | mail -s "[경고] 디스크 용량" $ALERT_EMAIL
fi

# 메모리 체크
mem_usage=$(free | awk 'NR==2 {printf "%.0f", $3/$2 * 100}')
if [ "$mem_usage" -gt 90 ]; then
    echo "[ALERT] 메모리 사용률 $mem_usage%" | mail -s "[경고] 메모리" $ALERT_EMAIL
fi
```

---

## 10. 테스트 전략

### 10.1 테스트 계층

```
┌─────────────────────────────────────────────────────────────────┐
│                        테스트 피라미드                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│                         /\                                      │
│                        /  \                                     │
│                       / E2E \      ← 소수의 핵심 시나리오        │
│                      /──────\                                   │
│                     /        \                                  │
│                    / 통합 테스트 \   ← API, DB 연동              │
│                   /────────────\                                │
│                  /              \                               │
│                 /   단위 테스트    \  ← 비즈니스 로직             │
│                /──────────────────\                             │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 10.2 단위 테스트 예시

```java
@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    @Test
    @DisplayName("진료 기록 생성 - 정상 케이스")
    void createMedicalRecord_Success() {
        // Given
        Long patientId = 1L;
        MedicalRecordCreateRequest request = MedicalRecordCreateRequest.builder()
                .patientId(patientId)
                .visitDate(LocalDate.now())
                .chiefComplaint("두통")
                .diagnosisCode("R51")
                .build();

        Patient patient = Patient.builder()
                .patientId(patientId)
                .name("홍길동")
                .build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(medicalRecordRepository.save(any())).thenAnswer(i -> {
            MedicalRecord record = i.getArgument(0);
            record.setRecordId(1L);
            return record;
        });

        // When
        MedicalRecordResponse response = medicalRecordService.createMedicalRecord(request);

        // Then
        assertThat(response.getRecordId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo("DRAFT");
        verify(medicalRecordRepository).save(any(MedicalRecord.class));
    }

    @Test
    @DisplayName("서명된 진료 기록 수정 시 예외 발생")
    void updateMedicalRecord_AlreadySigned_ThrowsException() {
        // Given
        Long recordId = 1L;
        MedicalRecord signedRecord = MedicalRecord.builder()
                .recordId(recordId)
                .status("COMPLETED")
                .signedAt(LocalDateTime.now())
                .build();

        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.of(signedRecord));

        MedicalRecordUpdateRequest request = new MedicalRecordUpdateRequest();

        // When & Then
        assertThatThrownBy(() -> medicalRecordService.updateMedicalRecord(recordId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("서명된 진료 기록은 수정할 수 없습니다");
    }

    @Test
    @DisplayName("RBAC - 행정직원은 진료기록 조회 불가")
    void getMedicalRecord_AdminStaff_AccessDenied() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(
            createAuthentication("admin001", "ADMIN_STAFF")
        );

        // When & Then
        assertThatThrownBy(() -> medicalRecordService.getMedicalRecord(1L))
                .isInstanceOf(AccessDeniedException.class);
    }
}
```

### 10.3 통합 테스트 예시

```java
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PatientApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String doctorToken;
    private String adminStaffToken;

    @BeforeEach
    void setUp() {
        doctorToken = createToken("doctor001", "DOCTOR");
        adminStaffToken = createToken("admin001", "ADMIN_STAFF");
    }

    @Test
    @DisplayName("환자 조회 - 의사 권한으로 성공")
    void getPatient_AsDoctor_Success() throws Exception {
        mockMvc.perform(get("/api/patients/1")
                        .header("Authorization", "Bearer " + doctorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(1))
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    @DisplayName("진료기록 조회 - 행정직원은 403 Forbidden")
    void getMedicalRecord_AsAdminStaff_Forbidden() throws Exception {
        mockMvc.perform(get("/api/medical-records/1")
                        .header("Authorization", "Bearer " + adminStaffToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 리소스에 대한 접근 권한이 없습니다."));
    }

    @Test
    @DisplayName("환자 등록 - 유효성 검증 실패")
    void createPatient_InvalidData_BadRequest() throws Exception {
        PatientCreateRequest request = PatientCreateRequest.builder()
                .name("")  // 필수 필드 누락
                .build();

        mockMvc.perform(post("/api/patients")
                        .header("Authorization", "Bearer " + doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("name"));
    }
}
```

---

## 11. 배포 및 인프라

### 11.1 Docker 구성

**Dockerfile**
```dockerfile
FROM eclipse-temurin:17-jdk-alpine as builder

WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

# 보안: non-root 사용자로 실행
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

**docker-compose.yml**
```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=db
      - DB_PORT=5432
      - DB_NAME=medicore
      - DB_USER=${DB_USER}
      - DB_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
    depends_on:
      db:
        condition: service_healthy
    restart: unless-stopped

  db:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=medicore
      - POSTGRES_USER=${DB_USER}
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./scripts/init-db.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USER} -d medicore"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

volumes:
  postgres_data:
```

---

## 12. 면접 대비

### 12.1 예상 질문 및 답변

**Q1: "이 로그는 왜 남겼나요?"**
```
답변:
"의료법과 개인정보보호법에 따라 환자 정보에 대한 모든 접근은 기록되어야 합니다.
감사 로그에는 5W1H 원칙으로 기록합니다.
- 누가(사용자 ID, 역할)
- 언제(타임스탬프)
- 어디서(IP, URI)
- 무엇을(리소스 종류, ID)
- 어떻게(HTTP 메소드, 파라미터)
- 결과(성공/실패/거부)

이 로그는 정보 유출 사고 시 영향 범위 파악, 내부 감사,
의료기관 인증평가 대응에 필수적으로 사용됩니다.
또한, 삭제와 수정이 불가능하도록 트리거로 보호하고 있습니다."
```

**Q2: "이 권한은 왜 분리했나요?"**
```
답변:
"병원에서 역할에 따른 접근 제어가 중요한 이유는:

1. 행정직원이 진료기록에 접근할 수 없게 한 이유:
   - 업무상 필요 없는 민감 정보 노출 방지
   - 개인정보보호법 최소 수집 원칙 준수

2. 간호사가 처방을 생성할 수 없게 한 이유:
   - 의료법상 처방은 의사만 가능
   - 의료 사고 방지

3. 전산관리자도 진료 정보에 직접 접근할 수 없게 한 이유:
   - 시스템 관리와 환자 진료는 별개
   - 내부자 위협 방지

각 역할은 업무 수행에 필요한 최소한의 권한만 부여받습니다.
이를 최소 권한 원칙(Principle of Least Privilege)이라고 합니다."
```

**Q3: "새벽 3시에 DB 서버가 다운되면 어떻게 하실 건가요?"**
```
답변:
"사전에 준비된 장애 대응 절차를 따릅니다:

1. 즉시 대응 (15분 이내):
   - 모니터링 알림 확인
   - 장애 등급 판정 (DB 다운은 Critical)
   - 담당자 연락 (에스컬레이션)

2. 원인 분석:
   - 서버 상태 확인 (systemctl status)
   - 로그 확인 (/var/log/postgresql/)
   - 디스크/메모리 상태 확인
   - 최근 변경 이력 확인

3. 조치:
   - 서버 재시작 시도
   - 필요시 백업에서 복구
   - 복구 후 데이터 무결성 검증

4. 사후 조치:
   - 장애 보고서 작성
   - 재발 방지 대책 수립

또한, 평소에 헬스체크 스크립트를 cron으로 돌려
조기에 이상 징후를 탐지합니다."
```

**Q4: "진료기록 수정은 어떻게 처리하나요?"**
```
답변:
"의료법에 따라 전자서명이 완료된 진료기록은 직접 수정이 불가능합니다.

수정이 필요한 경우:
1. 원본 데이터는 그대로 보존
2. 별도의 수정 이력 테이블(MEDICAL_RECORD_AMENDMENTS)에 기록
   - 수정자
   - 수정 사유 (필수!)
   - 수정된 필드
   - 수정 전/후 값
   - 수정 시각

3. 진료기록 상태를 'AMENDED'로 변경

이렇게 하면 원본 기록의 무결성을 유지하면서도
수정 이력을 완전히 추적할 수 있습니다."
```

**Q5: "검사 결과가 안 들어온다고 전화오면?"**
```
답변:
"체계적으로 확인합니다:

1. 영향 범위 파악:
   - 해당 환자만 문제인지, 전체 문제인지
   - 특정 검사만 문제인지, 전체 검사가 문제인지

2. 연계 로그 확인:
   - 메시지 수신 여부
   - 처리 결과 (성공/실패/에러)

3. 네트워크 확인:
   - 검사 장비 ↔ EMR 서버 간 통신 상태

4. 장비 상태 확인:
   - 검사실에 장비 상태 문의

5. 조치:
   - 실패 메시지 재처리
   - 긴급 시 검사실에 수동 입력 요청

6. 사후 처리:
   - 원인 분석 및 재발 방지"
```

### 12.2 포트폴리오 발표 포인트

```
1. 왜 이 프로젝트를 만들었는지 (동기)
   → 병원 전산직 실무 역량 증명

2. 병원 전산 환경의 특수성 이해 (도메인 지식)
   → EMR, OCS, PACS 등 시스템 이해
   → 의료법, 개인정보보호법 준수

3. 보안 설계 (RBAC + 감사 로그)
   → 왜 이렇게 설계했는지 설명
   → 실제 사고 사례와 연결

4. 외부 시스템 연계 (HL7)
   → 병원 전산의 핵심 업무

5. 운영 관점 (장애 대응, 백업)
   → 코드만큼 중요한 운영 역량

핵심 메시지:
"저는 단순히 코드를 짤 줄 아는 사람이 아니라,
병원 전산 환경을 이해하고 안전하게 운영할 수 있는 사람입니다."
```

### 12.3 GitHub README 구성

```markdown
# MediCore Hospital Backend System

병원 EMR 연계 백엔드 서버 - 병원 전산직 포트폴리오

## 프로젝트 소개

병원 전산직 실무에서 요구되는 핵심 역량을 증명하기 위한 프로젝트입니다.

### 주요 기능
- ✅ 환자/진료기록/처방/검사 관리 API
- ✅ 역할 기반 접근 제어 (RBAC)
- ✅ 감사 로그 시스템
- ✅ 외부 시스템 연계 (HL7 스타일)
- ✅ 장애 대응 및 운영 문서

### 기술 스택
- Java 17, Spring Boot 3.x
- PostgreSQL, JPA/Hibernate
- Spring Security, JWT
- Docker, Docker Compose

## 빠른 시작

```bash
git clone https://github.com/your/medicore-backend.git
cd medicore-backend
docker-compose up -d
```

API 문서: http://localhost:8080/swagger-ui.html

## 문서

- [시스템 아키텍처](docs/architecture/system-overview.md)
- [데이터베이스 설계](docs/database/erd.md)
- [API 명세서](docs/api/api-specification.md)
- [운영 가이드](docs/operations/deployment-guide.md)
- [장애 대응 매뉴얼](docs/operations/troubleshooting-guide.md)

## 테스트 계정

| 역할 | ID | PW | 권한 |
|-----|----|----|-----|
| 의사 | doctor001 | test1234! | 진료/처방/검사 |
| 간호사 | nurse001 | test1234! | 조회/환자관리 |
| 행정 | admin001 | test1234! | 원무/예약 |
| 전산 | itadmin001 | test1234! | 시스템관리 |

## 면접 대비

[예상 질문 및 답변](docs/interview-prep/expected-questions.md)
```

---

## 마무리

이 가이드를 따라 프로젝트를 완성하면:

1. **병원 전산 도메인 이해**를 증명할 수 있습니다
2. **보안 의식**을 갖춘 개발자임을 보여줄 수 있습니다
3. **운영 관점**의 사고를 할 수 있음을 증명합니다
4. **면접에서 자신감 있게** 설명할 수 있습니다

가장 중요한 것:
**"왜 이렇게 설계했는가"**를 설명할 수 있어야 합니다.

화이팅! 🏥💻
