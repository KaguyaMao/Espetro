// tools/vehicle-speed-report.mjs
// 汇总服务端当前生效的载具动力参数（kubejs/data 覆盖 + jar 内原版），
// 并按 Superb Warfare 的 VehicleEngineUtils 物理逐 tick 模拟，换算出：
//   极速（blocks/s、km/h）、0→1s 加速度（m/s²）、油门到底时间、达到 95% 极速时间、倒车极速。
//
// 用途：调平衡时看"配置数值 → 实际手感"的对应关系。
//
// 用法：
//   node tools/vehicle-speed-report.mjs             # 从面板重新拉取 kubejs/data 覆盖后生成
//   node tools/vehicle-speed-report.mjs --offline   # 只用本地缓存（build/tmp/veh-speed/server）
//
// 产出：build/tmp/veh-speed/vehicle-speed.csv / vehicle-speed.md

import https from 'node:https';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const PANEL = 'https://www.derpydoge.fun:20000';
const DAEMON = '647a21e800714185aec74849f1b0f9a6';
const UUID = 'cd06335bc1534e028d92b4b335be248a';

const ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const STAGE = path.join(ROOT, 'build/tmp/steer/stage/data');
const CACHE = path.join(ROOT, 'build/tmp/veh-speed');
const SERVER = path.join(CACHE, 'server');
// 世界数据包覆盖（优先级最高：world/datapacks/*/data/<ns>/sbw/vehicles/*.json）
const WDPACKS = path.join(CACHE, 'wdpacks');
const NS = ['dragonrise_reforge', 'fcp', 'superbwarfare', 'vvp'];
const OFFLINE = process.argv.includes('--offline');
const GAP = 1500;
const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

// ---------------- 面板文件接口（串行 + 冷却重试） ----------------
let cookie = '';
let query = '';

function raw(method, urlStr, headers, body) {
  return new Promise((resolve, reject) => {
    const u = new URL(urlStr);
    const req = https.request(
      { method, hostname: u.hostname, port: u.port || 443, path: u.pathname + u.search, headers, rejectUnauthorized: false },
      (res) => {
        let d = '';
        res.on('data', (c) => (d += c));
        res.on('end', () => resolve({ status: res.statusCode, body: d, cookie: (res.headers['set-cookie'] ?? []).map((l) => l.split(';')[0]).join('; ') }));
      }
    );
    req.on('error', reject);
    if (body !== undefined) req.write(body);
    req.end();
  });
}

let last = 0;
let chain = Promise.resolve();
function panel(method, pathName, body, tries = 8) {
  const run = async () => {
    for (let i = 1; i <= tries; i++) {
      const wait = last + GAP - Date.now();
      if (wait > 0) await sleep(wait);
      const r = await raw(method, PANEL + pathName, { 'X-Requested-With': 'XMLHttpRequest', Cookie: cookie, 'Content-Type': 'application/json' }, body);
      last = Date.now();
      let j = null;
      try { j = JSON.parse(r.body); } catch { /* ignore */ }
      if (j && j.status === 200) return j;
      const msg = j ? String(j.data ?? j.status) : 'HTTP ' + r.status + ' ' + r.body.slice(0, 120);
      if (i === tries) throw new Error(`${method} ${pathName} 失败: ${msg}`);
      await sleep(1200 * i);
    }
  };
  const p = chain.then(run, run);
  chain = p.catch(() => {});
  return p;
}

async function login() {
  const lr = await raw('POST', PANEL + '/api/auth/login', { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: 'boy', password: 'boY1145141919810Fuck' }));
  const token = String(JSON.parse(lr.body).data ?? '');
  if (!token) throw new Error('登录失败: ' + lr.body.slice(0, 200));
  cookie = lr.cookie;
  query = `?daemonId=${DAEMON}&uuid=${UUID}&token=${token}`;
}

async function listDir(dir) {
  try {
    const j = await panel('GET', '/api/files/list' + query + '&target=' + encodeURIComponent(dir) + '&page=0&page_size=500&file_name=');
    const arr = j.data?.items || j.data?.files || j.data?.data || j.data;
    if (!Array.isArray(arr)) return null;
    return arr.map((i) => i.name);
  } catch (e) {
    console.log('  ' + e.message);
    return null;
  }
}

