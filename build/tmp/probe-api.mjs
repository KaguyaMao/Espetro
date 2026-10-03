const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
(async () => {
  const loginRes = await fetch(`${PANEL}/api/auth/login`, {
    method: 'POST', headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
    body: JSON.stringify({ username: USER, password: PASS })
  });
  const token = String((await loginRes.json()).data ?? '');
  const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
  const paths = [
    ['POST', `/api/protected_instance/stop?${q}`],
    ['POST', `/api/protected_instance/open?${q}`],
    ['GET', `/api/instance?${q}`],
    ['PUT', `/api/instance?${q}`],
    ['GET', `/api/protected_instance/overview?${q}`],
    ['GET', `/api/service/remote_service_system?${q}`],
  ];
  for (const [m, p] of paths) {
    try {
      const r = await fetch(`${PANEL}${p}`, { method: m, headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }, body: m === 'PUT' ? JSON.stringify({}) : undefined });
      const t = (await r.text()).slice(0, 120);
      console.log(`${m} ${p.split('?')[0]} -> ${r.status} ${t.replace(/\n/g, ' ')}`);
    } catch (e) { console.log(`${m} ${p.split('?')[0]} -> ERR ${e.message}`); }
  }
})();
