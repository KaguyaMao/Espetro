// check-preset-lottery.mjs — 是否有候选数上限？C 是从什么时候开始参与的？
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');
const PANEL = 'https://www.derpydoge.fun:20000';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const sleep = (ms) => new Promise((r) => setTimeout(r, ms));
function req(method, urlStr, headers, body, getCookie, binary) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, (res) => {
      const chunks = [];
      res.on('data', (c) => chunks.push(c));
      res.on('end', () => {
        const buf = Buffer.concat(chunks);
        resolve({ status: res.statusCode, buf, body: binary ? null : buf.toString('utf8'), cookie: getCookie ? (res.headers['set-cookie'] ?? []).map((l) => l.split(';')[0]).join('; ') : undefined, headers: res.headers });
      });
    });
    r.on('error', reject); if (body !== undefined) r.write(body); r.end();
  });
}
const lr = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: 'boy', password: 'boY1145141919810Fuck' }), true);
const token = String(JSON.parse(lr.body).data ?? '');
const cookie = lr.cookie ?? '';
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;

// 1) 最新日志：所有抽取记录
const t = await req('POST', `${PANEL}/api/files/download?${q}&file_name=${encodeURIComponent('logs/latest.log')}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
const data = JSON.parse(t.body).data ?? {};
const m = String(data.addr ?? '').match(/^wss?:\/\/([^/:]+)(:\d+)?/);
const daemonUrl = 'https://' + new URL(PANEL).hostname + (m?.[2] ?? '');
const dl = await req('GET', `${daemonUrl}/download/${data.password}/latest.log`, {}, undefined, false, true);
const text = dl.buf.toString('utf8');
const lines = text.split('\n');
console.log(`日志 ${dl.buf.length} B，${lines.length} 行`);
console.log('=== 所有「Points 预设已选择」记录 ===');
const draws = [];
for (const l of lines) {
  const mm = /^\[(\d{1,2}\S{0,4}\d{4} \d{2}:\d{2}:\d{2})\..*Points 预设已选择: (\S+) \(mode=(\S+), candidates=(\d+)\)/.exec(l);
  if (mm) { draws.push({ at: mm[1], file: mm[2], mode: mm[3], candidates: Number(mm[4]) }); }
}
for (const d of draws) console.log(`  ${d.at}  → ${d.file}  (mode=${d.mode}, 候选数=${d.candidates})`);
const distinct = [...new Set(draws.map((d) => d.file))];
console.log(`共 ${draws.length} 次抽取，出现过 ${distinct.length} 种: ${distinct.join(', ')}`);

// 2) 目录里的候选文件与修改时间
for (const dir of ['EsWorld/CREATE_PLUS/Points', 'EsWorld/CREATE_PLUS/EsConfig']) {
  const r = await req('GET', `${PANEL}/api/files/list?page=0&page_size=200&file_name=&target=${encodeURIComponent(dir)}&${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
  const items = JSON.parse(r.body).data?.items ?? [];
  console.log(`=== ${dir} ===`);
  for (const i of items) {
    console.log(`   ${String(i.name).padEnd(30)} size=${String(i.size ?? '').padStart(8)}  mtime=${i.mtime ?? i.time ?? '(无)'}  type=${i.type}`);
  }
  await sleep(2600);
}
// 3) 最后一次抽取之后，日志里还有没有新的战局/抽取
const lastDrawIdx = lines.map((l, i) => (/Points 预设已选择/.test(l) ? i : -1)).filter((i) => i >= 0).pop();
if (lastDrawIdx !== undefined) {
  console.log('=== 最后一次抽取之后的日志行（尾 10）===');
  for (const l of lines.slice(lastDrawIdx, lastDrawIdx + 10)) console.log('   ' + l.replace(/\s+/g, ' ').trim().slice(0, 180));
}
