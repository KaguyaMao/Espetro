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
    await sleep(1200);
    const r = await fetch(`${PANEL}/api/files?target=${encodeURIComponent(target)}&${q}`, { method: 'PUT', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, body: JSON.stringify({ target }) });
    const t = await r.text();
    if (r.status === 200 && !t.includes('冷却')) return t;
  }
  throw new Error('READ FAILED ' + target);
}
async function write(target, text) {
  for (let attempt = 0; attempt < 8; attempt++) {
    await sleep(2500);
    const r = await fetch(`${PANEL}/api/files?${q}`, { method: 'PUT', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, body: JSON.stringify({ target, text }) });
    const b = await r.json();
    if (r.status === 200 && b.status === 200) return true;
  }
  return false;
}
// 需要修改的文件和载具键：含「突击炮」/「自行火炮」→ 3
const targets = {
  'plamc_5th_at.json': ['mgs'],
  'plamc_5th.json': ['mgs'],
  'us_2nd_stryker.json': ['mgs']
};
for (const [f, keys] of Object.entries(targets)) {
  const raw = await read('EsFactions/' + f);
  let content;
  try { const w = JSON.parse(raw); content = typeof w.data === 'string' ? w.data : raw; } catch { content = raw; }
  let data;
  try { data = JSON.parse(content.replace(/,\s*([\]}])/g, '$1')); } catch (e) { console.log(f + ' PARSE FAIL: ' + e.message.slice(0,80)); continue; }
  const vs = data.vehicles ?? {};
  const changes = [];
  for (const k of keys) {
    const v = vs[k];
    if (!v) continue;
    const before = v.vehicle_crew_seats;
    v.vehicle_crew_seats = 3;
    changes.push(`${k}: ${before === undefined ? '未设定' : before} -> 3（${v.display_name}）`);
  }
  if (changes.length === 0) { console.log(f + ': 无匹配'); continue; }
  const ok = await write('EsFactions/' + f, JSON.stringify(data, null, 2));
  console.log(f + (ok ? ' 写入OK' : ' 写入失败'));
  for (const c of changes) console.log('  ' + c);
}
