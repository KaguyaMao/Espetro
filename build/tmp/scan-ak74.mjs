import fs from 'node:fs';
import path from 'node:path';

// 找出编制里所有 AK-74 相关武器条目，打印其变体名与附件 NBT 摘要
const dir = process.argv[2];
const filter = process.argv[3] ?? 'ak74';
for (const f of fs.readdirSync(dir).filter(x => x.endsWith('.json')).sort()) {
  const j = JSON.parse(fs.readFileSync(path.join(dir, f), 'utf8'));
  const rows = [];
  for (const [ck, cls] of Object.entries(j.classes || {})) {
    for (const [vk, variant] of Object.entries(cls.variants || {})) {
      for (const cmd of (variant.commands || [])) {
        const s = String(cmd);
        if (new RegExp(filter, 'i').test(s)) {
          const gun = (s.match(/GunId:\s*"([^"]+)"/) || [])[1] || '(非枪械)';
          const attach = [...s.matchAll(/Attachment([A-Z_]+):\s*\{[^}]*AttachmentId:\s*"([^"]+)"/g)]
            .map(m => `${m[1]}=${m[2]}`).join(' ');
          rows.push(`      ${ck} / ${vk}  →  GunId=${gun}  附件: ${attach || '（无）'}`);
        }
      }
    }
  }
  if (rows.length) {
    console.log(`═══ ${f}  (${rows.length} 条)`);
    console.log(rows.join('\n'));
  }
}
