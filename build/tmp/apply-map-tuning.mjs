// apply-map-tuning.mjs — 兵票 / FOB / 部署时长 / 战局限时 一次性改到位
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');
const PANEL = 'https://www.derpydoge.fun:20000';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const sleep = (ms) => new Promise((r) => setTimeout(r, ms));
const DRY = process.argv.includes('--dry');

// ---- 目标值 ----
const BATTLE_TIMEOUT = 5400;      // 1.5 小时
const DEPLOY = { 'server_battlefield': 480, '越南': 300, 'CREATE_PLUS': 300 };
const TROOPS = { 'server_battlefield': { ATK: 100, DEF: 400 } };   // 其余两图保持 75/350
const FOB = {
  'server_battlefield': { build: 200.0, exclusion: 400.0 },
  '越南': { build: 150.0, exclusion: 300.0 },
  'CREATE_PLUS': { build: 150.0, exclusion: 300.0 },
};

const plan = [];   // {local, dir, name, check}
function edit(file, edits, label) {
  const p = `maps-edit/before/${file}`;
  let text = fs.readFileSync(p, 'utf8');
  const eol = text.includes('\r\n') ? '\r\n' : '\n';
  for (const [from, to, what] of edits) {
    const fromFixed = from.replace(/\r?\n/g, eol);
    const n = text.split(fromFixed).length - 1;
    if (n !== 1) { console.error(`  ❌ ${file} [${what}] 匹配 ${n} 次`); process.exitCode = 1; return; }
    text = text.replace(fromFixed, to.replace(/\r?\n/g, eol));
  }
  fs.writeFileSync(`maps-edit/after/${file}`, text, 'utf8');
  console.log(`  ✓ ${label}`);
  return text;
}
fs.mkdirSync('maps-edit/after', { recursive: true });

// ===== game.json =====
for (const map of ['server_battlefield', '越南', 'CREATE_PLUS']) {
  const file = `${map}__EsConfig__game.json`;
  const edits = [
    ['"deploy_timeout_seconds": 180,',
     `"deploy_timeout_seconds": ${DEPLOY[map]},`, 'deploy'],
    ['"team_select_seconds": 60,',
     `"team_select_seconds": 60,${DRY ? '' : ''}\n    "battle_timeout_seconds": ${BATTLE_TIMEOUT},`,
     'battleTimeout'],
  ];
  if (TROOPS[map]) {
    edits.push(['"initial_attack": 180,', `"initial_attack": ${TROOPS[map].ATK},`, 'initial_attack']);
    edits.push(['"initial_defend": 500,', `"initial_defend": ${TROOPS[map].DEF},`, 'initial_defend']);
  }
  const out = edit(file, edits, `${map}/game.json: 部署 ${DEPLOY[map]}s, 战局限时 ${BATTLE_TIMEOUT}s`
    + (TROOPS[map] ? `, 兵力 ${TROOPS[map].ATK}/${TROOPS[map].DEF}` : ''));
  if (out) {
    const j = JSON.parse(out);
    const ok = j.game.deploy_timeout_seconds === DEPLOY[map] && j.game.battle_timeout_seconds === BATTLE_TIMEOUT
      && (!TROOPS[map] || (j.troops.initial_attack === TROOPS[map].ATK && j.troops.initial_defend === TROOPS[map].DEF));
    console.log(`      校验 deploy=${j.game.deploy_timeout_seconds} battle=${j.game.battle_timeout_seconds} troops=${j.troops.initial_attack}/${j.troops.initial_defend} ${ok ? '✓' : '❌'}`);
    if (!ok) process.exitCode = 1;
  }
  plan.push([`maps-edit/after/${file}`, `EsWorld/${map}/EsConfig`, 'game.json']);
}

