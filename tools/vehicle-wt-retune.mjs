// 按 War Thunder（或现实公开数据）重标定装甲载具的极速与到达极速时间。
//
// 输入：
//   build/tmp/veh-speed/server/<ns>/<id>.json  —— 服务端 kubejs 现有覆盖（完整 JSON）
//   本文件内的 TARGETS 表                   —— 每台车的目标（WT RB 极速 / 现实极速）
// 输出：
//   build/tmp/veh-wt/out/<ns>/<id>.json     —— 只改 EngineInfo 三个字段的副本
//   build/tmp/veh-wt/plan.csv               —— 变更对照表
//
// 用法：node tools/vehicle-wt-retune.mjs
import fs from 'node:fs';
import path from 'node:path';

const ROOT = process.cwd();
const SRC = path.join(ROOT, 'build', 'tmp', 'veh-speed', 'server');
const OUT = path.join(ROOT, 'build', 'tmp', 'veh-wt', 'out');

// ---------------- 物理（照抄 VehicleEngineUtils 直线前进情形） ----------------
const DECAY = { Track: 0.79 * 1.05, Wheel: 0.79 * 1.05, WheelChair: 0.88 * 1.05 };
const THRUST = { Track: 0.15, Wheel: 0.15, WheelChair: 0.15 };

function runOnce(type, rate, increment, maxPower = 1) {
  const decay = DECAY[type];
  const thrust = THRUST[type];
  if (decay == null || thrust == null || rate == null) return null;

  let s = 0;
  let p = 0;
  const hist = [];
  for (let t = 1; t <= 6000; t++) {
    const f0 = 0.54 + 0.25 * (Math.abs(s) > 1e-6 ? 1 : 0);
    s = s + 0.05 * s;
    s *= f0;
    p = Math.min(p + increment * (maxPower - Math.abs(p) / 1.02), maxPower);
    s += thrust * Math.abs(rate) * p;
    hist.push(s);
  }
  const tail = hist.slice(-100);
  const top = tail.reduce((a, b) => a + b, 0) / tail.length;
  let t95 = null;
  for (let i = 0; i < hist.length; i++) {
    if (hist[i] >= 0.95 * top) {
      t95 = (i + 1) / 20;
      break;
    }
  }
  return { topKmh: top * 72, t95Sec: t95 };
}

/** 二分求 rate，使稳态极速 = targetKmh */
function solveRate(type, increment, targetKmh) {
  let lo = 0.01;
  let hi = 6;
  for (let i = 0; i < 60; i++) {
    const mid = (lo + hi) / 2;
    const r = runOnce(type, mid, increment);
    if (r.topKmh < targetKmh) lo = mid;
    else hi = mid;
  }
  return (lo + hi) / 2;
}

/** 二分求 increment，使 0→95% 极速时间 = targetSec */
function solveIncrement(type, rate, targetSec) {
  let lo = 0.0002;
  let hi = 0.2;
  for (let i = 0; i < 60; i++) {
    const mid = (lo + hi) / 2;
    const r = runOnce(type, rate, mid);
    if (r.t95Sec > targetSec) lo = mid;
    else hi = mid;
  }
  return (lo + hi) / 2;
}

