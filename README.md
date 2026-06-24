# 서버실 출입관리 시스템 (Server Room Access Management)

목업(`서버실 출입관리 (통합).html`)을 기반으로 구현한 풀스택 웹 애플리케이션입니다.

- **Backend**: Spring Boot 2.7 + MyBatis (JDK 11)
- **View**: JSP (JSTL)
- **Frontend**: Vanilla JS (fetch API)
- **DB**: PostgreSQL
- 단일 서버실 운영 (서버실 구분/입퇴실 구분 없음)

## 화면

| 경로 | 화면 | 설명 |
|------|------|------|
| `/`      | 사용자 - 출입 신청 | 신청 폼 + 오늘 출입 기록 |
| `/admin` | 관리자 - 대시보드   | KPI 카드 + 기간/검색 필터 + 출입 기록 테이블 |

## 사전 준비

- JDK **11** (IntelliJ Project SDK 를 11 로 지정)
- Maven 3.6+ (또는 IDE 내장 Maven)
- PostgreSQL 14+

## DB 초기화

```bash
# 1) DB 생성
psql -U postgres -c "CREATE DATABASE sram WITH ENCODING 'UTF8';"

# 2) 스키마 + 샘플 데이터
psql -U postgres -d sram -f db/schema.sql
psql -U postgres -d sram -f db/sample_data.sql
```

접속 정보는 `src/main/resources/application.yml`의 `spring.datasource`에서 수정합니다
(기본: `localhost:5432/sram`, `postgres` / `postgres`).

## 실행

```bash
mvn spring-boot:run
```

브라우저에서:
- 사용자: http://localhost:8080/
- 관리자: http://localhost:8080/admin

## REST API

| Method | URL | 설명 |
|--------|-----|------|
| GET  | `/api/access` | 출입 기록 검색 (`startDate`, `endDate`, `keyword`) |
| GET  | `/api/access/today` | 오늘 출입 기록 |
| GET  | `/api/access/stats` | 대시보드 KPI |
| POST | `/api/access` | 출입 신청(등록) — JSON 바디 |

### 출입 신청 예시

```bash
curl -X POST http://localhost:8080/api/access \
  -H "Content-Type: application/json" \
  -d '{"empName":"홍길동","empNo":"20210815","phone":"010-2345-6789","reason":"정기 점검"}'
```

## 프로젝트 구조

```
src/main/
├── java/com/example/sram/
│   ├── SramApplication.java          # 진입점 (@MapperScan)
│   ├── controller/
│   │   ├── PageController.java        # JSP 화면 (/ , /admin)
│   │   ├── AccessApiController.java   # REST API (/api/access)
│   │   └── ApiExceptionHandler.java   # 검증/예외 -> JSON
│   ├── service/AccessLogService.java  # 비즈니스 로직
│   ├── mapper/AccessLogMapper.java    # MyBatis 매퍼 인터페이스
│   └── dto/                           # AccessLog, AccessLogRequest, AccessSearch, DashboardStats
├── resources/
│   ├── application.yml
│   ├── mapper/AccessLogMapper.xml     # SQL
│   └── static/js/                     # clock.js, admin.js, user.js
└── webapp/WEB-INF/views/              # admin.jsp, user.jsp

db/
├── schema.sql                         # 테이블 + 인덱스
└── sample_data.sql                    # 목업 기준 샘플 데이터
```

## 데이터 모델 (access_log)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | bigint (identity) | PK |
| emp_name | varchar(50) | 성명 |
| emp_no | varchar(20) | 사번 |
| phone | varchar(20) | 연락처 |
| reason | varchar(200) | 출입 사유 |
| access_time | timestamp | 출입 시각 |
| created_at | timestamp | 생성 시각 |
