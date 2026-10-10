// fix-raider-mag.mjs — 修复「奇袭兵」红点变体备用弹匣：58x42(步枪) → 58x21(QCW-05)
// 用法: node fix-raider-mag.mjs <源目录> <输出目录>
import fs from 'node:fs';
import path from 'node:path';

const SRC = process.argv[2] ?? 'fxall';
const OUT = process.argv[3] ?? 'fix-factions';
fs.mkdirSync(OUT, { recursive: true });

const GOOD = 'taczmagazines:magazine{AmmoCount:50,AmmoId:"cib:58x21",MagazineFamily:"58x21_50",MaxCapacity:50}';
const GUN = 'cib:qcw05';

function lenient(text) {
  let out = '', inStr = false, esc = false;
  for (let i = 0; i < text.length; i++) {
    const c = text[i];
    if (inStr) { out += c; if (esc) esc = false; else if (c === '\\') esc = true; else if (c === '"') inStr = false; continue; }
    if (c === '"') { inStr = true; out += c; continue; }
    out += c;
  }
  return JSON.parse(out.replace(/,(\s*[}\]])/g, '$1'));
}

let changedFiles = 0, changedVariants = 0;
for (const f of fs.readdirSync(SRC).filter(x => x.endsWith('.json')).sort()) {
  const orig = lenient(fs.readFileSync(path.join(SRC, f), 'utf8'));
  const j = JSON.parse(JSON.stringify(orig));
  let touched = [];
  for (const [ck, cls] of Object.entries(j.classes || {})) {
    const isRaider = /RAIDER/i.test(ck) || String(cls.name ?? '').includes('奇袭');
    if (!isRaider) continue;
    for (const [vk, va] of Object.entries(cls.variants || {})) {
      const cmds = va.commands || [];
      // 该变体是否用 QCW-05
      const usesGun = cmds.some(c => String(c).includes(`GunId:"${GUN}"`));
      if (!usesGun) continue;
      // 参考：同职业"机瞄"变体的正确弹匣（含匣数）
      const refMag = ((cls.variants['机瞄'] || {}).commands || [])
        .map(String).find(c => c.startsWith('taczmagazines:magazine') && c.includes('cib:58x21'));
      const refCount = refMag ? Number((refMag.match(/\}\s*(\d+)$/) || [])[1] ?? 6) : 6;
      cmds.forEach((c, i) => {
        const s = String(c);
        if (!s.startsWith('taczmagazines:magazine')) return;
        if (s.includes('cib:58x21')) return;                  // 已正确
        const fixed = `${GOOD} ${refCount}`;
        if (s !== fixed) {
          cmds[i] = fixed;
          touched.push(`${ck}/${vk} #${i}: ${s}  →  ${fixed}`);
        }
      });
    }
  }
  if (!touched.length) continue;
  changedFiles++;
  changedVariants += touched.length;

  // 校验：除被改动的命令外，其余必须完全一致
  const a = JSON.parse(JSON.stringify(orig)), b = JSON.parse(JSON.stringify(j));
  for (const [ck, cls] of Object.entries(b.classes || {})) {
    for (const [vk, va] of Object.entries(cls.variants || {})) {
      const oa = a.classes[ck].variants[vk].commands;
      (va.commands || []).forEach((c, i) => { if (c !== oa[i]) { oa[i] = c; } });
    }
  }
  if (JSON.stringify(a) !== JSON.stringify(b)) {
    console.log(`!! ${f} 校验失败：除弹匣外还有差异，已跳过`);
    continue;
  }

  fs.writeFileSync(path.join(OUT, f), JSON.stringify(j, null, 2), 'utf8');
  console.log(`OK ${f}  修改 ${touched.length} 条`);
  touched.forEach(t => console.log('     ' + t));
}
console.log(`\n修改文件: ${changedFiles}，修改条目: ${changedVariants}`);
