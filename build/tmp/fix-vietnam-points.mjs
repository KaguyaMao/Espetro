// fix-vietnam-points.mjs — 修「越南」地图：补回 EsConfig/CapturePoints.json + 三套预设 DEFEND 300→350
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');
const PANEL = 'https://www.derpydoge.fun:20000';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const DEFEND = 350;
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
async function upload(local, dir, name) {
  for (let a = 0; a < 5; a++) {
    const t = await req('POST', `${PANEL}/api/files/upload?${q}&upload_dir=${encodeURIComponent(dir)}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
    let data = {};
    try { data = JSON.parse(t.body).data ?? {}; } catch { }
    if (!data.password) { await sleep(2500); continue; }
    const m = String(data.addr ?? '').match(/^wss?:\/\/([^/:]+)(:\d+)?/);
    const daemonUrl = 'https://' + new URL(PANEL).hostname + (m?.[2] ?? '');
    const buf = fs.readFileSync(local);
    const boundary = '----DSHUpload' + Date.now().toString(16);
    const head = Buffer.from(`--${boundary}\r\nContent-Disposition: form-data; name="file"; filename="${name}"\r\nContent-Type: application/octet-stream\r\n\r\n`);
    const tail = Buffer.from(`\r\n--${boundary}--\r\n`);
    const body = Buffer.concat([head, buf, tail]);
    const res = await req('POST', `${daemonUrl}/upload/${data.password}?overwrite=true`, { 'X-Requested-With': 'XMLHttpRequest', 'Content-Type': `multipart/form-data; boundary=${boundary}`, 'Content-Length': String(body.length) }, body);
    if (res.status === 200 && res.body.trim() === 'OK') return true;
    await sleep(2500);
  }
  return false;
}

fs.mkdirSync('vietnam-work/before', { recursive: true });
fs.mkdirSync('vietnam-work/after', { recursive: true });
const BASE = 'EsWorld/越南';

// 1) 备份并生成
const presets = ['CapturePointsA.json', 'CapturePointsB.json', 'CapturePointsC.json'];
let firstPreset = null;
for (const n of presets) {
  const text = await read(`${BASE}/Points/${n}`);
  if (text === null) { console.log(`❌ 读取失败 ${n}`); continue; }
  fs.writeFileSync(`vietnam-work/before/${n}`, text, 'utf8');
  const j = JSON.parse(text);
  const before = j.teamReinforcements.DEFEND;
  const out = text.replace(/"DEFEND"\s*:\s*\d+/, `"DEFEND": ${DEFEND}`);
  fs.writeFileSync(`vietnam-work/after/${n}`, out, 'utf8');
  const check = JSON.parse(out);
  if (n === 'CapturePointsA.json') firstPreset = out;
  console.log(`  ${n}: DEFEND ${before} → ${check.teamReinforcements.DEFEND}, ATTACK=${check.teamReinforcements.ATTACK}, modes=${JSON.stringify(check.modes)}, 点数=${check.plannedPoints.length} ${check.teamReinforcements.DEFEND === DEFEND ? '✓' : '❌'}`);
  await sleep(2600);
}
// 2) legacy 必需文件 = A 的内容（去掉 modes，保持 legacy 形状）
const legacy = firstPreset.replace(/^\{\r?\n(\s*)"modes"[^\r\n]*\r?\n/, (m, indent) => m.replace(/^\{\r?\n/, '{\n'));
fs.writeFileSync('vietnam-work/after/EsConfig__CapturePoints.json', legacy, 'utf8');
const lj = JSON.parse(legacy);
console.log(`  EsConfig/CapturePoints.json（新）: DEFEND=${lj.teamReinforcements.DEFEND} 点数=${lj.plannedPoints.length} modes=${JSON.stringify(lj.modes ?? null)} ✓`);

// 3) 上传
console.log('\n=== 上传 ===');
const jobs = [
  ['vietnam-work/after/CapturePointsA.json', `${BASE}/Points`, 'CapturePointsA.json'],
  ['vietnam-work/after/CapturePointsB.json', `${BASE}/Points`, 'CapturePointsB.json'],
  ['vietnam-work/after/CapturePointsC.json', `${BASE}/Points`, 'CapturePointsC.json'],
  ['vietnam-work/after/EsConfig__CapturePoints.json', `${BASE}/EsConfig`, 'CapturePoints.json'],
];
let ok = 0;
for (const [local, dir, name] of jobs) {
  const r = await upload(local, dir, name);
  if (r) ok++;
  console.log(`  ${r ? 'OK ' : 'FAIL'} ${dir}/${name}`);
  await sleep(800);
}
console.log(`上传成功 ${ok}/${jobs.length}`);

// 4) 回下载校验
console.log('\n=== 回下载校验 ===');
let v = 0;
for (const [local, dir, name] of jobs) {
  await sleep(2600);
  const t = await read(`${dir}/${name}`);
  const same = t !== null && t.trim() === fs.readFileSync(local, 'utf8').trim();
  if (same) v++;
  console.log(`  ${same ? 'MATCH' : 'DIFF '} ${dir}/${name}${t === null ? ' (读取失败)' : ''}`);
}
console.log(`\n校验一致 ${v}/${jobs.length}`);
