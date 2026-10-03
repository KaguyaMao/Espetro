// fix-points-troops.mjs — 修 CREATE_PLUS：补回必需配置 + 预设加 modes + 兵力统一 75/350
// 用法: node fix-points-troops.mjs [--dry]
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');
const PANEL = 'https://www.derpydoge.fun:20000';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const DRY = process.argv.includes('--dry');
const ATTACK = 75;
const DEFEND = 350;
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
const lr = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: 'boy', password: 'boY1145141919810Fuck' }), true);
const token = String(JSON.parse(lr.body).data ?? '');
const cookie = lr.cookie ?? '';
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
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
async function read(target) {
  for (let a = 0; a < 5; a++) {
    const r = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target }));
    try { const b = JSON.parse(r.body); if (b.status === 200) return String(b.data ?? ''); } catch { }
    await sleep(2600);
  }
  return null;
}

const BEFORE = 'points-work/before';
fs.mkdirSync('points-work/after', { recursive: true });
const readB = (n) => fs.readFileSync(`${BEFORE}/${n}`, 'utf8');
const writeA = (n, t) => fs.writeFileSync(`points-work/after/${n}`, t, 'utf8');

// 1) 预设 A/B：加 modes + 兵力 75/350
function tunePreset(text, withModes) {
  let out = text;
  if (withModes && !/"modes"\s*:/.test(out)) {
    out = out.replace(/^\{\n/, '{\n  "modes": ["AAS"],\n');
  }
  out = out.replace(/"ATTACK"\s*:\s*\d+/, `"ATTACK": ${ATTACK}`);
  out = out.replace(/"DEFEND"\s*:\s*\d+/, `"DEFEND": ${DEFEND}`);
  return out;
}
const presetA = tunePreset(readB('Points__CapturePointsA.json'), true);
const presetB = tunePreset(readB('Points__CapturePointsB.json'), true);
// 2) legacy 必需文件 = A 的内容（不带 modes，保持原 legacy 形状）
const legacy = tunePreset(readB('Points__CapturePointsA.json'), false);
// 3) game.json 初始兵力
const game = readB('EsConfig__game.json')
  .replace(/"initial_attack"\s*:\s*\d+/, `"initial_attack": ${ATTACK}`)
  .replace(/"initial_defend"\s*:\s*\d+/, `"initial_defend": ${DEFEND}`);

writeA('Points__CapturePointsA.json', presetA);
writeA('Points__CapturePointsB.json', presetB);
writeA('EsConfig__CapturePoints.json', legacy);
writeA('EsConfig__game.json', game);

// 校验本地生成的 JSON 与目标值
console.log('=== 本地生成校验 ===');
for (const [file, expectModes] of [['Points__CapturePointsA.json', true], ['Points__CapturePointsB.json', true], ['EsConfig__CapturePoints.json', false], ['EsConfig__game.json', false]]) {
  const t = fs.readFileSync(`points-work/after/${file}`, 'utf8');
  const j = JSON.parse(t);
  if (file === 'EsConfig__game.json') {
    console.log(`  ${file}: initial_attack=${j.troops.initial_attack} initial_defend=${j.troops.initial_defend} ${j.troops.initial_attack === ATTACK && j.troops.initial_defend === DEFEND ? 'OK' : '❌'}`);
  } else {
    const r = j.teamReinforcements;
    const modes = j.modes ? JSON.stringify(j.modes) : '(无)';
    console.log(`  ${file}: modes=${modes} ATTACK=${r.ATTACK} DEFEND=${r.DEFEND} 点数=${j.plannedPoints.length} batches=${j.totalBatches} ${r.ATTACK === ATTACK && r.DEFEND === DEFEND ? 'OK' : '❌'}`);
  }
}

if (DRY) { console.log('\n(dry-run，未上传)'); process.exit(0); }

// 上传
const jobs = [
  ['points-work/after/Points__CapturePointsA.json', 'EsWorld/CREATE_PLUS/Points', 'CapturePointsA.json'],
  ['points-work/after/Points__CapturePointsB.json', 'EsWorld/CREATE_PLUS/Points', 'CapturePointsB.json'],
  ['points-work/after/EsConfig__CapturePoints.json', 'EsWorld/CREATE_PLUS/EsConfig', 'CapturePoints.json'],
  ['points-work/after/EsConfig__game.json', 'EsWorld/CREATE_PLUS/EsConfig', 'game.json'],
];
console.log('\n=== 上传 ===');
let ok = 0;
for (const [local, dir, name] of jobs) {
  const r = await upload(local, dir, name);
  if (r) ok++;
  console.log(`  ${r ? 'OK ' : 'FAIL'} ${dir}/${name}`);
  await sleep(800);
}
console.log(`上传成功 ${ok}/${jobs.length}`);

// 回下载校验
console.log('\n=== 回下载校验 ===');
let v = 0;
for (const [, dir, name] of jobs) {
  await sleep(2600);
  const t = await read(`${dir}/${name}`);
  if (t === null) { console.log(`  FAIL ${dir}/${name} 读取失败`); continue; }
  const local = fs.readFileSync(`points-work/after/${dir.split('/').pop()}__${name}`.replace('EsWorld/CREATE_PLUS/', '').replace('Points__', 'Points__').replace('EsConfig__', 'EsConfig__'), 'utf8');
  const same = t.trim() === local.trim();
  if (same) v++;
  console.log(`  ${same ? 'MATCH' : 'DIFF '} ${dir}/${name} (${t.length} 字符)`);
}
console.log(`\n校验一致 ${v}/${jobs.length}`);
