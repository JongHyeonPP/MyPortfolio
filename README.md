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
# 프로젝트 클론
git clone https://github.com/your/medicore-backend.git
cd medicore-backend

# 실행
docker-compose up -d
```

API 문서: http://localhost:8080/swagger-ui.html

## 문서

- [전체 가이드](docs/guide/hospital-emr-portfolio-complete-guide.md)
- [시스템 아키텍처](docs/architecture/system-overview.md)
- [데이터베이스 설계](docs/database/erd.md)
- [보안 설계 (RBAC)](docs/security/rbac-audit.md)
- [API 명세서](docs/api/api-integration.md)
- [면접 대비](docs/interview-prep/expected-questions.md)

## 테스트 계정

| 역할 | ID | PW | 권한 |
|-----|----|----|-----|
| 의사 | doctor001 | test1234! | 진료/처방/검사 |
| 간호사 | nurse001 | test1234! | 조회/환자관리 |
| 행정 | admin001 | test1234! | 원무/예약 |
| 전산 | itadmin001 | test1234! | 시스템관리 |
