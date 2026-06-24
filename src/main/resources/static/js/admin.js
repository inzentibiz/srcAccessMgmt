/* 관리자 대시보드 — 검색 / 실시간 갱신 */
(function () {
  var form = document.getElementById('searchForm');
  var body = document.getElementById('logBody');

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
})();
