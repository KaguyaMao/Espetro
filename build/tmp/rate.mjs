import fs from 'fs';
const j = JSON.parse(fs.readFileSync('D:/minecraft/modp/Espetro/build/tmp/server-vehicles-fixed/ztz99a.json', 'utf8'));
const w = j.Weapons.Cannon;
console.log('Cannon 全部字段:');
console.log(Object.keys(w).join(', '));
console.log('\n值:');
for (const [k, v] of Object.entries(w)) {
	if (typeof v !== 'object') console.log(`  ${k}: ${v}`);
}