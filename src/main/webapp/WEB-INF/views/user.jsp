<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>서버실 출입관리 - 출입 신청</title>
  <style>
    *{ -webkit-font-smoothing:antialiased; box-sizing:border-box; margin:0; padding:0; }
    body{ background:#f3f2ee; font-family:-apple-system,BlinkMacSystemFont,'Pretendard',sans-serif; }
    @keyframes scpulse{ 0%,100%{ opacity:1; transform:scale(1);} 50%{ opacity:.35; transform:scale(.8);} }
    input,select,textarea{ font-family:inherit; }
    .field{ width:100%;padding:12px 14px;border:1px solid #e2e0db;border-radius:11px;font-size:15px;color:#1f1f1d;outline:none; }
    .field:focus{ border-color:oklch(0.5 0.13 255); }
  </style>
</head>
<body>
<div style="min-height:100vh;background:#f3f2ee;">

  <!-- Top bar -->
  <div style="display:flex;justify-content:space-between;align-items:center;padding:20px 44px;background:oklch(0.22 0.03 255);position:sticky;top:0;z-index:5;">
    <div style="display:flex;align-items:center;gap:30px;">
      <div style="display:flex;align-items:center;gap:11px;">
        <div style="width:34px;height:34px;border-radius:9px;background:oklch(0.5 0.13 255);display:flex;align-items:center;justify-content:center;">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
        </div>
        <div style="font-size:18px;font-weight:800;color:#fff;">서버실 출입관리</div>
      </div>
      <div style="display:flex;gap:4px;">
        <div style="font-size:14px;font-weight:700;color:#fff;background:oklch(0.3 0.04 255);padding:8px 16px;border-radius:9px;">출입 신청</div>
        <a href="/admin" style="text-decoration:none;font-size:14px;font-weight:600;color:oklch(0.72 0.04 255);padding:8px 16px;border-radius:9px;">대시보드</a>
      </div>
    </div>
    <div style="display:flex;align-items:center;gap:18px;">
      <div style="display:flex;align-items:center;gap:9px;color:#fff;">
        <span style="width:8px;height:8px;border-radius:50%;background:oklch(0.72 0.15 145);animation:scpulse 1.6s infinite;"></span>
        <span id="dateStr" style="font-size:14px;color:oklch(0.8 0.03 255);"></span>
        <span id="timeStr" style="font-size:18px;font-weight:800;font-variant-numeric:tabular-nums;color:#fff;"></span>
      </div>
    </div>
  </div>

  <div style="max-width:760px;margin:0 auto;padding:36px 28px 60px;">

    <!-- 1. 출입 신청 -->
    <div style="margin-bottom:14px;">
      <div style="font-size:22px;font-weight:800;color:#1f1f1d;">출입 신청</div>
      <div style="font-size:14px;color:#8a8880;margin-top:5px;">정보를 입력하고 신청하면 출입 기록이 등록됩니다. 신청 시각은 자동으로 기록됩니다.</div>
    </div>

    <form id="accessForm" style="background:#fff;border:1px solid #eae8e3;border-radius:18px;padding:28px 30px;margin-bottom:40px;">
      <div style="display:grid;grid-template-columns:1fr 1fr;gap:18px;">
        <div>
          <label style="font-size:13px;font-weight:700;color:#6b6a66;margin-bottom:7px;display:block;">성명 <span style="color:#d05">*</span></label>
          <input class="field" name="empName" required placeholder="홍길동">
        </div>
        <div>
          <label style="font-size:13px;font-weight:700;color:#6b6a66;margin-bottom:7px;display:block;">사번 <span style="color:#d05">*</span></label>
          <input class="field" name="empNo" required placeholder="20210815">
        </div>
        <div>
          <label style="font-size:13px;font-weight:700;color:#6b6a66;margin-bottom:7px;display:block;">연락처</label>
          <input class="field" name="phone" placeholder="010-2345-6789">
        </div>
        <div>
          <label style="font-size:13px;font-weight:700;color:#6b6a66;margin-bottom:7px;display:block;">신청 시각</label>
          <input class="field" id="reqTime" readonly
                 style="background:#faf9f6;border-color:#eee;color:#7a7973;font-variant-numeric:tabular-nums;">
        </div>
        <div style="grid-column:1 / span 2;">
          <label style="font-size:13px;font-weight:700;color:#6b6a66;margin-bottom:7px;display:block;">출입 사유 <span style="color:#d05">*</span></label>
          <input class="field" name="reason" required placeholder="예) 정기 점검, 장비 교체, 네트워크 작업">
        </div>
      </div>
      <button type="submit" id="submitBtn"
              style="width:100%;background:oklch(0.5 0.13 255);color:#fff;text-align:center;font-size:16px;font-weight:800;padding:15px;border:none;border-radius:13px;margin-top:24px;cursor:pointer;box-shadow:0 2px 8px oklch(0.5 0.13 255 / 0.35);">
        출입 신청하기
      </button>
    </form>

    <!-- 2. 오늘 출입 기록 -->
    <div style="display:flex;justify-content:space-between;align-items:flex-end;margin-bottom:14px;">
      <div>
        <div style="font-size:22px;font-weight:800;color:#1f1f1d;">오늘 출입 기록</div>
        <div id="dateStr2" style="font-size:14px;color:#8a8880;margin-top:5px;"></div>
      </div>
      <div style="text-align:right;">
        <div style="font-size:13px;color:#8a8880;font-weight:600;">오늘 출입 건수</div>
        <div style="font-size:30px;font-weight:800;color:oklch(0.5 0.13 255);line-height:1.1;">
          <span id="todayCount">${stats.todayCount}</span><span style="font-size:15px;color:#8a8880;font-weight:600;">건</span>
        </div>
      </div>
    </div>

    <div style="background:#fff;border:1px solid #eae8e3;border-radius:16px;overflow:hidden;">
      <div style="display:grid;grid-template-columns:90px 1.1fr 1.1fr 1.6fr;background:#faf9f6;padding:13px 22px;font-size:12px;font-weight:800;color:#9a988f;">
        <div>시각</div><div>성명</div><div>사번</div><div>사유</div>
      </div>
      <div id="todayBody">
        <c:forEach var="row" items="${logs}">
          <div style="display:grid;grid-template-columns:90px 1.1fr 1.1fr 1.6fr;align-items:center;padding:15px 22px;border-top:1px solid #f2f1ec;font-size:14px;color:#43423d;font-variant-numeric:tabular-nums;">
            <div>${row.accessTimeShort}</div>
            <div><c:out value="${row.empName}"/></div>
            <div><c:out value="${row.empNo}"/></div>
            <div style="color:#7a7973;"><c:out value="${row.reason}"/></div>
          </div>
        </c:forEach>
        <c:if test="${empty logs}">
          <div style="padding:40px;text-align:center;color:#9a988f;font-size:14px;">오늘 출입 기록이 없습니다.</div>
        </c:if>
      </div>
    </div>

  </div>
</div>

<!-- 비밀번호 안내 알림 (출입 신청 완료 시 표시) -->
<div id="pwModal" style="display:none;position:fixed;inset:0;z-index:50;background:rgba(20,24,38,0.55);align-items:center;justify-content:center;padding:24px;">
  <div style="background:#fff;border-radius:20px;padding:36px 40px;max-width:420px;width:100%;box-shadow:0 20px 60px rgba(0,0,0,0.3);text-align:center;">
    <div style="width:56px;height:56px;border-radius:50%;background:oklch(0.95 0.04 145);display:flex;align-items:center;justify-content:center;margin:0 auto 18px;font-size:28px;color:oklch(0.5 0.13 145);">✓</div>
    <div style="font-size:19px;font-weight:800;color:#1f1f1d;margin-bottom:10px;">출입 신청이 완료되었습니다</div>
    <div style="font-size:14px;color:#7a7973;line-height:1.5;">서버실 출입 비밀번호는 아래와 같습니다.</div>
    <div id="pwValue" style="margin:20px 0 24px;background:oklch(0.97 0.02 255);border:1px solid oklch(0.9 0.04 255);border-radius:14px;padding:20px;font-size:40px;font-weight:800;letter-spacing:0.12em;color:oklch(0.45 0.13 255);font-variant-numeric:tabular-nums;"></div>
    <button type="button" id="pwCloseBtn" style="width:100%;cursor:pointer;background:oklch(0.5 0.13 255);color:#fff;font-size:15px;font-weight:800;padding:13px;border:none;border-radius:12px;">확인</button>
  </div>
</div>

<script src="/js/clock.js"></script>
<script src="/js/user.js"></script>
</body>
</html>
