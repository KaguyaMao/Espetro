// modify-damage.mjs — 按用户规则修改载具武器数值与抗性
import fs from 'fs';
const DIR = 'D:/minecraft/modp/Espetro/build/tmp/server-vehicles/';
const OUT = 'D:/minecraft/modp/Espetro/build/tmp/server-vehicles-fixed/';
fs.mkdirSync(OUT, { recursive: true });

// ============ 抗性模板 ============
const DM_MBT = [
	"minecraft:arrow 0", "minecraft:trident 0", "minecraft:mob_attack 0",
	"minecraft:mob_attack_no_aggro 0", "minecraft:mob_projectile 0",
	"minecraft:player_attack 0", "#superbwarfare:projectile 0",
	"All - 20", "minecraft:lava + 20", "minecraft:lava * 10",
	"@minecraft:tnt * 4", "@minecraft:tnt_minecart * 4",
	"@#superbwarfare:aerial_bomb * 12", "All * 0.2",
	"superbwarfare:vehicle_strike * 2.5", "minecraft:explosion * 2",
	"superbwarfare:custom_explosion * 0.65", "superbwarfare:projectile_explosion * 0.65",
	"superbwarfare:mine * 0.5", "superbwarfare:lunge_mine * 0.5",
	"superbwarfare:projectile_hit * 1.3", "superbwarfare:grapeshot_hit * 0.1",
	"#superbwarfare:projectile_absolute * 0.15", "@#superbwarfare:aa_missile * 0.3",
	"@superbwarfare:small_cannon_shell * 0.7", "@superbwarfare:c4 * 4",
	"@#superbwarfare:at_rocket * 1.1", "@superbwarfare:gun_grenade * 1.25",
	"@superbwarfare:mortar_shell * 1.25", "@superbwarfare:tm_62 * 2.5"
];
const DM_IFV = [
	"All - 13", "minecraft:lava + 13", "minecraft:lava * 10",
	"@minecraft:tnt * 3", "@minecraft:tnt_minecart * 3", "All * 0.2",
	"minecraft:arrow * 1.5", "minecraft:trident * 1.5",
	"minecraft:mob_attack * 2.5", "minecraft:mob_attack_no_aggro * 2",
	"minecraft:mob_projectile * 1.5", "minecraft:explosion * 6",
	"minecraft:player_explosion * 6", "superbwarfare:custom_explosion * 2",
	"superbwarfare:projectile_explosion * 2", "superbwarfare:mine * 0.7",
	"superbwarfare:lunge_mine * 0.9", "superbwarfare:projectile_hit * 1.35",
	"superbwarfare:grapeshot_hit * 0.25", "superbwarfare:laser * 1.25",
	"@#superbwarfare:aerial_bomb * 3", "@#superbwarfare:aa_missile * 0.5",
	"#superbwarfare:projectile * 0.1", "#superbwarfare:projectile_absolute * 0.7",
	"#superbwarfare:vehicle_strike * 13", "@superbwarfare:mortar_shell * 1.1",
	"@superbwarfare:gun_grenade * 1.5", "@superbwarfare:javelin_missile * 0.8"
];

// ============ 工具 ============
/** 设置武器第 idx 种子弹的数值（idx=0 为默认弹药→顶层字段，否则 Override） */
function setAmmo(weapon, idx, { d, ed, er }) {
	if (!weapon) return 0;
	const at = weapon.AmmoType?.[idx];
	let changed = 0;
	if (typeof at === 'string' || idx === 0 && at === undefined && weapon.AmmoType === undefined) {
		if (d !== undefined && weapon.Damage !== d) { weapon.Damage = d; changed++; }
		if (ed !== undefined && weapon.ExplosionDamage !== ed) { weapon.ExplosionDamage = ed; changed++; }
		if (er !== undefined && weapon.ExplosionRadius !== er) { weapon.ExplosionRadius = er; changed++; }
	} else if (at && typeof at === 'object' && at.Override) {
		const o = at.Override;
		if (d !== undefined && o.Damage !== d) { o.Damage = d; changed++; }
		if (ed !== undefined && o.ExplosionDamage !== ed) { o.ExplosionDamage = ed; changed++; }
		if (er !== undefined && o.ExplosionRadius !== er) { o.ExplosionRadius = er; changed++; }
	}
	return changed;
}
/** 设置武器基础伤害（无弹药切换的武器） */
function setWeapon(weapon, { d, ed, er }) {
	if (!weapon) return 0;
	let changed = 0;
	if (d !== undefined && weapon.Damage !== d) { weapon.Damage = d; changed++; }
	if (ed !== undefined && weapon.ExplosionDamage !== ed) { weapon.ExplosionDamage = ed; changed++; }
	if (er !== undefined && weapon.ExplosionRadius !== er) { weapon.ExplosionRadius = er; changed++; }
	return changed;
}

