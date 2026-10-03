import fs from 'node:fs';
import path from 'node:path';

// 打印俄编制里所有含 AK-74 的变体：变体名 / 描述 / 当前瞄准镜附件
const dir = process.argv[2];
for (const f of fs.readdirSync(dir).filter(x => /^ru_.*\.json$/.test(x)).sort()) {
  const j = JSON.parse(fs.readFileSync(path.join(dir, f), 'utf8'));
  console.log(`═══ ${f}`);
  for (const [ck, cls] of Object.entries(j.classes || {})) {
    for (const [vk, variant] of Object.entries(cls.variants || {})) {
      const ak = (variant.commands || []).find(c => /ak74/i.test(String(c)));
      if (!ak) continue;
      const s = String(ak);
      const scope = (s.match(/AttachmentSCOPE:\s*\{[^}]*AttachmentId:\s*"([^"]+)"/) || [])[1] || '（无瞄准镜）';
      const others = [...s.matchAll(/Attachment([A-Z_]+):\s*\{[^}]*AttachmentId:\s*"([^"]+)"/g)]
        .filter(m => m[1] !== 'SCOPE').map(m => `${m[1]}=${m[2]}`).join(' ');
      console.log(`   ${(ck + '/' + vk).padEnd(42)} 变体名=${String(variant.name).padEnd(12)} 描述=${String(variant.description || '').padEnd(18)} SCOPE=${scope.padEnd(14)} ${others}`);
    }
  }
}