// ---------------- 目标表 ----------------
// kind: 'wt' = War Thunder RB 数据；'real' = 现实公开极速（倒车不动）
// turbine: true 的按 14s 标定，其余按 20s
const TARGETS = [
  // ---- 履带 ----
  { id: 'bmp3', kind: 'wt', fwd: 70, rev: 20, note: 'BMP-3' },
  { id: 'm113', kind: 'wt', fwd: 64, rev: 15, note: 'M113A1 (TOW) 同底盘近似' },
  { id: 'm1a2sepv1', kind: 'wt', fwd: 68, rev: 40, turbine: true, note: 'M1A2 SEP' },
  { id: 'm1a2sepv2', kind: 'wt', fwd: 68, rev: 40, turbine: true, note: 'M1A2 SEPv2' },
  { id: 'm3a3', kind: 'wt', fwd: 66, rev: 15, note: 'M3A3 Bradley' },
  { id: 't72b3', kind: 'wt', fwd: 60, rev: 4, note: 'T-72B3' },
  { id: 't90mh', kind: 'wt', fwd: 60, rev: 4, note: 'T-90M' },
  { id: 'zbd04a', kind: 'wt', fwd: 70, rev: 17, note: 'ZBD04A' },
  { id: 'zsd89a_covered', kind: 'wt', fwd: 60, rev: 9, note: 'ZSD63/PG87 同族近似' },
  { id: 'ztz96a', kind: 'wt', fwd: 59, rev: 6, note: 'ZTZ96A' },
  { id: 'ztz99a', kind: 'wt', fwd: 75, rev: 34, note: 'ZTZ99A' },
  { id: 'bmp1am', kind: 'wt', fwd: 65, rev: 11, note: 'BMP-1 同族近似' },
  { id: 'bmp2', kind: 'wt', fwd: 65, rev: 11, note: 'BMP-2' },
  { id: 'bmp2d', kind: 'wt', fwd: 65, rev: 11, note: 'BMP-2 近似' },
  { id: 'bmp2m', kind: 'wt', fwd: 65, rev: 11, note: 'BMP-2M' },
  { id: 't72av', kind: 'wt', fwd: 60, rev: 4, note: 'T-72AV (TURMS-T)' },
  // ---- 轮式（装甲） ----
  { id: 'm1126', kind: 'wt', fwd: 100, rev: 13, note: 'M1296 同底盘近似' },
  { id: 'm1128', kind: 'wt', fwd: 96, rev: 12, note: 'M1128' },
  { id: 'm1296', kind: 'wt', fwd: 100, rev: 13, note: 'M1296' },
  { id: 'stryker_dragoon', kind: 'wt', fwd: 100, rev: 13, note: 'M1296' },
  { id: 'stryker_m2', kind: 'wt', fwd: 100, rev: 13, note: 'M1296 同底盘近似' },
  { id: 'stryker_mgs', kind: 'wt', fwd: 96, rev: 12, note: 'M1128 同底盘近似' },
  { id: 'stryker_mortar', kind: 'wt', fwd: 100, rev: 13, note: 'M1296 同底盘近似' },
  { id: 'zbl08', kind: 'wt', fwd: 100, rev: 9, note: 'ZLT11 同底盘近似' },
  { id: 'zlt11', kind: 'wt', fwd: 100, rev: 9, note: 'ZLT11' },
  { id: 'zsl10', kind: 'wt', fwd: 88, rev: 7, note: 'ZSL92 同族近似' },
  { id: 'btr80', kind: 'wt', fwd: 87, rev: 12, note: 'BTR-80A' },
  { id: 'btr82', kind: 'wt', fwd: 80, rev: 11, note: 'BTR-82AT' },
  // ---- 现实公开数据（WT 无对应，倒车不动） ----
  { id: 'zbd05', kind: 'real', fwd: 65, note: 'ZBD-05 公开极速' },
  { id: 'ztd05', kind: 'real', fwd: 65, note: 'ZTD-05 公开极速' },
  { id: 'csk181', kind: 'real', fwd: 110, note: 'CSK-181 公开极速' },
  { id: 'matv', kind: 'real', fwd: 105, note: 'M-ATV 公开极速' },
  { id: 'matv_9in1', kind: 'real', fwd: 105, note: 'M-ATV 公开极速' },
  { id: 'matv_crow', kind: 'real', fwd: 105, note: 'M-ATV 公开极速' },
  { id: 'matv_tow', kind: 'real', fwd: 105, note: 'M-ATV 公开极速' },
  { id: 'gaz_tigr_gl', kind: 'real', fwd: 120, note: 'GAZ Tigr 公开极速' },
  { id: 'gaz_tigr_mg', kind: 'real', fwd: 120, note: 'GAZ Tigr 公开极速' },
  { id: 'gaz_tigr_rws', kind: 'real', fwd: 120, note: 'GAZ Tigr 公开极速' },
  { id: 'humveem2', kind: 'real', fwd: 113, note: 'HMMWV 公开极速' },
  { id: 'sx1', kind: 'keep-speed', fwd: 120.4, note: 'SX-1 极速保持现值，仅延长到达时间' },
];

const TURBINE_SEC = 14;
const DIESEL_SEC = 20;

// ---------------- 读现有文件 ----------------
function walk(dir) {
  const out = [];
  for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
    const p = path.join(dir, e.name);
    if (e.isDirectory()) out.push(...walk(p));
    else if (e.name.endsWith('.json')) out.push(p);
  }
  return out;
}

const files = new Map(); // id -> { ns, file }
for (const f of walk(SRC)) {
  const rel = path.relative(SRC, f).replaceAll('\\', '/');
  const [ns] = rel.split('/');
  const id = path.basename(f, '.json');
  files.set(`${ns}/${id}`, { ns, id, file: f });
}

function numField(text, key) {
  const m = text.match(new RegExp(`"${key}"\\s*:\\s*(-?[0-9.]+)`));
  return m ? Number(m[1]) : null;
}

function engineType(text) {
  const m = text.match(/"EngineType"\s*:\s*"([A-Za-z]+)"/);
  return m ? m[1] : null;
}

function replaceField(text, key, value, what) {
  const re = new RegExp(`("${key}"\\s*:\\s*)-?[0-9.]+`);
  if (!re.test(text)) throw new Error(`${what}: 找不到字段 ${key}`);
  return text.replace(re, `$1${value}`);
}

