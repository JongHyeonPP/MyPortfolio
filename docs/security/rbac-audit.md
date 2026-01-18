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
