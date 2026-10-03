// add-truck-heishan.mjs — 给黑山工厂 VehSpawn.json 补 truck/supply_truck（占位坐标）
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const TARGET = 'EsWorld/server_battlefield/EsConfig/VehSpawn.json';
const OUT_FILE = 'D:/minecraft/modp/Espetro/build/tmp/mcsm-file-out.txt';

async function jfetch(url, opts) {
	const ctrl = new AbortController();
	const t = setTimeout(() => ctrl.abort(), 30000);
	try { return await fetch(url, { ...opts, signal: ctrl.signal }); } finally { clearTimeout(t); }
}

const TRUCK_BLOCK = `    "truck": [
      {
        "id": "truck_1",
        "attack": {
          "x": 1250,
          "y": 12,
          "z": -810,
          "yaw": 90
        },
        "defend": {
          "x": -2620,
          "y": 10,
          "z": -500,
          "yaw": 0
        }
      },
      {
        "id": "truck_2",
        "attack": {
          "x": 1255,
          "y": 12,
          "z": -810,
          "yaw": 90
        },
        "defend": {
          "x": -2620,
          "y": 10,
          "z": -510,
          "yaw": 0
        }
      }
    ],
    "supply_truck": [
      {
        "id": "truck_3",
        "attack": {
          "x": 1250,
          "y": 12,
          "z": -820,
          "yaw": 90
        },
        "defend": {
          "x": -2630,
          "y": 10,
          "z": -500,
          "yaw": 0
        }
      },
      {
        "id": "truck_4",
        "attack": {
          "x": 1255,
          "y": 12,
          "z": -820,
          "yaw": 90
        },
        "defend": {
          "x": -2630,
          "y": 10,
          "z": -510,
          "yaw": 0
        }
      }
    ],
`;

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

	// 1) VehTypes 加 truck/supply_truck（"car" 之后）
	let fixed = raw.replace(/"car",\s*\n(\s*)"transport_helicopter"/, '"car",\n$1"truck",\n$1"supply_truck",\n$1"transport_helicopter"');
	// 2) spawn_points 加 truck/supply_truck 块（transport_helicopter 数组之前）
	fixed = fixed.replace(/(\n\s*)"transport_helicopter": \[/, '\n' + TRUCK_BLOCK.replace(/\n/g, '\n$1') + '$1"transport_helicopter": [');

	if (fixed === raw) {
		fs.writeFileSync(OUT_FILE, 'NO_CHANGE: 未找到插入点', 'utf8');
		return;
	}
	try { JSON.parse(fixed); } catch (e) {
		fs.writeFileSync(OUT_FILE, 'FIXED_JSON_INVALID: ' + e.message, 'utf8');
		return;
	}

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

	// verify
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
		+ 'HAS_TRUCK=' + after.includes('"truck"') + ' HAS_SUPPLY_TRUCK=' + after.includes('"supply_truck"'),
		'utf8');
}
main().catch((e) => {
	fs.writeFileSync(OUT_FILE, 'ERROR: ' + (e?.stack ?? e), 'utf8');
	process.exit(1);
});
