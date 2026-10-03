import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const path = require('path');
const https = require('https');

/** bump-turret-health.mjs — 把已加过 PartHealth 的载具"炮塔血量 +200" */
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT = process.argv[2];
const DELTA = Number(process.argv[3] ?? 200);
fs.mkdirSync(OUT, { recursive: true });

const NS_DIRS = {
  dragonrise: 'kubejs/data/dragonrise_reforge/sbw/vehicles',
  fcp: 'kubejs/data/fcp/sbw/vehicles',
};
const TARGETS = [
  ['dragonrise', 'm1a2sepv1.json'], ['dragonrise', 'm1a2sepv2.json'], ['dragonrise', 't72b3.json'],
  ['dragonrise', 't90mh.json'], ['dragonrise', 'ztz99a.json'], ['dragonrise', 'ztz96a.json'],
  ['dragonrise', 'bmp3.json'], ['dragonrise', 'm3a3.json'], ['dragonrise', 'zbd04a.json'],
  ['dragonrise', 'zbd05.json'], ['dragonrise', 'zbl08.json'], ['dragonrise', 'm1296.json'],
  ['fcp', 'bmp1am.json'], ['fcp', 'btr82.json'], ['fcp', 'stryker_dragoon.json'],
];

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
async function fetchJson(rel, token, cookie) {
  for (let a = 0; a < 6; a++) {
    const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
    const res = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: rel }));
    try { const d = String(JSON.parse(res.body).data ?? ''); if (d.trim().startsWith('{')) return d; } catch { }
    await sleep(3500);
  }
  return null;
}
async function upload(filePath, name, dir, token, cookie) {
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&upload_dir=${encodeURIComponent(dir)}&token=${token}`;
  const t = await req('POST', `${PANEL}/api/files/upload?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
  if (t.status !== 200) return false;
  const d = JSON.parse(t.body).data ?? {};
  const port = (String(d.addr).match(/^wss?:\/\/[^/:]+(:\d+)?/) || [])[1] ?? '';
  const daemonUrl = 'https://' + new URL(PANEL).hostname + port;
  const buf = fs.readFileSync(filePath);
  const b = '----DSHUpload' + Date.now().toString(16);
  const body = Buffer.concat([
    Buffer.from(`--${b}\r\nContent-Disposition: form-data; name="file"; filename="${name}"\r\nContent-Type: application/json\r\n\r\n`),
    buf, Buffer.from(`\r\n--${b}--\r\n`)]);
  const res = await req('POST', `${daemonUrl}/upload/${d.password}?overwrite=true`, {
    'X-Requested-With': 'XMLHttpRequest', 'Content-Type': `multipart/form-data; boundary=${b}`, 'Content-Length': String(body.length),
  }, body);
  return res.status === 200 && res.body.trim() === 'OK';
}

let { token, cookie } = await login();
const report = [];
for (const [ns, file] of TARGETS) {
  const rel = `${NS_DIRS[ns]}/${file}`;
  let text = await fetchJson(rel, token, cookie);
  if (text === null) { report.push(`FAIL 读取 ${rel}`); continue; }
  const before = JSON.parse(text);
  if (!before.PartHealth) { report.push(`跳过 ${rel}（没有 PartHealth）`); continue; }
  const old = Number(before.PartHealth.Turret);
  const ph = valueRange(text, 'PartHealth');
  if (!ph) { report.push(`FAIL 定位 PartHealth: ${rel}`); continue; }
  text = replaceValue(text, 'Turret', String(old + DELTA), ph.start);
  const after = JSON.parse(text);
  const local = path.join(OUT, `${ns}__${file}`);
  fs.writeFileSync(local, text, 'utf8');
  const up = await upload(local, file, NS_DIRS[ns], token, cookie);
  report.push(`${up ? '✔' : '✘'} ${file.padEnd(22)} 炮塔 ${old} → ${after.PartHealth.Turret}   （其它部件 ${after.PartHealth.LeftWheel}/${after.PartHealth.RightWheel}/${after.PartHealth.MainEngine}/${after.PartHealth.SubEngine}）`);
  await sleep(700);
}
fs.writeFileSync(path.join(OUT, 'report.txt'), report.join('\n'), 'utf8');
console.log(report.join('\n'));
