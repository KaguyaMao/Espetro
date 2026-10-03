// patch-54r-speed.mjs — 补上 PKP / SVD 的弹速（7.62x54R 不在伤害规则内，但弹速规则要生效）
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const path = require('path');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT = process.argv[2];
fs.mkdirSync(OUT, { recursive: true });

const JOBS = [
  { rel: 'tacz/[Tacz1.1.7+]CIBR_GunsPack_v0.3_1.1.7/data/cib/data/guns/pkp_data.json', speed: 412.5 },
  { rel: 'tacz/[Tacz1.1.7+]CIBR_GunsPack_v0.3_1.1.7/data/cib/data/guns/svd_data.json', speed: 415 },
];

function lenientParse(text) {
  let out = '', inStr = false, esc = false;
  for (let i = 0; i < text.length; i++) {
    const c = text[i];
    if (inStr) { out += c; if (esc) esc = false; else if (c === '\\') esc = true; else if (c === '"') inStr = false; continue; }
    if (c === '"') { inStr = true; out += c; continue; }
    if (c === '/' && text[i + 1] === '/') { while (i < text.length && text[i] !== '\n') i++; out += '\n'; continue; }
    if (c === '/' && text[i + 1] === '*') { const e = text.indexOf('*/', i + 2); i = e < 0 ? text.length : e + 1; out += '\n'; continue; }
    out += c;
  }
  return JSON.parse(out.replace(/,(\s*[}\]])/g, '$1').replace(/:\s*\+(\d)/g, ': $1'));
}
function valueRange(text, key, from = 0) {
  const re = new RegExp(`"${key}"\\s*:\\s*`, 'g'); re.lastIndex = from;
  const m = re.exec(text); if (!m) return null;
  const start = m.index + m[0].length, open = text[start];
  if (open !== '{' && open !== '[') {
    const m2 = /^-?[\d.]+|^(true|false|null)|^"[^"]*"/.exec(text.slice(start));
    return m2 ? { start, end: start + m2[0].length } : null;
  }
  const close = open === '{' ? '}' : ']';
  let depth = 0;
  for (let j = start; j < text.length; j++) {
    const c = text[j];
    if (c === '"') { j = text.indexOf('"', j + 1); if (j < 0) return null; continue; }
    if (c === '/' && text[j + 1] === '/') { while (j < text.length && text[j] !== '\n') j++; continue; }
    if (c === '/' && text[j + 1] === '*') { const e = text.indexOf('*/', j + 2); j = e < 0 ? text.length : e + 1; continue; }
    if (c === open) depth++; else if (c === close) { depth--; if (depth === 0) return { start, end: j + 1 }; }
  }
  return null;
}
const replaceValue = (t, k, lit, from = 0) => { const r = valueRange(t, k, from); return r ? t.slice(0, r.start) + lit + t.slice(r.end) : null; };

const sleep = (ms) => new Promise(r => setTimeout(r, ms));
function req(method, urlStr, headers, body, getCookie) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, (res) => {
      let d = ''; res.on('data', c => (d += c));
      res.on('end', () => resolve({ status: res.statusCode, body: d, cookie: getCookie ? (res.headers['set-cookie'] ?? []).map(l => l.split(';')[0]).join('; ') : undefined }));
    });
    r.on('error', reject); if (body !== undefined) r.write(body); r.end();
  });
}
async function login() {
  const r = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }), true);
  return { token: String(JSON.parse(r.body).data ?? ''), cookie: r.cookie ?? '' };
}
async function newTask(token, cookie, dir) {
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&upload_dir=${encodeURIComponent(dir)}&token=${token}`;
  const res = await req('POST', `${PANEL}/api/files/upload?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
  const d = JSON.parse(res.body).data ?? {};
  const port = (String(d.addr).match(/^wss?:\/\/[^/:]+(:\d+)?/) || [])[1] ?? '';
  return { password: String(d.password ?? ''), daemonUrl: 'https://' + new URL(PANEL).hostname + port };
}
async function upload(filePath, name, daemonUrl, password) {
  const buf = fs.readFileSync(filePath);
  const b = '----DSHUpload' + Date.now().toString(16);
  const body = Buffer.concat([
    Buffer.from(`--${b}\r\nContent-Disposition: form-data; name="file"; filename="${name}"\r\nContent-Type: application/json\r\n\r\n`),
    buf, Buffer.from(`\r\n--${b}--\r\n`)]);
  const res = await req('POST', `${daemonUrl}/upload/${password}?overwrite=true`, {
    'X-Requested-With': 'XMLHttpRequest', 'Content-Type': `multipart/form-data; boundary=${b}`, 'Content-Length': String(body.length),
  }, body);
  return res.status === 200 && res.body.trim() === 'OK';
}

let { token, cookie } = await login();
for (const job of JOBS) {
  let text = null;
  for (let a = 0; a < 5 && text === null; a++) {
    const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
    const res = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: job.rel }));
    try { const d = String(JSON.parse(res.body).data ?? ''); if (d.trim().startsWith('{')) text = d; else await sleep(3500); } catch { await sleep(3500); }
  }
  if (!text) { console.log(`FAIL 抓取 ${job.rel}`); continue; }
  const before = lenientParse(text);
  const bullet = valueRange(text, 'bullet');
  text = replaceValue(text, 'speed', String(job.speed), bullet.start) ?? text;
  const after = lenientParse(text);
  const name = path.posix.basename(job.rel);
  fs.writeFileSync(path.join(OUT, name), text, 'utf8');
  console.log(`${name}: 弹速 ${before.bullet?.speed} → ${after.bullet?.speed}  (瞄准 ${after.aim_time} 爆头 ${after.bullet?.extra_damage?.head_shot_multiplier} 扩散 ${after.inaccuracy?.stand})`);
  const dir = path.posix.dirname(job.rel);
  for (let a = 1; a <= 3; a++) {
    try {
      const { password, daemonUrl } = await newTask(token, cookie, dir);
      if (await upload(path.join(OUT, name), name, daemonUrl, password)) { console.log('   ✔ 已上传'); break; }
      await sleep(2500);
    } catch (e) { console.log(`   重试${a}: ${e?.message ?? e}`); await sleep(2500); try { ({ token, cookie } = await login()); } catch { } }
  }
  await sleep(800);
}
