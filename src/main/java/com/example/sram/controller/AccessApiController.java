package com.example.sram.controller;

import com.example.sram.dto.*;
import com.example.sram.service.AccessLogService;
import javax.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 출입관리 REST API (JS fetch 용).
 */
@RestController
@RequestMapping("/api/access")
public class AccessApiController {

    private final AccessLogService service;

    public AccessApiController(AccessLogService service) {
        this.service = service;
    }

    /** 출입 기록 검색 (관리자 대시보드 / 실시간 갱신) */
    @GetMapping
    public List<AccessLog> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword) {
        AccessSearch search = new AccessSearch();
        search.setStartDate(startDate);
        search.setEndDate(endDate);
        search.setKeyword(keyword);
        return service.search(search);
    }

    /** 오늘 출입 기록 */
    @GetMapping("/today")
    public List<AccessLog> today() {
        return service.todayLogs();
    }

    /** 대시보드 KPI */
    @GetMapping("/stats")
    public DashboardStats stats() {
        return service.stats();
    }

    /** 출입 신청(등록) — 메일은 커밋 후 비동기로 발송됨(AccessMailListener) */
    @PostMapping
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody AccessLogRequest req) {
        Long id = service.register(req);
        String accessPwd = service.generateAccessPassword();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("success", true, "id", id, "accessPwd", accessPwd,
                        "message", "출입 신청이 등록되었습니다."));
    }

    /** 현재 출입 비밀번호 조회 (관리자) — 마지막 변경일/경과일수 포함 */
    @GetMapping("/password")
    public Map<String, Object> getPassword() {
        Map<String, Object> res = new java.util.HashMap<>();
        res.put("password", service.getAccessPassword());
        java.time.LocalDateTime updatedAt = service.getAccessPasswordUpdatedAt();
        if (updatedAt != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(
                    updatedAt.toLocalDate(), java.time.LocalDate.now());
            res.put("updatedAt", updatedAt.format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            res.put("daysSince", days);
        }
        return res;
    }

    /** 출입 비밀번호 변경 (관리자) */
    @PostMapping("/password")
    public ResponseEntity<Map<String, Object>> updatePassword(@RequestBody Map<String, String> body) {
        String pwd = body.get("password");
        if (pwd == null || pwd.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "비밀번호를 입력하세요."));
        }
        service.updateAccessPassword(pwd);
        return ResponseEntity.ok(Map.of("success", true, "password", service.getAccessPassword(),
                "message", "출입 비밀번호가 변경되었습니다."));
    }

    /** 공지사항 변경 (관리자) — 빈 값이면 공지 해제 */
    @PostMapping("/notice")
    public ResponseEntity<Map<String, Object>> updateNotice(@RequestBody Map<String, String> body) {
        service.updateNotice(body.get("notice"));
        return ResponseEntity.ok(Map.of("success", true, "notice", service.getNotice(),
                "message", "공지사항이 저장되었습니다."));
    }
}
