// add-deploying-phase.mjs — 三图 logistics.json 允许在部署/备战阶段部署 Radio（从而能建所有工事）
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');
const PANEL = 'https://www.derpydoge.fun:20000';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const MAPS = ['server_battlefield', '越南', 'CREATE_PLUS'];
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
  for (let a = 0; a < 4; a++) {
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
fs.mkdirSync('deploy-phase/before', { recursive: true });
fs.mkdirSync('deploy-phase/after', { recursive: true });
for (const map of MAPS) {
  const text = await read(`EsWorld/${map}/EsConfig/logistics.json`);
  if (text === null) { console.log(`❌ ${map}: 读取失败`); continue; }
  fs.writeFileSync(`deploy-phase/before/${map}.json`, text, 'utf8');
  const before = JSON.parse(text).logistics.radio.allowed_phases;
  // 在数组里插入 "DEPLOYING"（兼容单行与多行两种写法，保留原有格式）
  let out = text;
  if (!/"DEPLOYING"/.test(out)) {
    out = out.replace(/"allowed_phases"\s*:\s*\[([^\]]*)\]/, (m, inner) => {
      if (!/"BATTLE"/.test(inner)) return m;
      if (/\n/.test(inner)) {
        const indent = (inner.match(/\n(\s*)"/) || [null, '  '])[1];
        return m.replace('"BATTLE"', `"DEPLOYING",\n${indent}"BATTLE"`);
      }
      return m.replace('"BATTLE"', '"DEPLOYING", "BATTLE"');
    });
  }
  fs.writeFileSync(`deploy-phase/after/${map}.json`, out, 'utf8');
  const after = JSON.parse(out).logistics.radio.allowed_phases;
  const ok = after.includes('DEPLOYING') && after.includes('BATTLE');
  console.log(`  ${map}: ${JSON.stringify(before)} → ${JSON.stringify(after)} ${ok ? '✓' : '❌'}`);
  if (!ok) process.exitCode = 1;
  await sleep(2600);
}
if (process.exitCode) { console.log('有失败，终止'); process.exit(1); }
console.log('\n=== 上传 ===');
let ok = 0;
for (const map of MAPS) {
  const r = await upload(`deploy-phase/after/${map}.json`, `EsWorld/${map}/EsConfig`, 'logistics.json');
  if (r) ok++;
  console.log(`  ${r ? 'OK ' : 'FAIL'} ${map}`);
  await sleep(700);
}
console.log(`上传成功 ${ok}/${MAPS.length}`);
console.log('\n=== 回下载校验 ===');
let v = 0;
for (const map of MAPS) {
  await sleep(2600);
  const t = await read(`EsWorld/${map}/EsConfig/logistics.json`);
  const same = t !== null && t.trim() === fs.readFileSync(`deploy-phase/after/${map}.json`, 'utf8').trim();
  const phases = t ? JSON.parse(t).logistics.radio.allowed_phases : null;
  if (same) v++;
  console.log(`  ${same ? 'MATCH' : 'DIFF '} ${map}  allowed_phases=${JSON.stringify(phases)}`);
}
console.log(`\n校验一致 ${v}/${MAPS.length}`);
