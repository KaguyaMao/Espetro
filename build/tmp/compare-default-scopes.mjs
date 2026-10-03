import fs from 'node:fs';
import path from 'node:path';

// 对比：各编制里 队长/载具队长/医护兵/工程兵/侦察兵/步枪兵 的 default 变体带什么瞄准镜
const dir = process.argv[2];
const roles = ['COMMANDER', 'CREW_Leader', 'MEDIC', 'ENGINEER', 'SCOUT', 'RIFLEMAN', 'GRENADIER', 'ANTITANK', 'HEAVYANTITANK'];
for (const f of fs.readdirSync(dir).filter(x => x.endsWith('.json')).sort()) {
  const j = JSON.parse(fs.readFileSync(path.join(dir, f), 'utf8'));
  const lines = [];
  for (const [ck, cls] of Object.entries(j.classes || {})) {
    if (!roles.some(r => ck.endsWith('_' + r))) continue;
    for (const [vk, variant] of Object.entries(cls.variants || {})) {
      if (vk !== 'default') continue;
      const gunCmd = (variant.commands || []).find(c => /modern_kinetic_gun|superbwarfare:.*gun/i.test(String(c)));
      const gun = gunCmd ? (String(gunCmd).match(/GunId:\s*"([^"]+)"/) || [])[1] : '(无枪)';
      const scope = gunCmd ? ((String(gunCmd).match(/AttachmentSCOPE:\s*\{[^}]*AttachmentId:\s*"([^"]+)"/) || [])[1] || '无') : '-';
      lines.push(`   ${ck.padEnd(34)} 枪=${String(gun).padEnd(18)} 镜=${String(scope).padEnd(14)} 描述=${variant.description || ''}`);
    }
  }
  if (lines.length) { console.log(`═══ ${f}`); console.log(lines.join('\n')); }
}
