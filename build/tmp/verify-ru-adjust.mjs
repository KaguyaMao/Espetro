// verify-ru-adjust.mjs
import fs from 'fs';
const FILES = ['ru_205th', 'ru_3th', 'ru_49th', 'ru_6th_tank'];
const problems = [];
for (const f of FILES) {
  const before = JSON.parse(fs.readFileSync(`fix-${f}.json`, 'utf8'));
  const after = JSON.parse(fs.readFileSync(`fix2-${f}.json`, 'utf8'));

  // 除 classes 外不得改动
  const strip = (j) => { const c = JSON.parse(JSON.stringify(j)); delete c.classes; return JSON.stringify(c); };
  if (strip(before) !== strip(after)) problems.push(`${f}: classes 之外的字段被改动`);

  const ids = Object.keys(after.classes);
  if (ids.length !== Object.keys(before.classes).length) problems.push(`${f}: 职业数量变化`);

  // 侦察兵 row=4 且位于 row4 第二位
  const scoutId = ids.find(k => /_SCOUT$/.test(k));
  const scout = after.classes[scoutId];
  if (scout.row !== 4) problems.push(`${f}: ${scoutId} row=${scout.row}`);
  const row4 = ids.filter(k => after.classes[k].row === 4);
  if (row4[1] !== scoutId) problems.push(`${f}: row4 顺序=${row4.join(',')}，侦察兵不在第二位`);

  // 精确射手：不能还有 pso1e，必须是 pso1
  for (const [cid, cls] of Object.entries(after.classes)) {
    if (!/MARKSMAN/.test(cid)) continue;
    for (const [vk, v] of Object.entries(cls.variants || {})) {
      const guns = (v.commands || []).filter(s => s.includes('AttachmentSCOPE'));
      for (const g of guns) {
        if (g.includes('huinuo:pso1e')) problems.push(`${f}/${cid}[${vk}]: 仍是 pso1e`);
        if (!g.includes('huinuo:pso1')) problems.push(`${f}/${cid}[${vk}]: 未找到 pso1`);
      }
      if (guns.length === 0) problems.push(`${f}/${cid}[${vk}]: 没有瞄具`);
    }
  }
  // 全文件不应再有 pso1e（俄军只有精确射手在用）
  const txt = fs.readFileSync(`fix2-${f}.json`, 'utf8');
  const n = (txt.match(/pso1e/g) || []).length;
  if (n > 0) problems.push(`${f}: 文件里仍有 ${n} 处 pso1e`);
}
console.log(problems.length === 0 ? '=== 俄军两项调整校验通过 ===' : `=== 问题 ${problems.length} 项 ===`);
problems.forEach(p => console.log('   X ' + p));
// 打印精确射手瞄具确认
const j = JSON.parse(fs.readFileSync('fix2-ru_205th.json', 'utf8'));
const m = Object.entries(j.classes).find(([k]) => /MARKSMAN/.test(k));
console.log('\n精确射手主武器（ru_205th）:');
console.log('  ' + m[1].variants.default.commands.find(s => s.includes('cib:svd')));
