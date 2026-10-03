import fs from 'fs';
const c = fs.readFileSync('D:/minecraft/modp/Espetro/build/tmp/supply-default.json', 'utf8');
// 剥离注释（保留字符串）
let out = '';
let i = 0;
let inStr = false;
while (i < c.length) {
	const ch = c[i];
	if (inStr) {
		out += ch;
		if (ch === '\\') { out += c[i+1] ?? ''; i += 2; continue; }
		if (ch === '"') inStr = false;
		i++;
		continue;
	}
	if (ch === '"') { inStr = true; out += ch; i++; continue; }
	if (ch === '/' && c[i+1] === '*') {
		const end = c.indexOf('*/', i + 2);
		if (end === -1) break;
		i = end + 2;
		continue;
	}
	if (ch === '/' && c[i+1] === '/') {
		const end = c.indexOf('\n', i + 2);
		i = end === -1 ? c.length : end;
		continue;
	}
	out += ch;
	i++;
}
const j = JSON.parse(out);
console.log('Default 模式: ' + (j.Default?.Mode ?? '?'));
const ov = j.VehicleOverrides ?? {};
const keys = Object.keys(ov);
console.log('VehicleOverrides 载具数: ' + keys.length);
for (const k of keys) {
	const v = ov[k];
	console.log(`  ${k}: Mode=${v?.Mode ?? '(继承默认)'} Heal=${v?.HealPercent ?? '-'}`);
}