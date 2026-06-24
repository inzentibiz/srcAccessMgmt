package com.example.sram.controller;

import com.example.sram.dto.AccessSearch;
import com.example.sram.service.AccessLogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * JSP 화면 렌더링 컨트롤러.
 *   /admin -> 관리자 대시보드
 *   /      -> 사용자 출입 신청
 */
@Controller
public class PageController {

    private final AccessLogService service;

    public PageController(AccessLogService service) {
        this.service = service;
    }

    /** 사용자 - 출입 신청 화면 */
    @GetMapping("/")
    public String userPage(Model model) {
        model.addAttribute("logs", service.todayLogs());
        model.addAttribute("stats", service.stats());
        return "user";   // /WEB-INF/views/user.jsp
    }

    /** 관리자 - 대시보드 화면 */
    @GetMapping("/admin")
    public String adminPage(@ModelAttribute AccessSearch search, Model model) {
        model.addAttribute("logs", service.search(search));
        model.addAttribute("stats", service.stats());
        model.addAttribute("search", search);
        return "admin";  // /WEB-INF/views/admin.jsp
    }
}
