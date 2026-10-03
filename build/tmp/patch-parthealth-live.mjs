// patch-parthealth-live.mjs — 直接读服务器当前载具 json → 改 PartHealth → 传回
// 坦克:      Turret 200 / LeftWheel 100 / RightWheel 100 / MainEngine 150 / SubEngine 150
// 步兵战车:  Turret 150 / 其余同上（履带 + 轮式同一套）
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const path = require('path');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const TMP = 'D:/minecraft/modp/Espetro/build/tmp';
const BAK = path.join(TMP, 'srv-vehicles-before-ph4');
const MIRROR = path.join(TMP, 'srv-vehicles-new');
const OUT_FILE = path.join(TMP, 'patch-parthealth-out.txt');
fs.mkdirSync(BAK, { recursive: true });
fs.mkdirSync(MIRROR, { recursive: true });

const DR = 'kubejs/data/dragonrise_reforge/sbw/vehicles';
const FCP = 'kubejs/data/fcp/sbw/vehicles';
const TUR = { tank: 150, ifv: 100 };
const TAIL = { LeftWheel: 40, RightWheel: 40, MainEngine: 60, SubEngine: 60 };
const JOBS = [
  ...[ 'm1a2sepv1', 'm1a2sepv2', 't72b3', 't90mh', 'ztz96a', 'ztz99a' ].map(n => ({ ns: 'dragonrise', dir: DR, name: n, kind: 'tank' })),
  ...[ 'bmp3', 'csk181', 'm1126', 'm1128', 'm113', 'm1296', 'm3a3', 'zbd04a', 'zbd05', 'zbl08', 'zlt11', 'zsl10', 'ztd05' ].map(n => ({ ns: 'dragonrise', dir: DR, name: n, kind: 'ifv' })),
  ...[ 'bmp1am', 'bmp2', 'bmp2d', 'bmp2m', 'btr80', 'btr82', 'gaz_tigr_gl', 'gaz_tigr_mg', 'gaz_tigr_rws', 'matv', 'matv_9in1', 'matv_crow', 'matv_tow', 'stryker_dragoon', 'stryker_m2', 'stryker_mgs', 'stryker_mortar' ].map(n => ({ ns: 'fcp', dir: FCP, name: n, kind: 'ifv' })),
];

