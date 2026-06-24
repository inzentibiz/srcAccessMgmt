-- =====================================================================
-- 서버실 출입관리 시스템 (Server Room Access Management) - PostgreSQL Schema
-- =====================================================================
-- DB 생성 (psql 슈퍼유저로 1회 실행 / SQL 클라이언트는 Auto-commit 상태에서)
--   CREATE DATABASE sram WITH ENCODING 'UTF8';
--   \c sram
-- =====================================================================
-- 단일 서버실 운영 — 서버실 구분(코드/명) 없이 출입 기록만 관리
-- =====================================================================

-- ---------------------------------------------------------------------
-- 출입 기록 (핵심 테이블)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS access_log (
    id           BIGINT        GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    emp_name     VARCHAR(50)   NOT NULL,           -- 성명
    emp_no       VARCHAR(20)   NOT NULL,           -- 사번
    phone        VARCHAR(255),                     -- 연락처 (AES-GCM 암호문 저장: "enc:"+Base64)
    reason       VARCHAR(200)  NOT NULL,           -- 출입 사유
    access_time  TIMESTAMP     NOT NULL DEFAULT now(),    -- 출입(신청) 시각
    created_at   TIMESTAMP     NOT NULL DEFAULT now()
);

-- 조회 성능 인덱스
CREATE INDEX IF NOT EXISTS idx_access_log_time      ON access_log (access_time DESC);
CREATE INDEX IF NOT EXISTS idx_access_log_emp_no    ON access_log (emp_no);
CREATE INDEX IF NOT EXISTS idx_access_log_emp_name  ON access_log (emp_name);
