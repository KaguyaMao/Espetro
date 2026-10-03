import fs from 'node:fs';
import path from 'node:path';

// list-faction-weapons.mjs — 枚举编制里出现的所有武器（TaCZ 枪 / SBW 枪），并统计使用它的职业变体
const dir = process.argv[2];
const guns = new Map();   // gunId -> {kind, users:Set, attachments:Set}

for (const f of fs.readdirSync(dir).filter(x => x.endsWith('.json')).sort()) {
  const j = JSON.parse(fs.readFileSync(path.join(dir, f), 'utf8'));
  const faction = j.faction?.name ?? f;
  for (const [ck, cls] of Object.entries(j.classes || {})) {
    for (const [vk, va] of Object.entries(cls.variants || {})) {
      for (const cmd of (va.commands || [])) {
        const s = String(cmd);
        let id = null, kind = null, attach = '';
        const taczGun = s.match(/^tacz:modern_kinetic_gun\{(.*)\}\s*(?:\d+)?$/);
        if (taczGun) {
          id = (s.match(/GunId:"([^"]+)"/) || [])[1];
          kind = 'TaCZ';
          attach = [...s.matchAll(/Attachment([A-Z_]+):\{[^{}]*\{?[^}]*\}?[^{}]*AttachmentId:"([^"]+)"/g)]
            .map(m => `${m[1]}=${m[2]}`).join(',');
        } else if (/^superbwarfare:[a-z0-9_]+/.test(s) && !/grenade|medical|armor|helmet|chest|pants|boots|ammo|shell|missile|rocket/i.test(s)) {
          id = s.split(/[{\s]/)[0];
          kind = 'SBW';
        }
        if (!id) continue;
        if (!guns.has(id)) guns.set(id, { kind, users: new Set(), attach: new Set() });
        const g = guns.get(id);
        g.users.add(`${faction}/${ck}/${vk}`);
        if (attach) g.attach.add(attach);
      }
    }
  }
}

console.log(`共 ${guns.size} 种武器\n`);
for (const [id, g] of [...guns.entries()].sort((a, b) => a[0].localeCompare(b[0]))) {
  const users = [...g.users];
  console.log(`${id}   [${g.kind}]  用于 ${users.length} 个职业变体`);
  const attach = [...g.attach];
  if (attach.length) console.log(`    附件组合: ${attach.join(' | ')}`);
}
