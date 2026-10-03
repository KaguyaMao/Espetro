// verify-faction-fixes.mjs — 校验 fix-*.json
import fs from 'fs';
const FILES = [
  'pla_112th_brigade', 'pla_112th_brigade_mesh', 'pla_118th_brigade', 'pla_195th',
  'ru_205th', 'ru_3th', 'ru_49th', 'ru_6th_tank',
  'us_1th_ar', 'us_1th_ri', 'us_2nd_stryker', 'us_redone'
];
const problems = [];
const stat = { spyglass: 0, m60: 0, big: 0, merged: 0 };

for (const f of FILES) {
  const isUS = f.startsWith('us_');
  const isRU = f.startsWith('ru_');
  const before = JSON.parse(fs.readFileSync(`out-${f}.json`, 'utf8'));
  const after = JSON.parse(fs.readFileSync(`fix-${f}.json`, 'utf8'));

  // 非目标字段检查：faction/VehTypes/vehicles 必须逐字节一致
  const strip = (j) => {
    const c = JSON.parse(JSON.stringify(j));
    delete c.classes;
    return JSON.stringify(c);
  };
  if (strip(before) !== strip(after)) problems.push(`${f}: faction/vehicles 被改动`);

  const classIds = Object.keys(after.classes);
  let variants = 0, spy = 0;
  for (const [cid, cls] of Object.entries(after.classes)) {
    for (const [vk, v] of Object.entries(cls.variants || {})) {
      variants++;
      const n = (v.commands || []).filter(s => s.startsWith('minecraft:spyglass')).length;
      if (n === 1) spy++;
      else problems.push(`${f}/${cid}[${vk}]: 望远镜 ${n} 个（应为 1）`);
      // 通用：所有枪械串不得出现 "}" 紧跟字母/数字（漏逗号的典型症状）
      for (const s of (v.commands || [])) {
        if (s.startsWith('tacz:modern_kinetic_gun') && /[}][A-Za-z_]/.test(s)) {
          problems.push(`${f}/${cid}[${vk}]: SNBT 疑似漏逗号 -> ${s.slice(0, 120)}`);
        }
      }
      // ① M4A1 不能还有 BURST
      for (const s of (v.commands || [])) {
        if (isUS && s.includes('GunId:"tacz:m4a1"') && s.includes('GunFireMode:"BURST"')) {
          problems.push(`${f}/${cid}[${vk}]: M4A1 仍是 BURST`);
        }
        // ④ MMG 不能还有 m249
        if (isUS && /_MMG$/.test(cid) && s.includes('GunId:"tacz:m249"')) {
          problems.push(`${f}/${cid}[${vk}]: MMG 仍是 m249`);
        }
      }
    }

    // ② ASSAULT 必须消失；RIFLEMAN 必须 6 变体
    if (isUS && /_ASSAULT$/.test(cid)) problems.push(`${f}: ${cid} 未被删除`);
    if (isUS && /_RIFLEMAN$/.test(cid)) {
      const keys = Object.keys(cls.variants);
      const want = ['default', '倍镜', '机瞄', '握把', '握把红点', '握把倍镜'];
      if (JSON.stringify(keys) !== JSON.stringify(want)) {
        problems.push(`${f}/${cid}: 变体=[${keys.join(',')}] 期望=[${want.join(',')}]`);
      } else stat.merged++;
      if (cls.maxPlayers !== 100 || cls.max_per_squad !== -1) {
        problems.push(`${f}/${cid}: 限员变成 ${cls.maxPlayers}/${cls.max_per_squad}`);
      }
    }

    // ③ BIG_SQUADMG 必须存在且 row3；SQUADMG 只剩 default
    if (isUS && /_BIG_SQUADMG$/.test(cid)) {
      stat.big++;
      if (cls.row !== 3) problems.push(`${f}/${cid}: row=${cls.row}（应为 3）`);
      const keys = Object.keys(cls.variants);
      if (JSON.stringify(keys) !== JSON.stringify(['default', '光瞄'])) {
        problems.push(`${f}/${cid}: 变体=[${keys.join(',')}] 期望=[default,光瞄]`);
      }
      if (cls.name !== '班组机枪') problems.push(`${f}/${cid}: 名=${cls.name}`);
    }
    if (isUS && /_SQUADMG$/.test(cid) && !/_BIG_SQUADMG$/.test(cid)) {
      const keys = Object.keys(cls.variants);
      if (JSON.stringify(keys) !== JSON.stringify(['default'])) {
        problems.push(`${f}/${cid}: row2 变体=[${keys.join(',')}] 期望=[default]`);
      }
      if (cls.row !== 2) problems.push(`${f}/${cid}: row=${cls.row}（应为 2）`);
    }

    // ④ MMG 必须是 M60 + 100 发 308
    if (isUS && /_MMG$/.test(cid)) {
      for (const [vk, v] of Object.entries(cls.variants || {})) {
        const gun = (v.commands || []).find(s => s.startsWith('tacz:modern_kinetic_gun') && s.includes('classicr:m60'));
        if (!gun) { problems.push(`${f}/${cid}[${vk}]: 缺少 M60`); continue; }
        stat.m60++;
        for (const need of ['GunFireMode:"AUTO"', 'GunCurrentAmmoCount:100', 'AmmoId:"tacz:308"', 'MagazineFamily:"308_100"', 'MaxCapacity:100']) {
          if (!gun.includes(need)) problems.push(`${f}/${cid}[${vk}]: M60 缺 ${need}`);
        }
        const spare = (v.commands || []).filter(s => s.startsWith('taczmagazines:magazine') && s.includes('308_100'));
        if (spare.length !== 1 || !/\s6$/.test(spare[0])) {
          problems.push(`${f}/${cid}[${vk}]: 备用弹链异常 -> ${JSON.stringify(spare)}`);
        }
        const res = ((v.resupply && v.resupply.items) || []).filter(it => it.id.includes('308_100'));
        if (res.length !== 1 || res[0].max !== 8) {
          problems.push(`${f}/${cid}[${vk}]: 补给弹链异常 -> ${JSON.stringify(res)}`);
        }
      }
    }

    // ⑤ 俄军：轻筒 default 补给必须有筒；重筒必须有 og7he 弹
    if (isRU && /_ANTITANK$/.test(cid)) {
      for (const [vk, v] of Object.entries(cls.variants || {})) {
        const hasGun = (v.commands || []).some(s => s.includes('GunId:"murasamet:rpg7_pg7heat"'));
        if (!hasGun) continue;   // 倍镜(AT4) 变体不适用
        const res = ((v.resupply && v.resupply.items) || []).some(it => it.id.includes('GunId:"murasamet:rpg7_pg7heat"'));
        if (!res) problems.push(`${f}/${cid}[${vk}]: 补给缺 rpg7_pg7heat 筒`);
      }
    }
    if (isRU && /_HEAVYANTITANK$/.test(cid)) {
      for (const [vk, v] of Object.entries(cls.variants || {})) {
        const res = ((v.resupply && v.resupply.items) || []).some(it => it.id.includes('AmmoId:"murasamet:og7he"'));
        if (!res) problems.push(`${f}/${cid}[${vk}]: 补给缺 og7he 弹`);
      }
    }

    // ⑥ 工兵必须有 C4 + 地雷补给
    if (/_ENGINEER$/.test(cid)) {
      for (const [vk, v] of Object.entries(cls.variants || {})) {
        const items = (v.resupply && v.resupply.items) || [];
        const c4 = items.find(it => it.id === 'superbwarfare:c4_bomb');
        const mine = items.find(it => it.id === 'superbwarfare:tm_62');
        if (!c4 || c4.count !== 1 || c4.max !== 1) problems.push(`${f}/${cid}[${vk}]: C4 补给异常 ${JSON.stringify(c4)}`);
        if (!mine || mine.count !== 1 || mine.max !== 3) problems.push(`${f}/${cid}[${vk}]: 地雷补给异常 ${JSON.stringify(mine)}`);
      }
    }
  }
  stat.spyglass += spy;
  console.log(`${f.padEnd(24)} 职业=${String(classIds.length).padStart(3)} 变体=${String(variants).padStart(3)} 望远镜覆盖=${spy}`);
}

