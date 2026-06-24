package com.example.sram.dto;

/**
 * 대시보드 KPI.
 */
public class DashboardStats {

    private long todayCount;       // 오늘 출입 건수
    private long todayDiff;        // 어제 대비 증감
    private long totalPeople;      // 누적 출입 인원(총 건수)

    public long getTodayCount() { return todayCount; }
    public void setTodayCount(long todayCount) { this.todayCount = todayCount; }

    public long getTodayDiff() { return todayDiff; }
    public void setTodayDiff(long todayDiff) { this.todayDiff = todayDiff; }

    public long getTotalPeople() { return totalPeople; }
    public void setTotalPeople(long totalPeople) { this.totalPeople = totalPeople; }
}
