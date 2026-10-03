// preset-odds.mjs — ①统计（含归档日志）所有抽取记录 ②模拟 new Random(seed).nextInt(3) 的分布与命中概率
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const zlib = require('zlib');
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
      res.on('end', () => { const buf = Buffer.concat(chunks); resolve({ status: res.statusCode, buf, body: binary ? null : buf.toString('utf8'), cookie: getCookie ? (res.headers['set-cookie'] ?? []).map((l) => l.split(';')[0]).join('; ') : undefined }); });
    });
    r.on('error', reject); if (body !== undefined) r.write(body); r.end();
  });
}
const lr = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: 'boy', password: 'boY1145141919810Fuck' }), true);
const token = String(JSON.parse(lr.body).data ?? '');
const cookie = lr.cookie ?? '';
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
async function download(rel, dest) {
  for (let a = 0; a < 4; a++) {
    const t = await req('POST', `${PANEL}/api/files/download?${q}&file_name=${encodeURIComponent(rel)}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
    let data = {};
    try { data = JSON.parse(t.body).data ?? {}; } catch { }
    if (!data.password) { await sleep(2500); continue; }
    const m = String(data.addr ?? '').match(/^wss?:\/\/([^/:]+)(:\d+)?/);
    const daemonUrl = 'https://' + new URL(PANEL).hostname + (m?.[2] ?? '');
    const res = await req('GET', `${daemonUrl}/download/${data.password}/${encodeURIComponent(rel.split('/').pop())}`, {}, undefined, false, true);
    if (res.status === 200 && res.buf.length > 100) { fs.writeFileSync(dest, res.buf); return res.buf; }
    await sleep(2500);
  }
  return null;
}
// 1) 列出 logs 里的归档
const r = await req('GET', `${PANEL}/api/files/list?page=0&page_size=500&file_name=&target=${encodeURIComponent('logs')}&${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
const items = (JSON.parse(r.body).data?.items ?? []).filter((i) => String(i.name).endsWith('.log.gz') && /2026-09-30|2026-10-01/.test(String(i.name)));
console.log(`今日归档日志 ${items.length} 个: ${items.map((i) => i.name).join(', ')}`);
fs.mkdirSync('preset-logs', { recursive: true });
const allDraws = [];
const scan = (text, tag) => {
  for (const l of text.split('\n')) {
    const m = /Points 预设已选择: (\S+) \(mode=(\S+), candidates=(\d+)\)/.exec(l);
    const t = /^\[(\d{1,2}\S{0,4}\d{4} \d{2}:\d{2}:\d{2})/.exec(l);
    if (m) allDraws.push({ at: t ? t[1] : '?', file: m[1], mode: m[2], candidates: Number(m[3]), src: tag });
  }
};
for (const it of items.slice(0, 8)) {
  const dest = `preset-logs/${it.name}`;
  const buf = await download(`logs/${it.name}`, dest);
  if (!buf) { console.log(`  ${it.name} 下载失败`); continue; }
  try { scan(zlib.gunzipSync(buf).toString('utf8'), it.name); } catch (e) { console.log(`  ${it.name} 解压失败 ${e.message}`); }
  await sleep(1500);
}
// 当前 latest.log
const cur = await download('logs/latest.log', 'preset-logs/latest.log');
if (cur) scan(cur.toString('utf8'), 'latest.log');
console.log('\n=== 全部抽取记录（按时间）===');
for (const d of allDraws) console.log(`  ${d.at}  ${d.file}  (candidates=${d.candidates})  [${d.src}]`);
const byFile = {};
for (const d of allDraws) byFile[d.file] = (byFile[d.file] ?? 0) + 1;
console.log(`总计 ${allDraws.length} 次: ${JSON.stringify(byFile)}`);

// 2) 概率模拟：候选 3 个（文件名排序后下标 0/1/2）
console.log('\n=== 模拟 new Random(seed).nextInt(3)（20 万随机种子）===');
const counts = [0, 0, 0];
for (let i = 0; i < 200000; i++) {
  const seed = Math.floor((Math.random() - 0.5) * 2 * 9007199254740991);
  counts[new javaLikeRandom(seed).nextInt(3)]++;
}
console.log(`  下标分布: ${counts.map((c, i) => i + ':' + (c / 2000).toFixed(2) + '%').join('  ')}  （均匀 ≈33.33%）`);
console.log('\n=== 连续 N 局都没抽到某一套的概率 ===');
for (let n = 1; n <= 10; n++) {
  const p = Math.pow(2 / 3, n);
  console.log(`  ${n} 局: ${(p * 100).toFixed(1)}%${p <= 0.05 ? '   ← 到这一步才值得怀疑' : ''}`);
}
// 用与 java.util.Random 相同的 LCG 复现（48 位种子）
function javaLikeRandom(seed) {
  let s = BigInt(Math.trunc(seed)) & 0xFFFFFFFFFFFFn;
  const next = (bits) => { s = (s * 0x5DEECE66Dn + 0xBn) & 0xFFFFFFFFFFFFn; return Number(s >> BigInt(48 - bits)); };
  return {
    nextInt(bound) {
      if ((bound & -bound) === bound) return Number((BigInt(bound) * BigInt(next(31))) >> 31n);
      let bits, val;
      do { bits = next(31); val = bits % bound; } while (bits - val + (bound - 1) < 0);
      return val;
    },
  };
}