// ===== logistics.json（FOB 半径；顶格与 radio.* 两处都要改）=====
for (const map of ['server_battlefield', '越南', 'CREATE_PLUS']) {
  const f = FOB[map];
  const file = `${map}__EsConfig__logistics.json`;
  const before = JSON.parse(fs.readFileSync(`maps-edit/before/${file}`, 'utf8')).logistics;
  const edits = [
    [`"radio_build_radius": ${before.radio_build_radius.toFixed(1)},`, `"radio_build_radius": ${f.build.toFixed(1)},`, 'radio_build_radius'],
    [`"radio_exclusion_radius": ${before.radio_exclusion_radius.toFixed(1)},`, `"radio_exclusion_radius": ${f.exclusion.toFixed(1)},`, 'radio_exclusion_radius'],
    [`"build_radius": ${before.radio.build_radius.toFixed(1)},`, `"build_radius": ${f.build.toFixed(1)},`, 'radio.build_radius'],
    [`"exclusion_radius": ${before.radio.exclusion_radius.toFixed(1)},`, `"exclusion_radius": ${f.exclusion.toFixed(1)},`, 'radio.exclusion_radius'],
  ];
  const out = edit(file, edits, `${map}/logistics.json: FOB 建造 ${f.build} / 排除 ${f.exclusion}`);
  if (out) {
    const j = JSON.parse(out).logistics;
    const ok = j.radio_build_radius === f.build && j.radio_exclusion_radius === f.exclusion
      && j.radio.build_radius === f.build && j.radio.exclusion_radius === f.exclusion;
    console.log(`      校验 顶格 ${j.radio_build_radius}/${j.radio_exclusion_radius}  radio ${j.radio.build_radius}/${j.radio.exclusion_radius} ${ok ? '✓' : '❌'}`);
    if (!ok) process.exitCode = 1;
  }
  plan.push([`maps-edit/after/${file}`, `EsWorld/${map}/EsConfig`, 'logistics.json']);
}

// ===== 黑山工厂兵票：B 套 + legacy（A 已是 100/400）=====
for (const [file, dir, name] of [
  ['server_battlefield__Points__CapturePointsB.json', 'EsWorld/server_battlefield/Points', 'CapturePointsB.json'],
  ['server_battlefield__EsConfig__CapturePoints.json', 'EsWorld/server_battlefield/EsConfig', 'CapturePoints.json'],
]) {
  const out = edit(file, [
    ['  "teamReinforcements": {\n    "ATTACK": 280,\n    "DEFEND": 1400\n  },',
     '  "teamReinforcements": {\n    "ATTACK": 100,\n    "DEFEND": 400\n  },', 'teamReinforcements'],
  ], `${dir.split('/').slice(1).join('/')}/${name}: 兵力 → 100/400`);
  if (out) {
    const j = JSON.parse(out);
    const ok = j.teamReinforcements.ATTACK === 100 && j.teamReinforcements.DEFEND === 400;
    console.log(`      校验 ${JSON.stringify(j.teamReinforcements)} ${ok ? '✓' : '❌'}`);
    if (!ok) process.exitCode = 1;
  }
  plan.push([`maps-edit/after/${file}`, dir, name]);
}

console.log(`\n共 ${plan.length} 个文件待上传`);
if (process.exitCode) { console.log('有校验失败，终止'); process.exit(1); }
if (DRY) { console.log('(dry-run，未上传)'); process.exit(0); }

// ===== 上传 =====
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
    await sleep(2500);
  }
  return false;
}
async function read(target) {
  for (let a = 0; a < 4; a++) {
    const r = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target }));
    try { const b = JSON.parse(r.body); if (b.status === 200) return String(b.data ?? ''); } catch { }
    await sleep(2600);
  }
  return null;
}
console.log('\n=== 上传 ===');
let ok = 0;
for (const [local, dir, name] of plan) {
  const r = await upload(local, dir, name);
  if (r) ok++;
  console.log(`  ${r ? 'OK ' : 'FAIL'} ${dir}/${name}`);
  await sleep(700);
}
console.log(`上传成功 ${ok}/${plan.length}`);
console.log('\n=== 回下载校验 ===');
let v = 0;
for (const [local, dir, name] of plan) {
  await sleep(2600);
  const t = await read(`${dir}/${name}`);
  const same = t !== null && t.trim() === fs.readFileSync(local, 'utf8').trim();
  if (same) v++;
  console.log(`  ${same ? 'MATCH' : 'DIFF '} ${dir}/${name}`);
}
console.log(`\n校验一致 ${v}/${plan.length}`);
