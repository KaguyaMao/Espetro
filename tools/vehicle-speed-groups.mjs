// tools/vehicle-speed-groups.mjs
// 把 tools/vehicle-speed-report.mjs 产出的全量 CSV 聚合成「按档位分组」的紧凑表：
// 同一组 = 同一套 EngineInfo 数值（Rf/Rb/Increment/Decrement），便于调平衡时横向比较。
//
// 用法：
//   node tools/vehicle-speed-report.mjs --offline   # 先出全量表（docs/vehicle-speed-report.csv）
//   node tools/vehicle-speed-groups.mjs             # 再出分组表（docs/vehicle-speed-groups.md）

import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
// --kubejs：只聚合 kubejs/data 覆盖的那批（对应 docs/vehicle-speed-report-kubejs.csv）
const SUFFIX = process.argv.includes('--kubejs') ? '-kubejs' : '';
const CSV = path.join(ROOT, `docs/vehicle-speed-report${SUFFIX}.csv`);
const OUT = path.join(ROOT, `docs/vehicle-speed-groups${SUFFIX}.md`);

/** 最小 CSV 解析（支持双引号包裹与 "" 转义） */
function parseCsv(text) {
  const rows = [];
  let row = [];
  let cell = '';
  let quoted = false;
  for (let i = 0; i < text.length; i++) {
    const ch = text[i];
    if (quoted) {
      if (ch === '"') {
        if (text[i + 1] === '"') { cell += '"'; i++; } else quoted = false;
      } else cell += ch;
    } else if (ch === '"') quoted = true;
    else if (ch === ',') { row.push(cell); cell = ''; }
    else if (ch === '\r') { /* skip */ }
    else if (ch === '\n') { row.push(cell); rows.push(row); row = []; cell = ''; }
    else cell += ch;
  }
  if (cell.length || row.length) { row.push(cell); rows.push(row); }
  return rows;
}

const table = parseCsv(fs.readFileSync(CSV, 'utf8').replace(/^\ufeff/, ''));
const rows = table.slice(1).filter((c) => c.length > 3).map((c) => ({
  ns: c[0], id: c[1], name: c[2], type: c[3],
  inc: c[4], dec: c[5], rf: c[6], rb: c[7], steer: c[8],
  speed: c[9], speedRate: c[10],
  topKmh: Number(c[12] || 0), sprint: c[13], reverse: c[14],
  accel: Number(c[15] || 0), throttle: c[16], t95: c[17],
  src: c[18],
}));

const TYPES = ['Track', 'Wheel', 'WheelChair', 'Ship', 'AirShip'];
const out = [];
out.push('# 载具档位分组（同一组 = 同一套 EngineInfo 数值）', '');
out.push('由 `tools/vehicle-speed-groups.mjs` 从 `docs/vehicle-speed-report.csv` 聚合生成；极速按 `VehicleEngineUtils` 逐 tick 模拟得出。', '');

for (const t of TYPES) {
  const list = rows.filter((r) => r.type === t && r.topKmh > 0);
  if (!list.length) continue;
  const groups = new Map();
  for (const r of list) {
    const key = [r.inc, r.dec, r.rf, r.rb].join('|');
    if (!groups.has(key)) groups.set(key, []);
    groups.get(key).push(r);
  }
  const arr = [...groups.values()].sort((a, b) => b[0].topKmh - a[0].topKmh);
  out.push(`## ${t}（${list.length} 台 / ${arr.length} 个档位组）`, '');
  out.push('| 极速 km/h | 冲刺 | 倒车 | 0-1s m/s² | 油门到底 s | Rf | Rb | Inc | Dec | 台数 | 代表载具 |');
  out.push('|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---|');
  for (const g of arr) {
    const r = g[0];
    const names = g.slice(0, 4).map((x) => x.name || x.id).join('、') + (g.length > 4 ? ` 等 ${g.length} 台` : '');
    out.push(`| ${r.topKmh.toFixed(1)} | ${r.sprint} | ${r.reverse} | ${r.accel.toFixed(2)} | ${r.throttle} | ${r.rf} | ${r.rb} | ${r.inc} | ${r.dec} | ${g.length} | ${names} |`);
  }
  out.push('');
}

const air = rows.filter((r) => ['Helicopter', 'Aircraft', 'Tom6'].includes(r.type));
if (air.length) {
  out.push(`## 飞行器原始字段（${air.length} 台，不做 km/h 换算）`, '');
  out.push('直升机水平速度由俯仰角决定（`deltaMovement += view * 0.061 * SpeedRate * power` 叠加 `0.022 * Lift * sin(俯仰)`），固定翼按 `force * SpeedRate` 持续推进，因此只列原始配置。', '');
  out.push('| 载具 | ID | 类型 | Inc | Dec | Speed | SpeedRate |');
  out.push('|---|---|---|---:|---:|---:|---:|');
  for (const r of air) out.push(`| ${r.name || r.id} | ${r.ns}:${r.id} | ${r.type} | ${r.inc || '—'} | ${r.dec || '—'} | ${r.speed || '—'} | ${r.speedRate || '—'} |`);
  out.push('');
}
fs.writeFileSync(OUT, out.join('\n'), 'utf8');
console.log(`分组表已写入 ${path.relative(ROOT, OUT)}（${rows.length} 台）`);
