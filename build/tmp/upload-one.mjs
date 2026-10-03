// upload-one.mjs — 上传单个文件到服务器指定目录（用于脚本/数据文件）
// 用法: node upload-one.mjs <本地文件> <目标目录> [目标文件名]
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const SRC = process.argv[2];
const DIR = process.argv[3];
const NAME = process.argv[4] ?? SRC.split(/[\\/]/).pop();
const sleep = ms => new Promise(r => setTimeout(r, ms));

function req(method, urlStr, headers, body, getCookie) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, res => {
      let d = ''; res.on('data', c => (d += c));
      res.on('end', () => resolve({ status: res.statusCode, body: d, cookie: getCookie ? (res.headers['set-cookie'] ?? []).map(l => l.split(';')[0]).join('; ') : undefined }));
    });
    r.on('error', reject);
    if (body !== undefined) r.write(body);
    r.end();
  });
}
async function login() {
  const r = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }), true);
  return { token: String(JSON.parse(r.body).data ?? ''), cookie: r.cookie ?? '' };
}
const ctx = await login();
let ok = false;
for (let a = 1; a <= 5 && !ok; a++) {
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&upload_dir=${encodeURIComponent(DIR)}&token=${ctx.token}`;
  const t = await req('POST', `${PANEL}/api/files/upload?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': ctx.cookie });
  let data = {};
  try { data = JSON.parse(t.body).data ?? {}; } catch { }
  const m = String(data.addr ?? '').match(/^wss?:\/\/([^/:]+)(:\d+)?/);
  const daemonUrl = 'https://' + new URL(PANEL).hostname + (m?.[2] ?? '');
  if (!data.password) { console.log(`创建任务失败(${a}): ${t.body.slice(0, 80)}`); await sleep(2500); continue; }
  const buf = fs.readFileSync(SRC);
  const boundary = '----DSHUpload' + Date.now().toString(16);
  const head = Buffer.from(`--${boundary}\r\nContent-Disposition: form-data; name="file"; filename="${NAME}"\r\nContent-Type: application/octet-stream\r\n\r\n`);
  const tail = Buffer.from(`\r\n--${boundary}--\r\n`);
  const body = Buffer.concat([head, buf, tail]);
  const res = await req('POST', `${daemonUrl}/upload/${data.password}?overwrite=true`, {
    'X-Requested-With': 'XMLHttpRequest',
    'Content-Type': `multipart/form-data; boundary=${boundary}`,
    'Content-Length': String(body.length)
  }, body);
  ok = res.status === 200 && res.body.trim() === 'OK';
  console.log(`${ok ? 'OK ' : '重试'} ${DIR}/${NAME} (${buf.length} B) ${ok ? '' : res.status + ' ' + res.body.slice(0, 80)}`);
  if (!ok) await sleep(2500);
}
process.exit(ok ? 0 : 1);
