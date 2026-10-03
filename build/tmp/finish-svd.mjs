// finish-svd.mjs — 给 SVD 应用 .308 伤害档（补齐上一次因"冷却中"失败的步骤）
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

const REL = 'tacz/[Tacz1.1.7+]CIBR_GunsPack_v0.3_1.1.7/data/cib/data/guns/svd_data.json';
const DMG = [{ distance: 75, damage: 15 }, { distance: 125, damage: 13 }, { distance: 'infinite', damage: 10 }];
let { token, cookie } = await login();

let text = null;
for (let a = 0; a < 8 && text === null; a++) {
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
  const res = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: REL }));
  try {
    const d = String(JSON.parse(res.body).data ?? '');
    if (d.trim().startsWith('{')) text = d; else { console.log(`  第${a + 1}次被限流/非 JSON：${d.slice(0, 40)}`); await sleep(4000); }
  } catch (e) { console.log(`  第${a + 1}次异常：${String(e.message).slice(0, 60)}`); await sleep(4000); }
  if (text === null) { ({ token, cookie } = await login()); }
}
if (text === null) { console.log('FAIL：一直读不到 SVD 数据'); process.exit(1); }

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
const file = path.join(OUT, 'svd_data.json');
fs.writeFileSync(file, text, 'utf8');
console.log(`svd: 伤害 ${before.bullet?.damage}→${after.bullet?.damage}  衰减 ${JSON.stringify(before.bullet?.extra_damage?.damage_adjust)} → ${JSON.stringify(after.bullet?.extra_damage?.damage_adjust)}`);

// 上传
let ok = false;
for (let a = 0; a < 4 && !ok; a++) {
  try {
    const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&upload_dir=${encodeURIComponent(path.posix.dirname(REL))}&token=${token}`;
    const t = await req('POST', `${PANEL}/api/files/upload?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
    const d = JSON.parse(t.body).data ?? {};
    const port = (String(d.addr).match(/^wss?:\/\/[^/:]+(:\d+)?/) || [])[1] ?? '';
    const daemonUrl = 'https://' + new URL(PANEL).hostname + port;
    const buf = fs.readFileSync(file);
    const bnd = '----DSHUpload' + Date.now().toString(16);
    const body = Buffer.concat([
      Buffer.from(`--${bnd}\r\nContent-Disposition: form-data; name="file"; filename="svd_data.json"\r\nContent-Type: application/json\r\n\r\n`),
      buf, Buffer.from(`\r\n--${bnd}--\r\n`)]);
    const res = await req('POST', `${daemonUrl}/upload/${d.password}?overwrite=true`, {
      'X-Requested-With': 'XMLHttpRequest', 'Content-Type': `multipart/form-data; boundary=${bnd}`, 'Content-Length': String(body.length),
    }, body);
    ok = res.status === 200 && res.body.trim() === 'OK';
    console.log(ok ? '   ✔ 已上传' : `   上传重试${a + 1}: ${res.body.slice(0, 80)}`);
  } catch (e) { console.log(`   上传异常：${String(e.message).slice(0, 60)}`); try { ({ token, cookie } = await login()); } catch { } }
  if (!ok) await sleep(3000);
}
