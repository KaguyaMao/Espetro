// edit-factions.mjs — 字节安全的按行编辑 EsFactions JSON:
//   pla_112th_brigade_mesh.json: PLA_112_mesh_ -> PLA_112th_mesh_ (15 键)
//   plamc_5th.json:               PLA_112_mesh_ -> PLA_5th_mesh_   (15 键)
//   ru_205th.json:                删除 VehTypes 中多余的 "truck_supply", 行
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');

const DIR = 'D:/minecraft/modp/Espetro/build/tmp/fx/';

function editLines(file, fn) {
  const path = DIR + file;
  const txt = fs.readFileSync(path, 'utf8');
  const eol = txt.includes('\r\n') ? '\r\n' : '\n';
  const lines = txt.split(/\r?\n/);
  const out = fn(lines, txt);
  const replaced = out.replaced;
  const result = out.lines.join(eol);
  if (result === txt) throw new Error(file + ': 无变化');
  fs.writeFileSync(path, result, 'utf8');
  console.log(file + ': replaced=' + replaced + ' eol=' + (eol === '\r\n' ? 'CRLF' : 'LF'));
}

// 1. pla_112th_brigade_mesh.json — 键前缀替换
editLines('pla_112th_brigade_mesh.json', (lines) => {
  let replaced = 0;
  const out = lines.map((l) => {
    if (l.includes('PLA_112_mesh_')) { replaced += (l.split('PLA_112_mesh_').length - 1); return l.split('PLA_112_mesh_').join('PLA_112th_mesh_'); }
    return l;
  });
  return { lines: out, replaced };
});

// 2. plamc_5th.json — 键前缀替换
editLines('plamc_5th.json', (lines) => {
  let replaced = 0;
  const out = lines.map((l) => {
    if (l.includes('PLA_112_mesh_')) { replaced += (l.split('PLA_112_mesh_').length - 1); return l.split('PLA_112_mesh_').join('PLA_5th_mesh_'); }
    return l;
  });
  return { lines: out, replaced };
});

// 3. ru_205th.json — 删除含 "truck_supply", 的行(仅 VehTypes 数组内一行)
editLines('ru_205th.json', (lines) => {
  const out = [];
  let removed = 0;
  for (const l of lines) {
    if (l.trim() === '"truck_supply",' || l.trim() === '"truck_supply"') { removed++; continue; }
    out.push(l);
  }
  if (removed !== 1) throw new Error('ru_205th: 期望删除 1 行 truck_supply, 实际 ' + removed);
  return { lines: out, replaced: removed };
});

// ==== 校验 ====
for (const f of ['pla_112th_brigade_mesh.json', 'plamc_5th.json', 'plamc_5th_at.json', 'ru_205th.json']) {
  const txt = fs.readFileSync(DIR + f, 'utf8');
  const j = JSON.parse(txt);
  const ks = Object.keys(j.classes ?? {});
  console.log('--- ' + f + ' classes=' + ks.length + ' e.g. ' + ks.slice(0, 3).join(', '));
  if (f === 'ru_205th.json') {
    const veh = JSON.stringify(j.VehTypes);
    console.log('    VehTypes=' + veh + ' | truck_supply left=' + (txt.split('truck_supply').length - 1));
    const a = [...j.VehTypes].sort(), b = Object.keys(j.vehicles).sort();
    console.log('    VehTypes==vehicles keys: ' + JSON.stringify(a) + ' vs ' + JSON.stringify(b) + ' -> ' + (JSON.stringify(a) === JSON.stringify(b)));
  }
  if (f !== 'plamc_5th_at.json' && f !== 'ru_205th.json') {
    console.log('    残留 PLA_112_mesh_: ' + (txt.split('PLA_112_mesh_').length - 1));
  }
}
