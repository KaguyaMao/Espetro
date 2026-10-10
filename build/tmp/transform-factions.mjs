// transform-factions.mjs — 编制盔甲韧性归零 + 删除不存在的 id + 替换缺失物品 + 修复坏 SNBT
// 规则：
//   1) 11 个已核实存在的盔甲 id → 在给予 NBT 里加 AttributeModifiers：
//      generic.armor_toughness，Operation=1(MULTIPLY_BASE)、Amount=-1.0 → 最终韧性恒为 0（与基础值无关）
//      不带 Slot 字段 → 任何装备槽都生效
//   2) 不存在的 id：mm_armor:mm_armor:blackboots_boots 删除（按用户要求按字面删）
//   3) 缺失物品替换：m15_anti_tank_mine → tm_62（显示名 M15 反坦克地雷）；m112_c4 → c4_bomb
//   4) 修复 6 处花括号不配平的给予字符串
import fs from 'fs';

const FILES = [
  'pla_112th_brigade', 'pla_112th_brigade_mesh', 'pla_118th_brigade', 'pla_195th',
  'ru_205th', 'ru_3th', 'ru_49th', 'ru_6th_tank',
  'us_1th_ar', 'us_1th_ri', 'us_2nd_stryker', 'us_redone'
];

const ARMOR = new Set([
  'dragonrise_reforge:fast_helmet',
  'dragonrise_reforge:med21_chest',
  'dragonrise_reforge:msv_chest',
  'dragonrise_reforge:msv_pants',
  'dragonrise_reforge:pants21',
  'dragonrise_reforge:t21_helmet',
  'mm_armor:blackboots_boots',
  'mm_armor:chestplateemrvest_6b_45upgraded_chestplate',
  'mm_armor:emruniform_leggings',
  'mm_armor:helmet_6b_47emrgoggles_helmet',
  'mm_armor:tsh_4helmet_helmet'
]);

const DELETE = new Set(['mm_armor:mm_armor:blackboots_boots']);

const REPLACE = new Map([
  ['superbwarfare:m15_anti_tank_mine',
    'superbwarfare:tm_62{RepairCost:0,display:{Name:\'{"text":"M15 反坦克地雷"}\'}}'],
  ['superbwarfare:m112_c4', 'superbwarfare:c4_bomb']
]);

// 精确结构修复（只在花括号不配平时套用）
const STRUCT_FIXES = [
  {
    // 机瞄：丢失了 AttachmentMUZZLE:{Count:1b,id:"tacz:attachment",tag:{ 前缀
    find: 'tacz:modern_kinetic_gun{AttachmentId:"cib:qcw05_js9_muffler"}},',
    replace: 'tacz:modern_kinetic_gun{AttachmentMUZZLE:{Count:1b,id:"tacz:attachment",tag:{AttachmentId:"cib:qcw05_js9_muffler"}},'
  },
  {
    // 红点：多包了一层 AttachmentSCOPE:{
    find: 'tacz:modern_kinetic_gun{AttachmentSCOPE:{AttachmentMUZZLE:',
    replace: 'tacz:modern_kinetic_gun{AttachmentMUZZLE:'
  }
];

function netBraces(s) {
  let d = 0;
  for (const c of s) { if (c === '{') d++; else if (c === '}') d--; }
  return d;
}

function uuidFor(id) {
  let h = 0;
  for (let i = 0; i < id.length; i++) h = (Math.imul(h, 31) + id.charCodeAt(i)) | 0;
  const a = 0x45535000 | 0;                     // 'ESP\0'
  const b = h;
  const c = Math.imul(h, 0x9e3779b1) | 0;
  const d = (h ^ 0x5bf03635) | 0;
  return `[I;${a},${b},${c},${d}]`;
}

function toughnessTag(id) {
  return '{AttributeModifiers:[{AttributeName:"minecraft:generic.armor_toughness",'
    + 'Name:"espetro_toughness_zero",Amount:-1.0,Operation:1,UUID:'
    + uuidFor(id) + '}]}';
}

/** 解析 give 字符串 → { id, snbt, countText } */
function parseGive(s) {
  const brace = s.indexOf('{');
  if (brace === -1) {
    const m = s.match(/^(\S+)(?:\s+(\d+))?$/);
    if (!m) throw new Error('无法解析: ' + s);
    return { id: m[1], snbt: null, countText: m[2] || '', raw: s };
  }
  const id = s.slice(0, brace).trim();
  let depth = 0, end = -1;
  for (let i = brace; i < s.length; i++) {
    if (s[i] === '{') depth++;
    else if (s[i] === '}') { depth--; if (depth === 0) { end = i; break; } }
  }
  if (end === -1) throw new Error('SNBT 未配平: ' + s);
  const snbt = s.slice(brace, end + 1);
  const rest = s.slice(end + 1).trim();
  return { id, snbt, countText: /^\d+$/.test(rest) ? rest : '', raw: s };
}

