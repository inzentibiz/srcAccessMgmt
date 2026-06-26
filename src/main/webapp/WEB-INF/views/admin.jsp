<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
        <a href="/" style="text-decoration:none;font-size:14px;font-weight:600;color:oklch(0.72 0.04 255);padding:8px 16px;border-radius:9px;">출입 신청</a>
        <div style="font-size:14px;font-weight:700;color:#fff;background:oklch(0.3 0.04 255);padding:8px 16px;border-radius:9px;">대시보드</div>
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

  <div style="max-width:1320px;margin:0 auto;padding:32px 44px 56px;">

    <div style="margin-bottom:22px;">
      <div style="font-size:24px;font-weight:800;color:#1f1f1d;">출입 현황 대시보드</div>
      <div style="font-size:14px;color:#8a8880;margin-top:5px;">서버실 · 실시간 모니터링</div>
    </div>

    <!-- ⚙ 설정 (접기): 출입 비밀번호 · 공지사항 -->
    <div style="background:#fff;border:1px solid #eae8e3;border-radius:16px;margin-bottom:24px;overflow:hidden;">
      <div id="settingsToggle" style="display:flex;align-items:center;justify-content:space-between;padding:16px 24px;cursor:pointer;user-select:none;">
        <div style="display:flex;align-items:center;gap:10px;">
          <span style="font-size:18px;">⚙️</span>
          <div>
            <div style="font-size:15px;font-weight:800;color:#1f1f1d;">설정</div>
            <div style="font-size:12px;color:#9a988f;margin-top:2px;">출입 비밀번호 · 공지사항 관리</div>
          </div>
        </div>
        <span id="settingsChevron" style="font-size:13px;font-weight:700;color:oklch(0.5 0.13 255);">펼치기 ▾</span>
      </div>

      <div id="settingsBody" style="display:none;border-top:1px solid #f0eee9;">

        <!-- 출입 비밀번호 변경 -->
        <div style="padding:18px 24px;display:flex;align-items:center;gap:16px;flex-wrap:wrap;">
          <div style="display:flex;align-items:center;gap:10px;">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="oklch(0.5 0.13 255)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
            <div>
              <div style="font-size:14px;font-weight:800;color:#1f1f1d;">서버실 출입 비밀번호</div>
              <div style="font-size:12px;color:#9a988f;margin-top:2px;">변경하면 이후 출입 신청 안내에 새 비밀번호가 표시됩니다.</div>
              <div id="pwSince" style="font-size:12px;color:oklch(0.5 0.13 255);font-weight:700;margin-top:4px;"></div>
            </div>
          </div>
          <div style="display:flex;align-items:center;gap:8px;margin-left:auto;">
            <input type="text" id="pwInput" placeholder="새 비밀번호 입력" maxlength="200"
                   style="width:200px;border:1px solid #e2e0db;border-radius:10px;padding:10px 14px;color:#1f1f1d;font-size:15px;font-variant-numeric:tabular-nums;outline:none;">
            <button type="button" id="pwSaveBtn"
                    style="background:oklch(0.5 0.13 255);color:#fff;border:none;border-radius:10px;padding:10px 20px;font-size:14px;font-weight:700;cursor:pointer;">저장</button>
            <span id="pwMsg" style="font-size:13px;font-weight:600;"></span>
          </div>
        </div>

        <!-- 공지사항 작성 -->
        <div style="padding:18px 24px;border-top:1px solid #f4f2ee;">
          <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:12px;flex-wrap:wrap;gap:10px;">
            <div style="display:flex;align-items:center;gap:10px;">
              <div style="font-size:18px;">📢</div>
              <div>
                <div style="font-size:14px;font-weight:800;color:#1f1f1d;">공지사항</div>
                <div style="font-size:12px;color:#9a988f;margin-top:2px;">출입 신청 화면 상단에 표시됩니다. (비우고 저장하면 공지 해제)</div>
              </div>
            </div>
            <div style="display:flex;align-items:center;gap:8px;">
              <span id="noticeMsg" style="font-size:13px;font-weight:600;"></span>
              <button type="button" id="noticeDeleteBtn"
                      style="background:#fff;color:#d05;border:1px solid #e7c3cb;border-radius:10px;padding:10px 18px;font-size:14px;font-weight:700;cursor:pointer;">삭제</button>
              <button type="button" id="noticeSaveBtn"
                      style="background:oklch(0.5 0.13 255);color:#fff;border:none;border-radius:10px;padding:10px 20px;font-size:14px;font-weight:700;cursor:pointer;">저장</button>
            </div>
          </div>
          <textarea id="noticeInput" maxlength="200" rows="2" placeholder="공지 내용을 입력하세요 (최대 200자)"
                    style="width:100%;border:1px solid #e2e0db;border-radius:10px;padding:12px 14px;color:#1f1f1d;font-size:14px;line-height:1.6;outline:none;resize:vertical;font-family:inherit;"><c:out value="${notice}"/></textarea>
        </div>

      </div>
    </div>

    <!-- KPI -->
    <div style="display:grid;grid-template-columns:repeat(2,1fr);gap:18px;margin-bottom:24px;">
      <div style="background:#fff;border:1px solid #eae8e3;border-radius:16px;padding:22px 24px;">
        <div style="font-size:13px;color:#8a8880;font-weight:600;">오늘 출입 건수</div>
        <div style="font-size:34px;font-weight:800;color:oklch(0.5 0.13 255);margin-top:8px;line-height:1;">
          <span id="kpiToday">${stats.todayCount}</span><span style="font-size:16px;color:#8a8880;font-weight:600;">건</span>
        </div>
        <div style="font-size:12px;color:oklch(0.6 0.13 145);margin-top:8px;">
          <c:choose>
            <c:when test="${stats.todayDiff >= 0}">▲ 어제보다 ${stats.todayDiff}건</c:when>
            <c:otherwise>▼ 어제보다 ${-stats.todayDiff}건</c:otherwise>
          </c:choose>
        </div>
      </div>
      <div style="background:#fff;border:1px solid #eae8e3;border-radius:16px;padding:22px 24px;">
        <div style="font-size:13px;color:#8a8880;font-weight:600;">총 출입 인원</div>
        <div style="font-size:34px;font-weight:800;color:#1f1f1d;margin-top:8px;line-height:1;">
          <span id="kpiTotal"><fmt:formatNumber value="${stats.totalPeople}"/></span><span style="font-size:16px;color:#8a8880;font-weight:600;">명</span>
        </div>
        <div style="font-size:12px;color:#a8a69e;margin-top:8px;">누적 출입 인원</div>
      </div>
    </div>

    <!-- log -->
    <div style="background:#fff;border:1px solid #eae8e3;border-radius:16px;padding:24px 26px;">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:18px;">
        <div>
          <div style="font-size:16px;font-weight:800;color:#1f1f1d;">출입 기록</div>
        </div>
        <form id="searchForm" style="display:flex;align-items:center;gap:10px;">
          <div style="display:flex;align-items:center;gap:8px;border:1px solid #e2e0db;border-radius:10px;padding:7px 13px;color:#43423d;font-size:14px;">
            <span style="color:#9a988f;font-size:12px;font-weight:700;">기간</span>
            <input type="date" name="startDate" value="${search.startDate}" style="border:none;outline:none;color:#43423d;font-size:14px;">
            <span style="color:#bdbbb4;">~</span>
            <input type="date" name="endDate" value="${search.endDate}" style="border:none;outline:none;color:#43423d;font-size:14px;">
          </div>
          <input type="text" name="keyword" value="<c:out value='${search.keyword}'/>" placeholder="성명 또는 사번으로 검색"
                 style="width:240px;border:1px solid #e2e0db;border-radius:10px;padding:9px 14px;color:#43423d;font-size:14px;outline:none;">
          <button type="submit" style="background:oklch(0.5 0.13 255);color:#fff;border:none;border-radius:10px;padding:9px 18px;font-size:14px;font-weight:700;cursor:pointer;">검색</button>
        </form>
      </div>
      <div style="border:1px solid #eee;border-radius:12px;overflow:hidden;">
        <div style="display:grid;grid-template-columns:160px 1fr 1fr 1.3fr 1fr;background:#faf9f6;padding:12px 20px;font-size:12px;font-weight:800;color:#9a988f;">
          <div>시각</div><div>성명</div><div>사번</div><div>사유</div><div>연락처</div>
        </div>
        <div id="logBody">
          <c:forEach var="row" items="${logs}">
            <div style="display:grid;grid-template-columns:160px 1fr 1fr 1.3fr 1fr;align-items:center;padding:14px 20px;border-top:1px solid #f2f1ec;font-size:14px;color:#43423d;font-variant-numeric:tabular-nums;">
              <div>${row.accessTimeStr}</div>
              <div><c:out value="${row.empName}"/></div>
              <div><c:out value="${row.empNo}"/></div>
              <div style="color:#7a7973;"><c:out value="${row.reason}"/></div>
              <div style="color:#9a988f;"><c:out value="${row.phone}"/></div>
            </div>
          </c:forEach>
          <c:if test="${empty logs}">
            <div style="padding:40px;text-align:center;color:#9a988f;font-size:14px;">출입 기록이 없습니다.</div>
          </c:if>
        </div>
      </div>
    </div>

  </div>
</div>

<script src="/js/clock.js"></script>
<script src="/js/admin.js"></script>
</body>
</html>
