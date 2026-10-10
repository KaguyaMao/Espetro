// Final verification: effective damage table for representative vehicles (before vs after)
import fs from 'node:fs';
import path from 'node:path';

const TMP = 'D:/minecraft/modp/Espetro/build/tmp';
const TDIR = path.join(TMP, 'sbw-tags');
const tagMap = new Map();
for (const file of fs.readdirSync(TDIR)) {
  const ns = file.split('__')[1];
  const name = file.replace(/^data__[^_]+__tags__damage_type__/, '').replace(/\.json$/, '');
  let json; try { json = JSON.parse(fs.readFileSync(path.join(TDIR, file), 'utf8')); } catch { continue; }
  tagMap.set(`${ns}:${name}`, (json.values ?? []).map(v => (typeof v === 'string' ? v : v?.id)).filter(Boolean).map(s => s.replace(/^#/, '#')));
}
const tagHas = (tag, dt, d = 0) => {
  if (d > 8) return false;
  for (const v of tagMap.get(tag) ?? []) {
    if (v.startsWith('#')) { if (tagHas(v.slice(1), dt, d + 1)) return true; }
    else if (v === dt) return true;
  }
  return false;
};
const RE = /^(?<prefix>(@#|#|@)?)(?<id>\w+(:\w+)?)\s*(?<operator>[-*+]?)\s*(?<value>([+-]?\d+(\.\d*)?)?)$/;
const parse = s => { const m = RE.exec(String(s).trim()); return m ? m.groups : null; };
const matches = (e, sc) => {
  if (!e) return false;
  if (e.prefix === '' && e.id === 'All') return true;
  if (e.prefix === '') return e.id === sc.dt;
  if (e.prefix === '#') return tagHas(e.id, sc.dt);
  if (e.prefix === '@') return e.id === sc.dir || (!sc.dir && e.id === sc.src);
  return false;
};
function calc(entries, sc, dmg) {
  let d = dmg;
  for (const raw of entries) {
    const e = parse(raw); if (!e || !matches(e, sc)) continue;
    if (e.operator === '*') d = d * parseFloat(e.value || '0');
    else if (e.operator === '-') d = Math.max(0, d - parseFloat(e.value || '0'));
    else if (e.operator === '+') d = Math.max(0, d + parseFloat(e.value || '0'));
    else if (e.value === '0') d = 0;
  }
  return d;
}
const CASES = [
  ['机炮AP直击(65)', { dt: 'superbwarfare:projectile_hit', dir: 'superbwarfare:small_cannon_shell' }, 65],
  ['主炮AP直击(200)', { dt: 'superbwarfare:projectile_hit', dir: 'superbwarfare:cannon_shell' }, 200],
  ['主炮AP直击(500)', { dt: 'superbwarfare:projectile_hit', dir: 'superbwarfare:cannon_shell' }, 500],
  ['机炮HE直击(5)', { dt: 'superbwarfare:projectile_hit', dir: 'superbwarfare:small_cannon_shell' }, 5],
  ['主炮HE直击(5)', { dt: 'superbwarfare:projectile_hit', dir: 'superbwarfare:cannon_shell' }, 5],
  ['机炮爆炸(20)', { dt: 'superbwarfare:custom_explosion', dir: 'superbwarfare:small_cannon_shell' }, 20],
  ['主炮爆炸(30)', { dt: 'superbwarfare:custom_explosion', dir: 'superbwarfare:cannon_shell' }, 30],
  ['主炮爆炸(120)', { dt: 'superbwarfare:custom_explosion', dir: 'superbwarfare:cannon_shell' }, 120],
  ['ATGM直击(650)', { dt: 'superbwarfare:projectile_hit', dir: 'superbwarfare:wire_guide_missile' }, 650],
  ['子弹(15)', { dt: 'superbwarfare:gunfire', dir: null }, 15],
  ['TNT(30)', { dt: 'minecraft:explosion', dir: 'minecraft:tnt' }, 30],
];
const show = (label, dirName) => {
  console.log(`\n===== ${label} =====`);
  console.log('case'.padEnd(18) + CASES.map(c => c[0].padEnd(16)).join(''));
  for (const f of ['dragonrise__bmp3.json', 'dragonrise__m1a2sepv2.json', 'fcp__t72av.json', 'dragonrise__mv3.json']) {
    const j = JSON.parse(fs.readFileSync(path.join(TMP, dirName, f), 'utf8'));
    const row = CASES.map(c => calc(j.DamageModifiers, c[1], c[2]).toFixed(2).padEnd(16)).join('');
    console.log(String(j.ID).padEnd(18) + row);
  }
};
show('修改前', 'srv-vehicles-fresh');
show('修改后', 'srv-vehicles-new');
