// verify-factions-out.mjs — 校验转换结果
import fs from 'fs';

const FILES = [
  'pla_112th_brigade', 'pla_112th_brigade_mesh', 'pla_118th_brigade', 'pla_195th',
  'ru_205th', 'ru_3th', 'ru_49th', 'ru_6th_tank',
  'us_1th_ar', 'us_1th_ri', 'us_2nd_stryker', 'us_redone'
];
const ARMOR = [
  'dragonrise_reforge:fast_helmet', 'dragonrise_reforge:med21_chest',
  'dragonrise_reforge:msv_chest', 'dragonrise_reforge:msv_pants', 'dragonrise_reforge:pants21',
  'dragonrise_reforge:t21_helmet', 'mm_armor:blackboots_boots',
  'mm_armor:chestplateemrvest_6b_45upgraded_chestplate', 'mm_armor:emruniform_leggings',
  'mm_armor:helmet_6b_47emrgoggles_helmet', 'mm_armor:tsh_4helmet_helmet'
];

function net(s) { let d = 0; for (const c of s) { if (c === '{') d++; else if (c === '}') d--; } return d; }

let problems = [];
let armorTagged = 0, armorUntagged = [];
const perFile = {};

for (const name of FILES) {
  const oldRaw = fs.readFileSync(`fac-${name}.json`, 'utf8');
  const newRaw = fs.readFileSync(`out-${name}.json`, 'utf8');
  const oldObj = JSON.parse(oldRaw);
  const newObj = JSON.parse(newRaw);

  // 1) 非 commands 部分必须完全一致
  const strip = (o) => {
    const c = JSON.parse(JSON.stringify(o));
    for (const cls of Object.values(c.classes || {}))
      for (const v of Object.values(cls.variants || {})) delete v.commands;
    return JSON.stringify(c);
  };
  if (strip(oldObj) !== strip(newObj)) problems.push(`${name}: commands 之外的内容被改动了！`);

  // 2) 遍历新内容做检查
  let taggedThisFile = 0, changedVariants = 0;
  for (const [cid, cls] of Object.entries(newObj.classes || {})) {
    for (const [vn, v] of Object.entries(cls.variants || {})) {
      const oldV = (((oldObj.classes || {})[cid] || {}).variants || {})[vn] || {};
      const oldCmds = oldV.commands || [];
      const newCmds = v.commands || [];
      if (JSON.stringify(oldCmds) !== JSON.stringify(newCmds)) changedVariants++;

      for (const s of newCmds) {
        if (net(s) !== 0) problems.push(`${name}/${cid}/${vn}: 花括号不配平 -> ${s.slice(0, 80)}`);
        if (/mm_armor:mm_armor:|m15_anti_tank_mine|m112_c4/.test(s))
          problems.push(`${name}/${cid}/${vn}: 仍存在应删除/替换的 id -> ${s.slice(0, 80)}`);
        const id = s.split('{')[0].trim();
        if (ARMOR.includes(id)) {
          if (s.includes('espetro_toughness_zero')
            && s.includes('minecraft:generic.armor_toughness')
            && s.includes('Operation:1') && s.includes('Amount:-1.0')) {
            taggedThisFile++; armorTagged++;
          } else armorUntagged.push(`${name}/${cid}/${vn}: ${s}`);
        }
      }
    }
  }
  perFile[name] = { tagged: taggedThisFile, changedVariants };
  console.log(`${name.padEnd(26)} 韧性归零 ${String(taggedThisFile).padStart(4)} 条   改动变体 ${changedVariants}`);
}

console.log('');
console.log('盔甲归零条目合计: ' + armorTagged + '（未标注: ' + armorUntagged.length + '）');
armorUntagged.slice(0, 10).forEach(x => console.log('   ! ' + x));
console.log('');
console.log(problems.length === 0 ? '=== 全部检查通过 ===' : ('=== 发现问题 ' + problems.length + ' 项 ==='));
problems.slice(0, 20).forEach(x => console.log('   X ' + x));

// 3) 样例 diff
console.log('');
console.log('=== 样例：us_1th_ar / US_1th_arma_ENGINEER / default 的前 12 条 ===');
const sample = JSON.parse(fs.readFileSync('out-us_1th_ar.json', 'utf8'))
  .classes['US_1th_arma_ENGINEER'].variants['default'].commands;
sample.slice(0, 12).forEach((s, i) => console.log('   [' + i + '] ' + s));

console.log('');
console.log('=== 样例：ru_205th 某职业的盔甲 4 件 ===');
const ru = JSON.parse(fs.readFileSync('out-ru_205th.json', 'utf8'));
const ruCmds = ru.classes['RU_205_COMMANDER'] ? ru.classes['RU_205_COMMANDER'].variants['default'].commands
  : Object.values(ru.classes)[0].variants['default'].commands;
ruCmds.filter(s => /mm_armor/.test(s)).forEach(s => console.log('   ' + s));

console.log('');
console.log('=== 样例：pla_118th_brigade 修好的两把枪 ===');
const pla = JSON.parse(fs.readFileSync('out-pla_118th_brigade.json', 'utf8'));
for (const [vn, v] of Object.entries(pla.classes['PLA_118_RAIDER'].variants)) {
  const g = (v.commands || []).find(s => s.startsWith('tacz:modern_kinetic_gun'));
  console.log('   [' + vn + '] 净值=' + net(g));
  console.log('   ' + g);
}
