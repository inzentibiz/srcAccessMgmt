package com.example.sram.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 출입 기록 1건 (access_log 테이블 매핑).
 */
public class AccessLog {

    private Long id;
    private String empName;        // 성명
    private String empNo;          // 사번
    private String phone;          // 연락처
    private String reason;         // 출입 사유
    private LocalDateTime accessTime;  // 출입 시각
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmpName() { return empName; }
    public void setEmpName(String empName) { this.empName = empName; }

    public String getEmpNo() { return empNo; }
    public void setEmpNo(String empNo) { this.empNo = empNo; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getAccessTime() { return accessTime; }
    public void setAccessTime(LocalDateTime accessTime) { this.accessTime = accessTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter T  = DateTimeFormatter.ofPattern("HH:mm");

    /** "2026-06-23 14:22" (관리자 화면) */
    public String getAccessTimeStr() {
        return accessTime == null ? "" : accessTime.format(DT);
    }

    /** "14:22" (사용자 화면) */
    public String getAccessTimeShort() {
        return accessTime == null ? "" : accessTime.format(T);
    }
}
