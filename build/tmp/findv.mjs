import fs from 'fs';
const local = 'D:/minecraft/modp/GScode/src/main/resources/data/dragonrise_reforge/sbw/vehicles/';
// 找 Velocity 在本地文件里的位置
const j = JSON.parse(fs.readFileSync(local + 'ztz99a.json', 'utf8'));
function findV(obj, path) {
	const out = [];
	if (obj && typeof obj === 'object') {
		for (const [k, v] of Object.entries(obj)) {
			if (k.toLowerCase().includes('velocity')) out.push(path + '/' + k + ' = ' + JSON.stringify(v));
			else out.push(...findV(v, path + '/' + k));
		}
	}
	return out;
}
const hits = findV(j, '');
console.log('ztz99a Velocity 字段:');
console.log(hits.slice(0, 10).join('\n'));
// 顶层结构
console.log('\n顶层键: ' + Object.keys(j).join(', '));