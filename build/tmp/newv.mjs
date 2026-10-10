import fs from 'fs';
const DIR = 'D:/minecraft/modp/Espetro/build/tmp/server-vehicles/';
for (const f of ['bmp1am.json','gaz_tigr_gl.json','gaz_tigr_mg.json','matv.json','matv_9in1.json','matv_crow.json','matv_tow.json','m113.json']) {
	const j = JSON.parse(fs.readFileSync(DIR + f, 'utf8'));
	console.log(`===== ${f} =====`);
	for (const [wn, w] of Object.entries(j.Weapons ?? {})) {
		console.log(`  [${wn}] D=${w.Damage ?? '-'} Expl=${w.ExplosionDamage ?? '-'}/${w.ExplosionRadius ?? '-'} AmmoType=${Array.isArray(w.AmmoType) ? w.AmmoType.length : 0} Projectile=${w.Projectile ?? '-'}`);
		const ats = Array.isArray(w.AmmoType) ? w.AmmoType : [];
		for (let i = 0; i < ats.length; i++) {
			const at = ats[i];
			if (typeof at === 'string') console.log(`    #${i} 默认: ${at}`);
			else if (at?.Override) console.log(`    #${i}: ${at.Ammo} D=${at.Override.Damage ?? '-'} Expl=${at.Override.ExplosionDamage ?? '-'}/${at.Override.ExplosionRadius ?? '-'}`);
		}
	}
	if (Object.keys(j.Weapons ?? {}).length === 0) console.log('  (无武器)');
}