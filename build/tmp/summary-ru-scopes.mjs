import fs from 'node:fs';
import path from 'node:path';

// 汇总俄编制里每个 AK-74 变体的瞄准镜状态（none / 1p68 / 1p78）
const dir = process.argv[2];
for (const f of fs.readdirSync(dir).filter(x => /^ru_.*\.json$/.test(x)).sort()) {
  const j = JSON.parse(fs.readFileSync(path.join(dir, f), 'utf8'));
  console.log(`═══ ${f}`);
  for (const [ck, cls] of Object.entries(j.classes || {})) {
    for (const [vk, va] of Object.entries(cls.variants || {})) {
      const cmd = (va.commands || []).find(c => /GunId:"ccrp:ak74m"/.test(String(c)));
      if (!cmd) continue;
      const s = String(cmd);
      const scope = (s.match(/AttachmentSCOPE:\{Count:1b,id:"tacz:attachment",tag:\{AttachmentId:"([^"]+)"\}\}/) || [])[1] || '（机瞄/无镜）';
      console.log(`   ${(ck + '/' + vk).padEnd(40)} ${String(va.name).padEnd(12)} 镜=${scope.padEnd(16)} 描述=${va.description || ''}`);
    }
  }
}
