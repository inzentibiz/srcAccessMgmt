package com.example.sram.service;

import com.example.sram.dto.*;
import com.example.sram.event.AccessRequestedEvent;
import com.example.sram.mapper.AccessLogMapper;
import com.example.sram.mapper.ConfigMapper;
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

    /** 출입 비밀번호 설정 키 / 기본값 */
    private static final String KEY_ACCESS_PWD = "access_password";
    private static final String DEFAULT_ACCESS_PWD = "7325*";

    /** 공지사항 설정 키 */
    private static final String KEY_NOTICE = "notice";

    private final AccessLogMapper mapper;
    private final ConfigMapper configMapper;
    private final CryptoService crypto;
    private final ApplicationEventPublisher eventPublisher;

    public AccessLogService(AccessLogMapper mapper, ConfigMapper configMapper, CryptoService crypto,
                            ApplicationEventPublisher eventPublisher) {
        this.mapper = mapper;
        this.configMapper = configMapper;
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

    /** 현재 서버실 출입 비밀번호 (DB 설정값, 없으면 기본값) */
    public String getAccessPassword() {
        String v = configMapper.selectValue(KEY_ACCESS_PWD);
        return (v == null || v.trim().isEmpty()) ? DEFAULT_ACCESS_PWD : v;
    }

    /** 출입 신청 완료 시 안내할 서버실 출입 비밀번호 */
    public String generateAccessPassword() {
        return getAccessPassword();
    }

    /** 출입 비밀번호 마지막 변경 시각 (없으면 null) */
    public LocalDateTime getAccessPasswordUpdatedAt() {
        return configMapper.selectUpdatedAt(KEY_ACCESS_PWD);
    }

    /** 공지사항 조회 (없으면 빈 문자열) */
    public String getNotice() {
        String v = configMapper.selectValue(KEY_NOTICE);
        return v == null ? "" : v;
    }

    /** 공지사항 저장 (빈 값이면 공지 해제) */
    @Transactional
    public void updateNotice(String notice) {
        String v = (notice == null) ? "" : notice.trim();
        if (v.length() > 200) v = v.substring(0, 200);
        configMapper.upsert(KEY_NOTICE, v);
    }

    /** 서버실 출입 비밀번호 변경 (관리자) */
    @Transactional
    public void updateAccessPassword(String newPwd) {
        configMapper.upsert(KEY_ACCESS_PWD, newPwd.trim());
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
