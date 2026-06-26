/* 관리자 대시보드 — 검색 / 실시간 갱신 */
(function () {
  var form = document.getElementById('searchForm');
  var body = document.getElementById('logBody');

  // ===== 설정 패널 접기/펼치기 =====
  (function () {
    var toggle = document.getElementById('settingsToggle');
    var panel = document.getElementById('settingsBody');
    var chevron = document.getElementById('settingsChevron');
    if (!toggle || !panel) return;
    toggle.addEventListener('click', function () {
      var open = panel.style.display !== 'none';
      panel.style.display = open ? 'none' : 'block';
      if (chevron) chevron.textContent = open ? '펼치기 ▾' : '접기 ▴';
    });
  })();

  function esc(s) {
    return String(s == null ? '' : s)
      .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  // 'yyyy-MM-ddTHH:mm:ss' -> 'yyyy-MM-dd HH:mm'
  function fmt(ts) {
    if (!ts) return '';
    return String(ts).replace('T', ' ').slice(0, 16);
  }

  function rowHtml(r) {
    return '<div style="display:grid;grid-template-columns:160px 1fr 1fr 1.3fr 1fr;align-items:center;' +
      'padding:14px 20px;border-top:1px solid #f2f1ec;font-size:14px;color:#43423d;font-variant-numeric:tabular-nums;">' +
      '<div>' + esc(fmt(r.accessTime)) + '</div>' +
      '<div>' + esc(r.empName) + '</div>' +
      '<div>' + esc(r.empNo) + '</div>' +
      '<div style="color:#7a7973;">' + esc(r.reason) + '</div>' +
      '<div style="color:#9a988f;">' + esc(r.phone) + '</div>' +
      '</div>';
  }

  function render(rows) {
    if (!rows.length) {
      body.innerHTML = '<div style="padding:40px;text-align:center;color:#9a988f;font-size:14px;">검색 결과가 없습니다.</div>';
    } else {
      body.innerHTML = rows.map(rowHtml).join('');
    }
    var c = document.getElementById('logCount');
    if (c) c.textContent = rows.length;
  }

  function search() {
    var params = new URLSearchParams(new FormData(form)).toString();
    fetch('/api/access?' + params)
      .then(function (res) { return res.json(); })
      .then(render)
      .catch(function (e) { console.error('검색 실패', e); });
  }

  if (form) {
    form.addEventListener('submit', function (e) {
      e.preventDefault();
      search();
    });
  }

  // KPI 30초마다 갱신
  function refreshStats() {
    fetch('/api/access/stats')
      .then(function (r) { return r.json(); })
      .then(function (s) {
        setText('kpiToday', s.todayCount);
        setText('kpiTotal', Number(s.totalPeople).toLocaleString());
      })
      .catch(function () {});
  }

  function setText(id, v) {
    var el = document.getElementById(id);
    if (el) el.textContent = v;
  }

  setInterval(refreshStats, 30000);

  // ===== 출입 비밀번호 변경 =====
  var pwInput = document.getElementById('pwInput');
  var pwSaveBtn = document.getElementById('pwSaveBtn');
  var pwMsg = document.getElementById('pwMsg');
  var pwSince = document.getElementById('pwSince');

  function renderSince(b) {
    if (!pwSince) return;
    if (b.updatedAt == null) { pwSince.textContent = ''; return; }
    var d = Number(b.daysSince);
    var ago = (d <= 0) ? '오늘 변경' : (d + '일 전 변경');
    pwSince.textContent = '마지막 변경: ' + b.updatedAt + ' (' + ago + ')';
  }

  function showPwMsg(text, ok) {
    if (!pwMsg) return;
    pwMsg.textContent = text;
    pwMsg.style.color = ok ? '#2e9e5b' : '#d05';
    if (text) setTimeout(function () { pwMsg.textContent = ''; }, 3000);
  }

  function loadPw() {
    fetch('/api/access/password')
      .then(function (r) { return r.json(); })
      .then(function (b) { if (pwInput) pwInput.value = b.password || ''; renderSince(b); })
      .catch(function () {});
  }

  function savePw() {
    var v = (pwInput.value || '').trim();
    if (!v) { showPwMsg('비밀번호를 입력하세요.', false); return; }
    pwSaveBtn.disabled = true;
    fetch('/api/access/password', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ password: v })
    })
      .then(function (res) { return res.json().then(function (b) { return { ok: res.ok, body: b }; }); })
      .then(function (r) {
        if (r.ok && r.body.success) {
          if (pwInput) pwInput.value = r.body.password;
          showPwMsg('✓ 변경되었습니다', true);
          loadPw();   // 변경일/경과일수 갱신
        } else {
          showPwMsg(r.body.message || '변경 실패', false);
        }
      })
      .catch(function () { showPwMsg('네트워크 오류', false); })
      .finally(function () { pwSaveBtn.disabled = false; });
  }

  if (pwSaveBtn) pwSaveBtn.addEventListener('click', savePw);
  if (pwInput) pwInput.addEventListener('keydown', function (e) { if (e.key === 'Enter') savePw(); });
  loadPw();

  // ===== 공지사항 저장 =====
  var noticeInput = document.getElementById('noticeInput');
  var noticeSaveBtn = document.getElementById('noticeSaveBtn');
  var noticeDeleteBtn = document.getElementById('noticeDeleteBtn');
  var noticeMsg = document.getElementById('noticeMsg');

  function showNoticeMsg(text, ok) {
    if (!noticeMsg) return;
    noticeMsg.textContent = text;
    noticeMsg.style.color = ok ? '#2e9e5b' : '#d05';
    if (text) setTimeout(function () { noticeMsg.textContent = ''; }, 3000);
  }

  function saveNotice() {
    noticeSaveBtn.disabled = true;
    fetch('/api/access/notice', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ notice: noticeInput.value || '' })
    })
      .then(function (res) { return res.json().then(function (b) { return { ok: res.ok, body: b }; }); })
      .then(function (r) {
        if (r.ok && r.body.success) {
          if (noticeInput) noticeInput.value = r.body.notice || '';
          showNoticeMsg('✓ 저장되었습니다', true);
        } else {
          showNoticeMsg(r.body.message || '저장 실패', false);
        }
      })
      .catch(function () { showNoticeMsg('네트워크 오류', false); })
      .finally(function () { noticeSaveBtn.disabled = false; });
  }

  function deleteNotice() {
    var hasText = (noticeInput.value || '').trim().length > 0;
    if (hasText && !confirm('공지사항을 삭제할까요?')) return;
    noticeDeleteBtn.disabled = true;
    fetch('/api/access/notice', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ notice: '' })
    })
      .then(function (res) { return res.json().then(function (b) { return { ok: res.ok, body: b }; }); })
      .then(function (r) {
        if (r.ok && r.body.success) {
          if (noticeInput) noticeInput.value = '';
          showNoticeMsg('✓ 삭제되었습니다', true);
        } else {
          showNoticeMsg(r.body.message || '삭제 실패', false);
        }
      })
      .catch(function () { showNoticeMsg('네트워크 오류', false); })
      .finally(function () { noticeDeleteBtn.disabled = false; });
  }

  if (noticeSaveBtn) noticeSaveBtn.addEventListener('click', saveNotice);
  if (noticeDeleteBtn) noticeDeleteBtn.addEventListener('click', deleteNotice);
})();
