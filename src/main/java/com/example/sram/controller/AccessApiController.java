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
}
