// nbt-dump.mjs — 解析结构 NBT（gzip），列出 palette / blocks / entities 概况与方块实体 NBT 键
// 用法: node nbt-dump.mjs <本地 .nbt>
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const zlib = require('zlib');

const buf = zlib.gunzipSync(fs.readFileSync(process.argv[2]));
let p = 0;

function readTag(type) {
  switch (type) {
    case 1: { const v = buf.readInt8(p); p += 1; return v; }
    case 2: { const v = buf.readInt16BE(p); p += 2; return v; }
    case 3: { const v = buf.readInt32BE(p); p += 4; return v; }
    case 4: { const v = buf.readBigInt64BE(p); p += 8; return Number(v); }
    case 5: { const v = buf.readFloatBE(p); p += 4; return v; }
    case 6: { const v = buf.readDoubleBE(p); p += 8; return v; }
    case 7: { const len = buf.readInt32BE(p); p += 4; const v = buf.subarray(p, p + len); p += len; return [...v].map(x => x > 127 ? x - 256 : x); }
    case 8: { const len = buf.readUInt16BE(p); p += 2; const v = buf.toString('utf8', p, p + len); p += len; return v; }
    case 9: {
      const itemType = buf.readUInt8(p); p += 1;
      const len = buf.readInt32BE(p); p += 4;
      const arr = [];
      for (let i = 0; i < len; i++) arr.push(readPayload(itemType));
      return arr;
    }
    case 10: return readCompound();
    case 11: { const len = buf.readInt32BE(p); p += 4; const arr = []; for (let i = 0; i < len; i++) { arr.push(buf.readInt32BE(p)); p += 4; } return arr; }
    case 12: { const len = buf.readInt32BE(p); p += 4; const arr = []; for (let i = 0; i < len; i++) { arr.push(Number(buf.readBigInt64BE(p))); p += 8; } return arr; }
    default: throw new Error('unknown tag type ' + type + ' at ' + p);
  }
}
function readPayload(type) { return readTag(type); }
function readCompound() {
  const obj = {};
  for (;;) {
    const type = buf.readUInt8(p); p += 1;
    if (type === 0) return obj;
    const nameLen = buf.readUInt16BE(p); p += 2;
    const name = buf.toString('utf8', p, p + nameLen); p += nameLen;
    obj[name] = readTag(type);
  }
}
if (buf.readUInt8(p) !== 10) throw new Error('root is not a compound');
p += 1;
const nameLen = buf.readUInt16BE(p); p += 2;
p += nameLen;
const root = readCompound();

console.log('size =', JSON.stringify(root.size));
const palettes = root.palettes || [root.palette];
console.log('palettes =', palettes.length, 'palette[0] 条目 =', palettes[0].length);
const blocks = root.blocks || [];
console.log('blocks =', blocks.length, 'entities =', (root.entities || []).length);
const byId = {};
for (const b of blocks) {
  const state = palettes[0][b.state];
  const id = state && state.Name;
  byId[id] = (byId[id] || 0) + 1;
}
console.log('--- 方块统计 ---');
for (const [id, n] of Object.entries(byId).sort((a, b) => b[1] - a[1])) console.log(String(n).padStart(4), id);
console.log('--- 带 NBT 的方块（键名）---');
const seen = new Map();
for (const b of blocks) {
  if (!b.nbt) continue;
  const state = palettes[0][b.state];
  const id = (b.nbt.id || (state && state.Name));
  const keys = Object.keys(b.nbt).filter(k => !['id', 'x', 'y', 'z'].includes(k));
  const sig = id + ' -> ' + JSON.stringify(keys);
  seen.set(sig, (seen.get(sig) || 0) + 1);
}
for (const [sig, n] of seen) console.log(String(n).padStart(4), sig);
console.log('--- 实体 ---');
for (const e of (root.entities || [])) {
  const nbt = e.nbt || {};
  console.log('  ', nbt.id, 'pos=' + JSON.stringify(e.pos), 'keys=' + JSON.stringify(Object.keys(nbt).filter(k => !['id', 'Pos', 'UUID'].includes(k))));
}
