package com.example.sram.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 대시보드(/admin) 접근 허용 사번 목록.
 * application.yml 의 {@code app.admin.emp-nos} 로 관리한다.
 */
@Component
@ConfigurationProperties(prefix = "app.admin")
public class AdminAccessProperties {

    /** 대시보드 접근이 허용된 사번 목록 */
    private List<String> empNos = new ArrayList<>();

    public List<String> getEmpNos() {
        return empNos;
    }

    public void setEmpNos(List<String> empNos) {
        this.empNos = empNos;
    }

    /** 해당 사번이 허용 목록에 있는지 */
    public boolean isAllowed(String empNo) {
        return empNo != null && empNos.contains(empNo.trim());
    }
}
