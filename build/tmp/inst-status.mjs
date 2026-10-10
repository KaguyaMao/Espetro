// inst-status.mjs — 查询实例状态（只读）
// 用法: node inst-status.mjs
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';

const STATUS_LABEL = { '-1': 'BUSY', 0: 'STOPPED', 1: 'STOPPING', 2: 'STARTING', 3: 'RUNNING' };

async function main() {
	const loginRes = await fetch(`${PANEL}/api/auth/login`, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
		body: JSON.stringify({ username: USER, password: PASS })
	});
	const loginBody = await loginRes.json();
	const token = String(loginBody.data ?? '');
	const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
	const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
	const res = await fetch(`${PANEL}/api/instance?${q}`, {
		headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }
	});
	const body = await res.json();
	const inst = body.data ?? body;
	const status = inst.status;
	console.log('status:', status, `(${STATUS_LABEL[String(status)] ?? 'UNKNOWN'})`);
	// 实例名在 config.nickname（/api/instance 顶层也有 nickname，但当前面板返回为空）
	console.log('nickname:', inst.nickname || inst.config?.nickname || inst.info?.nickname || '(unknown)');
	console.log('started:', inst.started ?? '(n/a)', '| processInfo:', JSON.stringify(inst.processInfo ?? {}).slice(0, 120));
	console.log('config.eventTask:', JSON.stringify(inst.config?.eventTask ?? {}).slice(0, 200));
}
main().catch((e) => { console.error(e); process.exit(1); });
