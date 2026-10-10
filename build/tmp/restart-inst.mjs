// restart-inst.mjs — 重启实例并等待重新运行
//
// 用法: node restart-inst.mjs [等待秒数=120]
//
// 旧版本 restart 之后又立刻 open（重复启动指令，可能被面板拒绝并干扰重启时序），已改为只 restart + 轮询。
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const WAIT_SECONDS = Number(process.argv[2] ?? 120);

(async () => {
  const loginRes = await fetch(`${PANEL}/api/auth/login`, {
    method: 'POST', headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
    body: JSON.stringify({ username: USER, password: PASS })
  });
  const token = String((await loginRes.json()).data ?? '');
  const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;

  const r = await fetch(`${PANEL}/api/protected_instance/restart?${q}`, {
    method: 'POST', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }
  });
  console.log('restart ->', r.status, (await r.text()).slice(0, 150));

  const deadline = Date.now() + WAIT_SECONDS * 1000;
  while (Date.now() < deadline) {
    await new Promise((x) => setTimeout(x, 3000));
    const s = await fetch(`${PANEL}/api/instance?${q}`, { headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie } });
    const st = ((await s.json()).data ?? {}).status;
    console.log('status =', st);
    if (st === 3) { console.log('RUNNING'); return; }
  }
  console.log('TIMEOUT waiting for running');
  process.exit(1);
})();
