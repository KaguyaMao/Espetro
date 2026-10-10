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
  const res = await fetch(`${PANEL}/api/instance?${q}`, { headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie } });
  const body = await res.json();
  const inst = body.data ?? body;
  const config = JSON.parse(JSON.stringify(inst.config));
  config.startCommand = 'sh run.sh';
  const variants = [
    { name: 'wrap-config', payload: { config } },
    { name: 'wrap-uuid-config', payload: { uuid: INSTANCE_ID, daemonId: DAEMON_ID, config } },
    { name: 'full-instance', payload: { ...inst, config } },
  ];
  for (const v of variants) {
    try {
      const r = await fetch(`${PANEL}/api/instance?${q}`, { method: 'PUT', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, body: JSON.stringify(v.payload) });
      console.log(v.name, r.status, (await r.text()).substring(0, 200));
    } catch (e) { console.log(v.name, 'ERR', e.message); }
    await new Promise(x => setTimeout(x, 2000));
  }
})();
