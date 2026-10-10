import fs from 'node:fs';
import zlib from 'node:zlib';

// 从 level.dat（gzip 压缩的 NBT）里按顺序提取可读字符串，用于查看 DataPacks.Enabled 的启用顺序
const f = process.argv[2];
const raw = zlib.gunzipSync(fs.readFileSync(f));
const strs = [];
let cur = '';
for (const b of raw) {
  if (b >= 0x20 && b < 0x7f) cur += String.fromCharCode(b);
  else { if (cur.length >= 4) strs.push(cur); cur = ''; }
}
if (cur.length >= 4) strs.push(cur);
const i = strs.findIndex(s => s === 'DataPacks');
console.log(`总字符串数=${strs.length}  DataPacks 位置=${i}`);
if (i >= 0) {
  // 打印 DataPacks 附近的字符串（Enabled / Disabled 列表顺序）
  const line = strs.slice(Math.max(0, i - 2), i + 200).map((s, k) => `  [${i - 2 + k}] ${s}`).join('\n');
  console.log(line);
  console.log('\n--- 含 file/ 或 pack 关键字的条目 ---');
  console.log(strs.filter(s => /^file\/|\.zip$|^va_|dragonrise/i.test(s)).join('\n') || '（无）');
} else {
  console.log('未找到 DataPacks，打印含 datapack 关键字的行:');
  console.log(strs.filter(s => /datapack|pack|vanilla|dragonrise|va_shotpos|fcp|kubejs/i.test(s)).slice(0, 40).join('\n'));
}
