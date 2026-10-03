import fs from 'fs';
const A = 'D:/minecraft/modp/Espetro/build/tmp/server-vehicles/';
const B = 'D:/minecraft/modp/Espetro/build/tmp/server-vehicles-fixed/';
const a = JSON.parse(fs.readFileSync(A + 'z20.json', 'utf8'));
const b = JSON.parse(fs.readFileSync(B + 'z20.json', 'utf8'));
console.log('规范化后相同: ' + (JSON.stringify(a) === JSON.stringify(b)));
if (JSON.stringify(a) !== JSON.stringify(b)) {
	// 找差异键
	const ka = Object.keys(a), kb = Object.keys(b);
	console.log('原始键: ' + ka.join(','));
	console.log('输出键: ' + kb.join(','));
	for (const k of ka) {
		if (JSON.stringify(a[k]) !== JSON.stringify(b[k])) console.log('差异: ' + k);
	}
}