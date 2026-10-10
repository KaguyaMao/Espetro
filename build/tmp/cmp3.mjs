import fs from 'fs';
const l = JSON.parse(fs.readFileSync('D:/minecraft/modp/GScode/src/main/resources/data/dragonrise_reforge/sbw/vehicles/zlt11.json', 'utf8'));
const cannon = l.Weapons.Cannon;
console.log('本地 Cannon 结构:');
console.log(JSON.stringify(cannon, null, 2).slice(0, 1500));