---
name: srv-access-mgmt-completion
description: 서버실 출입관리 시스템(SRAM) PDCA 사이클 완료 현황 및 후속 조치
metadata:
  type: project
---

## PDCA 사이클 완료 현황

**프로젝트**: 서버실 출입관리 시스템(SRAM)  
**완료일**: 2026-06-24  
**스택**: Spring Boot 2.7 / Java 11 / PostgreSQL / MyBatis / JSP  
**포트**: 9090 (dev 프로파일 기본)

## 구현 완료 기능

1. **내장 Tomcat 연결** — 포트 9090, dev/local 프로파일 분리
2. **출입 신청 폼 + 모달** — 신청 완료 시 비밀번호(7325*) 모달 표시
3. **연락처 자동 하이픈 변환** — JS 클라이언트에서 입력 시 010-1234-5678 형식
4. **연락처 AES-256-GCM 암호화** — 저장형식 `enc:Base64(IV||암호문+태그)`, DB 스키마 마이그레이션 완료
5. **메일 자동 발송** — wayne9044@inzent.com, SMTP smtp.inzent.com:25 (무인증 릴레이)
6. **로고 변경** — S 텍스트 → SVG 자물쇠 아이콘

## 개선 조치 완료 (3건, 품질점수 62→71)

| ID | 문제 | 해결 방법 | 검증 |
|----|------|---------|------|
| **B1+P1** | 메일 커밋 전 발송 + 동기 지연 | @TransactionalEventListener(AFTER_COMMIT) + @Async | 261ms 즉시 반환, 커밋 후 발송 |
| **B2** | DTO mutate (입력 변형) | 복사본 객체로 암호화, 원본은 평문 사용 | 컨트롤러 백업 제거 |
| **S7** | JSP XSS (미escaping) | `<c:out>` 적용 (user.jsp, admin.jsp) | 입출력값 escaping 적용 |

## 코드리뷰 현황 (bkit code-analyzer)

**15건 이슈 식별** → **3건 해결** (Critical 4→1, Major 6→3)

### 남은 Critical 이슈 (운영 배포용)

| 이슈 | 영향도 | 권장 조치 시점 |
|-----|--------|----------------|
| **S1** 무인증 노출 (`/admin`, `/api/access`) | 높음 | 배포 직전 (Spring Security 또는 커스텀 필터) |
| **S2** 출입비번 하드코딩 + 무인증 평문 반환 | 높음 | 배포 직전 (환경변수 + 별도 채널 전달) |
| **S3** DB 비밀번호 평문 커밋 | 높음 | 배포 직전 (.env 외부화) |

## 기술 결정 사항

**Why**: 
- 메일을 트랜잭션 커밋 후에 비동기로 발송 → 롤백 시 유령 메일 방지, 신청 응답 단축
- DTO 복사본으로 암호화 → 순서 변경 시 안전성 보장
- JSP `<c:out>` 적용 → 저장형 XSS 방지

**How to apply**: 
- 트랜잭션 기반 작업 후 알림/메일을 @TransactionalEventListener + @Async 패턴으로 처리
- 입력 DTO 변형이 필요할 때는 원본 보존 원칙 준수
- JSP/HTML 출력은 항상 escaping 의무화

## 관련 산출물

- **PDCA 보고서**: `docs/04-report/Srv_Access_Mgmt.report.md`
- **코드리뷰 분석**: `docs/03-analysis/code-review-2026-06-24.md`
- **신규 파일**: AccessRequestedEvent.java, AccessMailListener.java, CryptoService.java, MailService.java
- **수정 파일**: SramApplication.java (@EnableAsync), AccessLogService.java (이벤트 발행), user.jsp/admin.jsp (`<c:out>`)

## 배포 체크리스트 (운영 전 필수)

- [ ] S1: Spring Security 또는 커스텀 인증 필터 (세션/JWT)
- [ ] S2: 출입비번 환경변수화 + SMS/카톡 별도 채널
- [ ] S3: DB 비밀번호 `.env` 파일로 외부화

## 성능 지표

| 항목 | 값 |
|------|-----|
| 출입 신청 응답 | 261ms (메일 비동기 처리) |
| 메일 발송 스레드 | task-1 (AFTER_COMMIT) |
| 암호화 형식 | AES-256-GCM (IV 12B, 태그 128bit) |
| DB 마이그레이션 | access_log.phone VARCHAR(20)→VARCHAR(255) |
