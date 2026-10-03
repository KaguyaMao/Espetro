// check-points.mjs — 体检 CREATE_PLUS 点位配置：目录变化 + 每个文件的批次/点位/包围盒/重复/兵力一致性
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const path = require('path');
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
async function list(t) {
  const r = await req('GET', `${PANEL}/api/files/list?page=0&page_size=200&file_name=&target=${encodeURIComponent(t)}&${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
  try { const b = JSON.parse(r.body); return b.data?.items ?? []; } catch { return []; }
}
async function read(t) {
  for (let a = 0; a < 5; a++) {
    const r = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: t }));
    try { const b = JSON.parse(r.body); if (b.status === 200) return String(b.data ?? ''); } catch { }
    await sleep(2600);
  }
  return null;
}
const BASE = 'EsWorld/CREATE_PLUS';
const stamp = (i) => (i.mtime ? new Date(i.mtime).toISOString().slice(5, 16).replace('T', ' ') : '');

console.log('=== Points/ 目录 ===');
const pointsDir = await list(`${BASE}/Points`);
for (const i of pointsDir) console.log(`   ${String(i.name).padEnd(30)}${(i.size ?? 0).toString().padStart(8)}  ${stamp(i)}`);
await sleep(2600);
console.log('=== EsConfig/ 目录 ===');
const cfgDir = await list(`${BASE}/EsConfig`);
for (const i of cfgDir) if (String(i.name).endsWith('.json')) console.log(`   ${String(i.name).padEnd(30)}${(i.size ?? 0).toString().padStart(8)}  ${stamp(i)}`);

fs.mkdirSync('points-check', { recursive: true });
const files = [...pointsDir.filter((i) => String(i.name).endsWith('.json')).map((i) => `${BASE}/Points/${i.name}`),
  `${BASE}/EsConfig/CapturePoints.json`];
const loaded = [];
for (const f of files) {
  await sleep(2600);
  const text = await read(f);
  if (text === null) { console.log(`\n### ${f}: 读取失败`); continue; }
  const name = f.split('/').pop();
  fs.writeFileSync(path.join('points-check', name), text, 'utf8');
  let j = null, err = null;
  try { j = JSON.parse(text); } catch (e) { err = e.message; }
  console.log(`\n### ${name} (${text.length} 字符)`);
  if (!j) { console.log('   ❌ JSON 解析失败: ' + err); continue; }
  const pts = j.plannedPoints ?? [];
  const batches = {};
  for (const p of pts) batches[p.batch] = (batches[p.batch] ?? 0) + 1;
  console.log(`   modes=${JSON.stringify(j.modes ?? null)} objectiveMode=${j.objectiveMode ?? '(无)'} totalBatches=${j.totalBatches} endBehavior=${j.endBehavior}`);
  console.log(`   兵力: ${JSON.stringify(j.teamReinforcements)}  attackBatchCompletionReinforcement=${j.attackBatchCompletionReinforcement ?? '(无)'}`);
  console.log(`   点位数=${pts.length}，批次分布=${JSON.stringify(batches)}`);
  const names = pts.map((p) => p.name);
  const dup = names.filter((n, i) => names.indexOf(n) !== i);
  if (dup.length) console.log(`   ❌ 重名点位: ${[...new Set(dup)].join(', ')}`);
  const maxBatch = Math.max(0, ...pts.map((p) => p.batch));
  if (maxBatch > (j.totalBatches ?? 0)) console.log(`   ❌ 有点位批次 ${maxBatch} > totalBatches ${j.totalBatches}（这些点永远不会激活）`);
  for (let b = 1; b <= (j.totalBatches ?? 0); b++) if (!batches[b]) console.log(`   ❌ 批次 ${b} 没有任何点位 → 该批次永远无法完成，战局会卡住`);
  if (batches[maxBatch] !== undefined && maxBatch === j.totalBatches) {
    for (let b = 1; b <= j.totalBatches; b++) if (!batches[b]) console.log(`   (提示: 批次 ${b} 为空)`);
  }
  for (const p of pts) {
    const xs = [p.pos1.x, p.pos2.x], ys = [p.pos1.y, p.pos2.y], zs = [p.pos1.z, p.pos2.z];
    const sx = Math.abs(xs[0] - xs[1]) + 1, sy = Math.abs(ys[0] - ys[1]) + 1, sz = Math.abs(zs[0] - zs[1]) + 1;
    const vol = sx * sy * sz;
    const flag = vol > 200000 ? ' ⚠体积偏大' : (sy < 3 ? ' ⚠Y 太薄' : (sy > 40 ? ' ⚠Y 过高' : ''));
    console.log(`   ${String(p.name).padEnd(6)} 批次${String(p.batch).padEnd(2)} 尺寸 ${sx}×${sy}×${sz}  体积=${vol}  X[${Math.min(...xs)},${Math.max(...xs)}] Y[${Math.min(...ys)},${Math.max(...ys)}] Z[${Math.min(...zs)},${Math.max(...zs)}]${flag}`);
  }
  loaded.push({ name, j });
}
// 文件间差异（找出"新加的点"在哪个文件、和谁不同）
console.log('\n=== 文件间点位差异 ===');
for (let a = 0; a < loaded.length; a++) {
  for (let b = a + 1; b < loaded.length; b++) {
    const A = loaded[a].j.plannedPoints ?? [], B = loaded[b].j.plannedPoints ?? [];
    const key = (p) => `${p.name}|${p.batch}|${p.pos1.x},${p.pos1.y},${p.pos1.z}-${p.pos2.x},${p.pos2.y},${p.pos2.z}`;
    const ka = A.map(key), kb = B.map(key);
    const onlyA = ka.filter((x) => !kb.includes(x)), onlyB = kb.filter((x) => !ka.includes(x));
    if (onlyA.length || onlyB.length) {
      console.log(`   ${loaded[a].name} vs ${loaded[b].name}: 仅前者有 ${onlyA.length} 个, 仅后者有 ${onlyB.length} 个`);
      for (const x of onlyA) console.log(`      仅 ${loaded[a].name}: ${x}`);
      for (const x of onlyB) console.log(`      仅 ${loaded[b].name}: ${x}`);
    }
  }
}
