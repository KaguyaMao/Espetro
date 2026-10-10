// apply-faction-fixes.mjs — 7 项编制修复（对 out-*.json 应用，输出 fix-*.json）
//  ① 美军 M4A1 快慢机 BURST -> AUTO
//  ② 美军步枪兵合并：ASSAULT 的 握把/握把红点/握把倍镜 并入 RIFLEMAN，删除 ASSAULT 职业
//  ③ 美军班组机枪拆两行：row2 只留机瞄；新增 row3 BIG_SQUADMG（机瞄 + 光瞄）
//  ④ 美军通用机枪 M249 -> classicr:m60（100 发 tacz:308 弹链，备用 6，补给 max8）
//  ⑤ 俄军轻筒补给 +1 把 rpg7_pg7heat；重筒补给 +1 发 og7he 弹
//  ⑥ 所有编制工兵补给 +C4(1/1) +地雷(1/3)
//  ⑦ 所有职业所有变体末尾 +minecraft:spyglass 1
import fs from 'fs';

const FILES = [
  'pla_112th_brigade', 'pla_112th_brigade_mesh', 'pla_118th_brigade', 'pla_195th',
  'ru_205th', 'ru_3th', 'ru_49th', 'ru_6th_tank',
  'us_1th_ar', 'us_1th_ri', 'us_2nd_stryker', 'us_redone'
];

const M60_GUN = 'tacz:modern_kinetic_gun{GunCurrentAmmoCount:100,GunFireMode:"AUTO",'
  + 'GunId:"classicr:m60",HasBulletInBarrel:1b,TaCZMag_StoredMagazine:{Count:1b,'
  + 'id:"taczmagazines:magazine",tag:{AmmoCount:100,AmmoId:"tacz:308",'
  + 'MagazineFamily:"308_100",MaxCapacity:100}}}';
const M60_MAG = 'taczmagazines:magazine{AmmoCount:100,AmmoId:"tacz:308",'
  + 'MagazineFamily:"308_100",MaxCapacity:100}';
const SPYGLASS = 'minecraft:spyglass 1';

const report = {
  m4a1: 0, riflemanMerged: [], assaultDeleted: [], bigSquadMgAdded: [],
  m60Guns: 0, m60Mags: 0, m60Resupply: 0,
  ruAtLauncher: 0, ruHeavyAmmo: 0,
  engineerC4: 0, engineerMine: 0,
  spyglass: 0,
  warnings: []
};

function clone(o) { return JSON.parse(JSON.stringify(o)); }

