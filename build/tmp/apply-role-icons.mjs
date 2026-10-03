// apply-role-icons.mjs — 把 Downloads\HUD\Roles 新图标按 slug 命名写入模组 roles 目录
// 用法: node apply-role-icons.mjs [--write]
import fs from 'fs';
import crypto from 'crypto';

const SRC = 'C:/Users/Administrator/Downloads/HUD/Roles/';
const DST = 'D:/minecraft/modp/Espetro/src/main/resources/assets/espetro/textures/gui/roles/';
const WRITE = process.argv.includes('--write');

// 目标 slug -> 源文件（不含扩展名）
const MAP = {
  // 配置正在引用、直接换图
  rifleman: 'T_role_rifleman',
  automatic_rifleman: 'T_role_automaticrifleman',
  machine_gunner: 'T_role_machinegunner',
  grenadier: 'T_role_grenadier',
  light_at: 'T_role_lightantitank',
  heavy_at: 'T_role_heavyantitank',
  marksman: 'T_role_designatedmarksman',
  medic: 'T_role_medic',
  raider: 'T_role_raider',
  crewman: 'T_role_crewman',
  lead_crewman: 'T_role_crewman_squadleader',
  leader: 'T_role_squadleader',
  engineer: 'T_role_engineer',        // 战斗工兵改用它（配置 icon: sapper -> engineer）
  scout: 'T_role_scout',              // 侦察兵改用它（配置 icon -> scout）
  // 现存 slug，换图（当前无职业引用，备用）
  sniper: 'T_role_sniper',
  pilot: 'T_role_pilot',
  lead_pilot: 'T_role_pilot_squadleader',
  // 新增备用 slug
  sapper: 'T_role_sapper',
  breacher: 'T_role_breacher',
  rifleman_scoped: 'T_role_rifleman_scoped',
  unarmed: 'T_role_unarmed',
  dead: 'T_role_dead',
  rank_basicprivate: 'T_rank_basicprivate',
  rank_fireteamleader: 'T_rank_fireteamleader',
  rank_squadleader: 'T_rank_squadleader',
  incap_medic: 'Incap_Medic',
  incap_squadleader: 'Incap_squadleader',
  medic_alt: 'T_medic'
};

const sha = (b) => crypto.createHash('sha1').update(b).digest('hex').slice(0, 8);
const before = new Set(fs.readdirSync(DST).filter((f) => f.endsWith('.png')));
const lines = [];
let copied = 0;

for (const [slug, srcName] of Object.entries(MAP)) {
  const src = SRC + srcName + '.png';
  if (!fs.existsSync(src)) { lines.push(`缺失源文件 ${srcName}.png`); continue; }
  const buf = fs.readFileSync(src);
  const target = DST + slug + '.png';
  const existed = fs.existsSync(target);
  const oldLen = existed ? fs.statSync(target).size : 0;
  if (WRITE) fs.writeFileSync(target, buf);
  copied++;
  lines.push(`${existed ? '替换' : '新增'} ${slug}.png  ← ${srcName}.png  ${oldLen}B → ${buf.length}B  sha=${sha(buf)}`);
}

const after = fs.readdirSync(DST).filter((f) => f.endsWith('.png')).sort();
const untouched = after.filter((f) => before.has(f) && !Object.keys(MAP).includes(f.replace(/\.png$/, '')));

console.log(lines.join('\n'));
console.log(`\n共处理 ${copied} 个 → ${WRITE ? '已写入' : '（演练，未写盘）'} ${DST}`);
console.log(`目录现有 ${after.length} 个 PNG`);
console.log(`未改动的旧文件: ${untouched.join(', ') || '无'}`);
const missing = Object.keys(MAP).filter((s) => !after.includes(s + '.png'));
console.log(`目标缺失: ${missing.join(', ') || '无'}`);
