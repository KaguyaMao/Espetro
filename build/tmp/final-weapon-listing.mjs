// final-weapon-listing.mjs — 列出编制在用武器的服务端数据，并标出被覆盖的提供者
// 用法: node final-weapon-listing.mjs <本地tacz目录> <编制目录> <已抓取目录(可含 manifest)>
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const path = require('path');
const https = require('https');

const taczDir = process.argv[2];
const factionDir = process.argv[3];
const pulledDir = process.argv[4];

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a1a800714185aec74849f1b0f9a6'.replace('647a1a80', '647a21e8');
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';

function lenientParse(text) {
  let out = '', inStr = false, esc = false;
  for (let i = 0; i < text.length; i++) {
    const c = text[i];
    if (inStr) { out += c; if (esc) esc = false; else if (c === '\\') esc = true; else if (c === '"') inStr = false; continue; }
    if (c === '"') { inStr = true; out += c; continue; }
    if (c === '/' && text[i + 1] === '/') { while (i < text.length && text[i] !== '\n') i++; out += '\n'; continue; }
    if (c === '/' && text[i + 1] === '*') { const e = text.indexOf('*/', i + 2); i = e < 0 ? text.length : e + 1; out += '\n'; continue; }
    out += c;
  }
  return JSON.parse(out.replace(/,(\s*[}\]])/g, '$1').replace(/:\s*\+(\d)/g, ': $1'));
}
const sleep = (ms) => new Promise(r => setTimeout(r, ms));
function req(method, urlStr, headers, body, getCookie) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, (res) => {
      let d = '';
      res.on('data', c => (d += c));
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
let { token, cookie } = await login();

// 1) 扫描本地所有枪包，找出每个 gun id 的所有提供者文件（含跨命名空间覆盖）
const providers = new Map(); // gunId -> [{pack, ns, serverRel, local}]
const packNamespaces = new Map(); // pack 名 -> gunpack.meta.json 的 namespace
for (const pack of fs.readdirSync(taczDir)) {
  const p = path.join(taczDir, pack);
  try { if (!fs.statSync(p).isDirectory()) continue; } catch { continue; }
  try { packNamespaces.set(pack, JSON.parse(fs.readFileSync(path.join(p, 'gunpack.meta.json'), 'utf8')).namespace); } catch { }
  const root = path.join(p, 'data');
  if (!fs.existsSync(root)) continue;
  for (const ns of fs.readdirSync(root)) {
    const gd = path.join(root, ns, 'data', 'guns');
    if (!fs.existsSync(gd)) continue;
    for (const f of fs.readdirSync(gd).filter(x => x.endsWith('.json'))) {
      const gun = f.replace(/_data\.json$/, '').replace(/\.json$/, '');
      const id = `${ns}:${gun}`;
      if (!providers.has(id)) providers.set(id, []);
      providers.get(id).push({ pack, ns, serverRel: `tacz/${pack}/data/${ns}/data/guns/${f}`, local: path.join(gd, f) });
    }
  }
}

// 2) 编制在用的枪
const guns = new Set();
for (const f of fs.readdirSync(factionDir).filter(x => x.endsWith('.json'))) {
  const j = JSON.parse(fs.readFileSync(path.join(factionDir, f), 'utf8'));
  for (const cls of Object.values(j.classes || {})) for (const va of Object.values(cls.variants || {}))
    for (const cmd of (va.commands || [])) { const m = String(cmd).match(/GunId:"([^"]+)"/); if (m) guns.add(m[1]); }
}

// 3) 抓取服务端对应文件（优先用已抓取副本），顺序：默认包优先、覆写包最后
const fetchCache = new Map();
async function serverJson(g) {
  if (fetchCache.has(g.serverRel)) return fetchCache.get(g.serverRel);
  const flat = (pulledDir && fs.existsSync(path.join(pulledDir, 'manifest.json')))
    ? JSON.parse(fs.readFileSync(path.join(pulledDir, 'manifest.json'), 'utf8')).find(m => m.serverRel === g.serverRel) : null;
  if (flat) {
    try { const j = lenientParse(fs.readFileSync(path.join(pulledDir, flat.file), 'utf8')); fetchCache.set(g.serverRel, j); return j; } catch { }
  }
  for (let a = 0; a < 4; a++) {
    const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
    const res = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: g.serverRel }));
    try {
      const d = String(JSON.parse(res.body).data ?? '');
      if (d.trim().startsWith('{')) { const j = lenientParse(d); fetchCache.set(g.serverRel, j); return j; }
    } catch { }
    await sleep(3500);
  }
  return null;
}

