package com.example.sram.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * 민감정보(연락처) 암호화 유틸.
 *   - AES-256-GCM (IV 12바이트 랜덤 + 인증태그 128비트)
 *   - 저장 형식: "enc:" + Base64(IV || ciphertext+tag)
 *   - "enc:" 접두어가 없는 값(기존 평문/빈 값)은 그대로 통과시켜 마이그레이션 부담 없이 혼용 가능.
 */
@Component
public class CryptoService {

    /** 암호문 식별 접두어 */
    private static final String MARKER = "enc:";
    private static final int IV_LENGTH = 12;
    private static final int TAG_BITS = 128;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final SecretKey key;

    public CryptoService(@Value("${app.crypto.secret:sram-dev-secret-change-me}") String secret) {
        try {
            // 임의 길이의 시크릿을 SHA-256 으로 32바이트 AES 키로 변환
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(secret.getBytes(StandardCharsets.UTF_8));
            this.key = new SecretKeySpec(hash, "AES");
        } catch (Exception e) {
            throw new IllegalStateException("암호화 키 초기화 실패", e);
        }
    }

    /** 평문 → "enc:..." 암호문. null/빈 문자열은 그대로 반환. */
    public String encrypt(String plain) {
        if (plain == null || plain.isEmpty()) return plain;
        try {
            byte[] iv = new byte[IV_LENGTH];
            RANDOM.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            byte[] ct = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            byte[] out = new byte[iv.length + ct.length];
            System.arraycopy(iv, 0, out, 0, iv.length);
            System.arraycopy(ct, 0, out, iv.length, ct.length);
            return MARKER + Base64.getEncoder().encodeToString(out);
        } catch (Exception e) {
            throw new IllegalStateException("연락처 암호화 실패", e);
        }
    }

    /** "enc:..." 암호문 → 평문. 접두어 없으면(기존 평문) 원본 그대로 반환. */
    public String decrypt(String stored) {
        if (stored == null || !stored.startsWith(MARKER)) return stored;
        try {
            byte[] data = Base64.getDecoder().decode(stored.substring(MARKER.length()));
            byte[] iv = Arrays.copyOfRange(data, 0, IV_LENGTH);
            byte[] ct = Arrays.copyOfRange(data, IV_LENGTH, data.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            return new String(cipher.doFinal(ct), StandardCharsets.UTF_8);
        } catch (Exception e) {
            // 복호화 불가(키 불일치/손상 등) 시 원본 반환 — 화면 깨짐 방지
            return stored;
        }
    }
}