async function pullServerOverrides() {
  fs.mkdirSync(SERVER, { recursive: true });
  await login();
  let total = 0;
  for (const ns of NS) {
    const dir = `kubejs/data/${ns}/sbw/vehicles`;
    const names = await listDir(dir);
    if (!names) { console.log(`[无目录] ${dir}`); continue; }
    const ids = names.filter((n) => n.endsWith('.json'));
    for (const id of ids) {
      const j = await panel('PUT', '/api/files' + query, JSON.stringify({ target: `${dir}/${id}` }));
      const p = path.join(SERVER, ns, id);
      fs.mkdirSync(path.dirname(p), { recursive: true });
      fs.writeFileSync(p, String(j.data ?? ''), 'utf8');
      total++;
    }
    console.log(`服务端覆盖 ${ns}: ${ids.length} 个`);
  }
  console.log(`服务端覆盖合计 ${total} 个`);
}

// ---------------- 读取 & 解析 ----------------
function walk(dir) {
  const out = [];
  if (!fs.existsSync(dir)) return out;
  for (const ent of fs.readdirSync(dir, { withFileTypes: true })) {
    const p = path.join(dir, ent.name);
    if (ent.isDirectory()) out.push(...walk(p));
    else if (ent.name.endsWith('.json')) out.push(p);
  }
  return out;
}

/** 从 "EngineInfo": { ... } 里截出花括号块（跳过字符串内的括号） */
function engineBlock(text) {
  const i = text.indexOf('"EngineInfo"');
  if (i < 0) return '';
  const open = text.indexOf('{', i);
  if (open < 0) return '';
  let depth = 0;
  for (let k = open; k < text.length; k++) {
    const c = text[k];
    if (c === '"') {
      k++;
      while (k < text.length && !(text[k] === '"' && text[k - 1] !== '\\')) k++;
      continue;
    }
    if (c === '{') depth++;
    else if (c === '}') { depth--; if (depth === 0) return text.slice(open, k + 1); }
  }
  return '';
}

const num = (block, key) => {
  const m = block.match(new RegExp(`"${key}"\\s*:\\s*(-?[\\d.]+(?:[eE][-+]?\\d+)?)`));
  return m ? Number(m[1]) : null;
};

function parseVehicle(file, nsHint) {
  const text = fs.readFileSync(file, 'utf8');
  const typeM = text.match(/"EngineType"\s*:\s*"([^"]*)"/);
  const blk = engineBlock(text);
  return {
    ns: nsHint ?? '',
    id: path.basename(file, '.json'),
    engineType: typeM ? typeM[1] : '',
    text,
    increment: num(blk, 'Increment'),
    decrement: num(blk, 'Decrement'),
    maxForward: num(blk, 'MaxForwardSpeedRate'),
    maxBackward: num(blk, 'MaxBackwardSpeedRate'),
    steering: num(blk, 'SteeringSpeed'),
    speed: num(blk, 'Speed'),
    speedRate: num(blk, 'SpeedRate'),
  };
}

// ---------------- 物理（照抄 VehicleEngineUtils 的直线前进情形） ----------------
const DECAY = { Track: 0.79 * 1.05, Wheel: 0.79 * 1.05, WheelChair: 0.88 * 1.05, Ship: 0.80, AirShip: 0.99 };
const THRUST = { Track: 0.15, Wheel: 0.15, WheelChair: 0.15, Ship: 0.11, AirShip: 0.01 };

