// tools/vehicle-steering-pack.mjs — 重新生成「载具转向速度减半」服务端数据包
//
// 背景：SBW 系载具数据走数据包（data/<ns>/sbw/vehicles/<id>.json，且 synced=true），
// 因此用服务端数据包覆盖是最省事、纯服务端生效的做法。本脚本从客户端 mods 里的
// superbwarfare / dragonrise_reforge / vvp / fcp / sbw_addition 等 jar 里抽出载具 JSON，
// 把 EngineInfo.SteeringSpeed 减半，打包成 world/datapacks/espetro_steering.zip。
//
// 用法：
//   node tools/vehicle-steering-pack.mjs [--mods <客户端mods目录>] [--out <输出zip>]
//   上传：node <mcsm-tools>/mcsm.mjs upload <zip> world/datapacks espetro_steering.zip
//   生效：服务端 /reload
//
// 说明：
// - 只改 EngineType 为 Track / Wheel / Ship / AirShip / WheelChair 的载具（Ground 类）。
//   Aircraft / Helicopter / Tom6 用的是 YawSpeed/PitchSpeed/RollSpeed，不在本脚本范围。
// - EXCLUDE 里的载具保持原速；OVERLAY 指向已有服务端覆盖包（如 sbw_shotpos）时，
//   以其中的内容为基底，避免两个包互相覆盖丢改动。
import fs from 'node:fs';
import path from 'node:path';
import zlib from 'node:zlib';

const DEFAULT_MODS = 'D:/minecraft/squadMC预发布测试/versions/Squad预发布测试/mods';
const DEFAULT_OUT = 'build/tmp/steer/espetro_steering.zip';
const OVERLAY = 'build/tmp/steer/shotpos'; // 可选：已下载的服务端 sbw_shotpos 覆盖文件
const EXCLUDE = new Set(['zbd04a', 'zsd89a']); // 不减半的载具 id
// jar 处理顺序：后面的覆盖前面的（近似 mod 加载优先级）
const JAR_ORDER = ['dragonrise_reforge', 'fcp', 'vvp', 'sbw_addition', 'superbwarfare'];

const argOf = (name, dflt) => {
  const i = process.argv.indexOf(name);
  return i > 0 ? process.argv[i + 1] : dflt;
};
const MODS = argOf('--mods', DEFAULT_MODS);
const OUT = argOf('--out', DEFAULT_OUT);

// ---- 极简 zip 读取（中央目录 + inflateRaw） ----
function readZip(buf) {
  const eocd = (() => {
    for (let i = buf.length - 22; i >= Math.max(0, buf.length - 65557); i--) if (buf.readUInt32LE(i) === 0x06054b50) return i;
    throw new Error('EOCD not found');
  })();
  const count = buf.readUInt16LE(eocd + 10);
  let off = buf.readUInt32LE(eocd + 16);
  const out = [];
  for (let n = 0; n < count; n++) {
    if (buf.readUInt32LE(off) !== 0x02014b50) throw new Error('bad central dir');
    const method = buf.readUInt16LE(off + 10);
    const csize = buf.readUInt32LE(off + 20);
    const nlen = buf.readUInt16LE(off + 28);
    const elen = buf.readUInt16LE(off + 30);
    const clen = buf.readUInt16LE(off + 32);
    const lho = buf.readUInt32LE(off + 42);
    const name = buf.toString('utf8', off + 46, off + 46 + nlen);
    off += 46 + nlen + elen + clen;
    if (!name.endsWith('.json')) continue;
    const lnlen = buf.readUInt16LE(lho + 26);
    const lelen = buf.readUInt16LE(lho + 28);
    const start = lho + 30 + lnlen + lelen;
    const raw = buf.subarray(start, start + csize);
    out.push({ name, data: method === 0 ? raw : zlib.inflateRawSync(raw) });
  }
  return out;
}

