// apply-dirarmor-profile.mjs — 给载具 JSON 写入方向抗性档位（支持多条规则相乘）
//
// 引擎语义（dragonrise_reforge DirectionArmorRule + VehicleDirectionArmor）：
//   * 每条 `$...getSourceAngle(source, m)...` 规则给出倍率 max(1 - m*dot, 0.5)（dot：正面 +1 / 侧面 0 / 背面 -1）
//   * 一辆车上所有规则【连乘】（VehicleDirectionArmor 里 multiplier *= rule.apply(...)）
//   * 规则尾巴可带 `* (entity.getHealth() > T ? A : B)`；取 T=-1 即恒真 → 相当于一个常数因子 A
// 所以侧面（dot=0）只有靠「常数因子」才能离开 1.0，而三向目标可用多条规则精确拼出：
//
//   档位 exact05_11_15（正面 0.5 / 侧面 1.1 / 背面 1.5）：
//     m = [-0.3164172, 0.4122684, 0.4124984]，常数 1.1
//     Π(1-m) = 0.45454545 → ×1.1 = 0.5 ；Π(1+m) = 1.36363636 → ×1.1 = 1.5 ；侧面 = 1.1
//   档位 m05：单条 m = 0.5 → 0.5 / 1.0 / 1.5（侧面回不到 1.1）
//   档位 old_exact13：上一轮用过的 1.3 / 0.9 / 0.5
//
// 用法: node apply-dirarmor-profile.mjs <档位> [outDir=dirarmor] [--dry]
import fs from 'node:fs';
import path from 'node:path';

const JARDIR = 'vehjars';
const KJSDIR = 'vehdata';

const CONST_GATE = (c) => ` * (entity.getHealth() > -1 ? ${c} : ${c})`;
const PROFILES = {
  exact05_11_15: [
    '$entity.getSourceAngle(source, -0.3164163132) * damage',
    '$entity.getSourceAngle(source, 0.4122722554) * damage',
    '$entity.getSourceAngle(source, 0.4125) * damage' + CONST_GATE(1.1),
  ],
  m05: ['$entity.getSourceAngle(source, 0.5) * damage'],
  old_exact13: ['$entity.getSourceAngle(source, -0.4444444) * damage' + CONST_GATE(0.9)],
};
const MODE = process.argv[2] ?? 'exact05_11_15';
const OUT = process.argv[3] ?? 'dirarmor';
const RULES = PROFILES[MODE];
if (!RULES) { console.error(`未知档位 ${MODE}，可选: ${Object.keys(PROFILES).join(', ')}`); process.exit(2); }

// 出场载具中「非卡车/吉普」的 25 种（role 取自编制 vehicles{}；truck / supply_truck / car 视为卡车吉普）
const INPLAY_COMBAT = [
  'dragonrise_reforge:bmp3', 'dragonrise_reforge:m1126', 'dragonrise_reforge:m1128', 'dragonrise_reforge:m113',
  'dragonrise_reforge:m1296', 'dragonrise_reforge:m1a2sepv1', 'dragonrise_reforge:m1a2sepv2', 'dragonrise_reforge:m3a3',
  'dragonrise_reforge:t72b3', 'dragonrise_reforge:t90mh', 'dragonrise_reforge:uh60', 'dragonrise_reforge:z20',
  'dragonrise_reforge:zbd04a', 'dragonrise_reforge:zbd05', 'dragonrise_reforge:zbl08', 'dragonrise_reforge:zlt11',
  'dragonrise_reforge:zsd05', 'dragonrise_reforge:zsl10', 'dragonrise_reforge:ztd05', 'dragonrise_reforge:ztz96a',
  'dragonrise_reforge:ztz99a', 'fcp:bmp1am', 'fcp:btr80', 'fcp:btr82', 'vvp:mi_8',
];
const EXTRA = ['superbwarfare:drone', 'dragonrise_reforge:sx1', 'superbwarfare:annihilator'];
const TRUCK_JEEP = ['dragonrise_reforge:mv3_armed', 'dragonrise_reforge:mv3_supply', 'dragonrise_reforge:ural4320_supply',
  'fcp:gaz_tigr_rws', 'fcp:kamaz', 'fcp:matv_crow'];
const RESTORE_JAR_LINE = ['dragonrise_reforge:csk181'];
const TARGETS = [...INPLAY_COMBAT, ...EXTRA];

function findJarFile(ns, name) {
  for (const jar of fs.readdirSync(JARDIR, { withFileTypes: true })) {
    if (!jar.isDirectory()) continue;
    const p = path.join(JARDIR, jar.name, `${ns}__${name}.json`);
    if (fs.existsSync(p)) return { p, jar: jar.name };
  }
  return null;
}
function arrayRange(text) {
  const i = text.indexOf('"DamageModifiers"');
  if (i < 0) return null;
  const open = text.indexOf('[', i);
  if (open < 0) return null;
  let depth = 0, inStr = false, esc = false;
  for (let j = open; j < text.length; j++) {
    const ch = text[j];
    if (inStr) { if (esc) esc = false; else if (ch === '\\') esc = true; else if (ch === '"') inStr = false; continue; }
    if (ch === '"') { inStr = true; continue; }
    if (ch === '[') depth++;
    else if (ch === ']') { depth--; if (depth === 0) return { open, close: j }; }
  }
  return null;
}
function stripTrailingComma(text) { return text.replace(/,(\s*\])/g, '$1'); }

