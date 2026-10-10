// deploy-dirarmor.mjs — 备份 → 建目录 → 上传 → 回下载校验（方向抗性数据）
// 用法: node deploy-dirarmor.mjs [dirarmor] [--dry]
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const path = require('path');
const https = require('https');
const crypto = require('crypto');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT = process.argv[2] ?? 'dirarmor';
const DRY = process.argv.includes('--dry');
const sleep = (ms) => new Promise((r) => setTimeout(r, ms));
const sha = (b) => crypto.createHash('sha256').update(b).digest('hex');

function req(method, urlStr, headers, body, getCookie, binary) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, (res) => {
      const chunks = [];
      res.on('data', (c) => chunks.push(c));
      res.on('end', () => {
        const buf = Buffer.concat(chunks);
        resolve({ status: res.statusCode, buf, body: binary ? null : buf.toString('utf8'), cookie: getCookie ? (res.headers['set-cookie'] ?? []).map((l) => l.split(';')[0]).join('; ') : undefined });
      });
    });
    r.on('error', reject);
    if (body !== undefined) r.write(body);
    r.end();
  });
}
async function login() {
  const r = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }), true);
  return { token: String(JSON.parse(r.body).data ?? ''), cookie: r.cookie ?? '' };
}
let ctx = await login();
const q = () => `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${ctx.token}`;

async function listDir(rel) {
  for (let a = 0; a < 4; a++) {
    try {
      const r = await req('GET', `${PANEL}/api/files/list?page=0&page_size=500&file_name=&target=${encodeURIComponent(rel)}&${q()}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': ctx.cookie });
      const b = JSON.parse(r.body);
      if (b.status === 200) return true;
    } catch { }
    await sleep(2000);
  }
  return false;
}
async function mkdir(rel) {
  for (let a = 0; a < 3; a++) {
    const r = await req('POST', `${PANEL}/api/files/mkdir?${q()}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': ctx.cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: rel }));
    console.log(`  mkdir ${rel} -> ${r.status} ${r.body.slice(0, 100)}`);
    if (r.body.includes('"status":200')) return true;
    await sleep(2000);
  }
  return false;
}
async function downloadBin(rel, dest) {
  for (let a = 0; a < 5; a++) {
    try {
      const t = await req('POST', `${PANEL}/api/files/download?${q()}&file_name=${encodeURIComponent(rel)}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': ctx.cookie });
      const data = JSON.parse(t.body).data ?? {};
      if (!data.password) { await sleep(2500); continue; }
      const m = String(data.addr ?? '').match(/^wss?:\/\/([^/:]+)(:\d+)?/);
      const daemonUrl = 'https://' + new URL(PANEL).hostname + (m?.[2] ?? '');
      const fileName = encodeURIComponent(rel.split('/').pop());
      const res = await req('GET', `${daemonUrl}/download/${data.password}/${fileName}`, {}, undefined, false, true);
      if (res.status === 200 && res.buf.length > 0) { fs.writeFileSync(dest, res.buf); return res.buf; }
    } catch { }
    await sleep(2500);
  }
  return null;
}
async function upload(local, dir, name) {
  for (let a = 0; a < 5; a++) {
    try {
      const t = await req('POST', `${PANEL}/api/files/upload?${q()}&upload_dir=${encodeURIComponent(dir)}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': ctx.cookie });
      const data = JSON.parse(t.body).data ?? {};
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
      console.log(`  上传重试 ${name}: ${res.status} ${res.body.slice(0, 80)}`);
    } catch (e) { console.log(`  上传异常 ${name}: ${e.message}`); }
    await sleep(2500);
  }
  return false;
}

const man = JSON.parse(fs.readFileSync(path.join(OUT, 'manifest.json'), 'utf8'));
const files = man.files.filter((f) => f.status === 'ok');
const newOnes = files.filter((f) => f.newOverride);
const existing = files.filter((f) => !f.newOverride);

// 1) 二进制备份现有文件
console.log(`=== 备份现有 ${existing.length} 个文件（二进制）===`);
fs.mkdirSync(path.join(OUT, 'before-bin'), { recursive: true });
let backupOk = 0;
for (const f of existing) {
  const rel = `kubejs/data/${f.id.split(':')[0]}/sbw/vehicles/${f.id.split(':')[1]}.json`;
  const dest = path.join(OUT, 'before-bin', f.outName);
  if (fs.existsSync(dest) && fs.statSync(dest).size > 500) { backupOk++; continue; }
  const buf = await downloadBin(rel, dest);
  if (buf) { backupOk++; console.log(`  OK  ${rel} (${buf.length} B)`); }
  else console.log(`  FAIL ${rel}`);
  await sleep(700);
}
console.log(`备份完成 ${backupOk}/${existing.length}`);

// 2) 新目录
const namespaces = [...new Set(files.map((f) => f.id.split(':')[0]))];
console.log(`\n=== 确认目录（命名空间: ${namespaces.join(', ')}）===`);
for (const ns of namespaces) {
  const rel = `kubejs/data/${ns}/sbw/vehicles`;
  if (await listDir(rel)) console.log(`  存在 ${rel}`);
  else { console.log(`  不存在 ${rel}，创建`); await mkdir(rel); await sleep(800); }
}

// 3) 上传
console.log(`\n=== 上传 ${files.length} 个文件 ===`);
let upOk = 0;
for (const f of files) {
  const [ns, name] = f.id.split(':');
  const dir = `kubejs/data/${ns}/sbw/vehicles`;
  const ok = await upload(path.join(OUT, 'after', f.outName), dir, `${name}.json`);
  if (ok) upOk++;
  console.log(`  ${ok ? 'OK ' : 'FAIL'} ${dir}/${name}.json`);
  await sleep(700);
}
console.log(`上传完成 ${upOk}/${files.length}`);

// 4) 回下载校验
console.log(`\n=== 回下载校验 ===`);
let vOk = 0;
for (const f of files) {
  const [ns, name] = f.id.split(':');
  const rel = `kubejs/data/${ns}/sbw/vehicles/${name}.json`;
  const tmp = path.join(OUT, 'verify', f.outName);
  fs.mkdirSync(path.join(OUT, 'verify'), { recursive: true });
  const buf = await downloadBin(rel, tmp);
  const local = fs.readFileSync(path.join(OUT, 'after', f.outName));
  if (buf && sha(buf) === sha(local)) { vOk++; console.log(`  MATCH ${rel} (${buf.length} B)`); }
  else console.log(`  DIFF  ${rel} ${buf ? buf.length + ' B' : '下载失败'}`);
  await sleep(700);
}
console.log(`\n校验一致 ${vOk}/${files.length}`);
console.log(`新建文件（回滚=删除）: ${newOnes.map((f) => f.id).join(', ') || '无'}`);
process.exit(vOk === files.length && backupOk === existing.length ? 0 : 1);
