---
name: project-overview
description: Tech stack and structure of the SRAM (server room access management) project
metadata:
  type: project
---

Server Room Access Management (SRAM), artifactId `srv-access-mgmt`.

Stack: Spring Boot 2.7.18, Java 11, MyBatis, PostgreSQL, JSP (war packaging, embedded Tomcat Jasper). Server port 9090.

Key flow: user.jsp submits access request via /js/user.js fetch POST -> AccessApiController -> AccessLogService.register (encrypts phone via CryptoService, MyBatis insertLog) -> MailService sends synchronous SMTP notification. Admin dashboard (admin.jsp + admin.js) searches/lists logs; phone is decrypted in AccessLogService.

Contact phone is the sensitive field, encrypted at rest with AES-256-GCM (CryptoService, "enc:" marker prefix, plaintext passthrough for migration).

**Why:** captured during first code review 2026-06-23.
**How to apply:** use to orient future reviews; verify against current code before relying on specifics.