function insertNewArray(text, rules) {
  const brace = text.indexOf('{');
  if (brace < 0) return null;
  const body = rules.map((r) => `    ${JSON.stringify(r)}`).join(',\n');
  return { text: text.slice(0, brace + 1) + `\n  "DamageModifiers": [\n${body}\n  ],` + text.slice(brace + 1), action: `new-array(${rules.length})` };
}
function upsertRules(text, rules) {
  const re = /"\$[^"]*getSourceAngle[^"]*"/g;
  const matches = [...text.matchAll(re)];
  if (matches.length) {
    // 用整组规则替换「第一条到最末条」之间的内容（中间的分隔符一并丢弃）
    const first = matches[0];
    const last = matches[matches.length - 1];
    const lineStart = text.lastIndexOf('\n', first.index) + 1;
    const indent = /^\s*/.exec(text.slice(lineStart, first.index))[0];
    const block = rules.map((r) => JSON.stringify(r)).join(',\n' + indent);
    const start = first.index;
    const end = last.index + last[0].length;
    return { text: text.slice(0, start) + block + text.slice(end), action: `replace(${matches.length}->${rules.length})` };
  }
  const r = arrayRange(text);
  if (!r) return insertNewArray(text, rules) ?? { text: null, action: 'no-array' };
  const head = text.slice(0, r.close);
  const hasItems = head.slice(r.open + 1).trim().length > 0;
  const indentMatch = /\n(\s+)"[^"]*"\s*$/.exec(head);
  const indent = indentMatch ? indentMatch[1] : '    ';
  const closingIndentMatch = /\n(\s*)$/.exec(head);
  const closingIndent = closingIndentMatch ? closingIndentMatch[1] : '  ';
  const body = rules.map((x) => indent + JSON.stringify(x)).join(',\n');
  const insert = (hasItems ? ',\n' : '\n') + body + '\n' + closingIndent;
  return { text: text.slice(0, r.close) + insert + text.slice(r.close), action: `insert(${rules.length})` };
}

fs.mkdirSync(path.join(OUT, 'before'), { recursive: true });
fs.mkdirSync(path.join(OUT, 'after'), { recursive: true });
const manifest = [];

function emit(id, rules, note) {
  const [ns, name] = id.split(':');
  const kjsPath = path.join(KJSDIR, `${ns}__${name}.json`);
  const jarInfo = findJarFile(ns, name);
  const fromKjs = fs.existsSync(kjsPath);
  const srcPath = fromKjs ? kjsPath : jarInfo?.p;
  if (!srcPath) { manifest.push({ id, status: 'no-source' }); console.log(`[X] ${id} 找不到源文件`); return; }
  const srcText = fs.readFileSync(srcPath, 'utf8');
  let srcStrict = true;
  try { JSON.parse(srcText); } catch { srcStrict = false; }

  const outName = `${ns}__${name}.json`;
  fs.writeFileSync(path.join(OUT, 'before', outName), srcText, 'utf8');
  let text = srcStrict ? srcText : stripTrailingComma(srcText);
  text = upsertRules(text, rules).text;
  if (text === null) { manifest.push({ id, status: 'no-damage-modifiers' }); console.log(`[X] ${id} 无 DamageModifiers`); return; }

  let strict = true, err = null;
  try { JSON.parse(text); } catch (e) { strict = false; err = e.message; }
  fs.writeFileSync(path.join(OUT, 'after', outName), text, 'utf8');
  manifest.push({ id, status: strict ? 'ok' : 'INVALID', source: fromKjs ? 'kubejs' : `jar:${jarInfo.jar}`,
    newOverride: !fromKjs, fixedTrailingComma: !srcStrict, err, note: note ?? '', outName, rules });
  console.log(`${strict ? '[OK]' : '[BAD]'} ${id.padEnd(32)} 源=${fromKjs ? 'kubejs' : 'jar'}${!srcStrict ? ' [修尾随逗号]' : ''}${!fromKjs ? ' [新建覆盖]' : ''}${err ? ' ERR=' + err : ''}`);
}

console.log(`=== 档位 ${MODE}（${RULES.length} 条规则）===`);
for (const r of RULES) console.log(`   ${r}`);
console.log('');
for (const id of TARGETS) emit(id, RULES);
for (const id of RESTORE_JAR_LINE) {
  const [ns, name] = id.split(':');
  const jarInfo = findJarFile(ns, name);
  if (!jarInfo) { console.log(`[X] ${id} jar 中找不到`); continue; }
  const jarRule = /"\$[^"]*getSourceAngle[^"]*"/.exec(fs.readFileSync(jarInfo.p, 'utf8'))?.[0];
  if (!jarRule) { console.log(`[!] ${id} jar 里没有 $ 规则，跳过`); continue; }
  emit(id, [JSON.parse(jarRule)], '恢复 jar 原值');
}
fs.writeFileSync(path.join(OUT, 'manifest.json'), JSON.stringify({
  mode: MODE, rules: RULES, targets: TARGETS, truckJeepUntouched: TRUCK_JEEP, restore: RESTORE_JAR_LINE, files: manifest,
}, null, 2), 'utf8');
const bad = manifest.filter((m) => m.status !== 'ok');
console.log(`\n生成 ${manifest.length} 个文件 -> ${OUT}/after（异常 ${bad.length}）`);
if (bad.length) console.log(JSON.stringify(bad, null, 1));
