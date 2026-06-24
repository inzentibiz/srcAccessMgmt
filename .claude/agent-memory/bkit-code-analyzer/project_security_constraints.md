---
name: project-security-constraints
description: Known security-sensitive decisions and exposures in SRAM as of first review
metadata:
  type: project
---

As of 2026-06-23 first code review, these security-relevant facts held (verify before acting — may be fixed since):

- application.yml had plaintext DB passwords (dev: ibiz1229, local: postgres) committed.
- Crypto secret defaulted to `sram-dev-secret-change-me` via `${CRYPTO_SECRET:...}`; CryptoService derives AES key by plain SHA-256 of the secret (no salt/KDF).
- Server room access password was a hardcoded constant `7325*` in AccessLogService.generateAccessPassword(), returned to any caller of POST /api/access with no auth.
- No Spring Security on the project — /api/access, /admin all unauthenticated. Admin dashboard exposes decrypted phone numbers to any visitor.
- SMTP relay is internal IP-whitelisted, no auth (mail.smtp.auth: false) — by design per yml comment.

**Why:** these are deliberate-looking or load-bearing choices that shape severity judgements.
**How to apply:** when reviewing again, re-check whether these were remediated; don't re-flag as new if user already accepted a documented tradeoff, but DO confirm current state.
