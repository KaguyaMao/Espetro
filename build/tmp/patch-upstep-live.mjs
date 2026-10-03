// patch-upstep-live.mjs — 读取服务器当前载具 json → 把所有载具 UpStep 改为 1.75 → 传回
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
const UPSTEP = process.argv[2] ?? '1.75';
const BAK = path.join(TMP, `srv-vehicles-before-upstep-${UPSTEP}`);
const MIRROR = path.join(TMP, `srv-vehicles-upstep-${UPSTEP}`);
const OUT_FILE = path.join(TMP, `patch-upstep-${UPSTEP}-out.txt`);
fs.mkdirSync(BAK, { recursive: true });
fs.mkdirSync(MIRROR, { recursive: true });
const DR = 'kubejs/data/dragonrise_reforge/sbw/vehicles';
const FCP = 'kubejs/data/fcp/sbw/vehicles';
const DR_FILES = ['ah64', 'bmp3', 'csk181', 'm1126', 'm1128', 'm113', 'm1296', 'm1a2sepv1', 'm1a2sepv2', 'm3a3',
  'mv3_armed', 'mv3_supply', 'mv3', 'sx1_a', 'sx1', 't72b3', 't90mh', 'ural4320_supply', 'ural4320_zu23', 'ural4320',
  'z20', 'zbd04a', 'zbd05', 'zbl08', 'zlt11', 'zsl10', 'ztd05', 'ztz96a', 'ztz99a'];
const FCP_FILES = ['bmp1am', 'bmp2', 'bmp2d', 'bmp2m', 'btr80', 'btr82', 'gaz_tigr_gl', 'gaz_tigr_mg', 'gaz_tigr_rws',
  'matv_9in1', 'matv_crow', 'matv_tow', 'matv', 'stryker_dragoon', 'stryker_m2', 'stryker_mgs', 'stryker_mortar', 't72av'];
const JOBS = [
  ...DR_FILES.map(n => ({ ns: 'dragonrise', dir: DR, name: n })),
  ...FCP_FILES.map(n => ({ ns: 'fcp', dir: FCP, name: n })),
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

// ---- UpStep 文本处理 ----
function patchUpStep(text) {
  const hits = [...text.matchAll(/"UpStep"\s*:\s*(-?[0-9.]+)/g)];
  if (hits.length === 1) {
    const before = hits[0][1];
    const out = text.slice(0, hits[0].index) + `"UpStep": ${UPSTEP}` + text.slice(hits[0].index + hits[0][0].length);
    return { text: out, before, after: UPSTEP, added: false };
  }
  if (hits.length > 1) throw new Error(`UpStep 出现 ${hits.length} 次，需人工确认`);

  // 没有 UpStep → 插到 ID 行后（保留缩进与逗号风格）
  const lines = text.split('\n');
  const idIdx = lines.findIndex(l => /"ID"\s*:/.test(l));
  if (idIdx < 0) throw new Error('no ID line');
  const ind = lines[idIdx].match(/^\s*/)[0];
  // ID 行若原本是最后一行（无逗号）则补逗号
  if (!/,\s*$/.test(lines[idIdx])) lines[idIdx] = lines[idIdx].replace(/\s*$/, ',');
  lines.splice(idIdx + 1, 0, `${ind}"UpStep": ${UPSTEP},`);
  return { text: lines.join('\n'), before: '(无)', after: UPSTEP, added: true };
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
      const r = patchUpStep(raw);
      JSON.parse(r.text); // 校验
      fs.writeFileSync(path.join(MIRROR, local), r.text, 'utf8');
      const sent = await upload(ctx, job.dir, `${job.name}.json`, r.text);
      if (sent) { ok++; log.push(`OK   ${rel}  UpStep ${r.before} -> ${r.after}${r.added ? ' (新增)' : ''}`); }
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
