// write-server-net-tuning.mjs — 备份并改写服务端网络相关配置（server.properties / config/espoints-common.toml）
// 用法: node write-server-net-tuning.mjs [--dry]
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const DRY = process.argv.includes('--dry');
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
const lr = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }), true);
const token = String(JSON.parse(lr.body).data ?? '');
const cookie = lr.cookie ?? '';
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;

async function readFile(target) {
  for (let a = 0; a < 6; a++) {
    const r = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target }));
    try { const b = JSON.parse(r.body); if (b.status === 200) return String(b.data ?? ''); } catch { }
    await sleep(3000);
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
    console.log(`  上传重试 ${name}: ${res.status} ${res.body.slice(0, 60)}`);
    await sleep(2500);
  }
  return false;
}

fs.mkdirSync('net-tuning', { recursive: true });

// 1) server.properties：视距 / 模拟距离 / 实体广播范围
const propsText = await readFile('server.properties');
if (!propsText) { console.log('读取 server.properties 失败'); process.exit(1); }
fs.writeFileSync('net-tuning/server.properties.bak', propsText, 'utf8');
const want = {
  'view-distance': '12',
  'simulation-distance': '8',
  'entity-broadcast-range-percentage': '150',
};
let changed = 0;
const outLines = propsText.split('\n').map((line) => {
  const m = /^([a-zA-Z0-9._-]+)=(.*)$/.exec(line.trim());
  if (m && want[m[1]] !== undefined && m[2] !== want[m[1]]) {
    console.log(`  server.properties: ${m[1]}: ${m[2]} -> ${want[m[1]]}`);
    changed++;
    return `${m[1]}=${want[m[1]]}`;
  }
  return line;
});
const newProps = outLines.join('\n');
fs.writeFileSync('net-tuning/server.properties.new', newProps, 'utf8');
if (!DRY && changed) {
  const ok = await upload('net-tuning/server.properties.new', '.', 'server.properties');
  console.log(`  server.properties 上传: ${ok ? 'OK' : 'FAIL'}`);
}
await sleep(2000);

// 2) config/espoints-common.toml：战术地图传输预算
const espText = await readFile('config/espoints-common.toml');
if (espText) {
  fs.writeFileSync('net-tuning/espoints-common.toml.bak', espText, 'utf8');
  const want2 = { tacticalMapPlayerTransferKiBps: '64', tacticalMapGlobalTransferKiBps: '1024' };
  let changed2 = 0;
  const newEsp = espText.split('\n').map((line) => {
    const m = /^(\s*)([A-Za-z0-9_]+)(\s*=\s*)(\d+)(.*)$/.exec(line);
    if (m && want2[m[2]] !== undefined && m[4] !== want2[m[2]]) {
      console.log(`  espoints-common.toml: ${m[2]}: ${m[4]} -> ${want2[m[2]]}`);
      changed2++;
      return `${m[1]}${m[2]} = ${want2[m[2]]}${m[5]}`;
    }
    return line;
  }).join('\n');
  fs.writeFileSync('net-tuning/espoints-common.toml.new', newEsp, 'utf8');
  if (!DRY && changed2) {
    const ok = await upload('net-tuning/espoints-common.toml.new', 'config', 'espoints-common.toml');
    console.log(`  espoints-common.toml 上传: ${ok ? 'OK' : 'FAIL'}`);
  }
} else {
  console.log('读取 config/espoints-common.toml 失败（跳过）');
}
console.log(`完成（server.properties 改动 ${changed} 项${DRY ? '，dry-run 未上传' : ''}）`);
