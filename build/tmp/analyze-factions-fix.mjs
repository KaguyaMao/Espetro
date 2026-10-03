// analyze-factions-fix.mjs — 为 7 项编制修复收集数据
import fs from 'fs';

const FILES = [
  'pla_112th_brigade', 'pla_112th_brigade_mesh', 'pla_118th_brigade', 'pla_195th',
  'ru_205th', 'ru_3th', 'ru_49th', 'ru_6th_tank',
  'us_1th_ar', 'us_1th_ri', 'us_2nd_stryker', 'us_redone'
];

const load = (f) => JSON.parse(fs.readFileSync(`out-${f}.json`, 'utf8'));

function variantsOf(cls) {
  return Object.entries(cls.variants || {});
}

function gunOf(cmds) {
  const s = (cmds || []).find(x => x.startsWith('tacz:modern_kinetic_gun'));
  if (!s) return null;
  const get = (re) => { const m = s.match(re); return m ? m[1] : null; };
  const atts = [...s.matchAll(/Attachment(SCOPE|MUZZLE|GRIP|STOCK|LASER):\{Count:(\d+)b,id:"tacz:attachment",tag:\{AttachmentId:"([^"]+)"\}\}/g)]
    .filter(m => m[2] !== '0')
    .map(m => m[1] + '=' + m[3]);
  return {
    raw: s,
    gunId: get(/GunId:"([^"]+)"/),
    mode: get(/GunFireMode:"([^"]+)"/),
    ammo: get(/GunCurrentAmmoCount:(\d+)/),
    magId: get(/TaCZMag_StoredMagazine:\{Count:1b,id:"([^"]+)"/),
    magAmmo: get(/TaCZMag_StoredMagazine:.*?AmmoCount:(\d+)/),
    magFamily: get(/TaCZMag_StoredMagazine:.*?MagazineFamily:"([^"]+)"/),
    magCap: get(/TaCZMag_StoredMagazine:.*?MaxCapacity:(\d+)/),
    atts
  };
}

console.log('########## 1) M4A1 快慢机状态（美军） ##########');
for (const f of FILES) {
  if (!f.startsWith('us_')) continue;
  const j = load(f);
  const tally = new Map();
  for (const [cid, cls] of Object.entries(j.classes)) {
    for (const [vk, v] of variantsOf(cls)) {
      const g = gunOf(v.commands);
      if (!g || g.gunId !== 'tacz:m4a1') continue;
      const key = g.mode;
      tally.set(key, (tally.get(key) || 0) + 1);
      if (g.mode !== 'AUTO') console.log(`   ${f} / ${cid} / ${vk} -> ${g.mode}`);
    }
  }
  console.log(`   ${f}: ${[...tally.entries()].map(([k, v]) => k + '=' + v).join(', ')}`);
}

console.log('\n########## 2) 美军"步枪兵"相关（RIFLEMAN / ASSAULT）##########');
for (const f of FILES) {
  if (!f.startsWith('us_')) continue;
  const j = load(f);
  for (const [cid, cls] of Object.entries(j.classes)) {
    if (!/(RIFLEMAN|ASSAULT)/.test(cid)) continue;
    console.log(`--- ${f} / ${cid} 名=${cls.name} row=${cls.row} max=${cls.maxPlayers} /squad=${cls.max_per_squad} teamCount=${cls.team_count} unlockPerN=${cls.unlock_per_n} unlockMinSquad=${cls.unlock_min_squad}`);
    for (const [vk, v] of variantsOf(cls)) {
      const g = gunOf(v.commands);
      console.log(`     [${vk}] ${v.name} | 枪=${g ? g.gunId : '-'} 模式=${g ? g.mode : '-'} 弹=${g ? g.ammo : '-'} 匣=${g ? g.magAmmo + '/' + g.magCap : '-'} | ${g ? g.atts.join(' ') : ''}`);
    }
  }
}

console.log('\n########## 3) 班组机枪 / 通用机枪 / 机枪兵 对比（三国）##########');
for (const f of FILES) {
  const j = load(f);
  for (const [cid, cls] of Object.entries(j.classes)) {
    if (!/(SQUADMG|_MG|MMG)/.test(cid)) continue;
    const bits = [];
    for (const [vk, v] of variantsOf(cls)) {
      const g = gunOf(v.commands);
      bits.push(`[${vk}]${v.name}:${g ? g.gunId + '/' + g.mode : '-'}`);
    }
    console.log(`  ${f.padEnd(24)} ${cid.padEnd(26)} 名=${(cls.name || '').padEnd(8)} row=${String(cls.row).padEnd(2)} max=${cls.maxPlayers}/squad=${cls.max_per_squad}  ${bits.join('  ')}`);
  }
}

console.log('\n########## 4) 工兵 / 轻筒 / 重筒：给予物品里的爆炸物 + 补给表 ##########');
const EXPLOSIVE = /(c4|mine|tm_62|claymore|blu_43|lunge|rpg|at4|m72|pf_?89|launcher|rocket|m136|smaw|law)/i;
for (const f of FILES) {
  const j = load(f);
  for (const [cid, cls] of Object.entries(j.classes)) {
    if (!/(ENGINEER|ANTITANK|HEAVYANTITANK)/.test(cid)) continue;
    console.log(`--- ${f} / ${cid} 名=${cls.name}`);
    for (const [vk, v] of variantsOf(cls)) {
      const mine = (v.commands || []).filter(s => EXPLOSIVE.test(s.split('{')[0].split(/\s+/)[0]));
      const res = ((v.resupply && v.resupply.items) || []).map(i => i.id.split('{')[0] + '×' + i.count + '/max' + i.max);
      console.log(`     [${vk}] 给予里的爆炸物: ${mine.length ? mine.map(s => s.split('{')[0] + (s.includes(' ') ? ' ' + s.split(/\s+/).pop() : '')).join(' , ') : '(无)'}`);
      console.log(`           补给表(${res.length}): ${res.length ? res.join(' , ') : '(空)'}`);
    }
  }
}

console.log('\n########## 5) 望远镜现状 & 规模统计 ##########');
for (const f of FILES) {
  const j = load(f);
  let classes = 0, variants = 0, withSpy = 0;
  for (const [, cls] of Object.entries(j.classes)) {
    classes++;
    for (const [, v] of variantsOf(cls)) {
      variants++;
      if ((v.commands || []).some(s => s.startsWith('minecraft:spyglass'))) withSpy++;
    }
  }
  console.log(`  ${f.padEnd(24)} 职业=${String(classes).padStart(3)} 变体=${String(variants).padStart(3)} 已有望远镜变体=${withSpy}`);
}
