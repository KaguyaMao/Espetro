const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const loginRes = await fetch(`${PANEL}/api/auth/login`, {
  method: 'POST', headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
  body: JSON.stringify({ username: USER, password: PASS })
});
const token = String((await loginRes.json()).data ?? '');
const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
const sleep = ms => new Promise(r => setTimeout(r, ms));
async function read(target) {
  for (let attempt = 0; attempt < 6; attempt++) {
    await sleep(1500);
    const r = await fetch(`${PANEL}/api/files?target=${encodeURIComponent(target)}&${q}`, { method: 'PUT', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, body: JSON.stringify({ target }) });
    const t = await r.text();
    if (r.status === 200 && !t.includes('冷却')) return t;
  }
  return '<<READ FAILED>>';
}
const raw = await read('EsFactions/pla_195th.json');
let content;
try { const w = JSON.parse(raw); content = typeof w.data === 'string' ? w.data : raw; } catch { content = raw; }
const start = content.indexOf('"vehicles"');
console.log(content.slice(start, start + 5000));
