# 🏥 병원 EMR 연계 백엔드 서버 포트폴리오 - 완전 정복 가이드

## 병원 전산직 합격을 위한 실무형 프로젝트 설계 가이드라인

---

## 📚 목차

이 가이드는 5개의 파트로 구성되어 있습니다:

### [Part 1: 프로젝트 개요 및 시스템 아키텍처](01-overview-architecture.md)
- 프로젝트 개요 및 목적
- 병원 전산 환경 이해하기
- 시스템 아키텍처 설계
- 기술 스택 및 디렉토리 구조

### [Part 2: 데이터베이스 설계](02-database-design.md)
- ERD (Entity-Relationship Diagram)
- 전체 테이블 정의서 (Users, Roles, Patients, Medical Records, Prescriptions, Lab Results, Audit Logs)
- 데이터 사전
- 민감 데이터 암호화

### [Part 3: 역할 기반 접근 제어(RBAC) 및 감사 로그](03-rbac-audit.md)
- RBAC 개념 및 중요성
- 역할별 권한 매트릭스
- Spring Security 기반 구현
- 감사 로그 시스템 구현
- AOP 기반 자동 로깅
- 이상 접근 탐지

### [Part 4: API 설계 및 외부 시스템 연계](04-api-integration.md)
- RESTful API 설계 원칙
- 주요 API 명세서 (인증, 환자, 진료기록, 처방, 검사)
- HL7 메시지 이해
- 외부 시스템 연계 구현
- Mock 검사 장비 시뮬레이터

### [Part 5: 운영, 테스트, 배포 및 면접 대비](05-operations-interview.md)
- 장애 대응 매뉴얼
- 백업 및 복구 절차
- 테스트 전략
- Docker 배포
- **면접 예상 질문 및 답변**
- 포트폴리오 발표 포인트

---

## 🎯 이 가이드의 목적

병원 전산직은 단순히 "코딩을 잘하는 사람"을 뽑는 것이 아닙니다.

**병원에서 요구하는 핵심 역량:**
- 의료 데이터 흐름에 대한 이해
- 보안과 접근 통제
- 감사 추적
- 시스템 연동
- 장애 대응

이 포트폴리오는 위의 **모든 역량을 "증명"**할 수 있도록 설계되었습니다.

---

## 💡 왜 이 포트폴리오가 "확실하게 먹히는가"?

| 일반적인 포트폴리오 | 이 포트폴리오 |
|------------------|-------------|
| 단순 CRUD | 역할별 접근 제어 + 감사 로그 |
| 웹 개발 중심 | 시스템 연동 + 운영 관점 |
| 기능 나열 | "왜 이렇게 설계했는가" 설명 가능 |
| 코드만 있음 | 운영 문서 + 장애 대응 매뉴얼 포함 |

---

## 🔑 핵심 메시지

> **"저는 단순히 코드를 짤 줄 아는 사람이 아니라, 병원 전산 환경을 이해하고 안전하게 운영할 수 있는 사람입니다."**

---

## 📋 프로젝트 체크리스트

구현해야 할 핵심 기능:

### 필수 (Must Have)
- [ ] 환자/진료기록/처방/검사 CRUD API
- [ ] 역할 기반 접근 제어 (RBAC) - 의사/간호사/행정/전산
- [ ] 감사 로그 시스템 (5W1H 원칙)
- [ ] 외부 시스템 연계 Mock (HL7 스타일)
- [ ] JWT 인증/인가
- [ ] 운영 문서 (장애 대응, 백업/복구)

### 권장 (Should Have)
- [ ] 진료기록 수정 이력 관리 (Amendment)
- [ ] 이상 접근 탐지 리포트
- [ ] 위급 수치 알림 기능
- [ ] Swagger API 문서
- [ ] Docker 배포

### 선택 (Nice to Have)
- [ ] 통합 테스트
- [ ] CI/CD 파이프라인
- [ ] 모니터링 대시보드

---

## 🏆 면접 합격 핵심

면접에서 이런 질문에 **자신감 있게 답할 수 있어야 합니다:**

1. **"이 로그는 왜 남겼나요?"**
2. **"이 권한은 왜 분리했나요?"**
3. **"새벽 3시에 DB 서버가 다운되면 어떻게 하실 건가요?"**
4. **"진료기록 수정은 어떻게 처리하나요?"**
5. **"검사 결과가 안 들어온다고 전화오면?"**

→ 각 질문에 대한 상세 답변은 [Part 5](05-operations-interview.md)에 있습니다.

---

## 🚀 시작하기

1. 이 가이드를 처음부터 끝까지 읽으세요
2. Part 1부터 순서대로 구현하세요
3. 각 단계마다 "왜?"를 생각하세요
4. 면접 전에 Part 5를 반복해서 읽으세요

**화이팅! 🏥💻**

---

