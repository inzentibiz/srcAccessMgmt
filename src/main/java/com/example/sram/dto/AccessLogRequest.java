package com.example.sram.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 출입 신청(등록) 요청 바디.
 */
public class AccessLogRequest {

    /** 등록 후 채워지는 생성 PK (useGeneratedKeys) */
    private Long id;

    @NotBlank(message = "성명은 필수입니다.")
    private String empName;

    @NotBlank(message = "사번은 필수입니다.")
    private String empNo;

    @Pattern(regexp = "^$|^01[016-9]-?\\d{3,4}-?\\d{4}$", message = "연락처 형식이 올바르지 않습니다.")
    private String phone;

    @NotBlank(message = "출입 사유는 필수입니다.")
    private String reason;

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
}
