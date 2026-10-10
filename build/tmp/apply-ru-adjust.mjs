// apply-ru-adjust.mjs — 俄军两项调整
//  ① 侦察兵移到 row4 第二位（紧跟重型反坦克兵之后）
//  ② 精确射手瞄具 huinuo:pso1e -> huinuo:pso1
import fs from 'fs';

const FILES = ['ru_205th', 'ru_3th', 'ru_49th', 'ru_6th_tank'];
const report = { scoutMoved: [], scopeSwapped: 0, otherPso1e: [], row4After: {} };

for (const f of FILES) {
  const j = JSON.parse(fs.readFileSync(`fix-${f}.json`, 'utf8'));
  const entries = Object.entries(j.classes);

  // ---------- ② 精确射手瞄具 ----------
  for (const [cid, cls] of entries) {
    if (!/MARKSMAN/.test(cid)) continue;
    for (const [vk, v] of Object.entries(cls.variants || {})) {
      v.commands = (v.commands || []).map(s => {
        if (s.includes('AttachmentId:"huinuo:pso1e"')) {
          report.scopeSwapped++;
          return s.replace('AttachmentId:"huinuo:pso1e"', 'AttachmentId:"huinuo:pso1"');
        }
        return s;
      });
    }
  }
  // 记录其它职业是否也在用 pso1e（不擅自改）
  for (const [cid, cls] of entries) {
    if (/MARKSMAN/.test(cid)) continue;
    for (const v of Object.values(cls.variants || {})) {
      for (const s of (v.commands || [])) {
        if (s.includes('AttachmentId:"huinuo:pso1e"')) report.otherPso1e.push(`${f}/${cid}`);
      }
    }
  }

  // ---------- ① 侦察兵 -> row4 第二位 ----------
  const scoutId = entries.map(([k]) => k).find(k => /_SCOUT$/.test(k));
  const heavyId = entries.map(([k]) => k).find(k => /_HEAVYANTITANK$/.test(k));
  if (!scoutId) { console.log(`!! ${f}: 找不到 SCOUT`); continue; }
  j.classes[scoutId].row = 4;

  const rebuilt = {};
  for (const [cid, cls] of Object.entries(j.classes)) {
    if (cid === scoutId) continue;              // 原位置跳过
    rebuilt[cid] = cls;
    if (cid === heavyId) rebuilt[scoutId] = j.classes[scoutId];   // 紧跟重筒之后 = row4 第二位
  }
  if (!heavyId) { rebuilt[scoutId] = j.classes[scoutId]; }        // 兜底：放末尾
  j.classes = rebuilt;

  report.scoutMoved.push(`${f}: ${scoutId} -> row=${j.classes[scoutId].row}`);
  report.row4After[f] = Object.entries(j.classes)
    .filter(([, c]) => c.row === 4)
    .map(([k, c]) => c.name + '(' + k + ')');

  const out = JSON.stringify(j, null, 2) + '\n';
  JSON.parse(out);
  fs.writeFileSync(`fix2-${f}.json`, out, 'utf8');
}

console.log('=== ① 侦察兵移动 ===');
report.scoutMoved.forEach(x => console.log('   ' + x));
console.log('=== row4 最终顺序（客户端按此渲染）===');
for (const [f, list] of Object.entries(report.row4After)) {
  list.forEach((x, i) => console.log(`   ${f} [${i + 1}] ${x}`));
}
console.log(`=== ② 瞄具替换 pso1e -> pso1 : ${report.scopeSwapped} 处 ===`);
if (report.otherPso1e.length) {
  console.log('   注意：其它职业也在用 pso1e（未改动）: ' + [...new Set(report.otherPso1e)].join(', '));
}
fs.writeFileSync('ru-adjust-report.json', JSON.stringify(report, null, 1), 'utf8');