## 📁 파일 구성

```
docs/
├── README.md                    # 이 파일 (전체 개요)
├── 01-overview-architecture.md  # Part 1
├── 02-database-design.md        # Part 2
├── 03-rbac-audit.md             # Part 3
├── 04-api-integration.md        # Part 4
└── 05-operations-interview.md   # Part 5
```

총 분량: 약 15,000줄 (상세 코드 예시 포함)
# 병원 EMR 연계 백엔드 서버 포트폴리오 가이드

## Part 1: 프로젝트 개요 및 시스템 아키텍처

---

## 1. 프로젝트 개요

### 1.1 이 프로젝트를 만드는 이유

병원 전산직은 단순히 "코딩을 잘하는 사람"을 뽑는 것이 아닙니다. 병원에서 요구하는 핵심 역량:

- **의료 데이터 흐름에 대한 이해**: 환자 정보가 어디서 생성되어 어디로 흘러가는지
- **보안과 접근 통제**: 누가 어떤 정보에 접근할 수 있는지 통제
- **감사 추적**: 모든 행위에 대한 기록과 추적 가능성
- **시스템 연동**: EMR, OCS, PACS, LIS 등 다양한 시스템 간 데이터 교환
- **장애 대응**: 24시간 운영되는 의료 시스템의 안정성 확보

### 1.2 프로젝트 범위

```
포함하는 것:
├── 환자 관리 모듈
├── 진료 기록 관리 모듈
├── 처방 관리 모듈
├── 검사 결과 연계 모듈
├── 의료진 계정 관리 모듈
├── 역할 기반 접근 제어 시스템
├── 감사 로그 시스템
├── 외부 시스템 연계 시뮬레이터
└── 운영 문서 및 장애 대응 매뉴얼

포함하지 않는 것:
├── 프론트엔드 UI (최소한만 구현)
├── 실제 의료 장비 연동
├── 실제 보험 청구 시스템
└── 실시간 모니터링 대시보드 (선택 사항)
```

### 1.3 기술 스택

**Option A: Java 기반 (권장)**
```
Backend Framework: Spring Boot 3.x
Database: PostgreSQL 15+
ORM: JPA/Hibernate
Authentication: Spring Security + JWT
API Documentation: Springdoc OpenAPI (Swagger)
Build Tool: Gradle
Testing: JUnit 5 + Mockito
Logging: Logback + SLF4J
```

**Option B: C# 기반**
```
Backend Framework: ASP.NET Core 7+
Database: SQL Server 또는 PostgreSQL
ORM: Entity Framework Core
Authentication: ASP.NET Identity + JWT
API Documentation: Swashbuckle (Swagger)
Testing: xUnit + Moq
Logging: Serilog
```

**공통 인프라**
```
Containerization: Docker + Docker Compose
Version Control: Git
API Testing: Postman Collection 제공
```

### 1.4 디렉토리 구조

```
medicore-hospital-backend/
├── README.md                          # 프로젝트 소개 및 실행 방법
├── docs/                              # 문서화
│   ├── architecture/                  # 아키텍처 문서
│   │   ├── system-overview.md
│   │   ├── data-flow.md
│   │   └── security-design.md
│   ├── database/                      # DB 설계 문서
│   │   ├── erd.png
│   │   ├── table-definitions.md
│   │   └── data-dictionary.md
│   ├── api/                           # API 문서
│   │   └── api-specification.md
│   ├── operations/                    # 운영 문서 (매우 중요!)
│   │   ├── deployment-guide.md
│   │   ├── troubleshooting-guide.md
│   │   ├── backup-recovery.md
│   │   └── incident-response.md
│   └── interview-prep/                # 면접 대비
│       └── expected-questions.md
├── src/
│   └── main/
│       ├── java/com/medicore/
│       │   ├── MedicoreApplication.java
│       │   ├── config/                # 설정 클래스
│       │   ├── domain/                # 도메인 엔티티
│       │   ├── repository/            # 데이터 접근 계층
│       │   ├── service/               # 비즈니스 로직
│       │   ├── controller/            # API 컨트롤러
│       │   ├── security/              # 보안 관련
│       │   ├── audit/                 # 감사 로그
│       │   ├── integration/           # 외부 연계
│       │   └── exception/             # 예외 처리
│       └── resources/
│           ├── application.yml
│           └── db/migration/          # DB 마이그레이션
├── src/test/                          # 테스트 코드
├── docker/
│   ├── Dockerfile
│   └── docker-compose.yml
├── scripts/
│   ├── init-db.sql                    # 초기 데이터
│   ├── backup.sh                      # 백업 스크립트
│   └── health-check.sh                # 헬스체크 스크립트
└── postman/
    └── MediCore-API-Collection.json   # API 테스트 컬렉션
```

---

## 2. 병원 전산 환경 이해

