// icon-audit.mjs — 统计配置里用到的 icon slug 及缺失情况
import fs from 'fs';

const CFG = 'D:/minecraft/modp/Espetro/build/tmp/fxall/';
const JAR = 'D:/minecraft/modp/Espetro/src/main/resources/assets/espetro/textures/gui/roles/';
const NEW = 'C:/Users/Administrator/Downloads/HUD/Roles/';

const jarFiles = new Set(fs.readdirSync(JAR).map((f) => f.replace(/\.png$/i, '')));
const newFiles = new Set(fs.readdirSync(NEW).map((f) => f.replace(/\.png$/i, '')));

const slugCount = new Map();   // slug -> [classId...]
const imgCount = new Map();    // IconImage -> [classId...]
for (const f of fs.readdirSync(CFG).filter((x) => x.endsWith('.json'))) {
  const j = JSON.parse(fs.readFileSync(CFG + f, 'utf8'));
  for (const [cid, cls] of Object.entries(j.classes || {})) {
    const slug = cls.icon ?? '';
    const img = cls.iconImage ?? '';
    slugCount.set(slug, [...(slugCount.get(slug) || []), cid]);
    imgCount.set(img, [...(imgCount.get(img) || []), cid]);
  }
}

console.log('=== 配置中的 icon slug（共 ' + slugCount.size + ' 种）');
for (const [s, ids] of [...slugCount.entries()].sort()) {
  const inJar = jarFiles.has(s);
  const guess = [...newFiles].filter((n) => n.toLowerCase().replace(/^t_role_|^t_rank_|^incap_/, '') === s.toLowerCase());
  console.log(`${s.padEnd(24)} 职业×${String(ids.length).padStart(3)} jar有=${inJar ? 'Y' : 'N'} 新文件对应=${guess.join(',') || '—'}`);
}

console.log('\n=== 配置中的 IconImage 前缀统计');
const prefixes = new Map();
for (const [p, ids] of imgCount) {
  const key = p ? p.replace(/[^/]+$/, '') : '(空)';
  prefixes.set(key, (prefixes.get(key) || 0) + ids.length);
}
for (const [p, n] of [...prefixes.entries()].sort()) console.log(`${p} ×${n}`);

console.log('\n=== 新图标与 jar 现有图标的差异');
const jarOnly = [...jarFiles].filter((x) => !newFiles.has('T_role_' + x) && !newFiles.has(x)).sort();
const newOnly = [...newFiles].filter((x) => {
  const bare = x.replace(/^T_role_|^T_rank_|^Incap_/i, '');
  return !jarFiles.has(x) && !jarFiles.has(bare);
}).sort();
console.log('jar 有但新图标无对应: ' + (jarOnly.join(', ') || '无'));
console.log('新图标有但 jar 无（需新增文件名）: ' + newOnly.join(', '));
