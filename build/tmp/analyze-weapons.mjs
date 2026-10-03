// analyze-weapons.mjs — 提取所有载具的武器/弹药详情
import fs from 'fs';
const DIR = 'D:/minecraft/modp/Espetro/build/tmp/server-vehicles/';
const files = fs.readdirSync(DIR).filter((f) => f.endsWith('.json')).sort();

function ammoInfo(weapon, idx) {
	// AmmoType[idx]: 字符串(默认弹药) 或 {Ammo, Override}
	const at = weapon.AmmoType?.[idx];
	if (at === undefined) return null;
	if (typeof at === 'string') {
		return { ammo: at, src: 'default' };
	}
	return { ammo: at.Ammo, src: 'override', ov: at.Override };
}

for (const f of files) {
	const j = JSON.parse(fs.readFileSync(DIR + f, 'utf8'));
	console.log(`===== ${f} (${j.ID ?? '?'}) =====`);
	for (const [wn, w] of Object.entries(j.Weapons ?? {})) {
		const base = `Damage=${w.Damage ?? '-'} Expl=${w.ExplosionDamage ?? '-'}/${w.ExplosionRadius ?? '-'}`;
		const ammoCount = Array.isArray(w.AmmoType) ? w.AmmoType.length : 0;
		console.log(`  [${wn}] ${base} AmmoType=${ammoCount}`);
		for (let i = 0; i < ammoCount; i++) {
			const info = ammoInfo(w, i);
			if (!info) continue;
			if (info.src === 'default') {
				console.log(`    #${i} (默认): ${info.ammo}`);
			} else {
				const o = info.ov ?? {};
				console.log(`    #${i}: ${info.ammo} -> D=${o.Damage ?? '-'} Expl=${o.ExplosionDamage ?? '-'}/${o.ExplosionRadius ?? '-'} Proj=${o.Projectile ?? '-'}`);
			}
		}
	}
}