### 2.1 병원 정보 시스템 구성

```
┌─────────────────────────────────────────────────────────────────┐
│                        병원 정보 시스템 (HIS)                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │     EMR     │◄──►│     OCS     │◄──►│    PACS     │         │
│  │ 전자의무기록  │    │  처방전달    │    │  영상저장    │         │
│  └──────┬──────┘    └──────┬──────┘    └──────┬──────┘         │
│         │                  │                  │                 │
│         ▼                  ▼                  ▼                 │
│  ┌─────────────────────────────────────────────────────┐       │
│  │                  통합 데이터베이스                      │       │
│  └─────────────────────────────────────────────────────┘       │
│         │                  │                  │                 │
│         ▼                  ▼                  ▼                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │     LIS     │    │     RIS     │    │   원무/수납   │         │
│  │  검사정보    │    │  영상판독    │    │   시스템     │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

**각 시스템의 역할**

| 시스템 | 정식 명칭 | 주요 기능 |
|--------|----------|----------|
| EMR | Electronic Medical Record | 전자의무기록 저장/조회 |
| OCS | Order Communication System | 처방 전달 및 실행 |
| PACS | Picture Archiving and Communication System | 의료 영상 저장/전송 |
| LIS | Laboratory Information System | 검사실 정보 관리 |
| RIS | Radiology Information System | 영상의학과 업무 관리 |

### 2.2 의료 데이터의 특수성

**왜 의료 데이터가 특별한가?**

1. **법적 보존 의무**: 의료법에 따라 진료기록은 최소 10년 보존
2. **민감 정보**: 개인정보보호법상 민감정보로 분류
3. **생명과 직결**: 잘못된 데이터는 환자 생명에 영향
4. **감사 대상**: 건강보험심사평가원, 의료기관 인증 평가 대상
5. **무결성 필수**: 한 번 기록된 내용은 수정 시에도 이력 보존

**의료법 관련 조항 (면접 필수 암기)**

```
의료법 제22조 (진료기록부 등)
- 의료인은 진료기록부 등을 비치하고 환자의 주된 증상,
  진단 및 치료 내용 등을 상세히 기록하고 서명하여야 한다.

의료법 제23조 (전자의무기록)
- 전자의무기록은 전자서명법에 따른 전자서명이 있어야 한다.
- 전자의무기록을 안전하게 관리·보존하는 데 필요한 시설과 장비를 갖추어야 한다.

의료법 시행규칙 제15조 (진료기록부 등의 보존)
- 환자 명부: 5년
- 진료기록부: 10년
- 처방전: 2년
- 수술기록: 10년
- 검사소견기록: 5년
```

### 2.3 병원 전산직의 실제 업무

**일상적 업무**
- 시스템 모니터링 및 장애 대응
- 사용자 계정 관리 (의사, 간호사 등)
- 권한 설정 및 변경
- 데이터 백업 확인
- 외부 시스템 연계 오류 처리
- 검사 장비 인터페이스 관리

**주기적 업무**
- 보안 패치 적용
- DB 성능 튜닝
- 로그 분석 및 보고
- 재해복구 훈련
- 의료기관 인증 대비 자료 준비

---

## 3. 시스템 아키텍처 설계

### 3.1 전체 시스템 구성도

```
┌────────────────────────────────────────────────────────────────────────┐
│                           MediCore Hospital Backend                     │
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│   ┌──────────────────────────────────────────────────────────────┐    │
│   │                      API Gateway Layer                        │    │
│   │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │    │
│   │  │   인증/인가   │  │  Rate Limit │  │  Request    │          │    │
│   │  │   Filter    │  │   Filter    │  │  Logging    │          │    │
│   │  └─────────────┘  └─────────────┘  └─────────────┘          │    │
│   └──────────────────────────────────────────────────────────────┘    │
│                                    │                                   │
│   ┌────────────────────────────────▼─────────────────────────────┐    │
│   │                      Service Layer                            │    │
│   │                                                               │    │
│   │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │    │
│   │  │   Patient   │  │  Medical    │  │ Prescription│          │    │
│   │  │   Service   │  │  Record Svc │  │   Service   │          │    │
│   │  └─────────────┘  └─────────────┘  └─────────────┘          │    │
│   │                                                               │    │
│   │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │    │
│   │  │    Lab      │  │    User     │  │   Audit     │          │    │
│   │  │   Service   │  │   Service   │  │   Service   │          │    │
│   │  └─────────────┘  └─────────────┘  └─────────────┘          │    │
│   └──────────────────────────────────────────────────────────────┘    │
│                                    │                                   │
│   ┌────────────────────────────────▼─────────────────────────────┐    │
│   │                      Data Access Layer                        │    │
│   │  ┌─────────────────────────────────────────────────────┐     │    │
│   │  │                   JPA Repositories                   │     │    │
│   │  └─────────────────────────────────────────────────────┘     │    │
│   └──────────────────────────────────────────────────────────────┘    │
│                                    │                                   │
│   ┌────────────────────────────────▼─────────────────────────────┐    │
│   │                      Database Layer                           │    │
│   │  ┌─────────────────┐          ┌─────────────────┐            │    │
│   │  │   PostgreSQL    │          │   Audit Log DB  │            │    │
│   │  │   (Main DB)     │          │   (분리 저장)    │            │    │
│   │  └─────────────────┘          └─────────────────┘            │    │
│   └──────────────────────────────────────────────────────────────┘    │
│                                                                        │
│   ┌──────────────────────────────────────────────────────────────┐    │
│   │                   External Integration Layer                  │    │
│   │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │    │
│   │  │  Lab Device │  │  Insurance  │  │   Other     │          │    │
│   │  │  Interface  │  │  Interface  │  │  Hospital   │          │    │
│   │  │   (Mock)    │  │   (Mock)    │  │   (Mock)    │          │    │
│   │  └─────────────┘  └─────────────┘  └─────────────┘          │    │
│   └──────────────────────────────────────────────────────────────┘    │
│                                                                        │
└────────────────────────────────────────────────────────────────────────┘
```

### 3.2 레이어별 책임

**API Gateway Layer**
```
책임:
- 모든 요청의 진입점
- 인증 토큰 검증 (JWT)
- 권한 확인 (RBAC)
- 요청/응답 로깅
- Rate Limiting