/** 单次模拟：rate = MaxForwardSpeedRate/MaxBackwardSpeedRate，maxPower 默认 1（冲刺 1.25/1.3 另算） */
function runOnce(v, rate, maxPower = 1) {
  const type = v.engineType;
  const decay = DECAY[type];
  const thrust = THRUST[type];
  if (decay == null || thrust == null || rate == null) return null;

  const incRaw = v.increment ?? 0.001;
  const inc = type === 'AirShip' ? incRaw * 0.05 : incRaw;
  const sign = rate < 0 ? -1 : 1;           // 倒车档位为负时方向相反
  const R = Math.abs(rate);

  let s = 0;                                 // blocks/tick（沿视线）
  let p = 0;
  let throttleTick = null;
  let vAt20 = 0;
  const hist = [];

  for (let t = 1; t <= 4000; t++) {
    // 1) 阻力
    if (type === 'Track' || type === 'Wheel') {
      const f0 = 0.54 + 0.25 * (Math.abs(s) > 1e-6 ? 1 : 0);
      s = s + 0.05 * s;
      s *= f0;
    } else if (type === 'WheelChair') {
      const f0 = 0.63 + 0.25 * (Math.abs(s) > 1e-6 ? 1 : 0);
      s = s + 0.05 * s;
      s *= f0;
    } else if (type === 'Ship') {
      // shipEngine：水中 f = 0.835 − 0.04·min(浸没高度, bbHeight) + 0.005|dot|，先 v += 0.04v 再乘 f
      // 浸没高度按 0.5 近似（船体半浸）
      const f = 0.835 - 0.04 * 0.5 + 0.005;
      s = s + 0.04 * s;
      s *= f;
    } else if (type === 'AirShip') {
      const f = Math.min(0.99, Math.max(0.01, 0.91499 - 0.01 * s * s + 0.07 + 0.031 * (Math.abs(s) > 1e-6 ? 1 : 0)));
      s *= f;
    }
    // 2) 油门爬升（Increment 是油门速率）
    p = Math.min(p + inc * (maxPower - Math.abs(p) / 1.02), maxPower);
    if (throttleTick == null && p >= 0.99) throttleTick = t;
    // 3) 推力
    s += sign * thrust * R * p;
    if (t === 20) vAt20 = Math.abs(s);
    hist.push(Math.abs(s));
  }

  // 极速取最后 100 tick 的平均（避开地形/浮点抖动）
  const tail = hist.slice(-100);
  const top = tail.reduce((a, b) => a + b, 0) / tail.length;
  // 达到 95% 极速的时间
  let t95 = null;
  for (let i = 0; i < hist.length; i++) {
    if (hist[i] >= 0.95 * top) { t95 = i + 1; break; }
  }

  return {
    topBps: top * 20,
    topKmh: top * 72,
    accel1s: vAt20 * 20,          // 1s 内达到的速度（blocks/s）＝平均加速度 m/s²
    throttleSec: throttleTick == null ? null : throttleTick / 20,
    t95Sec: t95 == null ? null : t95 / 20,
  };
}

function simulate(v) {
  const fwd = runOnce(v, v.maxForward, 1);
  const rev = runOnce(v, v.maxBackward, 1);
  if (!fwd) return null;
  return {
    ...fwd,
    reverseKmh: rev ? rev.topKmh : null,
    sprintKmh: (() => {
      const sprintPower = v.engineType === 'Wheel' ? 1.3 : v.engineType === 'Track' ? 1.25 : 1.25;
      const s = runOnce(v, v.maxForward, sprintPower);
      return s ? s.topKmh : null;
    })(),
  };
}

// ---------------- 主流程 ----------------
if (!OFFLINE) await pullServerOverrides();

const langFile = path.join(CACHE, 'lang.json');
const lang = fs.existsSync(langFile) ? JSON.parse(fs.readFileSync(langFile, 'utf8')) : {};

const byKey = new Map();
for (const f of walk(STAGE)) {
  const rel = path.relative(STAGE, f).replaceAll('\\', '/'); // <ns>/sbw/vehicles/<id>.json
  const ns = rel.split('/')[0] ?? '';
  const v = parseVehicle(f, ns);
  byKey.set(`${ns}/${v.id}`, v);
}
// 服务端覆盖：同 ns/id 覆盖 jar；服务端独有则新增
let overrides = 0;
for (const f of walk(SERVER)) {
  const rel = path.relative(SERVER, f).replaceAll('\\', '/');
  const [ns, file] = rel.split('/');
  const v = parseVehicle(f, ns);
  v.id = file.replace(/\.json$/, '');
  v.fromServer = true;
  if (byKey.has(`${ns}/${v.id}`)) overrides++;
  byKey.set(`${ns}/${v.id}`, v);
}
console.log(`服务端覆盖命中 ${overrides} 个`);