const report = { armor: 0, deleted: [], replaced: [], fixed: [], filesChanged: [] };

for (const name of FILES) {
  const path = `fac-${name}.json`;
  const raw = fs.readFileSync(path, 'utf8');
  const obj = JSON.parse(raw);
  let fileChanges = 0;

  for (const [classId, cls] of Object.entries(obj.classes || {})) {
    for (const [variantName, variant] of Object.entries(cls.variants || {})) {
      const cmds = variant.commands || [];
      const kept = [];
      cmds.forEach((original, index) => {
        let s = original;
        const where = `${name} / ${classId} / ${variantName} [${index}]`;

        // --- 4) 结构修复 ---
        if (netBraces(s) !== 0) {
          for (const fix of STRUCT_FIXES) {
            if (s.includes(fix.find) && netBraces(s) !== 0) {
              s = s.replace(fix.find, fix.replace);
              report.fixed.push(`${where} 枪 SNBT 结构修复`);
            }
          }
          let net = netBraces(s);
          if (net > 0) {
            // 缺 N 个右括号：补在结尾计数之前
            const m = s.match(/^(.*?)(\s+\d+)?$/);
            s = (m[1] + '}'.repeat(net) + (m[2] || ''));
            report.fixed.push(`${where} 补 ${net} 个 }`);
          } else if (net < 0) {
            report.fixed.push(`!! ${where} 仍多 ${-net} 个 }（未自动处理）`);
            kept.push(s);
            return;
          }
        }

        // --- 解析 ---
        let parsed;
        try { parsed = parseGive(s); } catch (e) {
          report.fixed.push(`!! ${where} 解析失败: ${e.message}`);
          kept.push(s);
          return;
        }

        // --- 2) 删除不存在的 id ---
        if (DELETE.has(parsed.id)) {
          report.deleted.push(`${where}  ${parsed.raw}`);
          fileChanges++;
          return;
        }

        // --- 3) 替换缺失物品 ---
        if (REPLACE.has(parsed.id)) {
          const replacement = REPLACE.get(parsed.id) + (parsed.countText ? ' ' + parsed.countText : '');
          report.replaced.push(`${where}  ${parsed.id}${parsed.countText ? ' ' + parsed.countText : ''}  ->  ${replacement}`);
          kept.push(replacement);
          fileChanges++;
          return;
        }

        // --- 1) 盔甲韧性归零 ---
        if (ARMOR.has(parsed.id)) {
          let snbt = toughnessTag(parsed.id);
          if (parsed.snbt) {
            // 已有 NBT：把 AttributeModifiers 并进去（插在最后一个 } 之前）
            const inner = parsed.snbt.slice(1, -1);
            snbt = '{' + inner + (inner.endsWith(',') ? '' : ',')
              + 'AttributeModifiers:[{AttributeName:"minecraft:generic.armor_toughness",'
              + 'Name:"espetro_toughness_zero",Amount:-1.0,Operation:1,UUID:'
              + uuidFor(parsed.id) + '}]}';
          }
          kept.push(parsed.id + snbt + (parsed.countText ? ' ' + parsed.countText : ''));
          report.armor++;
          fileChanges++;
          return;
        }

        kept.push(s);
      });
      if (kept.length !== cmds.length || kept.some((v, i) => v !== cmds[i])) {
        variant.commands = kept;
      }
    }
  }

  const out = JSON.stringify(obj, null, 2) + '\n';
  // 校验：必须仍是合法 JSON 且 round-trip 稳定
  const again = JSON.stringify(JSON.parse(out), null, 2) + '\n';
  if (again !== out) throw new Error(name + ': 序列化不稳定');
  if (fileChanges > 0) {
    fs.writeFileSync(`out-${name}.json`, out, 'utf8');
    report.filesChanged.push(`${name} (${fileChanges} 处)`);
  } else {
    fs.writeFileSync(`out-${name}.json`, raw, 'utf8');
  }
}

console.log('=== 汇总 ===');
console.log('盔甲韧性归零条目: ' + report.armor);
console.log('删除的不存在条目: ' + report.deleted.length);
report.deleted.forEach(x => console.log('   - ' + x));
console.log('替换的缺失物品: ' + report.replaced.length);
report.replaced.forEach(x => console.log('   * ' + x));
console.log('结构修复: ' + report.fixed.length);
report.fixed.forEach(x => console.log('   ~ ' + x));
console.log('改动文件: ' + report.filesChanged.join(', '));
fs.writeFileSync('transform-report.json', JSON.stringify(report, null, 1), 'utf8');
