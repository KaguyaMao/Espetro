// Compare save-datapack vehicle copies vs server copies (semantic)
import fs from 'node:fs';
import path from 'node:path';

const SAVE = 'D:/minecraft/squadMC预发布测试/versions/Squad预发布测试/saves/新的世界/datapacks/dragonrise_reforge/data/dragonrise_reforge/sbw/vehicles';
const SRV = 'D:/minecraft/modp/Espetro/build/tmp/srv-vehicles-fresh';

for (const name of fs.readdirSync(SAVE).filter(f => f.endsWith('.json'))) {
  const srvFile = path.join(SRV, 'dragonrise__' + name);
  if (!fs.existsSync(srvFile)) continue;
  let a, b;
  try { a = JSON.parse(fs.readFileSync(path.join(SAVE, name), 'utf8')); b = JSON.parse(fs.readFileSync(srvFile, 'utf8')); } catch { console.log(`${name}: parse fail`); continue; }
  const keysA = Object.keys(a), keysB = Object.keys(b);
  const onlyA = keysA.filter(k => !keysB.includes(k));
  const onlyB = keysB.filter(k => !keysA.includes(k));
  const modA = JSON.stringify(a.DamageModifiers ?? []), modB = JSON.stringify(b.DamageModifiers ?? []);
  const diffs = [];
  if (onlyA.length) diffs.push('存档独有字段: ' + onlyA.join(','));
  if (onlyB.length) diffs.push('服务器独有字段: ' + onlyB.join(','));
  if (a.MaxHealth !== b.MaxHealth) diffs.push(`MaxHealth ${a.MaxHealth} vs ${b.MaxHealth}`);
  if (modA !== modB) diffs.push(`DamageModifiers 不同(存档${(a.DamageModifiers ?? []).length}/服务器${(b.DamageModifiers ?? []).length})`);
  // HE damage compare
  const heOf = j => {
    const out = [];
    for (const [wn, w] of Object.entries(j.Weapons ?? {})) {
      if (!w || typeof w !== 'object') continue;
      const ammos = Array.isArray(w.AmmoType) ? w.AmmoType : (w.AmmoType ? [w.AmmoType] : []);
      for (const x of ammos) {
        if (typeof x === 'string') { if (/_he/.test(x)) out.push(`${wn}/${x}=${w.Damage}`); }
        else if (/_he/.test(x.Ammo ?? '')) out.push(`${wn}/${x.Ammo}=${x.Override?.Damage ?? w.Damage}`);
      }
    }
    return out.join(' ');
  };
  const heA = heOf(a), heB = heOf(b);
  if (heA !== heB) diffs.push(`HE伤害 存档[${heA}] vs 服务器[${heB}]`);
  console.log(`${name}: ${diffs.length ? diffs.join(' | ') : '一致'}`);
}
