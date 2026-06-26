package com.example.sram.config;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 대시보드(/admin) 및 관리자 전용 API 접근 제어.
 * 허용 사번 목록({@code app.admin.emp-nos})에 포함된 사용자만 통과시킨다.
 *
 * <p>현재 ibiz/그룹웨어 SSO 연동 전이므로 사번 출처는 다음 순서로 해석한다:
 * <ol>
 *   <li>세션 속성 {@code empNo}</li>
 *   <li>요청 파라미터 {@code empNo} (전달되면 세션에 저장)</li>
 * </ol>
 * 추후 ibiz 연동 시 {@link #resolveEmpNo(HttpServletRequest)} 만
 * 헤더/토큰 기반으로 교체하면 된다.
 */
public class AdminAccessInterceptor implements HandlerInterceptor {

    /** 세션에 저장되는 현재 접속자 사번 키 */
    public static final String SESSION_EMP_NO = "empNo";

    private final AdminAccessProperties props;

    public AdminAccessInterceptor(AdminAccessProperties props) {
        this.props = props;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 출입 신청(POST /api/access)은 일반 사용자 기능이므로 통과
        if ("POST".equalsIgnoreCase(request.getMethod())
                && "/api/access".equals(request.getServletPath())) {
            return true;
        }

        String empNo = resolveEmpNo(request);
        if (props.isAllowed(empNo)) {
            return true;
        }

        return deny(request, response, empNo);
    }

    /** 현재 접속자의 사번 해석 (SSO 연동 전 임시 로직). */
    private String resolveEmpNo(HttpServletRequest request) {

        // ===================== [테스트용 임시 코드] =====================
        // ibiz 연동 전까지 현재 접속자 사번을 394092 로 강제 고정한다.
        // ▶ 실제 연동 시: 바로 아래 한 줄만 주석 처리(또는 삭제)하면
        //   원래 로직(세션 → empNo 파라미터)으로 되돌아간다.
        if (true) return "394092";
        // ===============================================================

        HttpSession session = request.getSession();

        Object sessionEmpNo = session.getAttribute(SESSION_EMP_NO);
        if (sessionEmpNo != null && !sessionEmpNo.toString().isBlank()) {
            return sessionEmpNo.toString();
        }

        String paramEmpNo = request.getParameter("empNo");
        if (paramEmpNo != null && !paramEmpNo.isBlank()) {
            session.setAttribute(SESSION_EMP_NO, paramEmpNo.trim());
            return paramEmpNo.trim();
        }

        return null;
    }

    /** 미인증/미허가 응답. API 경로면 JSON, 그 외엔 HTML 안내. */
    private boolean deny(HttpServletRequest request, HttpServletResponse response, String empNo)
            throws Exception {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);   // 403
        response.setCharacterEncoding("UTF-8");

        String detail = (empNo == null || empNo.isBlank())
                ? "사번이 확인되지 않았습니다."
                : "사번 " + empNo + " 은(는) 대시보드 접근 권한이 없습니다.";

        if (request.getServletPath().startsWith("/api/")) {
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write(
                    "{\"success\":false,\"message\":\"" + escapeJson(detail) + "\"}");
        } else {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(
                    "<!doctype html><html lang='ko'><head><meta charset='utf-8'>"
                    + "<title>접근 권한 없음</title></head>"
                    + "<body style=\"font-family:system-ui,'Segoe UI',sans-serif;text-align:center;"
                    + "padding:90px 20px;color:#43423d;\">"
                    + "<div style='font-size:56px;font-weight:800;color:#d05;'>403</div>"
                    + "<p style='font-size:18px;margin:14px 0;'>대시보드 접근 권한이 없습니다.</p>"
                    + "<p style='color:#9a988f;font-size:14px;'>" + escapeHtml(detail) + "</p>"
                    + "<p style='margin-top:24px;'><a href='/' "
                    + "style='color:#06c;text-decoration:none;'>← 출입 신청 화면으로</a></p>"
                    + "</body></html>");
        }
        return false;
    }

    private String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
