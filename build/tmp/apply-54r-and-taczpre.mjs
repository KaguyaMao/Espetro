// apply-54r-and-taczpre.mjs — ① tacz-pre.toml 打开 DefaultPackDebug ② PKP/SVD 伤害按 .308 档
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
async function fetchText(rel, token, cookie) {
  for (let a = 0; a < 5; a++) {
    const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
    const res = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: rel }));
    try { const d = String(JSON.parse(res.body).data ?? ''); if (d.trim().length > 0) return d; } catch { }
    await sleep(3500);
  }
  return null;
}
async function upload(filePath, name, dir, mime, token, cookie) {
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&upload_dir=${encodeURIComponent(dir)}&token=${token}`;
  const t = await req('POST', `${PANEL}/api/files/upload?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
  const d = JSON.parse(t.body).data ?? {};
  const port = (String(d.addr).match(/^wss?:\/\/[^/:]+(:\d+)?/) || [])[1] ?? '';
  const daemonUrl = 'https://' + new URL(PANEL).hostname + port;
  const buf = fs.readFileSync(filePath);
  const b = '----DSHUpload' + Date.now().toString(16);
  const body = Buffer.concat([
    Buffer.from(`--${b}\r\nContent-Disposition: form-data; name="file"; filename="${name}"\r\nContent-Type: ${mime}\r\n\r\n`),
    buf, Buffer.from(`\r\n--${b}--\r\n`)]);
  const res = await req('POST', `${daemonUrl}/upload/${d.password}?overwrite=true`, {
    'X-Requested-With': 'XMLHttpRequest', 'Content-Type': `multipart/form-data; boundary=${b}`, 'Content-Length': String(body.length),
  }, body);
  return res.status === 200 && res.body.trim() === 'OK';
}

let { token, cookie } = await login();

// ---------- ① tacz-pre.toml ----------
const tomlRel = 'tacz/tacz-pre.toml';
let toml = await fetchText(tomlRel, token, cookie);
if (toml === null) console.log('FAIL 读取 tacz-pre.toml');
else {
  console.log('=== 服务端原 tacz-pre.toml ===');
  console.log(toml.replace(/\r/g, ''));
  let next;
  if (/DefaultPackDebug\s*=\s*false/i.test(toml)) next = toml.replace(/DefaultPackDebug\s*=\s*false/i, 'DefaultPackDebug = true');
  else if (/DefaultPackDebug\s*=\s*true/i.test(toml)) next = toml;
  else next = toml.replace(/\s*$/, '\n') + '\n[gunpack]\n# 保护手动修改过的默认枪包（tacz_default_gun）不被启动时覆盖\nDefaultPackDebug = true\n';
  fs.writeFileSync(path.join(OUT, 'tacz-pre.toml'), next, 'utf8');
  if (next !== toml) {
    const ok = await upload(path.join(OUT, 'tacz-pre.toml'), 'tacz-pre.toml', 'tacz', 'text/plain', token, cookie);
    console.log(`\n=== 改后 ===\n${next.replace(/\r/g, '')}\n上传: ${ok ? '✔ OK' : '✘ 失败'}`);
  } else console.log('\n（已经是 true，无需改动）');
  await sleep(800);
}

// ---------- ② PKP / SVD 伤害按 .308 ----------
const GUNS = [
  { rel: 'tacz/[Tacz1.1.7+]CIBR_GunsPack_v0.3_1.1.7/data/cib/data/guns/pkp_data.json' },
  { rel: 'tacz/[Tacz1.1.7+]CIBR_GunsPack_v0.3_1.1.7/data/cib/data/guns/svd_data.json' },
];
const DMG = [{ distance: 75, damage: 15 }, { distance: 125, damage: 13 }, { distance: 'infinite', damage: 10 }];

for (const g of GUNS) {
  let text = await fetchText(g.rel, token, cookie);
  if (text === null) { console.log(`FAIL 读取 ${g.rel}`); continue; }
  const before = lenientParse(text);
  const bullet = valueRange(text, 'bullet');
  text = replaceValue(text, 'damage', String(DMG[0].damage), bullet.start) ?? text;
  const b2 = valueRange(text, 'bullet');
  const ex = valueRange(text, 'extra_damage', b2.start);
  const da = ex ? valueRange(text, 'damage_adjust', ex.start) : null;
  if (da) {
    const arr = '[' + DMG.map(e => `{"distance":${typeof e.distance === 'string' ? `"${e.distance}"` : e.distance},"damage":${e.damage}}`).join(',') + ']';
    text = text.slice(0, da.start) + arr + text.slice(da.end);
  }
  const after = lenientParse(text);
  const name = path.posix.basename(g.rel);
  fs.writeFileSync(path.join(OUT, name), text, 'utf8');
  const ok = await upload(path.join(OUT, name), name, path.posix.dirname(g.rel), 'application/json', token, cookie);
  console.log(`${name}: 伤害 ${before.bullet?.damage}→${after.bullet?.damage}  衰减 ${JSON.stringify(before.bullet?.extra_damage?.damage_adjust)} → ${JSON.stringify(after.bullet?.extra_damage?.damage_adjust)}  上传: ${ok ? '✔ OK' : '✘ 失败'}`);
  await sleep(800);
}