// ---- 极简 zip 写出（deflateRaw） ----
function writeZip(entries) {
  const locals = [];
  const centrals = [];
  let offset = 0;
  for (const { name, data } of entries) {
    const nameBuf = Buffer.from(name, 'utf8');
    const comp = zlib.deflateRawSync(data);
    const crc = zlib.crc32 ? zlib.crc32(data) : crc32fallback(data);
    const lh = Buffer.alloc(30);
    lh.writeUInt32LE(0x04034b50, 0); lh.writeUInt16LE(20, 4); lh.writeUInt16LE(0x0800, 6);
    lh.writeUInt16LE(8, 8); lh.writeUInt16LE(0, 10); lh.writeUInt16LE(0x21, 12);
    lh.writeUInt32LE(crc, 14); lh.writeUInt32LE(comp.length, 18); lh.writeUInt32LE(data.length, 22);
    lh.writeUInt16LE(nameBuf.length, 26); lh.writeUInt16LE(0, 28);
    locals.push(lh, nameBuf, comp);
    const ch = Buffer.alloc(46);
    ch.writeUInt32LE(0x02014b50, 0); ch.writeUInt16LE(20, 4); ch.writeUInt16LE(20, 6); ch.writeUInt16LE(0x0800, 8);
    ch.writeUInt16LE(8, 10); ch.writeUInt16LE(0, 12); ch.writeUInt16LE(0x21, 14);
    ch.writeUInt32LE(crc, 16); ch.writeUInt32LE(comp.length, 20); ch.writeUInt32LE(data.length, 24);
    ch.writeUInt16LE(nameBuf.length, 28); ch.writeUInt32LE(offset, 42);
    centrals.push(ch, nameBuf);
    offset += 30 + nameBuf.length + comp.length;
  }
  const cd = Buffer.concat(centrals);
  const eocd = Buffer.alloc(22);
  eocd.writeUInt32LE(0x06054b50, 0); eocd.writeUInt16LE(entries.length, 8); eocd.writeUInt16LE(entries.length, 10);
  eocd.writeUInt32LE(cd.length, 12); eocd.writeUInt32LE(offset, 16);
  return Buffer.concat([...locals, cd, eocd]);
}
function crc32fallback(buf) {
  let c, crc = 0xffffffff;
  for (let i = 0; i < buf.length; i++) {
    c = (crc ^ buf[i]) & 0xff;
    for (let k = 0; k < 8; k++) c = c & 1 ? 0xedb88320 ^ (c >>> 1) : c >>> 1;
    crc = (crc >>> 8) ^ c;
  }
  return (crc ^ 0xffffffff) >>> 0;
}

// ---- 收集载具数据：jar 内 data/<ns>/sbw/vehicles/*.json ----
const jars = fs.readdirSync(MODS).filter((f) => f.endsWith('.jar') && JAR_ORDER.some((k) => f.includes(k)));
jars.sort((a, b) => JAR_ORDER.findIndex((k) => a.includes(k)) - JAR_ORDER.findIndex((k) => b.includes(k)));
const files = new Map();
for (const jar of jars) {
  const buf = fs.readFileSync(path.join(MODS, jar));
  let entries;
  try { entries = readZip(buf); } catch (e) { console.log(`跳过 ${jar}: ${e.message}`); continue; }
  let n = 0;
  for (const { name, data } of entries) {
    if (!/^data\/[^/]+\/sbw\/vehicles\/[^/]+\.json$/.test(name)) continue;
    files.set(name, data); // 后处理的 jar 覆盖前者
    n++;
  }
  console.log(`${jar}: ${n} 个载具 json`);
}
if (OVERLAY && fs.existsSync(OVERLAY)) {
  for (const f of fs.readdirSync(OVERLAY)) {
    if (!f.endsWith('.json')) continue;
    files.set(`data/dragonrise_reforge/sbw/vehicles/${f}`, fs.readFileSync(path.join(OVERLAY, f)));
  }
  console.log(`叠加服务端覆盖层: ${OVERLAY}`);
}

// ---- 减半 SteeringSpeed ----
const halve = (o, c = { n: 0 }) => {
  if (Array.isArray(o)) { o.forEach((v) => halve(v, c)); return c; }
  if (o && typeof o === 'object') for (const k of Object.keys(o)) {
    if (k === 'SteeringSpeed' && typeof o[k] === 'number') { o[k] /= 2; c.n++; } else halve(o[k], c);
  }
  return c;
};
const out = [{ name: 'pack.mcmeta', data: Buffer.from(JSON.stringify({ pack: { pack_format: 15, description: 'EsPetro: vehicle steering speed x0.5' } }, null, 2)) }];
const stat = { changed: 0, skipped: 0, noField: 0, broken: [] };
for (const [name, buf] of [...files.entries()].sort((a, b) => a[0].localeCompare(b[0]))) {
  const id = path.basename(name, '.json');
  let data;
  try { data = JSON.parse(buf.toString('utf8').replace(/^\uFEFF/, '')); } catch { stat.broken.push(name); continue; }
  if (EXCLUDE.has(id)) { stat.skipped++; continue; }
  if (halve(data).n === 0) { stat.noField++; continue; }
  out.push({ name, data: Buffer.from(JSON.stringify(data, null, 2)) });
  stat.changed++;
}
fs.mkdirSync(path.dirname(OUT), { recursive: true });
fs.writeFileSync(OUT, writeZip(out));
console.log(`写入 ${OUT}: ${out.length} 条目, ${fs.statSync(OUT).size} B`);
console.log(`改动 ${stat.changed} / 排除 ${stat.skipped} / 无转向字段 ${stat.noField} / 源文件损坏 ${stat.broken.length}`);
if (stat.broken.length) console.log('源 JSON 损坏（未入包，建议报告给模组作者）: ' + stat.broken.join(', '));
