# 아카이브 인덱스 — 2026-06

| Feature | 단계 | 품질점수 | 아카이브일 | 문서 |
|---------|------|----------|------------|------|
| Srv_Access_Mgmt | archived | 71/100 (개선 후) | 2026-06-24 | [analysis](Srv_Access_Mgmt/Srv_Access_Mgmt.analysis.md) · [report](Srv_Access_Mgmt/Srv_Access_Mgmt.report.md) |

## Srv_Access_Mgmt — 서버실 출입관리 시스템
- 출입 신청 → 비밀번호 안내 → 메일 알림 전 과정 구현 (Spring Boot 2.7 / PostgreSQL / MyBatis / JSP)
- 연락처 AES-256-GCM 암호화, 출입 신청 메일 자동 발송(커밋 후 비동기)
- 코드리뷰 후 개선 3건 적용(메일 비동기화, DTO 무변형, JSP XSS), 품질 62→71점
- 운영 배포 전 잔여 Critical 3건: 인증 부재, 출입비번 분리, DB 비밀번호 외부화
