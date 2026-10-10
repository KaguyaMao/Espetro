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
const target = 'EsFactions/pla_195th.json';
const sleep = ms => new Promise(r => setTimeout(r, ms));
async function read(target) {
  for (let attempt = 0; attempt < 6; attempt++) {
    await sleep(1500);
    const r = await fetch(`${PANEL}/api/files?target=${encodeURIComponent(target)}&${q}`, { method: 'PUT', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, body: JSON.stringify({ target }) });
    const t = await r.text();
    if (r.status === 200 && !t.includes('冷却')) return t;
  }
  throw new Error('READ FAILED');
}
const raw = await read(target);
let content;
try { const w = JSON.parse(raw); content = typeof w.data === 'string' ? w.data : raw; } catch { content = raw; }
// 容错解析（去掉尾随逗号）
const fixed = content.replace(/,\s*([\]}])/g, '$1');
let data;
try { data = JSON.parse(fixed); } catch (e) { console.log('PARSE FAIL: ' + e.message.slice(0,120)); process.exit(1); }
const vehicles = data.vehicles ?? {};
let changed = [];
for (const key of ['mbt', 'mgs']) {
  const v = vehicles[key];
  if (!v) continue;
  if (v.vehicle_crew_seats === undefined || v.vehicle_crew_seats === null) {
    v.vehicle_crew_seats = 3;
    changed.push(key);
  } else {
    console.log(key + ' 已有 vehicle_crew_seats = ' + v.vehicle_crew_seats);
  }
}
if (changed.length === 0) { console.log('无需修改'); process.exit(0); }
const payload = JSON.stringify(data, null, 2);
// 写回
for (let attempt = 0; attempt < 6; attempt++) {
  await sleep(2000);
  const w = await fetch(`${PANEL}/api/files?${q}`, { method: 'PUT', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, body: JSON.stringify({ target, text: payload }) });
  const wt = await w.json();
  console.log('write attempt', attempt, 'status:', w.status, JSON.stringify(wt).slice(0, 100));
  if (w.status === 200 && wt.status === 200) break;
}
console.log('已修改:', changed.join(', '), '-> vehicle_crew_seats: 3');
