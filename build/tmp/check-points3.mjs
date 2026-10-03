// check-points3.mjs — 计算 region 覆盖的世界坐标范围，判断每个点位是否在地图数据内
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');
const PANEL = 'https://www.derpydoge.fun:20000';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
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
const r = await req('GET', `${PANEL}/api/files/list?page=0&page_size=500&file_name=&target=${encodeURIComponent('EsWorld/CREATE_PLUS/region')}&${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
const items = (JSON.parse(r.body).data?.items ?? []).map((i) => String(i.name)).filter((n) => n.endsWith('.mca'));
const cells = items.map((n) => { const m = /^r\.(-?\d+)\.(-?\d+)\.mca$/.exec(n); return m ? [Number(m[1]), Number(m[2])] : null; }).filter(Boolean);
const cxs = cells.map((c) => c[0]), czs = cells.map((c) => c[1]);
const minCx = Math.min(...cxs), maxCx = Math.max(...cxs), minCz = Math.min(...czs), maxCz = Math.max(...czs);
console.log(`region 文件 ${items.length} 个；cx ${minCx}..${maxCx}，cz ${minCz}..${maxCz}`);
console.log(`对应世界坐标约: X ${minCx * 512} .. ${(maxCx + 1) * 512 - 1}   Z ${minCz * 512} .. ${(maxCz + 1) * 512 - 1}`);
const has = (cx, cz) => cells.some((c) => c[0] === cx && c[1] === cz);
console.log(`抽查: r.1.-3.mca 存在? ${has(1, -3)}   r.0.-3.mca 存在? ${has(0, -3)}   r.2.-3.mca 存在? ${has(2, -3)}`);

const files = fs.readdirSync('points-check').filter((f) => f.endsWith('.json'));
for (const f of files) {
  const j = JSON.parse(fs.readFileSync(`points-check/${f}`, 'utf8'));
  console.log(`\n=== ${f} ===`);
  for (const p of j.plannedPoints ?? []) {
    const x1 = Math.min(p.pos1.x, p.pos2.x), x2 = Math.max(p.pos1.x, p.pos2.x);
    const z1 = Math.min(p.pos1.z, p.pos2.z), z2 = Math.max(p.pos1.z, p.pos2.z);
    const cx1 = x1 >> 9, cx2 = x2 >> 9, cz1 = z1 >> 9, cz2 = z2 >> 9;
    let have = 0, total = 0, missingList = [];
    for (let cx = cx1; cx <= cx2; cx++) for (let cz = cz1; cz <= cz2; cz++) { total++; if (has(cx, cz)) have++; else missingList.push(`r.${cx}.${cz}`); }
    const pct = total ? Math.round(have / total * 100) : 0;
    const tag = have === total ? '✓ 全覆盖' : (have === 0 ? '❌ 完全在数据外' : `⚠ 部分缺失(${pct}%)`);
    console.log(`  ${String(p.name).padEnd(4)} 批${String(p.batch).padEnd(2)} region ${have}/${total}  ${tag}${missingList.length && have < total ? '  缺: ' + missingList.slice(0, 6).join(',') : ''}`);
  }
}
