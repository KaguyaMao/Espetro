const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
(async () => {
  const loginRes = await fetch(`${PANEL}/api/auth/login`, { method: 'POST', headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, body: JSON.stringify({ username: USER, password: PASS }) });
  const token = String((await loginRes.json()).data ?? '');
  const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
  const probes = [
    ['GET', `${PANEL}/api/overview/instance?${q}`],
    ['GET', `${PANEL}/api/instance/list?${q}`],
    ['GET', `${PANEL}/api/instance/info?${q}`],
    ['GET', `${PANEL}/api/files/list?page=0&page_size=5&file_name=&target=logs&${q}`],
    ['GET', `${PANEL}/api/daemon/instance?${q}`],
  ];
  for (const [m, url] of probes) {
    try {
      const r = await fetch(url, { method: m, headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie } });
      const t = await r.text();
      console.log(m, url.split('/api/')[1].split('?')[0], r.status, t.substring(0, 250));
    } catch (e) { console.log(m, url.split('/api/')[1].split('?')[0], 'ERR', e.message); }
    await new Promise(x => setTimeout(x, 1500));
  }
})();
