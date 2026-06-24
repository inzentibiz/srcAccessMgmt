package com.example.sram.dto;

import java.time.LocalDate;

/**
 * 출입 기록 검색 조건 (관리자 대시보드 필터).
 */
public class AccessSearch {

    private LocalDate startDate;   // 기간 시작 (yyyy-MM-dd)
    private LocalDate endDate;     // 기간 종료
    private String keyword;        // 성명 또는 사번

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
}
