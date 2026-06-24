/* 상단바 실시간 시계 — 목업의 renderVals() 재현 */
(function () {
  var DAYS = ['일', '월', '화', '수', '목', '금', '토'];
  function pad(x) { return String(x).padStart(2, '0'); }

  function tick() {
    var n = new Date();
    var dateStr = n.getFullYear() + '년 ' + (n.getMonth() + 1) + '월 ' + n.getDate() +
                  '일 (' + DAYS[n.getDay()] + ')';
    var timeStr = pad(n.getHours()) + ':' + pad(n.getMinutes()) + ':' + pad(n.getSeconds());

    setText('dateStr', dateStr);
    setText('dateStr2', dateStr);
    setText('timeStr', timeStr);
    setText('logDate', dateStr);
    // 신청 시각 입력칸(읽기전용)
    var rt = document.getElementById('reqTime');
    if (rt) rt.value = dateStr + ' ' + timeStr;
  }

  function setText(id, v) {
    var el = document.getElementById(id);
    if (el) el.textContent = v;
  }

  tick();
  setInterval(tick, 1000);
})();