const log = [];
const sleep = ms => new Promise(r => setTimeout(r, ms));
function httpsReq(method, urlStr, headers, body, getCookie) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const req = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, res => {
      let d = ''; res.on('data', c => (d += c));
      res.on('end', () => resolve({ status: res.statusCode, body: d, cookie: getCookie ? (res.headers['set-cookie'] ?? []).map(l => l.split(';')[0]).join('; ') : undefined }));
    });
    req.on('error', reject);
    if (body !== undefined) req.write(body);
    req.end();
  });
}
async function login() {
  const r = await httpsReq('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }), true);
  const token = String(JSON.parse(r.body).data ?? '');
  if (!token) throw new Error('login failed');
  return { token, cookie: r.cookie ?? '' };
}
async function readFile(rel, ctx) {
  for (let a = 1; a <= 8; a++) {
    const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${ctx.token}`;
    const res = await httpsReq('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': ctx.cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: rel }));
    let data = '';
    try { data = String(JSON.parse(res.body).data ?? ''); } catch { data = ''; }
    if (data.trim().startsWith('{')) return data;
    await sleep(2500);
    try { Object.assign(ctx, await login()); } catch { }
  }
  throw new Error('read failed: ' + rel);
}
async function newTask(ctx, dir) {
  for (let a = 1; a <= 6; a++) {
    const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&upload_dir=${encodeURIComponent(dir)}&token=${ctx.token}`;
    const res = await httpsReq('POST', `${PANEL}/api/files/upload?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': ctx.cookie });
    try {
      const data = JSON.parse(res.body).data ?? {};
      const m = String(data.addr ?? '').match(/^wss?:\/\/([^/:]+)(:\d+)?/);
      if (data.password) return { password: String(data.password), daemonUrl: 'https://' + new URL(PANEL).hostname + (m?.[2] ?? '') };
    } catch { }
    await sleep(2000);
    try { Object.assign(ctx, await login()); } catch { }
  }
  throw new Error('upload task failed');
}
async function upload(ctx, dir, name, text) {
  for (let a = 1; a <= 6; a++) {
    try {
      const { password, daemonUrl } = await newTask(ctx, dir);
      const buf = Buffer.from(text, 'utf8');
      const boundary = '----DSHUpload' + Date.now().toString(16);
      const head = Buffer.from(`--${boundary}\r\nContent-Disposition: form-data; name="file"; filename="${name}"\r\nContent-Type: application/json\r\n\r\n`);
      const tail = Buffer.from(`\r\n--${boundary}--\r\n`);
      const body = Buffer.concat([head, buf, tail]);
      const res = await httpsReq('POST', `${daemonUrl}/upload/${password}?overwrite=true`, { 'X-Requested-With': 'XMLHttpRequest', 'Content-Type': `multipart/form-data; boundary=${boundary}`, 'Content-Length': String(body.length) }, body);
      if (res.status === 200 && res.body.trim() === 'OK') return true;
      log.push(`  上传重试${a}: ${res.status} ${res.body.slice(0, 80)}`);
    } catch (e) { log.push(`  上传重试${a} 异常: ${e?.message ?? e}`); }
    await sleep(2500);
    try { Object.assign(ctx, await login()); } catch { }
  }
  return false;
}

// ---- PartHealth 文本处理 ----
function patchPartHealth(text, turret) {
  const re = /"PartHealth"\s*:\s*\{[^}]*\}/;
  const build = (style, indent) => {
    const vals = { Turret: turret, ...TAIL };
    if (style === 'compact') return `"PartHealth": {` + Object.entries(vals).map(([k, v]) => `"${k}":${v}`).join(',') + `}`;
    return `"PartHealth": {\n` + Object.entries(vals).map(([k, v]) => `${indent}  "${k}": ${v}`).join(',\n') + `\n${indent}}`;
  };
  const m = re.exec(text);
  if (m) {
    const style = /\n/.test(m[0]) ? 'pretty' : 'compact';
    const indent = (text.slice(0, m.index).match(/[ \t]*$/)?.[0]) ?? '  ';
    const before = m[0];
    const after = build(style, indent);
    return { text: text.slice(0, m.index) + after + text.slice(m.index + m[0].length), before, after, added: false };
  }
  // 没有 PartHealth -> 插到 MaxHealth 行后
  const lines = text.split('\n');
  const i = lines.findIndex(l => /"MaxHealth"\s*:/.test(l));
  if (i < 0) throw new Error('no MaxHealth line');
  const ind = lines[i].match(/^\s*/)[0];
  const entry = `${ind}"PartHealth": {` + Object.entries({ Turret: turret, ...TAIL }).map(([k, v]) => `"${k}":${v}`).join(',') + `},`;
  lines.splice(i + 1, 0, entry);
  return { text: lines.join('\n'), before: '(无)', after: entry.trim(), added: true };
}

async function main() {
  const ctx = await login();
  let ok = 0;
  for (const job of JOBS) {
    const rel = `${job.dir}/${job.name}.json`;
    const local = `${job.ns}__${job.name}.json`;
    try {
      const raw = await readFile(rel, ctx);
      fs.writeFileSync(path.join(BAK, local), raw, 'utf8');
      const r = patchPartHealth(raw, TUR[job.kind]);
      JSON.parse(r.text); // 校验
      fs.writeFileSync(path.join(MIRROR, local), r.text, 'utf8');
      const sent = await upload(ctx, job.dir, `${job.name}.json`, r.text);
      if (sent) { ok++; log.push(`OK   ${rel}  [${job.kind}] ${r.added ? '新增' : '更新'} ${r.before} -> Turret ${TUR[job.kind]}`); }
      else log.push(`FAIL ${rel} (上传失败)`);
    } catch (e) {
      log.push(`FAIL ${rel}: ${e?.message ?? e}`);
    }
    await sleep(400);
  }
  log.push(`完成: ${ok}/${JOBS.length}`);
  fs.writeFileSync(OUT_FILE, log.join('\n'), 'utf8');
  console.log(log.join('\n'));
  if (ok !== JOBS.length) process.exit(1);
}
main().catch(e => { log.push('ERROR: ' + (e?.stack ?? e)); fs.writeFileSync(OUT_FILE, log.join('\n'), 'utf8'); console.log(log.join('\n')); process.exit(1); });
