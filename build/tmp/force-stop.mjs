// force-stop.mjs — 强制停止实例（安全版）
//
// 用法:
//   node force-stop.mjs          正常 stop（实例未运行时直接报错退出，不会误触发 restart/kill）
//   node force-stop.mjs --kill   stop 失败后再尝试 kill/force_stop 端点
//
// 旧版本会无脑依次 POST restart/kill 等 5 个端点——"force stop" 反而可能把实例重启，故重写。
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const KILL_FALLBACK = process.argv.includes('--kill');

(async () => {
  const loginRes = await fetch(`${PANEL}/api/auth/login`, {
    method: 'POST', headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
    body: JSON.stringify({ username: USER, password: PASS })
  });
  const token = String((await loginRes.json()).data ?? '');
  const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;

  async function status() {
    const r = await fetch(`${PANEL}/api/instance?${q}`, { headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie } });
    const b = await r.json();
    return (b.data ?? b).status;
  }
  async function waitStopped(seconds) {
    const deadline = Date.now() + seconds * 1000;
    while (Date.now() < deadline) {
      const s = await status();
      if (s === 0) return true;
      await new Promise((r) => setTimeout(r, 2000));
    }
    return false;
  }

  const before = await status();
  if (before === 0) {
    console.log('实例已是停止状态 (status=0)，无事可做');
    return;
  }

  const stopRes = await fetch(`${PANEL}/api/protected_instance/stop?${q}`, {
    method: 'POST', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }
  });
  console.log('stop ->', stopRes.status, (await stopRes.text()).slice(0, 120));
  if (await waitStopped(90)) {
    console.log('STOPPED');
    return;
  }

  if (!KILL_FALLBACK) {
    console.log('停止超时；如需强杀请加 --kill');
    process.exit(1);
  }
  for (const ep of ['force_stop', 'kill']) {
    try {
      const r = await fetch(`${PANEL}/api/protected_instance/${ep}?${q}`, {
        method: 'POST', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }
      });
      console.log(`${ep} -> ${r.status} ${(await r.text()).slice(0, 100)}`);
    } catch (e) { console.log(`${ep} -> ERR ${e.message}`); }
    if (await waitStopped(30)) { console.log('STOPPED (' + ep + ')'); return; }
  }
  console.log('仍未能停止');
  process.exit(1);
})();
