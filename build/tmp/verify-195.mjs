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
const r = await fetch(`${PANEL}/api/files?target=${encodeURIComponent('EsFactions/pla_195th.json')}&${q}`, { method: 'PUT', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, body: JSON.stringify({ target: 'EsFactions/pla_195th.json' }) });
const t = await r.text();
let content; try { const w = JSON.parse(t); content = typeof w.data === 'string' ? w.data : t; } catch { content = t; }
// 显示 mbt/mgs/acv 片段
for (const key of ['mbt', 'mgs', 'acv']) {
  const idx = content.indexOf('"' + key + '"');
  console.log('=== ' + key + ' ===');
  console.log(content.slice(idx, idx + 260));
  console.log();
}