// ============ 逐车修改 ============
const report = [];
function edit(file, fn, note) {
	const path = DIR + file;
	if (!fs.existsSync(path)) { report.push(`${file}: 不存在，跳过`); return; }
	const j = JSON.parse(fs.readFileSync(path, 'utf8'));
	const changed = fn(j.Weapons ?? {}, j);
	fs.writeFileSync(OUT + file, JSON.stringify(j, null, 2), 'utf8');
	report.push(`${file}: ${changed} 处修改${note ? '（' + note + '）' : ''}`);
}

// ===== 中系 =====
edit('ztz99a.json', (W) => {
	let c = 0;
	c += setAmmo(W.Cannon, 0, { d: 500, ed: 5, er: 2 });
	c += setAmmo(W.Cannon, 1, { d: 200, ed: 50, er: 10 });
	c += setWeapon(W.MachineGun, { d: 8 });
	c += setWeapon(W.PassengerMachineGun, { d: 20 });
	return c;
});
edit('zlt11.json', (W) => {
	let c = 0;
	c += setAmmo(W.Cannon, 0, { d: 400, ed: 5, er: 2 });
	c += setAmmo(W.Cannon, 1, { d: 5, ed: 40, er: 7 });
	c += setWeapon(W.MachineGun, { d: 8 });
	c += setWeapon(W.PassengerMachineGun, { d: 20 });
	return c;
});
edit('ztd05.json', (W) => {
	let c = 0;
	c += setAmmo(W.Cannon, 0, { d: 400, ed: 5, er: 2 });
	c += setAmmo(W.Cannon, 1, { d: 5, ed: 40, er: 7 });
	c += setWeapon(W.MachineGun, { d: 8 });
	c += setWeapon(W.PassengerMachineGun, { d: 20 });
	return c;
});
edit('zbd04a.json', (W) => {
	let c = 0;
	c += setAmmo(W.Cannon, 0, { d: 75, ed: 5, er: 2 });
	c += setAmmo(W.Cannon, 1, { d: 30, ed: 20, er: 5 });
	c += setWeapon(W.MainMachineGun, { d: 8 });
	return c;
});
edit('zbd05.json', (W) => {
	let c = 0;
	c += setAmmo(W.Cannon, 0, { d: 75, ed: 5, er: 2 });
	c += setAmmo(W.Cannon, 1, { d: 30, ed: 20, er: 5 });
	c += setWeapon(W.MachineGun, { d: 8 });
	return c;
});
edit('zbl08.json', (W) => {
	let c = 0;
	c += setAmmo(W.Cannon, 0, { d: 75, ed: 5, er: 2 });
	c += setAmmo(W.Cannon, 1, { d: 30, ed: 20, er: 5 });
	c += setWeapon(W.MachineGun, { d: 8 });
	return c;
});
edit('zsl10.json', (W) => {
	// ZSL-10 仅一把车顶 12.7mm 机枪
	return setWeapon(W.MachineGun, { d: 20 });
});
edit('csk181.json', (W) => {
	return setWeapon(W.PassengerMachineGun, { d: 20 });
});

// ===== 俄系 =====
edit('t72b3.json', (W) => {
	let c = 0;
	c += setAmmo(W.Cannon, 0, { d: 450, ed: 5, er: 2 });
	c += setAmmo(W.Cannon, 1, { d: 200, ed: 50, er: 10 });
	c += setWeapon(W.MachineGun, { d: 12 });
	c += setWeapon(W.PassengerMachineGun, { d: 20 });
	return c;
});
edit('t90mh.json', (W) => {
	let c = 0;
	c += setAmmo(W.Cannon, 0, { d: 450, ed: 5, er: 2 });
	c += setAmmo(W.Cannon, 1, { d: 200, ed: 50, er: 10 });
	c += setWeapon(W.MachineGun, { d: 12 });
	c += setWeapon(W.PassengerMachineGun, { d: 20 });
	return c;
});
edit('bmp3.json', (W) => {
	let c = 0;
	c += setAmmo(W.Cannon, 0, { d: 65, ed: 5, er: 2 });
	c += setAmmo(W.Cannon, 1, { d: 30, ed: 20, er: 5 });
	c += setWeapon(W.MainMachineGun, { d: 12 });
	return c;
});
edit('bmp2.json', (W) => {
	let c = 0;
	c += setAmmo(W.Cannon, 0, { d: 65, ed: 5, er: 2 });
	c += setAmmo(W.Cannon, 1, { d: 30, ed: 20, er: 5 });
	c += setWeapon(W.Coax, { d: 12 });
	return c;
});
edit('bmp2d.json', (W) => {
	let c = 0;
	c += setAmmo(W.Cannon, 0, { d: 65, ed: 5, er: 2 });
	c += setAmmo(W.Cannon, 1, { d: 30, ed: 20, er: 5 });
	c += setWeapon(W.Coax, { d: 12 });
	return c;
});
edit('btr82.json', (W) => {
	let c = 0;
	c += setAmmo(W.Cannon, 0, { d: 65, ed: 5, er: 2 });
	c += setAmmo(W.Cannon, 1, { d: 30, ed: 20, er: 5 });
	c += setWeapon(W.Coax, { d: 12 });
	return c;
});
edit('btr80.json', (W) => {
	// BTR-80: Cannon 是 14.5mm（不在规则），只改同轴 7.62
	return setWeapon(W.Coax, { d: 12 });
});
edit('gaz_tigr_rws.json', (W) => {
	return setWeapon(W.PassengerMachineGun, { d: 20 });
});

