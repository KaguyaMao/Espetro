// gen-vehicle-table.mjs — 载具介绍表（武器/备弹/伤害/射速/编制）
import fs from 'fs';

const VDIR = 'D:/minecraft/modp/Espetro/build/tmp/server-vehicles-fixed/';
const FDIR = 'C:/Users/Administrator/Desktop/编制文件/';
const SUPPLY = 'D:/minecraft/modp/Espetro/build/tmp/supply-default.json';

// 弹药友好名
const AMMO_NAME = {
	'superbwarfare:large_shell_ap': '大口径穿甲弹',
	'superbwarfare:large_shell_he': '大口径高爆/破甲弹',
	'superbwarfare:small_shell_ap': '小口径穿甲弹(机炮)',
	'superbwarfare:small_shell_he': '小口径高爆弹',
	'superbwarfare:medium_anti_ground_missile': '线控反坦克导弹',
	'superbwarfare:heavy_ammo': '重型弹药(重机枪)',
	'superbwarfare:rifle_ammo': '步枪弹',
	'superbwarfare:grenade_40mm': '40mm榴弹',
	'superbwarfare:large_anti_ground_missile': '大型反坦克导弹',
	'superbwarfare:small_cannon_shell': '小口径炮弹'
};
function ammoName(id) {
	return AMMO_NAME[id] ?? String(id ?? '').split(':').pop();
}
// 武器友好名
const WN = {
	Cannon: '主炮/机炮', MachineGun: '同轴机枪', MainMachineGun: '主用机枪',
	PassengerMachineGun: '车顶机枪', Coax: '同轴机枪', '100MM_Cannon': '100mm炮',
	Konkurs: 'Konkurs导弹', Missile: '车载导弹', TOW: 'TOW导弹', Rocket: '火箭弹',
	'@Missile': '反坦克导弹', SeekMissile: '制导导弹', DriverAAMissile: '防空导弹',
	Grenade: '榴弹发射器', Crows: 'CROWS武器站', Mortar: '迫击炮', AutoCannon: '机炮',
	Empty: '(空)', PassengerMissile: '乘客导弹'
};
function wn(k) { return WN[k] ?? k; }

// 载具显示名（从数据 ID 反查——用文件 ID 字段）
const vfiles = fs.readdirSync(VDIR).filter((f) => f.endsWith('.json')).sort();

// 解析 supply（剥离注释）
function stripComments(c) {
	let out = ''; let i = 0; let inStr = false;
	while (i < c.length) {
		const ch = c[i];
		if (inStr) { out += ch; if (ch === '\\') { out += c[i+1] ?? ''; i += 2; continue; } if (ch === '"') inStr = false; i++; continue; }
		if (ch === '"') { inStr = true; out += ch; i++; continue; }
		if (ch === '/' && c[i+1] === '*') { const end = c.indexOf('*/', i + 2); if (end === -1) break; i = end + 2; continue; }
		if (ch === '/' && c[i+1] === '/') { const end = c.indexOf('\n', i + 2); i = end === -1 ? c.length : end; continue; }
		out += ch; i++;
	}
	return out;
}
const supply = JSON.parse(stripComments(fs.readFileSync(SUPPLY, 'utf8')));

// 编制 → 实体映射
const factionVehicles = new Map(); // entityId -> Set(编制名)
for (const f of fs.readdirSync(FDIR).filter((x) => x.endsWith('.json'))) {
	const j = JSON.parse(fs.readFileSync(FDIR + f, 'utf8'));
	const fname = j.faction?.name ?? f;
	for (const v of Object.values(j.vehicles ?? {})) {
		const ents = Array.isArray(v.entity) ? v.entity : [v.entity];
		for (const e of ents) {
			if (!e) continue;
			if (!factionVehicles.has(e)) factionVehicles.set(e, new Set());
			factionVehicles.get(e).add(fname);
		}
	}
}

let out = '# 载具介绍总表\n\n';
for (const f of vfiles) {
	const j = JSON.parse(fs.readFileSync(VDIR + f, 'utf8'));
	const vid = j.ID ?? f.replace('.json', '');
	const factions = factionVehicles.get(vid);
	const ws = j.Weapons ?? {};
	out += `\n## ${vid}（${j.Weapons ? '' : ''}${vid.split(':').pop()}）\n`;
	// 武器表
	out += '| 武器 | 弹药 | 伤害 | 爆炸伤/范围 | 射速 |\n|---|---|---|---|---|\n';
	for (const [wk, w] of Object.entries(ws)) {
		const ats = Array.isArray(w.AmmoType) ? w.AmmoType : [];
		const rpm = w.RPM;
		const rate = rpm ? rpm + ' RPM' : (w.EmptyReloadTime ? '装填' + w.EmptyReloadTime + 't' : (w.ShootAnimationTime ? w.ShootAnimationTime + 't/发' : '-'));
		if (ats.length === 0) {
			const expl = w.ExplosionDamage ? `${w.ExplosionDamage}/${w.ExplosionRadius ?? '-'}` : '-';
			out += `| ${wn(wk)} | ${ammoName(w.Projectile)} | ${w.Damage ?? '-'} | ${expl} | ${rate} |\n`;
		} else {
			for (let i = 0; i < ats.length; i++) {
				const at = ats[i];
				let d, ed, er, an;
				if (typeof at === 'string') { d = w.Damage; ed = w.ExplosionDamage; er = w.ExplosionRadius; an = ammoName(at); }
				else if (at?.Override) { d = at.Override.Damage; ed = at.Override.ExplosionDamage; er = at.Override.ExplosionRadius; an = ammoName(at.Ammo); }
				else continue;
				const expl = ed ? `${ed}/${er ?? '-'}` : '-';
				out += `| ${wn(wk)}${i > 0 ? `#${i + 1}` : ''} | ${an} | ${d ?? '-'} | ${expl} | ${rate} |\n`;
			}
		}
	}
	if (Object.keys(ws).length === 0) out += '| (无武器) | - | - | - | - |\n';
	// 补给备弹
	const supp = supply.VehicleOverrides?.[vid];
	if (supp?.AmmoOverrides) {
		const parts = Object.entries(supp.AmmoOverrides).map(([k, v]) => `${ammoName(k)} ${v.FixedAmount ?? v.Mode ?? '?'}`);
		out += `**补给备弹**：${parts.join('、')}${supp.HealPercent ? `（回血${supp.HealPercent}%）` : ''}\n`;
	} else {
		out += '**补给备弹**：默认（MAGAZINE 弹匣制）\n';
	}
	// 编制
	if (factions && factions.size > 0) {
		out += `**编制**：${[...factions].join('、')}\n`;
	} else {
		out += '**编制**：未配置\n';
	}
}

fs.writeFileSync('D:/minecraft/modp/Espetro/build/tmp/vehicle-intro-table.md', out, 'utf8');
console.log('生成完成，长度 ' + out.length);