console.log('\n=== 统计 ===');
console.log(`望远镜覆盖变体 ${stat.spyglass}；步枪兵合并 ${stat.merged}/4；新增 BIG_SQUADMG ${stat.big}/4；M60 ${stat.m60}/8`);
console.log('');
if (problems.length === 0) console.log('=== 全部检查通过 ===');
else { console.log(`=== 问题 ${problems.length} 项 ===`); problems.forEach(p => console.log('   X ' + p)); }

console.log('\n=== 抽查：us_1th_ar 的步枪兵 / 班组机枪 / 通用机枪 ===');
const u = JSON.parse(fs.readFileSync('fix-us_1th_ar.json', 'utf8'));
for (const cid of ['US_1th_arma_RIFLEMAN', 'US_1th_arma_SQUADMG', 'US_1th_arma_BIG_SQUADMG', 'US_1th_arma_MMG']) {
  const cls = u.classes[cid];
  if (!cls) { console.log(`(缺 ${cid})`); continue; }
  console.log(`--- ${cid} 名=${cls.name} row=${cls.row} max=${cls.maxPlayers}/squad=${cls.max_per_squad} 变体=${Object.keys(cls.variants).join(',')}`);
  for (const [vk, v] of Object.entries(cls.variants)) {
    const gun = (v.commands || []).find(s => s.startsWith('tacz:modern_kinetic_gun'));
    console.log(`   [${vk}] ${v.name} 枪=${gun ? (gun.match(/GunId:"([^"]+)"/) || [])[1] : '-'} 模式=${gun ? (gun.match(/GunFireMode:"([^"]+)"/) || [])[1] : '-'} 尾项=${(v.commands || []).slice(-1)[0]}`);
  }
}
console.log('\n=== 抽查：ru_205th 轻筒/重筒/工兵补给 ===');
const r = JSON.parse(fs.readFileSync('fix-ru_205th.json', 'utf8'));
for (const cid of ['RU_205th_ANTITANK', 'RU_205th_HEAVYANTITANK', 'RU_205th_ENGINEER']) {
  for (const [vk, v] of Object.entries(r.classes[cid].variants)) {
    console.log(`--- ${cid}[${vk}]`);
    for (const it of ((v.resupply && v.resupply.items) || [])) console.log(`     ${it.id.split('{')[0]}${it.id.includes('GunId') ? '(' + (it.id.match(/GunId:"([^"]+)"/) || [])[1] + ')' : ''}${it.id.includes('AmmoId') ? '(' + (it.id.match(/AmmoId:"([^"]+)"/) || [])[1] + ')' : ''} x${it.count}/max${it.max}`);
  }
}
