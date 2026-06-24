package com.example.sram.service;

import com.example.sram.event.AccessRequestedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 출입 신청 메일 발송 리스너.
 *   - 트랜잭션 커밋 이후(AFTER_COMMIT)에만 발송 → 롤백 시 "유령 메일" 방지
 *   - 별도 스레드(@Async)에서 발송 → 신청 응답 지연 방지
 */
@Component
public class AccessMailListener {

    private final MailService mailService;

    public AccessMailListener(MailService mailService) {
        this.mailService = mailService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAccessRequested(AccessRequestedEvent e) {
        mailService.sendAccessRequest(
                e.getEmpName(), e.getEmpNo(), e.getPhone(), e.getReason(), e.getAccessTime());
    }
}
