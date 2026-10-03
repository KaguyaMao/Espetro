// modify-damage2.mjs — 更正数值 + 补充新载具
import fs from 'fs';
const DIR = 'D:/minecraft/modp/Espetro/build/tmp/server-vehicles/';
const OUT = 'D:/minecraft/modp/Espetro/build/tmp/server-vehicles-fixed/';
fs.mkdirSync(OUT, { recursive: true });

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

function load(file) {
	const out = OUT + file;
	const dir = DIR + file;
	if (fs.existsSync(out)) return JSON.parse(fs.readFileSync(out, 'utf8'));
	if (fs.existsSync(dir)) return JSON.parse(fs.readFileSync(dir, 'utf8'));
	return null;
}
function save(file, j) {
	fs.writeFileSync(OUT + file, JSON.stringify(j, null, 2), 'utf8');
}
function setAmmo(w, idx, { d, ed, er }) {
	if (!w) return 0;
	const at = w.AmmoType?.[idx];
	let c = 0;
	if (typeof at === 'string') {
		if (d !== undefined && w.Damage !== d) { w.Damage = d; c++; }
		if (ed !== undefined && w.ExplosionDamage !== ed) { w.ExplosionDamage = ed; c++; }
		if (er !== undefined && w.ExplosionRadius !== er) { w.ExplosionRadius = er; c++; }
	} else if (at && typeof at === 'object' && at.Override) {
		const o = at.Override;
		if (d !== undefined && o.Damage !== d) { o.Damage = d; c++; }
		if (ed !== undefined && o.ExplosionDamage !== ed) { o.ExplosionDamage = ed; c++; }
		if (er !== undefined && o.ExplosionRadius !== er) { o.ExplosionRadius = er; c++; }
	}
	return c;
}
function setWeapon(w, { d, ed, er }) {
	if (!w) return 0;
	let c = 0;
	if (d !== undefined && w.Damage !== d) { w.Damage = d; c++; }
	if (ed !== undefined && w.ExplosionDamage !== ed) { w.ExplosionDamage = ed; c++; }
	if (er !== undefined && w.ExplosionRadius !== er) { w.ExplosionRadius = er; c++; }
	return c;
}
function setDM(file, dm) {
	const j = load(file);
	if (!j) return;
	if (JSON.stringify(j.DamageModifiers ?? null) !== JSON.stringify(dm)) {
		j.DamageModifiers = dm;
		save(file, j);
		console.log(`${file}: 抗性已设`);
	}
}
function edit(file, fn, note) {
	const j = load(file);
	if (!j) { console.log(`${file}: 不存在`); return; }
	const c = fn(j.Weapons ?? {}, j);
	save(file, j);
	console.log(`${file}: ${c} 处修改${note ? '（' + note + '）' : ''}`);
}

// ===== 1. 更正 105mm HE =====
edit('zlt11.json', (W) => setAmmo(W.Cannon, 1, { d: 200, ed: 30, er: 7 }), '105mm HE 更正');
edit('ztd05.json', (W) => setAmmo(W.Cannon, 1, { d: 200, ed: 30, er: 7 }), '105mm HE 更正');
edit('stryker_mgs.json', (W) => setAmmo(W.Cannon, 1, { d: 200, ed: 30, er: 7 }), '105mm HE 更正');

// ===== 2. 100mm 榴弹 =====
edit('bmp3.json', (W) => setAmmo(W['100MM_Cannon'], 0, { d: 200, ed: 30, er: 7 }), '100mm 榴弹');
edit('zbd04a.json', (W) => setAmmo(W['100MM_Cannon'], 0, { d: 200, ed: 30, er: 7 }), '100mm 榴弹');

// ===== 3. Konkurs =====
for (const f of ['bmp2.json', 'bmp2d.json', 'bmp2m.json']) {
	edit(f, (W) => setWeapon(W.Konkurs, { d: 550, ed: 30, er: 5 }), 'Konkurs');
}

// ===== 4. medium_anti_ground_missile =====
edit('ztz99a.json', (W) => setAmmo(W.Cannon, 2, { d: 650, ed: 30, er: 5 }), '反坦克导弹');
edit('ztd05.json', (W) => setAmmo(W.Cannon, 2, { d: 650, ed: 30, er: 5 }), '反坦克导弹');
edit('zbd04a.json', (W) => setAmmo(W['100MM_Cannon'], 1, { d: 650, ed: 30, er: 5 }), '反坦克导弹');
edit('bmp3.json', (W) => setAmmo(W['100MM_Cannon'], 1, { d: 650, ed: 30, er: 5 }), '反坦克导弹');

// ===== 5. Missile =====
for (const f of ['m3a3.json', 'zbd05.json', 'zbl08.json']) {
	edit(f, (W) => setWeapon(W.Missile, { d: 650, ed: 30, er: 5 }), '导弹');
}

// ===== 6. btr80 14.5mm =====
edit('btr80.json', (W) => setWeapon(W.Cannon, { d: 38 }), '14.5mm');

// ===== 7. 新增载具 =====
// m113（美系输送车，12.7mm 机枪）
edit('m113.json', (W) => setWeapon(W.Cannon, { d: 20 }), '12.7mm');
// bmp1am（俄系 30mm + 7.62）
edit('bmp1am.json', (W) => {
	let c = 0;
	c += setAmmo(W.Cannon, 0, { d: 65, ed: 5, er: 2 });
	c += setAmmo(W.Cannon, 1, { d: 30, ed: 20, er: 5 });
	c += setWeapon(W.Coax, { d: 12 });
	return c;
}, '30mm/7.62');
// bmp2m（俄系 30mm + Konkurs + 7.62）
edit('bmp2m.json', (W) => {
	let c = 0;
	c += setAmmo(W.Cannon, 0, { d: 65, ed: 5, er: 2 });
	c += setAmmo(W.Cannon, 1, { d: 30, ed: 20, er: 5 });
	c += setWeapon(W.Konkurs, { d: 550, ed: 30, er: 5 });
	c += setWeapon(W.Coax, { d: 12 });
	return c;
}, '30mm/Konkurs/7.62');
// gaz_tigr_mg（俄系 7.62）
edit('gaz_tigr_mg.json', (W) => setWeapon(W.MachineGun, { d: 12 }), '7.62');
// matv / matv_9in1（美系 12.7）
edit('matv.json', (W) => setWeapon(W.PassengerMachineGun, { d: 20 }), '12.7');
edit('matv_9in1.json', (W) => setWeapon(W.PassengerMachineGun, { d: 20 }), '12.7');
// matv_crow（美系 CROWS 12.7）
edit('matv_crow.json', (W) => setWeapon(W.Crows, { d: 20 }), '12.7');

// 抗性（新增载具 → 第二类）
const NEW_IFV = ['m113.json', 'bmp1am.json', 'bmp2m.json', 'gaz_tigr_gl.json',
	'gaz_tigr_mg.json', 'matv.json', 'matv_9in1.json', 'matv_crow.json', 'matv_tow.json'];
for (const f of NEW_IFV) setDM(f, DM_IFV);

// 验证
let invalid = 0;
for (const f of fs.readdirSync(OUT)) {
	try { JSON.parse(fs.readFileSync(OUT + f, 'utf8')); } catch { invalid++; console.log('INVALID ' + f); }
}
console.log('invalid: ' + invalid);