구현 컴포넌트:
- JwtAuthenticationFilter
- RoleAuthorizationFilter
- RequestLoggingFilter
- GlobalExceptionHandler
```

**Service Layer**
```
책임:
- 비즈니스 로직 처리
- 트랜잭션 관리
- 도메인 규칙 검증
- 감사 이벤트 발행

구현 컴포넌트:
- PatientService
- MedicalRecordService
- PrescriptionService
- LabResultService
- UserService
- AuditService
```

**Data Access Layer**
```
책임:
- 데이터베이스 접근 추상화
- 쿼리 최적화
- 엔티티-테이블 매핑
```

**External Integration Layer**
```
책임:
- 외부 시스템과의 통신
- 메시지 포맷 변환 (HL7, JSON)
- 연결 실패 시 재시도 로직
- 연계 로그 기록
```

### 3.3 데이터 흐름도

**진료 기록 생성 흐름**
```
[의사 클라이언트]
      │
      ▼ (1) POST /api/medical-records
┌─────────────────┐
│ JwtAuthFilter   │──► 토큰 검증 실패 → 401 Unauthorized
└────────┬────────┘
         │ (2) 토큰 유효
         ▼
┌─────────────────┐
│ RoleAuthFilter  │──► 권한 부족 → 403 Forbidden
└────────┬────────┘
         │ (3) 의사 권한 확인
         ▼
┌─────────────────┐
│ MedicalRecord   │
│   Controller    │
└────────┬────────┘
         │ (4) 요청 데이터 검증
         ▼
┌─────────────────┐
│ MedicalRecord   │
│    Service      │
└────────┬────────┘
         │ (5) 비즈니스 로직
         │     - 환자 존재 확인
         │     - 진료 기록 생성
         │     - 감사 로그 발행
         ▼
┌─────────────────┐
│   Repository    │
└────────┬────────┘
         │ (6) DB 저장
         ▼
┌─────────────────┐  ┌─────────────────┐
│   Main DB       │  │  Audit Log DB   │
│ (진료기록 저장)   │  │  (접근 이력 저장) │
└─────────────────┘  └─────────────────┘
```

### 3.4 보안 아키텍처

```
┌─────────────────────────────────────────────────────────────────┐
│                       Security Architecture                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │                    Authentication Layer                    │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐       │ │
│  │  │   Login     │  │    JWT      │  │   Token     │       │ │
│  │  │  Service    │  │  Provider   │  │  Validator  │       │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘       │ │
│  └───────────────────────────────────────────────────────────┘ │
│                              │                                  │
│  ┌───────────────────────────▼───────────────────────────────┐ │
│  │                    Authorization Layer                     │ │
│  │  ┌─────────────────────────────────────────────────────┐  │ │
│  │  │                RBAC (Role-Based Access Control)      │  │ │
│  │  │                                                      │  │ │
│  │  │  DOCTOR ──────► 진료기록 CRUD, 처방 생성, 검사 조회   │  │ │
│  │  │  NURSE ───────► 진료기록 조회, 처방 조회, 환자 관리   │  │ │
│  │  │  ADMIN ───────► 환자 등록, 수납, 예약 관리           │  │ │
│  │  │  IT_ADMIN ────► 계정 관리, 감사 로그 조회, 시스템 설정│  │ │
│  │  └─────────────────────────────────────────────────────┘  │ │
│  └───────────────────────────────────────────────────────────┘ │
│                              │                                  │
│  ┌───────────────────────────▼───────────────────────────────┐ │
│  │                      Audit Layer                           │ │
│  │  ┌─────────────────────────────────────────────────────┐  │ │
│  │  │              All Access Logged                       │  │ │
│  │  │  WHO + WHEN + WHAT + WHERE + RESULT                 │  │ │
│  │  └─────────────────────────────────────────────────────┘  │ │
│  └───────────────────────────────────────────────────────────┘ │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```
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
# 병원 EMR 연계 백엔드 서버 포트폴리오 가이드

## Part 3: 역할 기반 접근 제어(RBAC) 및 감사 로그 시스템

---

## 5. 역할 기반 접근 제어(RBAC) 구현

### 5.1 RBAC가 병원에서 중요한 이유

```
실제 사고 사례:
- 의사 A가 자신이 담당하지 않는 연예인 환자의 진료 기록을 조회 → 개인정보 유출
- 행정직원 B가 호기심으로 VIP 환자의 처방 내역을 조회 → 민감정보 접근 위반
- 간호사 C가 처방을 임의로 수정 → 의료 사고 위험

