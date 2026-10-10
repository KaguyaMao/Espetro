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
const fixed = content.replace(/,\s*([\]}])/g, '$1');
const data = JSON.parse(fixed);
const v = data.vehicles;
console.log('mbt.vehicle_crew_seats =', v.mbt.vehicle_crew_seats);
console.log('mgs.vehicle_crew_seats =', v.mgs.vehicle_crew_seats);
console.log('acv.vehicle_crew_seats =', v.acv.vehicle_crew_seats);
console.log('JSON 解析 OK');