// 世界数据包覆盖（最高优先级；目前只有 sbw_shotpos 里的几台）
let worldOverrides = 0;
for (const f of walk(WDPACKS)) {
  const rel = path.relative(WDPACKS, f).replaceAll('\\', '/');
  const [ns, file] = rel.split('/');
  const v = parseVehicle(f, ns);
  v.id = file.replace(/\.json$/, '');
  v.fromWorld = true;
  worldOverrides++;
  byKey.set(`${ns}/${v.id}`, v);
}
if (worldOverrides) console.log(`世界数据包覆盖 ${worldOverrides} 个`);

const rows = [];
const cleanName = (s) => String(s ?? '').replace(/§./g, '').trim();
for (const v of byKey.values()) {
  const name = cleanName(lang[`${v.ns}.${v.id}`] ?? lang[`${v.ns}:${v.id}`] ?? '');
  rows.push({ ...v, name, sim: simulate(v) });
}
rows.sort((a, b) => (a.ns + a.id).localeCompare(b.ns + b.id));

// CSV
const csvEsc = (s) => `"${String(s ?? '').replaceAll('"', '""')}"`;
const head = ['命名空间', 'ID', '中文名', '引擎类型', 'Increment', 'Decrement', 'MaxForwardSpeedRate', 'MaxBackwardSpeedRate', 'SteeringSpeed', 'Speed', 'SpeedRate', '极速(b/s)', '极速(km/h)', '冲刺极速(km/h)', '倒车极速(km/h)', '0-1s加速度(m/s²)', '油门到底(s)', '达95%极速(s)', '覆盖来源'];
const lines = [head.join(',')];
for (const r of rows) {
  const s = r.sim;
  lines.push([
    csvEsc(r.ns), csvEsc(r.id), csvEsc(r.name), csvEsc(r.engineType),
    r.increment ?? '', r.decrement ?? '', r.maxForward ?? '', r.maxBackward ?? '', r.steering ?? '',
    r.speed ?? '', r.speedRate ?? '',
    s ? s.topBps.toFixed(2) : '', s ? s.topKmh.toFixed(1) : '', s?.sprintKmh?.toFixed(1) ?? '', s?.reverseKmh?.toFixed(1) ?? '',
    s ? s.accel1s.toFixed(2) : '', s?.throttleSec != null ? s.throttleSec.toFixed(2) : '', s?.t95Sec != null ? s.t95Sec.toFixed(2) : '',
    csvEsc(r.fromWorld ? '世界数据包' : r.fromServer ? 'kubejs/data' : 'jar'),
  ].join(','));
}
fs.writeFileSync(path.join(CACHE, 'vehicle-speed.csv'), '\ufeff' + lines.join('\r\n'), 'utf8');

