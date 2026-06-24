package com.example.sram.event;

/**
 * 출입 신청이 등록되었음을 알리는 이벤트.
 * 메일 본문에 쓸 값은 모두 평문(연락처 포함)으로 담는다.
 */
public class AccessRequestedEvent {

    private final String empName;
    private final String empNo;
    private final String phone;       // 평문 연락처
    private final String reason;
    private final String accessTime;  // "yyyy-MM-dd HH:mm"

    public AccessRequestedEvent(String empName, String empNo, String phone, String reason, String accessTime) {
        this.empName = empName;
        this.empNo = empNo;
        this.phone = phone;
        this.reason = reason;
        this.accessTime = accessTime;
    }

    public String getEmpName() { return empName; }
    public String getEmpNo() { return empNo; }
    public String getPhone() { return phone; }
    public String getReason() { return reason; }
    public String getAccessTime() { return accessTime; }
}
