// verify-all.mjs — 列出所有文件最终数值 + 未覆盖武器
import fs from 'fs';
const OUT = 'D:/minecraft/modp/Espetro/build/tmp/server-vehicles-fixed/';
const files = fs.readdirSync(OUT).filter((f) => f.endsWith('.json')).sort();

const DM_MBT_FIRST = 'minecraft:arrow 0';
const DM_IFV_FIRST = 'All - 13';

for (const f of files) {
	const j = JSON.parse(fs.readFileSync(OUT + f, 'utf8'));
	const dm = j.DamageModifiers;
	let dmType = '无抗性';
	if (Array.isArray(dm) && dm[0] === DM_MBT_FIRST) dmType = '主战坦克抗性';
	else if (Array.isArray(dm) && dm[0] === DM_IFV_FIRST) dmType = '步战/输送车抗性';
	else if (Array.isArray(dm)) dmType = '其他抗性(' + dm.length + '条)';
	console.log(`===== ${f} [${dmType}] =====`);
	for (const [wn, w] of Object.entries(j.Weapons ?? {})) {
		const ats = Array.isArray(w.AmmoType) ? w.AmmoType : [];
		let line = `  [${wn}] D=${w.Damage ?? '-'} Expl=${w.ExplosionDamage ?? '-'}/${w.ExplosionRadius ?? '-'}`;
		if (ats.length > 0) {
			const parts = [];
			for (let i = 0; i < ats.length; i++) {
				const at = ats[i];
				if (typeof at === 'string') parts.push(`#${i}=${at.split(':').pop()}(基础)`);
				else if (at && at.Override) parts.push(`#${i}=${String(at.Ammo).split(':').pop()}(D=${at.Override.Damage ?? '-'} E=${at.Override.ExplosionDamage ?? '-'}/${at.Override.ExplosionRadius ?? '-'})`);
			}
			line += ' | ' + parts.join('; ');
		}
		console.log(line);
	}
}
