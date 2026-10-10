// check-points2.mjs — ①同方案内箱体重叠/间距 ②点位所在区块是否已生成（region 文件存在）
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
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
  const r = await req('GET', `${PANEL}/api/files/list?page=0&page_size=500&file_name=&target=${encodeURIComponent(t)}&${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
  try { const b = JSON.parse(r.body); return b.data?.items ?? []; } catch { return []; }
}

const files = fs.readdirSync('points-check').filter((f) => f.endsWith('.json'));
const box = (p) => ({
  name: p.name, batch: p.batch,
  x1: Math.min(p.pos1.x, p.pos2.x), x2: Math.max(p.pos1.x, p.pos2.x),
  y1: Math.min(p.pos1.y, p.pos2.y), y2: Math.max(p.pos1.y, p.pos2.y),
  z1: Math.min(p.pos1.z, p.pos2.z), z2: Math.max(p.pos1.z, p.pos2.z),
});
const overlap = (a, b) => a.x1 <= b.x2 && b.x1 <= a.x2 && a.z1 <= b.z2 && b.z1 <= a.z2;

console.log('=== ① 同方案内箱体水平重叠（X/Z 相交）===');
for (const f of files) {
  const j = JSON.parse(fs.readFileSync(`points-check/${f}`, 'utf8'));
  const bs = (j.plannedPoints ?? []).map(box);
  const hits = [];
  for (let i = 0; i < bs.length; i++) for (let k = i + 1; k < bs.length; k++) if (overlap(bs[i], bs[k])) hits.push(`${bs[i].name}(批${bs[i].batch}) × ${bs[k].name}(批${bs[k].batch})`);
  console.log(`  ${f}: ${hits.length ? hits.join(', ') : '无重叠 ✓'}`);
  // 相邻批次中心距离
  const center = (b) => [(b.x1 + b.x2) / 2, (b.z1 + b.z2) / 2];
  const pairs = [];
  for (let i = 1; i < bs.length; i++) {
    const [ax, az] = center(bs[i - 1]), [bx, bz] = center(bs[i]);
    pairs.push(`${bs[i - 1].name}→${bs[i].name}: ${Math.round(Math.hypot(ax - bx, az - bz))} 格`);
  }
  console.log(`     相邻批次中心距: ${pairs.join(' | ')}`);
}

console.log('\n=== ② 点位所在 region 文件是否存在 ===');
const regions = new Set((await list('EsWorld/CREATE_PLUS/region')).filter((i) => String(i.name).endsWith('.mca')).map((i) => String(i.name)));
console.log(`  region 目录共 ${regions.size} 个 .mca`);
for (const f of files) {
  const j = JSON.parse(fs.readFileSync(`points-check/${f}`, 'utf8'));
  const missing = [];
  for (const p of j.plannedPoints ?? []) {
    const b = box(p);
    const cx1 = b.x1 >> 9, cx2 = b.x2 >> 9, cz1 = b.z1 >> 9, cz2 = b.z2 >> 9;
    let have = 0, total = 0;
    for (let cx = cx1; cx <= cx2; cx++) for (let cz = cz1; cz <= cz2; cz++) { total++; if (regions.has(`r.${cx}.${cz}.mca`)) have++; }
    const name = `r.${cx1}.${cz1}.mca`;
    if (have === 0) missing.push(`${b.name}(批${b.batch}) 一个 region 都没有 [${name} …]`);
    else if (have < total) missing.push(`${b.name}(批${b.batch}) 覆盖 ${have}/${total} 个 region 文件`);
  }
  console.log(`  ${f}:`);
  for (const m of missing) console.log(`     ⚠ ${m}`);
  if (!missing.length) console.log('     全部落在已生成 region 内 ✓');
}
