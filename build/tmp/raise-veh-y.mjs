// raise-veh-y.mjs — 三张图所有载具刷车点 Y +2
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');
const PANEL = 'https://www.derpydoge.fun:20000';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const MAPS = ['server_battlefield', '越南', 'CREATE_PLUS'];
const RISE = 2;
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
async function read(target) {
  for (let a = 0; a < 4; a++) {
    const r = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target }));
    try { const b = JSON.parse(r.body); if (b.status === 200) return String(b.data ?? ''); } catch { }
    await sleep(2600);
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
    await sleep(2500);
  }
  return false;
}
fs.mkdirSync('vehy/before', { recursive: true });
fs.mkdirSync('vehy/after', { recursive: true });

const report = [];
for (const map of MAPS) {
  const text = await read(`EsWorld/${map}/EsConfig/VehSpawn.json`);
  if (text === null) { console.log(`❌ ${map}: 读取失败`); process.exitCode = 1; continue; }
  fs.writeFileSync(`vehy/before/${map}.json`, text, 'utf8');
  const j = JSON.parse(text);
  // 统计 pose 数
  let poses = 0;
  for (const list of Object.values(j.spawn_points ?? {})) for (const p of list) { if (p.attack) poses++; if (p.defend) poses++; }
  const yKeys = (text.match(/"y"\s*:/g) ?? []).length;
  const otherY = Object.keys(j).filter((k) => /y/i.test(k) && k !== 'spawn_points');
  console.log(`--- ${map}: 类型 ${Object.keys(j.spawn_points ?? {}).length}，pose ${poses} 个，"y" 键 ${yKeys} 个，其它含 y 的顶层键 ${JSON.stringify(otherY)}`);
  if (yKeys !== poses) { console.log(`   ⚠ "y" 键数与 pose 数不一致，请人工确认`); process.exitCode = 1; continue; }
  // 只替换 "y": 数值
  const out = text.replace(/("y"\s*:\s*)(-?\d+(?:\.\d+)?)/g, (m, head, num) => {
    const v = Number(num) + RISE;
    return head + (num.includes('.') ? v.toFixed(1) : String(v));
  });
  fs.writeFileSync(`vehy/after/${map}.json`, out, 'utf8');
  // 校验
  const j2 = JSON.parse(out);
  let ok = true, shown = 0;
  const samples = [];
  for (const [type, list] of Object.entries(j.spawn_points ?? {})) {
    const list2 = j2.spawn_points[type] ?? [];
    if (list.length !== list2.length) { ok = false; break; }
    for (let i = 0; i < list.length; i++) {
      for (const side of ['attack', 'defend']) {
        const a = list[i][side], b = list2[i][side];
        if (!a && !b) continue;
        if (!a || !b || b.y !== a.y + RISE || b.x !== a.x || b.z !== a.z || b.yaw !== a.yaw) { ok = false; break; }
        if (shown < 2) { samples.push(`${type}.${list[i].id}.${side}: ${a.y} → ${b.y}`); shown++; }
      }
    }
  }
  console.log(`   ${ok ? '✓ 校验通过' : '❌ 校验失败'}  例: ${samples.join(' | ')}`);
  if (!ok) process.exitCode = 1;
  report.push(map);
  await sleep(2600);
}
if (process.exitCode) { console.log('\n有失败，未上传'); process.exit(1); }

console.log('\n=== 上传 ===');
let ok2 = 0;
for (const map of report) {
  const r = await upload(`vehy/after/${map}.json`, `EsWorld/${map}/EsConfig`, 'VehSpawn.json');
  if (r) ok2++;
  console.log(`  ${r ? 'OK ' : 'FAIL'} ${map}`);
  await sleep(700);
}
console.log(`上传成功 ${ok2}/${report.length}`);
console.log('\n=== 回下载校验（与本地 after 一致 + Y 值再确认）===');
let v = 0;
for (const map of report) {
  await sleep(2600);
  const t = await read(`EsWorld/${map}/EsConfig/VehSpawn.json`);
  const same = t !== null && t.trim() === fs.readFileSync(`vehy/after/${map}.json`, 'utf8').trim();
  if (same) v++;
  console.log(`  ${same ? 'MATCH' : 'DIFF '} ${map}`);
}
console.log(`\n校验一致 ${v}/${report.length}`);
