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
  console.log(JSON.stringify(inst.config, null, 1).substring(0, 3000));
})().catch(e => { console.error('ERR', e.message); process.exit(1); });
