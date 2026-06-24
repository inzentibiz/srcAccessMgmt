# Code Review Report — 서버실 출입관리 시스템 (SRAM)

- 분석일: 2026-06-24
- 대상: 이번 세션 개발분 (암호화/메일/출입신청)
- 도구: bkit code-analyzer (confidence ≥ 70% 만 보고)

## Summary
- 리뷰 파일: 11개 (CryptoService, MailService, AccessLogService, AccessApiController, application.yml, user.js, admin.js, user.jsp, admin.jsp, AccessLogMapper.xml, pom.xml)
- 이슈: 15건 — 🔴 Critical 4 / 🟡 Major 6 / 🟢 Minor 5
- **품질 점수: 62 / 100**
- 판정: 🔴 Critical 4건 — 배포 전 수정 권장 (특히 인증 부재·비밀번호 노출)

---

## 🔒 Security

### Critical
- **S1. 인증/인가 전무** (`AccessApiController.java`, `admin.jsp`) — `/admin`·`/api/access`·`/api/access/today` 무인증. 복호화된 평문 연락처가 전 직원에게 노출. → admin 화면/조회 API 인증 적용, 연락처 마스킹.
- **S2. 출입 비밀번호 하드코딩 + 무인증 API 평문 반환** (`AccessLogService.java:55`, `AccessApiController.java`) — `7325*`가 소스 커밋 + 신청만 하면 응답으로 획득. → 시크릿 외부화, 승인 후 별도 채널 전달.
- **S3. DB 비밀번호 평문 커밋** (`application.yml:53-54,68-69`) — `ibiz/ibiz1229` 등 평문. → `${DB_PASSWORD}` 환경변수화, `.env.example`만 커밋.

### Major
- **S4. 약한 키 파생** (`CryptoService.java:36-38`) — 솔트 없는 SHA-256 1회. → PBKDF2/HKDF, 기본 시크릿 부팅 거부, 길이 검증. (AES-GCM/IV/태그 구현 자체는 적정)
- **S5. 복호화 실패 silent 원본 반환** (`CryptoService.java:72-74`) — GCM 무결성 검증 효과 무력화. → `log.warn` + `(복호화 불가)` 플레이스홀더.
- **S6. 평문 연락처 메일 평문 전송** (`AccessApiController.java`, 무인증 SMTP 릴레이) — → 본문 마스킹 검토, STARTTLS.
- **S7. JSP `${}` 미escaping (저장형 XSS 잔존)** (`admin.jsp:101-104`, `user.jsp:104-106`) — JSP EL raw 출력. → `<c:out>` 또는 서버단 sanitize. (JS 렌더 경로는 esc() 적용되어 양호)

---

## 🐞 Bugs

### Critical
- **B1. 메일이 트랜잭션 커밋 전 발송 — 롤백 시 유령 메일** (`AccessApiController.java`, `AccessLogService.java`) — register 반환 후 커밋 단계 롤백 시 신청 미저장인데 메일은 발송. → `@TransactionalEventListener(AFTER_COMMIT)`.

### Major
- **B2. register가 입력 DTO를 제자리 변형(mutate)** (`AccessLogService.java:41`) — 컨트롤러가 평문을 미리 백업해야만 동작, 순서 바뀌면 암호문이 메일로. → register가 입력 변형하지 않도록 리팩터.
- **B3. 신청자 신원 미검증** (`AccessLogService`, `AccessApiController`) — 임의 사번으로 기록 생성 가능. → 사번-성명 매칭/인증 컨텍스트.

### Minor
- **B4. 평문/암호문 혼용 영구화 가능** (`CryptoService.java:46`) — 신규는 항상 암호화 강제 권장.

---

## ⚙️ Performance
- **P1. (Major) 메일 동기 발송이 응답 지연** (`AccessApiController.java`, 타임아웃 5초) — → `@Async` + 커밋 후 발송.
- **P2. (Minor) 조회 시 N건 순차 복호화 + 페이징 부재** (`AccessLogService.java:48-52`, `AccessLogMapper.xml`) — → LIMIT/페이지네이션.
- **P3. (Minor) KPI 30초 폴링 + 카운트 쿼리 3개** (`admin.js:70`) — → 단일 집계 쿼리.

---

## 🧹 Quality
- **Q1. (Minor) `esc()` 3곳 중복** (`user.js`, `admin.js`, `MailService.java`) — → 공통 util / `HtmlUtils.htmlEscape`.
- **Q2. (Minor) 메일 HTML 자바 문자열 하드코딩** (`MailService.java:57-76`) — → 템플릿 엔진.
- **Q3. (Minor) 응답 포맷 비표준 `Map.of`** (`AccessApiController.java:77`) — → 공통 ApiResponse DTO.

---

## 권장사항 (우선순위)
1. 인증 도입 (S1) — 가장 시급, 연락처 마스킹
2. 출입 비밀번호 분리 (S2)
3. 시크릿 외부화 (S3, S4)
4. 메일을 커밋 후 비동기로 (B1 + P1)
5. JSP `<c:out>` 적용 (S7)
6. DTO mutate 제거 (B2)

## 잘된 점
- SQL injection: MyBatis `#{}` 바인딩 + 동적쿼리로 안전 (`AccessLogMapper.xml`)
- JS 렌더 경로 XSS escaping 적용
- AES-GCM 구현(랜덤 12B IV, 128bit 태그) 적정
- 메일 실패가 신청을 막지 않는 예외 처리 의도 양호
