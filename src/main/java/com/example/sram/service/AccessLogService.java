package com.example.sram.service;

import com.example.sram.dto.*;
import com.example.sram.event.AccessRequestedEvent;
import com.example.sram.mapper.AccessLogMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 출입관리 비즈니스 로직.
 */
@Service
public class AccessLogService {

    private static final DateTimeFormatter MAIL_TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final AccessLogMapper mapper;
    private final CryptoService crypto;
    private final ApplicationEventPublisher eventPublisher;

    public AccessLogService(AccessLogMapper mapper, CryptoService crypto,
                            ApplicationEventPublisher eventPublisher) {
        this.mapper = mapper;
        this.crypto = crypto;
        this.eventPublisher = eventPublisher;
    }

    /** 관리자 대시보드 - 조건 검색 (연락처 복호화) */
    public List<AccessLog> search(AccessSearch search) {
        List<AccessLog> logs = mapper.findLogs(search);
        decryptPhones(logs);
        return logs;
    }

    /** 오늘 출입 기록 (연락처 복호화) */
    public List<AccessLog> todayLogs() {
        List<AccessLog> logs = mapper.findTodayLogs();
        decryptPhones(logs);
        return logs;
    }

    /** 출입 신청(등록) — 연락처 암호화 후 저장. 메일은 커밋 후 비동기 발송 */
    @Transactional
    public Long register(AccessLogRequest req) {
        String plainPhone = req.getPhone();

        // 입력 DTO를 변형하지 않고, 연락처를 암호화한 복사본으로 저장
        AccessLogRequest toInsert = new AccessLogRequest();
        toInsert.setEmpName(req.getEmpName());
        toInsert.setEmpNo(req.getEmpNo());
        toInsert.setPhone(crypto.encrypt(plainPhone));
        toInsert.setReason(req.getReason());
        mapper.insertLog(toInsert);

        // 커밋 후 메일 발송 (롤백 시 발송되지 않음). 본문엔 평문 연락처 사용
        String accessTime = LocalDateTime.now().format(MAIL_TS);
        eventPublisher.publishEvent(new AccessRequestedEvent(
                req.getEmpName(), req.getEmpNo(), plainPhone, req.getReason(), accessTime));

        // useGeneratedKeys 로 채워진 id 반환 (XML 의 keyProperty="id")
        return toInsert.getId();
    }

    /** 조회 결과의 연락처를 평문으로 복호화 */
    private void decryptPhones(List<AccessLog> logs) {
        for (AccessLog log : logs) {
            log.setPhone(crypto.decrypt(log.getPhone()));
        }
    }

    /** 서버실 출입 비밀번호 (고정값) */
    private static final String ACCESS_PASSWORD = "7325*";

    /** 출입 신청 완료 시 안내할 서버실 출입 비밀번호 */
    public String generateAccessPassword() {
        return ACCESS_PASSWORD;
    }

    /** 대시보드 KPI */
    public DashboardStats stats() {
        DashboardStats s = new DashboardStats();
        long today = mapper.countToday();
        long yesterday = mapper.countYesterday();
        s.setTodayCount(today);
        s.setTodayDiff(today - yesterday);
        s.setTotalPeople(mapper.countTotal());
        return s;
    }
}
