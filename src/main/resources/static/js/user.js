/* 사용자 출입 신청 — 등록 / 오늘 기록 갱신 */
(function () {
  var form = document.getElementById('accessForm');
  var btn = document.getElementById('submitBtn');
  var body = document.getElementById('todayBody');
  var pwModal = document.getElementById('pwModal');
  var pwValue = document.getElementById('pwValue');
  var pwCloseBtn = document.getElementById('pwCloseBtn');

  function showPw(pwd) {
    if (!pwModal) { alert('출입 비밀번호: ' + pwd); return; }
    pwValue.textContent = pwd || '----';
    pwModal.style.display = 'flex';
  }
  function hidePw() {
    if (pwModal) pwModal.style.display = 'none';
  }
  if (pwCloseBtn) pwCloseBtn.addEventListener('click', hidePw);
  // 배경(오버레이) 클릭 시 닫기 (모달 내부 클릭은 유지)
  if (pwModal) pwModal.addEventListener('click', function (e) {
    if (e.target === pwModal) hidePw();
  });

  // 연락처 입력 자동 하이픈 변환 (예: 01012345678 -> 010-1234-5678)
  function formatPhone(v) {
    var d = String(v || '').replace(/\D/g, '').slice(0, 11); // 숫자만, 최대 11자리
    if (d.length < 4) return d;
    if (d.length < 8) return d.slice(0, 3) + '-' + d.slice(3);
    return d.slice(0, 3) + '-' + d.slice(3, 7) + '-' + d.slice(7);
  }
  var phoneInput = form ? form.querySelector('input[name="phone"]') : null;
  if (phoneInput) {
    phoneInput.addEventListener('input', function () {
      phoneInput.value = formatPhone(phoneInput.value);
    });
  }

  function esc(s) {
    return String(s == null ? '' : s)
      .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  function timeShort(ts) {
    if (!ts) return '';
    return String(ts).replace('T', ' ').slice(11, 16); // HH:mm
  }

  function rowHtml(r) {
    return '<div style="display:grid;grid-template-columns:90px 1.1fr 1.1fr 1.6fr;align-items:center;' +
      'padding:15px 22px;border-top:1px solid #f2f1ec;font-size:14px;color:#43423d;font-variant-numeric:tabular-nums;">' +
      '<div>' + esc(timeShort(r.accessTime)) + '</div>' +
      '<div>' + esc(r.empName) + '</div>' +
      '<div>' + esc(r.empNo) + '</div>' +
      '<div style="color:#7a7973;">' + esc(r.reason) + '</div>' +
      '</div>';
  }

  function refreshToday() {
    fetch('/api/access/today')
      .then(function (r) { return r.json(); })
      .then(function (rows) {
        if (!rows.length) {
          body.innerHTML = '<div style="padding:40px;text-align:center;color:#9a988f;font-size:14px;">오늘 출입 기록이 없습니다.</div>';
        } else {
          body.innerHTML = rows.map(rowHtml).join('');
        }
        var c = document.getElementById('todayCount');
        if (c) c.textContent = rows.length;
      })
      .catch(function (e) { console.error(e); });
  }

  if (form) {
    form.addEventListener('submit', function (e) {
      e.preventDefault();

      var data = {};
      new FormData(form).forEach(function (v, k) { data[k] = v; });

      btn.disabled = true;
      var prev = btn.textContent;
      btn.textContent = '신청 중...';

      fetch('/api/access', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      })
        .then(function (res) {
          return res.json().then(function (b) { return { ok: res.ok, body: b }; });
        })
        .then(function (r) {
          if (r.ok && r.body.success) {
            form.reset();
            showPw(r.body.accessPwd);
            refreshToday();
          } else {
            var msg = r.body.message || '신청에 실패했습니다.';
            if (r.body.errors) {
              msg += '\n' + Object.values(r.body.errors).join('\n');
            }
            alert(msg);
          }
        })
        .catch(function (e) {
          console.error(e);
          alert('네트워크 오류가 발생했습니다.');
        })
        .finally(function () {
          btn.disabled = false;
          btn.textContent = prev;
        });
    });
  }
})();
