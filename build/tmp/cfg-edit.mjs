// cfg-edit.mjs — 用 Node 显式 UTF-8 编辑 espetro-server.toml 的 nativeVehicles
// 用法: node cfg-edit.mjs <源文件> <输出文件> <空|测试>
import fs from 'node:fs';

const [src, out, mode] = process.argv.slice(2);
const text = fs.readFileSync(src, 'utf8');               // 显式 UTF-8，绝不用 PowerShell 读写
const line = 'nativeVehicles =';
const m = text.match(/^[ \t]*nativeVehicles[ \t]*=.*$/m);
if (!m) { console.error('未找到 nativeVehicles 行'); process.exit(1); }
const current = m[0];
const value = (mode === '测试' ? 'nativeVehicles = ["fcp:bmp2"]' : 'nativeVehicles = []');
const fixed = text.replace(/^[ \t]*nativeVehicles[ \t]*=.*$/m, value);
fs.writeFileSync(out, fixed, 'utf8');
console.log('原行: ' + current);
console.log('新行: ' + value);
console.log('字节: ' + Buffer.byteLength(text, 'utf8') + ' → ' + Buffer.byteLength(fixed, 'utf8'));
