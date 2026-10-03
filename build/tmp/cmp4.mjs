import fs from 'fs';
const A = 'D:/minecraft/modp/Espetro/build/tmp/server-vehicles/';
const B = 'D:/minecraft/modp/Espetro/build/tmp/server-vehicles-fixed/';
for (const f of ['ah64.json', 'sx1.json', 'z20.json']) {
	const a = fs.readFileSync(A + f, 'utf8');
	const b = fs.readFileSync(B + f, 'utf8');
	console.log(`${f}: ${a === b ? '相同' : '不同! len ' + a.length + ' vs ' + b.length}`);
	if (a !== b) {
		const ja = JSON.parse(a), jb = JSON.parse(b);
		console.log('  原始 DM 数: ' + (ja.DamageModifiers?.length ?? 0) + ', 输出 DM 数: ' + (jb.DamageModifiers?.length ?? 0));
	}
}