// mg-audit.mjs — 列出各编制中 机枪/步兵 类职业实际装备的枪械（GunId）
import fs from 'fs';

const CFG = 'D:/minecraft/modp/Espetro/build/tmp/fxall/';
const RE_GUNID = /GunId\s*:\s*"([^"]+)"/g;
const KEYS = /MG|RIFLEMAN|ASSAULT|MARKSMAN|SCOUT|ENGINEER|RAIDER|ANTITANK/i;

for (const f of fs.readdirSync(CFG).filter((x) => x.endsWith('.json')).sort()) {
  const j = JSON.parse(fs.readFileSync(CFG + f, 'utf8'));
  console.log(`\n### ${f}`);
  for (const [cid, cls] of Object.entries(j.classes || {})) {
    if (!KEYS.test(cid)) continue;
    const per = [];
    for (const [vid, v] of Object.entries(cls.variants || {})) {
      const text = (v.commands || []).join('\n');
      const guns = new Set();
      let m; RE_GUNID.lastIndex = 0;
      while ((m = RE_GUNID.exec(text)) !== null) guns.add(m[1]);
      per.push(`${vid}=[${[...guns].join('+')}]`);
    }
    console.log(`${cid.padEnd(28)} icon=${String(cls.icon).padEnd(20)} name=${String(cls.name).padEnd(8)} ${per.join(' ')}`);
  }
}