// ===== 美系 =====
edit('m1a2sepv2.json', (W) => {
	let c = 0;
	c += setAmmo(W.Cannon, 0, { d: 577, ed: 5, er: 2 });
	c += setAmmo(W.Cannon, 1, { d: 350, ed: 35, er: 6 });
	c += setWeapon(W.MachineGun, { d: 12 });
	c += setWeapon(W.PassengerMachineGun, { d: 20 });
	return c;
});
edit('m3a3.json', (W) => {
	let c = 0;
	c += setAmmo(W.Cannon, 0, { d: 85, ed: 5, er: 2 });
	c += setAmmo(W.Cannon, 1, { d: 30, ed: 20, er: 4 });
	c += setWeapon(W.MainMachineGun, { d: 12 });
	return c;
});
edit('stryker_dragoon.json', (W) => {
	let c = 0;
	c += setAmmo(W.Cannon, 0, { d: 90, ed: 5, er: 2 });
	c += setAmmo(W.Cannon, 1, { d: 30, ed: 30, er: 5 });
	c += setWeapon(W.Coax, { d: 12 });
	return c;
});
edit('stryker_m2.json', (W) => {
	return setWeapon(W.Coax, { d: 12 });
});
edit('stryker_mgs.json', (W) => {
	let c = 0;
	c += setAmmo(W.Cannon, 0, { d: 400, ed: 5, er: 2 });
	c += setAmmo(W.Cannon, 1, { d: 5, ed: 40, er: 7 });
	c += setWeapon(W.PassengerMachineGun, { d: 20 });
	c += setWeapon(W.Coax, { d: 12 });
	return c;
});

// ===== 抗性 =====
const MBT_FILES = ['ztz99a.json', 't72b3.json', 't90mh.json', 'm1a2sepv2.json'];
const IFV_FILES = ['zlt11.json', 'ztd05.json', 'zbd04a.json', 'zbd05.json', 'zbl08.json',
	'zsl10.json', 'csk181.json', 'bmp3.json', 'bmp2.json', 'bmp2d.json', 'btr80.json',
	'btr82.json', 'gaz_tigr_rws.json', 'm3a3.json', 'stryker_dragoon.json', 'stryker_m2.json',
	'stryker_mgs.json', 'stryker_mortar.json'];

for (const f of MBT_FILES) {
	const path = OUT + f;
	if (!fs.existsSync(path)) continue;
	const j = JSON.parse(fs.readFileSync(path, 'utf8'));
	if (JSON.stringify(j.DamageModifiers ?? null) !== JSON.stringify(DM_MBT)) {
		j.DamageModifiers = DM_MBT;
		fs.writeFileSync(path, JSON.stringify(j, null, 2), 'utf8');
		report.push(`${f}: DamageModifiers -> 主战坦克抗性`);
	}
}
for (const f of IFV_FILES) {
	const path = OUT + f;
	if (!fs.existsSync(path)) continue;
	const j = JSON.parse(fs.readFileSync(path, 'utf8'));
	if (JSON.stringify(j.DamageModifiers ?? null) !== JSON.stringify(DM_IFV)) {
		j.DamageModifiers = DM_IFV;
		fs.writeFileSync(path, JSON.stringify(j, null, 2), 'utf8');
		report.push(`${f}: DamageModifiers -> 步战/输送车抗性`);
	}
}

// 验证 JSON
let invalid = 0;
for (const f of fs.readdirSync(OUT)) {
	try { JSON.parse(fs.readFileSync(OUT + f, 'utf8')); } catch { invalid++; console.log('INVALID: ' + f); }
}
console.log('invalid: ' + invalid);
console.log(report.join('\n'));