// Markdown
const TYPES = ['Track', 'Wheel', 'WheelChair', 'Ship', 'AirShip'];
const md = [];
md.push('# 服务端载具动力参数（Superb Warfare 物理换算）', '');
md.push('数值来源：`kubejs/data/<ns>/sbw/vehicles/*.json`（服主覆盖优先）+ 各 mod jar 内 `data/<ns>/sbw/vehicles/*.json`。');
md.push('');
md.push('换算依据 `VehicleEngineUtils.kt`（Sound 直行、平地、满油门、无伤）：');
md.push('- 地面 Track/Wheel：`v = (v + 0.05v) × (0.54 + 0.25|dot|) + 0.15·R·power`');
md.push('- WheelChair：阻力基数 0.63；Ship（水中）：`v × 1.04 × (0.835 − 0.04·浸没 + 0.005) + 0.11·R·power`（浸没按 0.5 近似）；AirShip：`clamp(0.91499 − 0.01v² + 0.07 + 0.031|dot|, 0.01, 0.99)`、`0.01·R·power`，`powerAdd = Increment × 0.05`（AirShip 为近似值）');
md.push('- `Increment`/`Decrement` 是**油门爬升速率**（不是车速加速度）；车速加速度 = `推力系数 × R`。');
md.push('- 换算：1 block/tick = 20 block/s = 72 km/h；1 block/tick² = 400 m/s²（1 格 = 1 米）。');
md.push('');
for (const t of TYPES) {
  const list = rows.filter((r) => r.engineType === t && r.sim);
  if (!list.length) continue;
  list.sort((a, b) => b.sim.topKmh - a.sim.topKmh);
  md.push(`## ${t}（${list.length} 台）`, '');
  md.push('| 载具 | 命名空间:ID | Increment | Decrement | 前进档 | 倒车档 | 极速 km/h | 冲刺 km/h | 倒车 km/h | 0-1s 加速 m/s² | 油门到底 s | 达 95% s |');
  md.push('|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|');
  for (const r of list) {
    const s = r.sim;
    md.push(`| ${r.name || r.id} | ${r.ns}:${r.id} | ${r.increment ?? '—'} | ${r.decrement ?? '—'} | ${r.maxForward ?? '—'} | ${r.maxBackward ?? '—'} | ${s.topKmh.toFixed(1)} | ${s.sprintKmh?.toFixed(1) ?? '—'} | ${s.reverseKmh?.toFixed(1) ?? '—'} | ${s.accel1s.toFixed(2)} | ${s.throttleSec?.toFixed(2) ?? '—'} | ${s.t95Sec?.toFixed(2) ?? '—'} |`);
  }
  md.push('');
}
const others = rows.filter((r) => !r.sim);
const air = others.filter((r) => r.engineType === 'Helicopter' || r.engineType === 'Aircraft' || r.engineType === 'Tom6');
const rest = others.filter((r) => !air.includes(r));
if (air.length) {
  md.push(`## 飞行器（直升机/固定翼，${air.length} 台；速度不按地面公式换算）`, '');
  md.push('直升机的水平速度由**俯仰角**决定（`VehicleEngineUtils.helicopterEngine`：`deltaMovement += view * 0.061 * SpeedRate * power`，同时叠加 `0.022 * Lift * sin(俯仰)` 的分量；阻力 `clamp(0.93499 − 0.01|v|² + 0.07·Speed + 0.031|dot|, 0.01, 0.99)`），固定翼则按 `force * SpeedRate` 持续推进，因此不换算成 km/h，只给原始配置。');
  md.push('');
  md.push('| 载具 | 命名空间:ID | 类型 | Increment | Decrement | Speed | SpeedRate | 备注 |');
  md.push('|---|---|---|---:|---:|---:|---:|---|');
  for (const r of air) {
    md.push(`| ${r.name || r.id} | ${r.ns}:${r.id} | ${r.engineType} | ${r.increment ?? '—'} | ${r.decrement ?? '—'} | ${r.speed ?? '—'} | ${r.speedRate ?? '—'} | ${r.fromWorld ? '世界数据包' : r.fromServer ? 'kubejs/data' : ''} |`);
  }
  md.push('');
}
if (rest.length) {
  md.push(`## 其他（固定武器/无引擎，${rest.length} 台）`, '');
  for (const r of rest) md.push(`- ${r.name || r.id} — ${r.ns}:${r.id}（${r.engineType || '无引擎字段'}）`);
  md.push('');
}
fs.writeFileSync(path.join(CACHE, 'vehicle-speed.md'), md.join('\n'), 'utf8');

// 控制台摘要
const withSim = rows.filter((r) => r.sim);
console.log(`载具总数 ${rows.length}，可换算 ${withSim.length}`);
for (const t of TYPES) {
  const list = withSim.filter((r) => r.engineType === t);
  if (!list.length) continue;
  const k = list.map((r) => r.sim.topKmh);
  console.log(`  ${t}: ${list.length} 台, ${Math.min(...k).toFixed(0)}–${Math.max(...k).toFixed(0)} km/h`);
}
const ground = withSim.filter((r) => r.engineType === 'Track' || r.engineType === 'Wheel').sort((a, b) => b.sim.topKmh - a.sim.topKmh);
console.log('最快 8 台地面载具:');
for (const r of ground.slice(0, 8)) console.log(`  ${r.name || r.id} (${r.ns}:${r.id}) ${r.sim.topKmh.toFixed(0)} km/h, 0-1s ${r.sim.accel1s.toFixed(1)} m/s², Rf=${r.maxForward}`);
console.log('最慢 5 台地面载具:');
for (const r of ground.slice(-5)) console.log(`  ${r.name || r.id} (${r.ns}:${r.id}) ${r.sim.topKmh.toFixed(0)} km/h, Rf=${r.maxForward}`);