결과:
- 해당 직원 징계 및 해고
- 병원 행정 처분
- 개인정보보호법 위반으로 과태료/벌금
- 환자의 손해배상 청구
- 병원 신뢰도 하락
```

### 5.2 역할별 권한 매트릭스

| 리소스 | 액션 | 의사 | 간호사 | 행정직원 | 전산관리자 |
|-------|------|------|-------|---------|----------|
| **PATIENT** | CREATE | ✓ | - | ✓ | - |
| **PATIENT** | READ | ✓ | ✓ | ✓ | - |
| **PATIENT** | UPDATE | ✓ | ✓ | ✓ | - |
| **MEDICAL_RECORD** | CREATE | ✓ | - | **-** | - |
| **MEDICAL_RECORD** | READ | ✓ | ✓ | **-** | - |
| **MEDICAL_RECORD** | UPDATE | ✓ | - | **-** | - |
| **PRESCRIPTION** | CREATE | ✓ | **-** | **-** | - |
| **PRESCRIPTION** | READ | ✓ | ✓ | **-** | - |
| **LAB_ORDER** | CREATE | ✓ | - | - | - |
| **LAB_RESULT** | READ | ✓ | ✓ | - | - |
| **LAB_RESULT** | CREATE | - | - | - | - |
| **USER** | CRUD | - | - | - | ✓ |
| **AUDIT_LOG** | READ | - | - | - | ✓ |
| **SYSTEM_CONFIG** | CRUD | - | - | - | ✓ |

**핵심 포인트:**
- 행정직원은 진료기록/처방 접근 불가
- 간호사는 처방 생성 불가
- 전산관리자도 환자 진료 정보 직접 접근 불가

### 5.3 Spring Security 기반 구현

#### 5.3.1 보안 설정

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuditLogService auditLogService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                .accessDeniedHandler(new JwtAccessDeniedHandler(auditLogService)))
            .authorizeHttpRequests(auth -> auth
                // 인증 불필요
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/health").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // 감사 로그 - 전산관리자만
                .requestMatchers("/api/audit/**").hasRole("IT_ADMIN")

                // 사용자 관리 - 전산관리자만
                .requestMatchers("/api/users/**").hasRole("IT_ADMIN")

                // 시스템 설정 - 전산관리자만
                .requestMatchers("/api/system/**").hasRole("IT_ADMIN")

                // 나머지는 인증 필요
                .anyRequest().authenticated()
            )
            .addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
```

#### 5.3.2 JWT 토큰 제공자

```java
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;  // 30분

    private SecretKey key;

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put("userId", user.getUserId());
        claims.put("name", user.getName());
        claims.put("role", user.getRole().getRoleCode());
        claims.put("department", user.getDepartment());

        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidity);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        String username = claims.getSubject();
        Long userId = claims.get("userId", Long.class);
        String roleCode = claims.get("role", String.class);
        String name = claims.get("name", String.class);
        String department = claims.get("department", String.class);

        Collection<? extends GrantedAuthority> authorities =
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleCode));

        CustomUserDetails userDetails = CustomUserDetails.builder()
                .userId(userId)
                .username(username)
                .name(name)
                .roleCode(roleCode)
                .department(department)
                .authorities(authorities)
                .build();

        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("토큰이 만료되었습니다.");
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("유효하지 않은 토큰입니다.");
        }
    }
}
```

#### 5.3.3 권한 검사 서비스

