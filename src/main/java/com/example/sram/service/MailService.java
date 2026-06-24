package com.example.sram.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

/**
 * 출입 신청 메일 발송 (iBiz MailHandler 방식 참고: JavaMailSender + MimeMessageHelper, HTML).
 */
@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    /** 메일 제목 (고정) */
    private static final String SUBJECT = "[서버실 출입관리 시스템] 서버실 출입 신청이 있습니다.";

    private final JavaMailSender mailSender;

    /** 수신자 (요구사항: 1명) */
    @Value("${app.mail.to:wayne9044@inzent.com}")
    private String to;

    /** 발신자 주소 */
    @Value("${app.mail.from:iBiz@inzent.com}")
    private String from;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 출입 신청 알림 메일 발송.
     * 발송 실패가 출입 신청 자체를 막지 않도록 예외는 로깅만 하고 삼킨다.
     */
    public void sendAccessRequest(String empName, String empNo, String phone, String reason, String accessTime) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from, "서버실 출입관리");
            helper.setTo(to);
            helper.setSubject(SUBJECT);
            helper.setText(buildHtml(empName, empNo, phone, reason, accessTime), true);
            mailSender.send(message);
            log.info("출입 신청 메일 발송 완료 -> {} (사번 {})", to, empNo);
        } catch (Exception e) {
            // 메일 실패는 신청 처리에 영향 주지 않음
            log.error("출입 신청 메일 발송 실패 (사번 {}): {}", empNo, e.toString(), e);
        }
    }

    private String buildHtml(String empName, String empNo, String phone, String reason, String accessTime) {
        String s = nz(empName), n = nz(empNo), p = nz(phone), r = nz(reason), t = nz(accessTime);
        return "<div style=\"font-family:'Malgun Gothic',sans-serif;font-size:14px;color:#222;line-height:1.7;\">"
                + "<p>서버실 출입 신청이 접수되었습니다.</p>"
                + "<table style=\"border-collapse:collapse;font-size:14px;\">"
                + row("성명", s)
                + row("사번", n)
                + row("연락처", p)
                + row("사유", r)
                + row("신청시각", t)
                + "</table>"
                + "</div>";
    }

    private String row(String label, String value) {
        return "<tr>"
                + "<th style=\"text-align:left;background:#f5f5f5;border:1px solid #ddd;padding:6px 14px;\">" + label + "</th>"
                + "<td style=\"border:1px solid #ddd;padding:6px 14px;\">" + esc(value) + "</td>"
                + "</tr>";
    }

    private static String nz(String s) { return s == null ? "" : s; }

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}