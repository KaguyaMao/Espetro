// sync-kubejs-vehicles.mjs — 把服务端 kubejs 的龙之崛起载具数据同步到客户端实例的 kubejs
//
// 背景：SBW 只认"优先级最高的那份 data/dragonrise_reforge/sbw/vehicles/<id>.json"。
// 实测优先级：KubeJS 虚拟数据包 > 模组 jar 内 data/ > 存档 datapacks/ 里的数据包。
// 因此服务端走 kubejs、客户端单机也走 kubejs，两边内容保持一致即可行为一致。
//
// 用法: node sync-kubejs-vehicles.mjs [--dry]
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';

const REMOTE_DIR = 'kubejs/data/dragonrise_reforge';
const CLIENT_DIR = 'D:/minecraft/squadMC预发布测试/versions/Squad预发布测试/kubejs/data/dragonrise_reforge';
const DRY = process.argv.includes('--dry');

function httpsReq(method, urlStr, headers, body, getCookie) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const req = https.request({
      method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search,
      headers, rejectUnauthorized: false
    }, (res) => {
      let data = '';
      res.on('data', (c) => (data += c));
      res.on('end', () => resolve({ status: res.statusCode, body: data, cookie: getCookie ? (res.headers['set-cookie'] ?? []).map((l) => l.split(';')[0]).join('; ') : undefined }));
    });
    req.on('error', reject);
    if (body !== undefined) req.write(body);
    req.end();
  });
}
const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

const login = await httpsReq('POST', `${PANEL}/api/auth/login`, {
  'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest'
}, JSON.stringify({ username: USER, password: PASS }), true);
const token = String(JSON.parse(login.body).data ?? '');
const cookie = login.cookie ?? '';
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;

async function readRemote(target) {
  for (let a = 0; a < 6; a++) {
    const res = await httpsReq('PUT', `${PANEL}/api/files?${q}`, {
      'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json'
    }, JSON.stringify({ target }));
    const body = JSON.parse(res.body);
    const data = typeof body.data === 'string' ? body.data : '';
    if (data.trim().startsWith('{')) return data;
    await sleep(2500); // 面板限流
  }
  return null;
}

async function listRemote(target) {
  for (let a = 0; a < 5; a++) {
    const res = await httpsReq('GET', `${PANEL}/api/files/list?page=0&page_size=200&file_name=&target=${encodeURIComponent(target)}&${q}`,
      { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
    const b = JSON.parse(res.body);
    if (b.status === 200) return (b.data?.items ?? []).filter((i) => i.type !== 0 && i.type !== 'directory').map((i) => i.name);
    await sleep(2500);
  }
  return null;
}

const jobs = [];
const vehicleNames = await listRemote(`${REMOTE_DIR}/sbw/vehicles`);
if (!vehicleNames) throw new Error('无法列出服务端载具文件');
for (const n of vehicleNames.filter((x) => x.endsWith('.json'))) {
  jobs.push({ remote: `${REMOTE_DIR}/sbw/vehicles/${n}`, local: `${CLIENT_DIR}/sbw/vehicles/${n}` });
}
jobs.push({ remote: `${REMOTE_DIR}/supply_station/default.json`, local: `${CLIENT_DIR}/supply_station/default.json` });

let written = 0, same = 0, failed = 0;
for (const j of jobs) {
  const text = await readRemote(j.remote);
  if (text === null) { console.log(`FAIL  ${j.remote}`); failed++; await sleep(1200); continue; }
  const exists = fs.existsSync(j.local);
  const old = exists ? fs.readFileSync(j.local, 'utf8') : null;
  if (old === text) { same++; }
  else {
    if (!DRY) { fs.mkdirSync(require('path').dirname(j.local), { recursive: true }); fs.writeFileSync(j.local, text, 'utf8'); }
    written++;
    console.log(`${DRY ? '[dry] ' : ''}写入 ${j.local.split('/kubejs/')[1]}  ${old === null ? '(新增)' : `(${old.length} → ${text.length} 字符)`}`);
  }
  await sleep(900);
}
console.log(`\n完成：更新 ${written} 个，已一致 ${same} 个，失败 ${failed} 个（共 ${jobs.length} 个）`);
