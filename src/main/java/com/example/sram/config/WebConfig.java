package com.example.sram.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 웹 MVC 설정 — 대시보드 접근 제어 인터셉터 등록.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AdminAccessProperties adminAccessProperties;

    public WebConfig(AdminAccessProperties adminAccessProperties) {
        this.adminAccessProperties = adminAccessProperties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminAccessInterceptor(adminAccessProperties))
                .addPathPatterns(
                        "/admin",                 // 관리자 대시보드 페이지
                        "/api/access",            // 전체 출입 기록 검색(GET) — POST(신청)는 인터셉터에서 통과
                        "/api/access/password",   // 출입 비밀번호 조회/변경
                        "/api/access/notice"      // 공지사항 변경
                );
    }
}
