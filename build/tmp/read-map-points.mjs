// read-map-points.mjs — 读取服务端 EsWorld/CREATE_PLUS/EsConfig 下的点位配置，统计各类点位
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const https = require('https');
const PANEL = 'https://www.derpydoge.fun:20000';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const sleep = (ms) => new Promise((r) => setTimeout(r, ms));
function req(method, urlStr, headers, body, getCookie) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, (res) => {
      let d = ''; res.on('data', (c) => (d += c));
      res.on('end', () => resolve({ status: res.statusCode, body: d, cookie: getCookie ? (res.headers['set-cookie'] ?? []).map((l) => l.split(';')[0]).join('; ') : undefined }));
    });
    r.on('error', reject); if (body !== undefined) r.write(body); r.end();
  });
}
const lr = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: 'boy', password: 'boY1145141919810Fuck' }), true);
const token = String(JSON.parse(lr.body).data ?? '');
const cookie = lr.cookie ?? '';
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;

async function read(target) {
  for (let a = 0; a < 5; a++) {
    const r = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target }));
    try { const b = JSON.parse(r.body); if (b.status === 200) return String(b.data ?? ''); } catch { }
    await sleep(2600);
  }
  return null;
}
const dir = 'EsWorld/CREATE_PLUS/EsConfig';
const files = ['CapturePoints.json', 'outposts.json', 'bastion.json', 'spawn_points.json', 'logistics.json', 'VehSpawn.json', 'TacticalMap.json'];
for (const f of files) {
  const text = await read(`${dir}/${f}`);
  if (text === null) { console.log(`--- ${f}: 读取失败`); continue; }
  console.log(`--- ${f} (${text.length} 字符)`);
  let j = null;
  try { j = JSON.parse(text); } catch { console.log('  (非严格 JSON)'); }
  if (j) {
    if (j.plannedPoints) {
      const batches = {};
      for (const p of j.plannedPoints) batches[p.batch ?? '?'] = (batches[p.batch ?? '?'] ?? 0) + 1;
      console.log(`  据点 ${j.plannedPoints.length} 个，批次分布 ${JSON.stringify(batches)}，totalBatches=${j.totalBatches}，endBehavior=${j.endBehavior}`);
      console.log(`  点名列: ${j.plannedPoints.map((p) => p.name).join(', ')}`);
    }
    if (j.outposts) console.log(`  前哨站 ${j.outposts.length} 个: ${JSON.stringify(j.outposts).slice(0, 300)}`);
    if (j.spawnPoints) console.log(`  出生点: ${Object.keys(j.spawnPoints).join(', ')}`);
    if (j.VehTypes) console.log(`  载具刷新类型: ${j.VehTypes.join(', ')}；每类型点: ${Object.entries(j.spawn_points ?? {}).map(([k, v]) => k + '=' + v.length).join(', ')}`);
    if (j.logistics) console.log(`  后勤/无线电点: ${Object.keys(j.logistics).join(', ')}`);
    if (j.bastion) console.log(`  兵站参数: ${JSON.stringify(j.bastion)}`);
    if (j.map || j.tacticalMap) console.log(`  战术地图键: ${Object.keys(j).join(', ')}`);
  }
  await sleep(2600);
}
