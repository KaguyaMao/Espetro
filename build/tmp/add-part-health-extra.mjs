import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const path = require('path');
const https = require('https');

/** add-part-health-extra.mjs — 给指定载具加顶层 PartHealth（按类别） */
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT = process.argv[2];
const JOBS = process.argv.slice(3).map(s => { const [ns, file, kind] = s.split(':'); return { ns, file, kind }; });
fs.mkdirSync(OUT, { recursive: true });

const BLOCKS = {
  MBT: '{"Turret":300,"LeftWheel":100,"RightWheel":100,"MainEngine":150,"SubEngine":150}',
  IFV: '{"Turret":200,"LeftWheel":100,"RightWheel":100,"MainEngine":150,"SubEngine":150}',
};
const NS_DIRS = {
  dragonrise: 'kubejs/data/dragonrise_reforge/sbw/vehicles',
  fcp: 'kubejs/data/fcp/sbw/vehicles',
};

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
const flat = (o, p = '', out = {}) => {
  if (o === null || typeof o !== 'object') { out[p] = o; return out; }
  if (Array.isArray(o)) { o.forEach((v, i) => flat(v, `${p}[${i}]`, out)); return out; }
  for (const k of Object.keys(o)) flat(o[k], p ? `${p}.${k}` : k, out);
  return out;
};

let { token, cookie } = await login();
const report = [];
for (const job of JOBS) {
  const rel = `${NS_DIRS[job.ns]}/${job.file}`;
  const text = await fetchJson(rel, token, cookie);
  if (text === null) { report.push(`FAIL 读取 ${rel}`); continue; }
  const before = JSON.parse(text);
  if (before.PartHealth) { report.push(`跳过 ${rel}（已有 PartHealth ${JSON.stringify(before.PartHealth)}）`); continue; }
  const block = BLOCKS[job.kind] ?? BLOCKS.IFV;
  const m = text.match(/^([ \t]*)"MaxHealth"\s*:\s*[^,\n]*,?[ \t]*$/m);
  let next;
  if (m) next = text.replace(m[0], `${m[0].replace(/,?\s*$/, ',')}\n${m[1]}"PartHealth": ${block},`);
  else { const i = text.indexOf('{'); next = text.slice(0, i + 1) + `\n  "PartHealth": ${block},` + text.slice(i + 1); }
  const after = JSON.parse(next);
  const fa = flat(before), fb = flat(after);
  const stray = [...new Set([...Object.keys(fa), ...Object.keys(fb)])].filter(k => !k.startsWith('PartHealth') && JSON.stringify(fa[k]) !== JSON.stringify(fb[k]));
  const local = path.join(OUT, `${job.ns}__${job.file}`);
  fs.writeFileSync(local, next, 'utf8');
  const up = await upload(local, job.file, NS_DIRS[job.ns], token, cookie);
  report.push(`${up ? '✔' : '✘'} ${rel}  →  PartHealth=${JSON.stringify(after.PartHealth)}${stray.length ? `  ⚠其它改动: ${stray.join(',')}` : ''}`);
  await sleep(700);
}
fs.writeFileSync(path.join(OUT, 'report.txt'), report.join('\n'), 'utf8');
console.log(report.join('\n'));
