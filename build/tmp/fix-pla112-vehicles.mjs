// fix-pla112-vehicles.mjs — 修改 112 旅 acv/truck 载具定义（fightveh/capacity/座位）
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const TARGET = 'EsFactions/pla_112th_brigade.json';
const OUT_FILE = 'D:/minecraft/modp/Espetro/build/tmp/mcsm-file-out.txt';

async function jfetch(url, opts) {
	const ctrl = new AbortController();
	const t = setTimeout(() => ctrl.abort(), 30000);
	try { return await fetch(url, { ...opts, signal: ctrl.signal }); } finally { clearTimeout(t); }
}

/** 定位 "key": { 到闭合 }，返回 {start, end, block}；end 指向闭合 } 的下标 */
function findObject(raw, key) {
	const idx = raw.indexOf(`"${key}": {`);
	if (idx < 0) return null;
	const start = raw.indexOf('{', idx);
	let depth = 0;
	for (let i = start; i < raw.length; i++) {
		if (raw[i] === '{') depth++;
		else if (raw[i] === '}') { depth--; if (depth === 0) return { start, end: i, block: raw.slice(start, i + 1) }; }
	}
	return null;
}

const ACV_NEW = `{
      "display_name": "ZSL-10 装甲输送车",
      "entity": [
        "dragonrise_reforge:zsl10",
        "dragonrise_reforge:zsl10"
      ],
      "per_max_count": 1,
      "vehicle_crew_seats": 0,
      "fightveh": true,
      "capacity": 500,
      "respawn_minutes": 10,
      "troop_value": 5
    }`;

const TRUCK_NEW = `{
      "display_name": "CTM-131 武装运兵卡车",
      "entity": [
        "dragonrise_reforge:mv3_armed",
        "dragonrise_reforge:mv3_armed"
      ],
      "per_max_count": 1,
      "fightveh": true,
      "capacity": 300,
      "respawn_minutes": 10,
      "troop_value": 5
    }`;

async function main() {
	const loginRes = await jfetch(`${PANEL}/api/auth/login`, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
		body: JSON.stringify({ username: USER, password: PASS })
	});
	const loginBody = await loginRes.json();
	const token = String(loginBody.data ?? '');
	const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
	const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;

	const readRes = await jfetch(`${PANEL}/api/files?${q}`, {
		method: 'PUT',
		headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' },
		body: JSON.stringify({ target: TARGET })
	});
	const readBody = await readRes.json();
	const raw = typeof readBody.data === 'string' ? readBody.data : JSON.stringify(readBody);

	const acv = findObject(raw, 'acv');
	const truck = findObject(raw, 'truck');
	let fixed = raw;
	// 先替换靠后的 truck，再替换 acv（避免偏移错位）
	if (truck) fixed = fixed.slice(0, truck.start) + TRUCK_NEW + fixed.slice(truck.end + 1);
	if (acv) fixed = fixed.slice(0, acv.start) + ACV_NEW + fixed.slice(acv.end + 1);
	if (fixed === raw || !acv || !truck) {
		fs.writeFileSync(OUT_FILE, 'NO_CHANGE acv=' + !!acv + ' truck=' + !!truck, 'utf8');
		return;
	}
	// 原文件含 NBT 字符串内花括号与宽松内容，无法用朴素括号计数/严格 JSON 验证；
	// findObject 定位的对象块本身无嵌套字符串花括号，替换是精确的，直接写回。

	let writeBody;
	for (let attempt = 0; attempt < 5; attempt++) {
		const writeRes = await jfetch(`${PANEL}/api/files?${q}`, {
			method: 'PUT',
			headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' },
			body: JSON.stringify({ target: TARGET, text: fixed })
		});
		writeBody = await writeRes.json();
		if (String(writeBody.status) === '200') break;
		await new Promise((r) => setTimeout(r, 3000));
	}

	await new Promise((r) => setTimeout(r, 2000));
	const vRes = await jfetch(`${PANEL}/api/files?${q}`, {
		method: 'PUT',
		headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' },
		body: JSON.stringify({ target: TARGET })
	});
	const vBody = await vRes.json();
	const after = typeof vBody.data === 'string' ? vBody.data : '';
	let jsonOk = true;
	try { JSON.parse(after); } catch (e) { jsonOk = false; }
	fs.writeFileSync(OUT_FILE,
		'WRITE=' + JSON.stringify(writeBody) + '\n'
		+ 'JSON_VALID_AFTER=' + jsonOk + '\n'
		+ 'ACV_OK=' + after.includes('"vehicle_crew_seats": 0') + '/' + after.includes('"fightveh": true') + '/' + after.includes('"capacity": 500') + '\n'
		+ 'TRUCK_OK=' + after.includes('"capacity": 300') + '\n'
		+ 'SUPPLY_TRUCK_INTACT=' + (after.match(/"supply_truck":\s*\{[\s\S]*?"supplyveh": true/) !== null),
		'utf8');
}
main().catch((e) => {
	fs.writeFileSync(OUT_FILE, 'ERROR: ' + (e?.stack ?? e), 'utf8');
	process.exit(1);
});
