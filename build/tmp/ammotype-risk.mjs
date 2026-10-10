import fs from 'node:fs';
import path from 'node:path';

// 列出 AmmoType 为数组(>1 项或含对象元素) 的武器：这类字段在调参界面按"应用"时会被写成字符串
const dirs = {
  '包内(jar)': 'jar-vehicles',
  '客户端基础包': 'D:/minecraft/squadMC预发布测试/versions/Squad预发布测试/saves/新的世界/datapacks/dragonrise_reforge/data/dragonrise_reforge/sbw/vehicles',
  '调参包 va_shotpos': 'D:/minecraft/squadMC预发布测试/versions/Squad预发布测试/saves/新的世界/datapacks/va_shotpos/data/dragonrise_reforge/sbw/vehicles',
};
for (const [label, d] of Object.entries(dirs)) {
  const risky = [], bad = [];
  for (const f of fs.readdirSync(d).filter(x => x.endsWith('.json'))) {
    const j = JSON.parse(fs.readFileSync(path.join(d, f), 'utf8'));
    for (const [wn, wd] of Object.entries(j.Weapons || {})) {
      const a = wd && wd.AmmoType;
      if (typeof a === 'string') bad.push(`${f.replace('.json', '')}/${wn}`);
      else if (Array.isArray(a) && (a.length > 1 || a.some(x => x && typeof x === 'object')))
        risky.push(`${f.replace('.json', '')}/${wn}(${a.length})`);
    }
  }
  console.log(`  ${label}:`);
  console.log(`     高危字段(多弹药/带 Override) ${risky.length} 处: ${risky.slice(0, 30).join(', ')}${risky.length > 30 ? ` …共 ${risky.length}` : ''}`);
  console.log(`     已被字符串化 ${bad.length} 处: ${bad.join(', ') || '（无）'}`);
}
