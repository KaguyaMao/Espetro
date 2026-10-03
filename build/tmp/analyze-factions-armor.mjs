// analyze-factions-armor.mjs — 扫描 EsFactions 里所有给予物品，提取盔甲候选
// 用法: node analyze-factions-armor.mjs
import fs from 'fs';

const FILES = [
  'pla_112th_brigade', 'pla_112th_brigade_mesh', 'pla_118th_brigade', 'pla_195th',
  'ru_205th', 'ru_3th', 'ru_49th', 'ru_6th_tank',
  'us_1th_ar', 'us_1th_ri', 'us_2nd_stryker', 'us_redone'
];

const ARMOR_HINT = /(helmet|helm|_hat|cap|chest|vest|armor|armour|plate|legging|pants|trouser|boot|shoe|mask|gas_?mask)/i;

/** 解析 give 字符串：id[{snbt}] [count] */
function parseGive(raw) {
  const s = raw.trim();
  const brace = s.indexOf('{');
  let id, snbt = null, rest = '';
  if (brace === -1) {
    const parts = s.split(/\s+/);
    id = parts[0];
    rest = parts.slice(1).join(' ');
  } else {
    id = s.slice(0, brace).trim();
    // 配平花括号
    let depth = 0, end = -1;
    for (let i = brace; i < s.length; i++) {
      const c = s[i];
      if (c === '{') depth++;
      else if (c === '}') { depth--; if (depth === 0) { end = i; break; } }
    }
    if (end === -1) throw new Error('未配平的 SNBT: ' + s);
    snbt = s.slice(brace, end + 1);
    rest = s.slice(end + 1).trim();
  }
  const count = rest ? parseInt(rest, 10) : 1;
  return { id, snbt, count: Number.isFinite(count) ? count : 1 };
}

const ids = new Map();      // id -> {total, armor, files:Set, samples:[], hasNbt, slots:Set}
const armorEntries = [];    // 明细
const resupplyIds = new Map();

for (const name of FILES) {
  const path = `fac-${name}.json`;
  if (!fs.existsSync(path)) { console.log('缺文件 ' + path); continue; }
  const j = JSON.parse(fs.readFileSync(path, 'utf8'));
  const classes = j.classes || {};
  for (const [classId, cls] of Object.entries(classes)) {
    const variants = cls.variants || {};
    for (const [variantName, variant] of Object.entries(variants)) {
      const cmds = variant.commands || [];
      cmds.forEach((raw, index) => {
        let p;
        try { p = parseGive(raw); } catch (e) { console.log(`解析失败 ${name}/${classId}/${variantName}[${index}]: ${e.message}`); return; }
        const armor = ARMOR_HINT.test(p.id);
        const rec = ids.get(p.id) || { id: p.id, total: 0, armor, files: new Set(), variants: new Set(), hasNbt: false, sample: null };
        rec.total++;
        rec.files.add(name);
        rec.variants.add(`${classId}/${variantName}`);
        if (p.snbt) { rec.hasNbt = true; rec.sample = p.snbt; }
        ids.set(p.id, rec);
        if (armor) {
          armorEntries.push({ file: name, classId, variantName, index, raw, ...p });
        }
      });
      const res = variant.resupply && variant.resupply.items ? variant.resupply.items : [];
      for (const item of res) {
        const id = String(item.id || '').split('{')[0];
        resupplyIds.set(id, (resupplyIds.get(id) || 0) + 1);
      }
    }
  }
}

console.log('=== 盔甲候选（按 id 关键字命中）===');
const armorIds = [...ids.values()].filter(v => v.armor).sort((a, b) => a.id.localeCompare(b.id));
for (const v of armorIds) {
  console.log(`${v.id.padEnd(46)} 出现 ${String(v.total).padStart(4)} 次  已有NBT=${v.hasNbt ? '是' : '否'}  编制=${v.files.size}`);
  if (v.sample) console.log(`    现有 NBT 样例: ${v.sample.slice(0, 200)}`);
}
console.log(`\n盔甲候选 id 数 = ${armorIds.length}，条目数 = ${armorEntries.length}`);

console.log('\n=== 所有出现过的物品 id（含非盔甲，供人工核对）===');
const all = [...ids.values()].sort((a, b) => b.total - a.total);
for (const v of all) console.log(`${String(v.total).padStart(5)}  ${v.armor ? '[盔甲?]' : '       '} ${v.id}${v.hasNbt ? '  (含NBT)' : ''}`);
console.log(`\n不同 id 总数 = ${all.length}`);

console.log('\n=== resupply.items 里的 id（补给列表）===');
for (const [id, n] of [...resupplyIds.entries()].sort((a, b) => b[1] - a[1])) {
  console.log(`${String(n).padStart(5)}  ${ARMOR_HINT.test(id) ? '[盔甲?]' : '       '} ${id}`);
}

fs.writeFileSync('faction-armor-entries.json', JSON.stringify(armorEntries, null, 1), 'utf8');
console.log('\n明细已写出 faction-armor-entries.json');