```java
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final RolePermissionRepository permissionRepository;
    private final AuditLogService auditLogService;

    public boolean hasPermission(Long roleId, String resource, String action) {
        return permissionRepository.existsByRoleIdAndResourceAndAction(roleId, resource, action);
    }

    // 조건부 권한 확인 (예: 본인 담당 환자만)
    public boolean hasPermissionWithCondition(Long roleId, String resource,
                                              String action, Map<String, Object> context) {
        Optional<RolePermission> permission =
            permissionRepository.findByRoleIdAndResourceAndAction(roleId, resource, action);

        if (permission.isEmpty()) {
            return false;
        }

        if (permission.get().getConditions() == null) {
            return true;
        }

        return evaluateConditions(permission.get().getConditions(), context);
    }

    private boolean evaluateConditions(JsonNode conditions, Map<String, Object> context) {
        // 예: {"own_patient_only": true}
        if (conditions.has("own_patient_only") && conditions.get("own_patient_only").asBoolean()) {
            Long currentUserId = (Long) context.get("currentUserId");
            Long patientDoctorId = (Long) context.get("patientDoctorId");
            return currentUserId != null && currentUserId.equals(patientDoctorId);
        }
        return true;
    }
}
```

#### 5.3.4 AOP 기반 권한 검사

```java
// 커스텀 어노테이션
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    String resource();
    String action();
}

// Aspect
@Aspect
@Component
@RequiredArgsConstructor
public class ResourceAccessAspect {

    private final PermissionService permissionService;
    private final AuditLogService auditLogService;

    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        CustomUserDetails user = getCurrentUser();

        String resource = requirePermission.resource();
        String action = requirePermission.action();

        boolean hasPermission = permissionService.hasPermission(
            user.getRoleId(), resource, action
        );

        if (!hasPermission) {
            // 감사 로그 기록 (권한 거부)
            auditLogService.logAccessDenied(
                user, resource, action,
                "권한 부족: " + user.getRoleCode() + " 역할은 " +
                resource + ":" + action + " 권한이 없습니다."
            );

            throw new AccessDeniedException(
                "해당 리소스에 대한 접근 권한이 없습니다. [" + resource + ":" + action + "]"
            );
        }
    }

    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            throw new AccessDeniedException("인증 정보가 없습니다.");
        }
        return (CustomUserDetails) auth.getPrincipal();
    }
}
```

#### 5.3.5 컨트롤러에서 사용

```java
@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @GetMapping("/{recordId}")
    @RequirePermission(resource = "MEDICAL_RECORD", action = "READ")
    public ResponseEntity<MedicalRecordResponse> getMedicalRecord(
            @PathVariable Long recordId) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecord(recordId));
    }

    @PostMapping
    @RequirePermission(resource = "MEDICAL_RECORD", action = "CREATE")
    public ResponseEntity<MedicalRecordResponse> createMedicalRecord(
            @Valid @RequestBody MedicalRecordCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medicalRecordService.createMedicalRecord(request));
    }

    @PutMapping("/{recordId}")
    @RequirePermission(resource = "MEDICAL_RECORD", action = "UPDATE")
    public ResponseEntity<MedicalRecordResponse> updateMedicalRecord(
            @PathVariable Long recordId,
            @Valid @RequestBody MedicalRecordUpdateRequest request) {
        return ResponseEntity.ok(medicalRecordService.updateMedicalRecord(recordId, request));
    }
}
```

#### 5.3.6 권한 거부 핸들러

```java
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        // 감사 로그 기록
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails user) {
            auditLogService.logAccessDenied(
                user,
                extractResource(request.getRequestURI()),
                request.getMethod(),
                accessDeniedException.getMessage()
            );
        }

        // 응답
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(403)
                .error("Forbidden")
                .message("해당 리소스에 대한 접근 권한이 없습니다.")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
```

---

## 6. 감사 로그(Audit Log) 시스템

### 6.1 감사 로그의 중요성

**왜 병원에서 감사 로그가 필수인가?**

```
1. 법적 요구사항
   - 의료법: 전자의무기록의 관리 의무
   - 개인정보보호법: 개인정보 처리 내역 기록
   - 정보통신망법: 접근 기록 보관 의무

2. 의료기관 인증평가
   - 정보보호 기준: 환자 정보 접근 기록 관리
   - 추적 가능성: 누가, 언제, 무엇을 했는지 확인 가능해야 함

3. 사고 대응
   - 정보 유출 사고 시 영향 범위 파악
   - 책임 소재 확인
   - 포렌식 증거 자료

4. 내부 감사
   - 불필요한 정보 접근 탐지
   - 이상 행위 모니터링
   - 보안 정책 준수 확인
```

### 6.2 감사 로그 기록 원칙 (5W1H)

```
WHO    - 누가 (user_id, username, role)
WHEN   - 언제 (created_at)
WHERE  - 어디서 (request_ip, request_uri)
WHAT   - 무엇을 (resource_type, resource_id)
WHY    - 왜 (action, action_detail)
HOW    - 어떻게 (request_method, request_params)
RESULT - 결과 (result, response_status, failure_reason)
```

