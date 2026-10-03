import fs from 'node:fs';
import path from 'node:path';

// 校验：所有带 1P68 的 AK-74 条目里，AttachmentSCOPE 前后都有逗号（SNBT 合法），且键序与已有写法一致
const SCOPE = 'AttachmentSCOPE:{Count:1b,id:"tacz:attachment",tag:{AttachmentId:"huinuo:1p68"}}';
const dir = process.argv[2];
const files = process.argv.slice(3);

let total = 0, bad = 0, withoutScope = [];
for (const f of files) {
  const j = JSON.parse(fs.readFileSync(path.join(dir, f), 'utf8'));
  for (const [ck, cls] of Object.entries(j.classes || {})) {
    for (const [vk, va] of Object.entries(cls.variants || {})) {
      for (const cmd of (va.commands || [])) {
        const s = String(cmd);
        if (!/GunId:"ccrp:ak74m"/.test(s)) continue;
        const i = s.indexOf(SCOPE);
        const isIron = /机瞄/.test(String(va.name || '') + String(va.description || ''));
        if (i < 0) { if (!isIron) withoutScope.push(`${f} ${ck}/${vk} [${va.description}]`); continue; }
        total++;
        const before = s[i - 1], after = s[i + SCOPE.length];
        if (before !== ',' || after !== ',') {
          bad++;
          console.log(`  ✘ ${f} ${ck}/${vk}  前="${before}" 后="${after}"`);
        }
      }
    }
  }
}
console.log(`含 1P68 的 AK74 条目 = ${total}，逗号异常 = ${bad}`);
console.log(`非机瞄却仍缺瞄准镜的条目 = ${withoutScope.length}`);
for (const w of withoutScope) console.log('   ! ' + w);

const j = JSON.parse(fs.readFileSync(path.join(dir, files[0]), 'utf8'));
const medic = String(j.classes.RU_205th_MEDIC.variants.default.commands.find(x => /ak74/i.test(x)));
const rifle = String(j.classes.RU_205th_RIFLEMAN.variants.default.commands.find(x => /ak74/i.test(x)));
console.log('\nMEDIC/default 改后:');
console.log('  ' + medic);
console.log('\nRIFLEMAN/default 参照:');
console.log('  ' + rifle);
