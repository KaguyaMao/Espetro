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
const files = ['pla_112th_brigade_mesh.json','pla_112th_brigade.json','pla_118th_brigade.json','pla_195th.json','plamc_5th_at.json','plamc_5th.json','plamc_7th.json','ru_205th.json','ru_3th.json','ru_49th.json','ru_6th_tank.json','us_1th_ar.json','us_1th_ri.json','us_2nd_stryker.json','us_redone.json'];
for (const f of files) {
  const raw = await read('EsFactions/' + f);
  let content;
  try { const w = JSON.parse(raw); content = typeof w.data === 'string' ? w.data : raw; } catch { content = raw; }
  let data;
  try { data = JSON.parse(content.replace(/,\s*([\]}])/g, '$1')); } catch (e) { console.log(f + ' PARSE FAIL: ' + e.message.slice(0,80)); continue; }
  const vs = data.vehicles ?? {};
  console.log('===== ' + f + ' =====');
  for (const [k, v] of Object.entries(vs)) {
    const name = v.display_name ?? '(无display_name)';
    const crew = v.vehicle_crew_seats;
    const entity = Array.isArray(v.entity) ? v.entity[0] : '';
    console.log(`  ${k}: 名称="${name}" 注册名=${entity} crew_seats=${crew === undefined ? '未设定' : crew}`);
  }
}