const fmt = (j) => {
  if (!j) return '（抓取失败）';
  const ex = j.bullet?.extra_damage || {};
  return `伤害 ${j.bullet?.damage} | 爆头×${ex.head_shot_multiplier} | 无视护甲 ${ex.armor_ignore} | 瞄准 ${j.aim_time}s | 扩散 站${j.inaccuracy?.stand}/移${j.inaccuracy?.move}/蹲${j.inaccuracy?.sneak}/卧${j.inaccuracy?.lie}/镜${j.inaccuracy?.aim} | RPM ${j.rpm} | 弹速 ${j.bullet?.speed} | 弹匣 ${j.ammo_amount} | 重量 ${j.weight} | 衰减 ${JSON.stringify(ex.damage_adjust ?? null).replace(/"/g, '')}`;
};

const out = [];
const table = [];
for (const gun of [...guns].sort()) {
  const list = providers.get(gun) || [];
  out.push(`\n### ${gun}`);
  if (!list.length) { out.push('   ⚠ 该枪在本机枪包里没有独立数据文件（可能复用其它枪的数据，见 index 的 data 字段）'); continue; }
  const gunNs = gun.split(':')[0];
  // 覆盖 = 由"别的命名空间的枪包"提供的同名文件
  const withRole = list.map(g => {
    const packNs = packNamespaces.get(g.pack) ?? g.ns;
    return { ...g, override: packNs !== gunNs };
  });
  const effective = withRole.find(g => g.override) ?? withRole[0];
  for (const g of withRole) {
    const j = await serverJson(g);
    out.push(`   [${g.pack}]  ${g.override ? '覆盖文件（附加包，通常后加载→生效）' : '本体/默认包'}`);
    out.push(`      ${fmt(j)}`);
    out.push(`      路径: ${g.serverRel}`);
  }
  const ej = await serverJson(effective);
  if (ej) table.push({ gun, pack: effective.pack, override: effective.override, j: ej });
}
fs.writeFileSync('weapon-listing-srv.md', out.join('\n'), 'utf8');

console.log('# 服务端编制武器数据（TaCZ）\n');
console.log('| 武器 | 数据来源包 | 伤害(自动/半自动) | 爆头× | 无视护甲 | 瞄准时间 | 扩散(站/移/蹲/卧/开镜) | RPM | 弹速 | 弹匣 | 重量 | 距离衰减 |');
console.log('|---|---|---|---|---|---|---|---|---|---|---|---|');
for (const t of table) {
  const j = t.j, ex = j.bullet?.extra_damage || {}, inc = j.inaccuracy || {}, semi = j.fire_mode_adjust?.semi || {};
  const dmg = semi.damage !== undefined ? `${j.bullet?.damage} / ${(j.bullet?.damage ?? 0) + semi.damage}` : `${j.bullet?.damage}`;
  const fall = Array.isArray(ex.damage_adjust) ? ex.damage_adjust.map(x => `${x.distance}m→${x.damage}`).join(' ') : '—';
  console.log(`| ${t.gun} | ${t.pack}${t.override ? ' ⚠覆盖' : ''} | ${dmg} | ${ex.head_shot_multiplier} | ${ex.armor_ignore} | ${j.aim_time}s | ${inc.stand}/${inc.move}/${inc.sneak}/${inc.lie}/${inc.aim} | ${j.rpm} | ${j.bullet?.speed} | ${j.ammo_amount} | ${j.weight} | ${fall} |`);
}
console.log('\n（详细文件路径见 weapon-listing-srv.md）');