// ---------------- 计算并写文件 ----------------
const plan = [];
let changed = 0;

for (const t of TARGETS) {
  // 直接按 id 找源文件（各 id 在服务端互不重名）
  let entry = null;
  for (const [, val] of files) {
    if (val.id === t.id) {
      entry = val;
      break;
    }
  }
  if (!entry) {
    plan.push({ id: t.id, error: '找不到源文件' });
    continue;
  }

  const text = fs.readFileSync(entry.file, 'utf8');
  const type = engineType(text);
  const curRf = numField(text, 'MaxForwardSpeedRate');
  const curRb = numField(text, 'MaxBackwardSpeedRate');
  const curInc = numField(text, 'Increment');
  if (!type || curRf == null || curRb == null || curInc == null) {
    plan.push({ id: t.id, error: `字段缺失 type=${type}` });
    continue;
  }

  const before = runOnce(type, curRf, curInc);
  const targetSec = t.turbine ? TURBINE_SEC : DIESEL_SEC;

  // 1) 极速：keep-speed 保持，其余按目标解 rate
  const newRf = t.kind === 'keep-speed' ? curRf : solveRate(type, curInc, t.fwd);
  const newRb = t.kind === 'wt' ? solveRate(type, curInc, t.rev) : curRb;

  // 2) 到达时间：用新的 rate 解 increment
  const newInc = solveIncrement(type, newRf, targetSec);

  // 3) 复核
  const after = runOnce(type, newRf, newInc);
  const afterRev = runOnce(type, newRb, newInc);
  const errKmh = Math.abs(after.topKmh - (t.kind === 'keep-speed' ? before.topKmh : t.fwd));
  const errSec = Math.abs(after.t95Sec - targetSec);

  let out = text;
  out = replaceField(out, 'MaxForwardSpeedRate', round(newRf, 4), `${entry.ns}/${t.id}`);
  out = replaceField(out, 'MaxBackwardSpeedRate', round(newRb, 4), `${entry.ns}/${t.id}`);
  out = replaceField(out, 'Increment', round(newInc, 6), `${entry.ns}/${t.id}`);

  const outDir = path.join(OUT, entry.ns);
  fs.mkdirSync(outDir, { recursive: true });
  const outFile = path.join(outDir, `${t.id}.json`);
  fs.writeFileSync(outFile, out, 'utf8');
  changed++;

  plan.push({
    id: t.id,
    ns: entry.ns,
    type,
    note: t.note,
    source: t.kind,
    turbine: Boolean(t.turbine),
    sec: targetSec,
    beforeRf: curRf,
    beforeInc: curInc,
    beforeTop: round(before.topKmh, 1),
    beforeT95: round(before.t95Sec, 1),
    afterRf: round(newRf, 4),
    afterRb: round(newRb, 4),
    afterInc: round(newInc, 6),
    afterTop: round(after.topKmh, 1),
    afterT95: round(after.t95Sec, 2),
    afterRev: round(afterRev.topKmh, 1),
    errKmh: round(errKmh, 3),
    errSec: round(errSec, 3),
  });
}

function round(x, d) {
  const k = 10 ** d;
  return Math.round(x * k) / k;
}

// ---------------- 输出计划表 ----------------
const head = [
  'ns', 'id', 'type', 'source', 'turbine', 'sec', 'note',
  'beforeRf', 'beforeInc', 'beforeTop', 'beforeT95',
  'afterRf', 'afterRb', 'afterInc', 'afterTop', 'afterT95', 'afterRev', 'errKmh', 'errSec',
];
const lines = [head.join(',')];
for (const p of plan) {
  if (p.error) {
    lines.push([p.id].join(',') + `,,,,,,,,,,,,,,,,,,ERROR:${p.error}`);
    continue;
  }
  lines.push(head.map((k) => p[k] ?? '').join(','));
}
fs.mkdirSync(path.join(ROOT, 'build', 'tmp', 'veh-wt'), { recursive: true });
fs.writeFileSync(path.join(ROOT, 'build', 'tmp', 'veh-wt', 'plan.csv'), lines.join('\n'), 'utf8');

const errors = plan.filter((p) => p.error);
const bigErr = plan.filter((p) => !p.error && (p.errKmh > 0.15 || p.errSec > 0.1));
console.log(`已生成 ${changed} 个文件`);
if (errors.length) console.log(`失败 ${errors.length}: ${errors.map((e) => e.id + '(' + e.error + ')').join(', ')}`);
if (bigErr.length) console.log(`复核超差 ${bigErr.length}: ${bigErr.map((e) => `${e.id} km/h±${e.errKmh} s±${e.errSec}`).join(', ')}`);
console.log('计划表 build/tmp/veh-wt/plan.csv');