for (const f of FILES) {
  const isUS = f.startsWith('us_');
  const isRU = f.startsWith('ru_');
  const j = JSON.parse(fs.readFileSync(`out-${f}.json`, 'utf8'));
  const classes = j.classes;

  for (const [cid, cls] of Object.entries(classes)) {
    // ---------- ① M4A1 全自动 ----------
    if (isUS) {
      for (const v of Object.values(cls.variants || {})) {
        v.commands = (v.commands || []).map(s => {
          if (s.startsWith('tacz:modern_kinetic_gun') && s.includes('GunId:"tacz:m4a1"')
            && s.includes('GunFireMode:"BURST"')) {
            report.m4a1++;
            return s.replace('GunFireMode:"BURST"', 'GunFireMode:"AUTO"');
          }
          return s;
        });
      }
    }

    // ---------- ④ 美军通用机枪 -> M60 ----------
    if (isUS && /_MMG$/.test(cid)) {
      for (const [vk, v] of Object.entries(cls.variants || {})) {
        let spareDone = false, resDone = false;
        v.commands = (v.commands || []).map(s => {
          if (s.startsWith('tacz:modern_kinetic_gun') && s.includes('GunId:"tacz:m249"')) {
            // 保留原有瞄具配件（若有）
            const scope = s.match(/AttachmentSCOPE:\{Count:1b,id:"tacz:attachment",tag:\{AttachmentId:"[^"]+"\}\}/);
            const built = scope
              ? M60_GUN.replace('tacz:modern_kinetic_gun{', 'tacz:modern_kinetic_gun{' + scope[0] + ',')
              : M60_GUN;
            report.m60Guns++;
            return built;
          }
          if (!spareDone && s.startsWith('taczmagazines:magazine') && s.includes('tacz:556x45')) {
            spareDone = true;
            report.m60Mags++;
            return M60_MAG + ' 6';
          }
          return s;
        });
        if (!spareDone) report.warnings.push(`${f}/${cid}[${vk}] 未找到 5.56 备用弹匣`);
        const items = (v.resupply && v.resupply.items) || [];
        v.resupply.items = items.map(it => {
          if (!resDone && it.id.includes('tacz:556x45')) {
            resDone = true;
            report.m60Resupply++;
            return { ...it, id: M60_MAG, count: 1, max: 8 };
          }
          return it;
        });
        if (!resDone) report.warnings.push(`${f}/${cid}[${vk}] 未找到 5.56 补给项`);
      }
    }

    // ---------- ⑤ 俄军轻筒/重筒补给 ----------
    if (isRU && /_ANTITANK$/.test(cid)) {
      for (const v of Object.values(cls.variants || {})) {
        const items = (v.resupply && v.resupply.items) || [];
        const hasLauncher = items.some(it => it.id.includes('GunId:"murasamet:rpg7_pg7heat"'));
        if (!hasLauncher) {
          const str = (v.commands || []).find(s => s.includes('GunId:"murasamet:rpg7_pg7heat"'));
          if (str) {
            items.push({ id: str, count: 1, max: 1 });
            report.ruAtLauncher++;
          } else report.warnings.push(`${f}/${cid} 找不到 rpg7_pg7heat 的给予串`);
        }
        v.resupply.items = items;
      }
    }
    if (isRU && /_HEAVYANTITANK$/.test(cid)) {
      for (const v of Object.values(cls.variants || {})) {
        const items = (v.resupply && v.resupply.items) || [];
        if (!items.some(it => it.id.includes('AmmoId:"murasamet:og7he"'))) {
          items.push({ id: 'tacz:ammo{AmmoId:"murasamet:og7he"}', count: 1, max: 1 });
          report.ruHeavyAmmo++;
        }
        v.resupply.items = items;
      }
    }

    // ---------- ⑥ 工兵 C4 + 地雷补给 ----------
    if (/_ENGINEER$/.test(cid)) {
      for (const [vk, v] of Object.entries(cls.variants || {})) {
        if (!v.resupply || !Array.isArray(v.resupply.items)) {
          report.warnings.push(`${f}/${cid}[${vk}] 没有 resupply，跳过 ⑥`);
          continue;
        }
        const items = v.resupply.items;
        if (!items.some(it => it.id.startsWith('superbwarfare:c4_bomb'))) {
          items.push({ id: 'superbwarfare:c4_bomb', count: 1, max: 1 });
          report.engineerC4++;
        }
        if (!items.some(it => it.id.startsWith('superbwarfare:tm_62'))) {
          items.push({ id: 'superbwarfare:tm_62', count: 1, max: 3 });
          report.engineerMine++;
        }
      }
    }

    // ---------- ⑦ 望远镜（所有职业所有变体，末尾追加） ----------
    for (const v of Object.values(cls.variants || {})) {
      if (!Array.isArray(v.commands)) v.commands = [];
      if (!v.commands.some(s => s.startsWith('minecraft:spyglass'))) {
        v.commands.push(SPYGLASS);
        report.spyglass++;
      }
    }
  }

  // ---------- ② 步枪兵合并（美军） ----------
  if (isUS) {
    for (const [cid, cls] of Object.entries(classes)) {
      if (!/_ASSAULT$/.test(cid)) continue;
      const rifleId = cid.replace(/_ASSAULT$/, '_RIFLEMAN');
      const rifle = classes[rifleId];
      if (!rifle) { report.warnings.push(`${f}: 找不到 ${rifleId}`); continue; }
      const keep = ['握把', '握把红点', '握把倍镜'];
      for (const k of keep) {
        const src = cls.variants[k];
        if (!src) { report.warnings.push(`${f}/${cid}: 缺变体 ${k}`); continue; }
        if (rifle.variants[k]) { report.warnings.push(`${f}/${rifleId}: 变体键冲突 ${k}`); continue; }
        rifle.variants[k] = src;
      }
      delete classes[cid];
      report.assaultDeleted.push(`${f}/${cid}`);
      report.riflemanMerged.push(`${f}/${rifleId} 变体=[${Object.keys(rifle.variants).join(',')}]`);
    }
  }

  // ---------- ③ 班组机枪拆两行（美军） ----------
  if (isUS) {
    const rebuilt = {};
    for (const [cid, cls] of Object.entries(classes)) {
      rebuilt[cid] = cls;
      if (!/_SQUADMG$/.test(cid)) continue;
      const newId = cid.replace(/_SQUADMG$/, '_BIG_SQUADMG');
      // 先克隆（此时含 default(机瞄) + 光瞄），再把 row2 裁成只剩机瞄
      const big = clone(cls);
      big.row = 3;
      big.unlock_min_squad = 6;
      delete cls.variants['光瞄'];
      // 加载器规则：strict_count=true 时 Σ变体上限 必须 == 职业上限
      if (cls.strict_count) {
        for (const v of Object.values(cls.variants)) v.maxPlayers = cls.maxPlayers;
      }
      rebuilt[newId] = big;
      report.bigSquadMgAdded.push(
        `${f}/${newId} row3 变体=[${Object.keys(big.variants).join(',')}] ; ${cid} row2 变体=[${Object.keys(cls.variants).join(',')}]`);
    }
    j.classes = rebuilt;
  }

  const out = JSON.stringify(j, null, 2) + '\n';
  JSON.parse(out);
  fs.writeFileSync(`fix-${f}.json`, out, 'utf8');
}

console.log('=== 应用结果 ===');
console.log('① M4A1 BURST->AUTO 处数: ' + report.m4a1);
console.log('② 合并的步枪兵: ' + report.riflemanMerged.length);
report.riflemanMerged.forEach(x => console.log('   ' + x));
console.log('   删除的职业: ' + report.assaultDeleted.join(', '));
console.log('③ 新增班组机枪(row3): ' + report.bigSquadMgAdded.length);
report.bigSquadMgAdded.forEach(x => console.log('   ' + x));
console.log(`④ M60: 枪 ${report.m60Guns} 把 / 备用弹链 ${report.m60Mags} 组 / 补给 ${report.m60Resupply} 处`);
console.log(`⑤ 俄军轻筒补筒 ${report.ruAtLauncher} 处 / 重筒补弹 ${report.ruHeavyAmmo} 处`);
console.log(`⑥ 工兵补给: C4 ${report.engineerC4} 处 / 地雷 ${report.engineerMine} 处`);
console.log(`⑦ 望远镜: ${report.spyglass} 个变体`);
if (report.warnings.length) {
  console.log('=== 警告 ===');
  report.warnings.forEach(x => console.log('   ! ' + x));
}
fs.writeFileSync('fix-report.json', JSON.stringify(report, null, 1), 'utf8');