### 6.3 AOP 기반 자동 로깅 구현

#### 6.3.1 감사 로그 어노테이션

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {
    String resource();                    // 리소스 유형
    String action();                      // 액션 유형
    String resourceIdParam() default "";  // 리소스 ID 파라미터명
    String patientIdParam() default "";   // 환자 ID 파라미터명
    boolean logRequestBody() default false;
}
```

#### 6.3.2 감사 로그 Aspect

```java
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;
    private final PatientRepository patientRepository;
    private final ObjectMapper objectMapper;

    @Around("@annotation(audited)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, Audited audited) throws Throwable {
        long startTime = System.currentTimeMillis();

        CustomUserDetails user = getCurrentUser();
        HttpServletRequest request = getCurrentRequest();

        String resourceId = extractResourceId(joinPoint, audited.resourceIdParam());
        Long patientId = extractPatientId(joinPoint, audited.patientIdParam());

        AuditLog.AuditLogBuilder logBuilder = AuditLog.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .userRole(user.getRoleCode())
                .userDepartment(user.getDepartment())
                .action(audited.action())
                .resourceType(audited.resource())
                .resourceId(resourceId)
                .patientId(patientId)
                .requestIp(getClientIp(request))
                .requestUri(request.getRequestURI())
                .requestMethod(request.getMethod())
                .requestParams(maskSensitiveData(request.getParameterMap()))
                .userAgent(request.getHeader("User-Agent"));

        // 환자 정보 비정규화
        if (patientId != null) {
            patientRepository.findById(patientId).ifPresent(patient -> {
                logBuilder.patientNo(patient.getPatientNo());
                logBuilder.patientName(maskName(patient.getName()));
            });
        }

        Object result = null;

        try {
            result = joinPoint.proceed();
            logBuilder.result("SUCCESS").responseStatus(200);
            return result;

        } catch (AccessDeniedException e) {
            logBuilder.result("DENIED")
                     .responseStatus(403)
                     .failureReason(e.getMessage());
            throw e;

        } catch (Exception e) {
            logBuilder.result("FAILURE")
                     .responseStatus(500)
                     .failureReason(e.getMessage());
            throw e;

        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            logBuilder.executionTimeMs((int) executionTime);
            saveAuditLogAsync(logBuilder.build());
        }
    }

    // 민감 데이터 마스킹
    private String maskSensitiveData(Map<String, String[]> params) {
        if (params == null || params.isEmpty()) return null;

        Map<String, String[]> masked = new HashMap<>(params);
        List<String> sensitiveFields = List.of("password", "residentNo", "ssn");

        for (String field : sensitiveFields) {
            if (masked.containsKey(field)) {
                masked.put(field, new String[]{"****"});
            }
        }

        try {
            return objectMapper.writeValueAsString(masked);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    // 이름 마스킹 (홍*동)
    private String maskName(String name) {
        if (name == null || name.length() < 2) return "***";
        if (name.length() == 2) return name.charAt(0) + "*";
        return name.charAt(0) + "*".repeat(name.length() - 2) + name.charAt(name.length() - 1);
    }

    @Async
    protected void saveAuditLogAsync(AuditLog auditLog) {
        try {
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("감사 로그 저장 실패: {}", e.getMessage(), e);
            logToFile(auditLog);  // DB 실패 시 파일로 백업
        }
    }

    private void logToFile(AuditLog auditLog) {
        log.info("AUDIT|{}|{}|{}|{}|{}|{}|{}|{}|{}",
                auditLog.getCreatedAt(),
                auditLog.getUserId(),
                auditLog.getUsername(),
                auditLog.getUserRole(),
                auditLog.getAction(),
                auditLog.getResourceType(),
                auditLog.getResourceId(),
                auditLog.getPatientId(),
                auditLog.getResult());
    }
}
```

#### 6.3.3 컨트롤러에서 사용

```java
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/{patientId}")
    @RequirePermission(resource = "PATIENT", action = "READ")
    @Audited(resource = "PATIENT", action = "READ",
             resourceIdParam = "patientId", patientIdParam = "patientId")
    public ResponseEntity<PatientResponse> getPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientService.getPatient(patientId));
    }

    @PostMapping
    @RequirePermission(resource = "PATIENT", action = "CREATE")
    @Audited(resource = "PATIENT", action = "CREATE", logRequestBody = true)
    public ResponseEntity<PatientResponse> createPatient(
            @Valid @RequestBody PatientCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patientService.createPatient(request));
    }

    @GetMapping("/search")
    @RequirePermission(resource = "PATIENT", action = "READ")
    @Audited(resource = "PATIENT", action = "SEARCH")
    public ResponseEntity<Page<PatientSummary>> searchPatients(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String patientNo,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(patientService.searchPatients(name, patientNo, pageable));
    }
}
```

### 6.4 감사 로그 조회 API

```java
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('IT_ADMIN')")  // 전산관리자만!
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/logs")
    @Audited(resource = "AUDIT_LOG", action = "SEARCH")
    public ResponseEntity<Page<AuditLogResponse>> searchAuditLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String result,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        AuditLogSearchCriteria criteria = AuditLogSearchCriteria.builder()
                .userId(userId)
                .patientId(patientId)
                .resourceType(resourceType)
                .action(action)
                .result(result)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        return ResponseEntity.ok(auditLogService.searchAuditLogs(criteria, pageable));
    }

    // 특정 환자에 대한 접근 이력
    @GetMapping("/patient/{patientId}/access-history")
    @Audited(resource = "AUDIT_LOG", action = "READ", patientIdParam = "patientId")
    public ResponseEntity<List<PatientAccessHistory>> getPatientAccessHistory(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(auditLogService.getPatientAccessHistory(patientId, days));
    }

    // 이상 접근 탐지 리포트
    @GetMapping("/reports/anomaly")
    @Audited(resource = "AUDIT_LOG", action = "READ")
    public ResponseEntity<AnomalyReport> getAnomalyReport(
            @RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(auditLogService.generateAnomalyReport(hours));
    }

    // 권한 거부 이력
    @GetMapping("/reports/access-denied")
    @Audited(resource = "AUDIT_LOG", action = "READ")
    public ResponseEntity<Page<AuditLogResponse>> getAccessDeniedLogs(
            @RequestParam(defaultValue = "7") int days,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(auditLogService.getAccessDeniedLogs(days, pageable));
    }
}
```

### 6.5 이상 접근 탐지

```java
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AnomalyReport generateAnomalyReport(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);

        List<AnomalyItem> anomalies = new ArrayList<>();

        // 1. 비정상 시간대 접근 (새벽 2-5시)
        List<AuditLog> afterHoursAccess = auditLogRepository.findAfterHoursAccess(since, 2, 5);
        anomalies.addAll(afterHoursAccess.stream()
            .map(log -> new AnomalyItem("AFTER_HOURS", log, "비정상 시간대 접근"))
            .toList());

        // 2. 대량 환자 정보 조회 (1시간 내 50명 이상)
        List<Object[]> bulkAccess = auditLogRepository.findBulkPatientAccess(since, 50);
        anomalies.addAll(bulkAccess.stream()
            .map(row -> new AnomalyItem("BULK_ACCESS",
                (Long) row[0], (Long) row[1],
                "대량 환자 정보 조회: " + row[1] + "건"))
            .toList());

        // 3. 권한 거부 빈발 (1시간 내 5회 이상)
        List<Object[]> frequentDenied = auditLogRepository.findFrequentAccessDenied(since, 5);
        anomalies.addAll(frequentDenied.stream()
            .map(row -> new AnomalyItem("FREQUENT_DENIED",
                (Long) row[0], (Long) row[1],
                "권한 거부 빈발: " + row[1] + "회"))
            .toList());

        return AnomalyReport.builder()
                .generatedAt(LocalDateTime.now())
                .periodHours(hours)
                .totalAnomalies(anomalies.size())
                .anomalies(anomalies)
                .build();
    }

    // 환자별 접근 이력
    public List<PatientAccessHistory> getPatientAccessHistory(Long patientId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);

        List<AuditLog> logs = auditLogRepository
            .findByPatientIdAndCreatedAtAfterOrderByCreatedAtDesc(patientId, since);

        return logs.stream()
            .map(log -> PatientAccessHistory.builder()
                    .accessedAt(log.getCreatedAt())
                    .userId(log.getUserId())
                    .username(log.getUsername())
                    .userRole(log.getUserRole())
                    .department(log.getUserDepartment())
                    .action(log.getAction())
                    .resourceType(log.getResourceType())
                    .requestIp(log.getRequestIp())
                    .result(log.getResult())
                    .build())
            .toList();
    }
}
```

### 6.6 감사 로그 보존 정책

```java
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class AuditLogRetentionConfig {

    private final AuditLogRepository auditLogRepository;

    @Value("${audit.retention.years:5}")
    private int retentionYears;  // 의료법상 최소 5년

    // 매월 1일 새벽 3시에 실행
    @Scheduled(cron = "0 0 3 1 * *")
    public void archiveOldAuditLogs() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusYears(retentionYears);

        log.info("감사 로그 아카이빙 시작: {} 이전 데이터", cutoffDate);

        // 아카이브 테이블로 이동 (삭제하지 않음!)
        int archivedCount = auditLogRepository.archiveOldLogs(cutoffDate);

        log.info("감사 로그 아카이빙 완료: {}건", archivedCount);
    }
}
```
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
