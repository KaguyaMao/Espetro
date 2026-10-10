// zoom-deploy.mjs — 上传 14 个 DefaultZoom 改动文件并回下载校验（before/ 已是二进制原样，作回滚点）
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
const OUT = process.argv[2] ?? 'zoom';
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
    r.on('error', reject); if (body !== undefined) r.write(body); r.end();
  });
}
const lr = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }), true);
const token = String(JSON.parse(lr.body).data ?? '');
const cookie = lr.cookie ?? '';
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;

async function downloadBin(rel, dest) {
  for (let a = 0; a < 5; a++) {
    try {
      const t = await req('POST', `${PANEL}/api/files/download?${q}&file_name=${encodeURIComponent(rel)}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
      const data = JSON.parse(t.body).data ?? {};
      if (!data.password) { await sleep(2500); continue; }
      const m = String(data.addr ?? '').match(/^wss?:\/\/([^/:]+)(:\d+)?/);
      const daemonUrl = 'https://' + new URL(PANEL).hostname + (m?.[2] ?? '');
      const res = await req('GET', `${daemonUrl}/download/${data.password}/${encodeURIComponent(rel.split('/').pop())}`, {}, undefined, false, true);
      if (res.status === 200 && res.buf.length > 100) { fs.writeFileSync(dest, res.buf); return res.buf; }
    } catch { }
    await sleep(2500);
  }
  return null;
}
async function upload(local, dir, name) {
  for (let a = 0; a < 5; a++) {
    try {
      const t = await req('POST', `${PANEL}/api/files/upload?${q}&upload_dir=${encodeURIComponent(dir)}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
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
      console.log(`  重试 ${name}: ${res.status} ${res.body.slice(0, 60)}`);
    } catch (e) { console.log(`  异常 ${name}: ${e.message}`); }
    await sleep(2500);
  }
  return false;
}

const man = JSON.parse(fs.readFileSync(path.join(OUT, 'manifest.json'), 'utf8'));
const files = man.files.filter((f) => f.status === 'ok');
console.log(`=== 上传 ${files.length} 个文件 ===`);
let up = 0;
for (const f of files) {
  const [ns, name] = f.id.split(':');
  const dir = `kubejs/data/${ns}/sbw/vehicles`;
  const ok = await upload(path.join(OUT, 'after', `${ns}__${name}.json`), dir, `${name}.json`);
  if (ok) up++;
  console.log(`  ${ok ? 'OK ' : 'FAIL'} ${dir}/${name}.json`);
  await sleep(600);
}
console.log(`上传成功 ${up}/${files.length}`);

console.log(`\n=== 回下载 SHA256 校验 ===`);
fs.mkdirSync(path.join(OUT, 'verify'), { recursive: true });
let v = 0;
for (const f of files) {
  const [ns, name] = f.id.split(':');
  const rel = `kubejs/data/${ns}/sbw/vehicles/${name}.json`;
  const tmp = path.join(OUT, 'verify', `${ns}__${name}.json`);
  const buf = await downloadBin(rel, tmp);
  const local = fs.readFileSync(path.join(OUT, 'after', `${ns}__${name}.json`));
  if (buf && sha(buf) === sha(local)) { v++; console.log(`  MATCH ${rel} (${buf.length} B)`); }
  else console.log(`  DIFF  ${rel} ${buf ? buf.length + ' B' : '下载失败'}`);
  await sleep(600);
}
console.log(`\n校验一致 ${v}/${files.length}`);
process.exit(v === files.length && up === files.length ? 0 : 1);
