// apply-velocity.mjs — 把本地 GScode 的 Velocity 值应用到服务端文件（只改 Velocity 字段）
import fs from 'fs';

const LOCAL_DIR = 'D:/minecraft/modp/GScode/src/main/resources/data/dragonrise_reforge/sbw/vehicles/';
const SERVER_DIR = 'D:/minecraft/modp/Espetro/build/tmp/server-vehicles/';
const OUT_DIR = 'D:/minecraft/modp/Espetro/build/tmp/server-vehicles-fixed/';
fs.mkdirSync(OUT_DIR, { recursive: true });

const files = fs.readdirSync(SERVER_DIR).filter((f) => f.endsWith('.json'));

/** 收集对象中所有 key 含 "velocity"（不区分大小写）的路径与值 */
function collectVelocity(obj, path, out) {
	if (obj && typeof obj === 'object') {
		for (const [k, v] of Object.entries(obj)) {
			const p = path + '/' + k;
			if (k.toLowerCase().includes('velocity')) {
				out.push({ path: p, value: v });
			}
			collectVelocity(v, p, out);
		}
	}
}

/** 按路径取对象值 */
function getByPath(obj, path) {
	const parts = path.split('/').filter(Boolean);
	let cur = obj;
	for (const p of parts) {
		if (cur == null || typeof cur !== 'object' || !(p in cur)) return undefined;
		cur = cur[p];
	}
	return cur;
}

for (const f of files) {
	const localPath = LOCAL_DIR + f;
	const serverPath = SERVER_DIR + f;
	if (!fs.existsSync(localPath)) {
		console.log(`${f}: 本地无此文件，跳过`);
		continue;
	}
	let local, server;
	try {
		local = JSON.parse(fs.readFileSync(localPath, 'utf8'));
		server = JSON.parse(fs.readFileSync(serverPath, 'utf8'));
	} catch (e) {
		console.log(`${f}: 解析失败 ${e.message}`);
		continue;
	}
	const velos = [];
	collectVelocity(local, '', velos);
	let changed = 0;
	const applied = [];
	for (const { path, value } of velos) {
		const parts = path.split('/').filter(Boolean);
		// 在服务端逐级检查是否存在
		let cur = server;
		let ok = true;
		for (const p of parts) {
			if (cur == null || typeof cur !== 'object' || !(p in cur)) { ok = false; break; }
			cur = cur[p];
		}
		if (ok) {
			// 找到父对象，设置值
			const parent = getByPath(server, parts.slice(0, -1).join('/'));
			if (parent && typeof parent === 'object') {
				const key = parts[parts.length - 1];
				if (JSON.stringify(parent[key]) !== JSON.stringify(value)) {
					parent[key] = value;
					changed++;
					applied.push(path + ' -> ' + JSON.stringify(value));
				}
			}
		}
	}
	fs.writeFileSync(OUT_DIR + f, JSON.stringify(server, null, 2), 'utf8');
	console.log(`${f}: 共 ${velos.length} 个 Velocity（本地），服务端应用 ${changed} 个`);
	if (applied.length > 0 && applied.length <= 6) {
		for (const a of applied) console.log('   ' + a);
	}
}
console.log('\n完成，输出到 ' + OUT_DIR);
