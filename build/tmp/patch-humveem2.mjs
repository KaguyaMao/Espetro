// patch-humveem2.mjs — 修正 fcp jar 内损坏的 humveem2.json（仅去掉 OBB 数组开头多余的 w）
import fs from 'node:fs';
const SRC = 'D:/minecraft/modp/Espetro/build/tmp/modjar-veh/fcp/data/fcp/sbw/vehicles/humveem2.json';
const OUT = 'D:/minecraft/modp/Espetro/build/tmp/kjs-edit/data/fcp/sbw/vehicles/humveem2.json';

const text = fs.readFileSync(SRC, 'utf8');
const probe = text.match(/"OBB"\s*:\s*\[\s*\w/);
const fixed = text.replace(/"OBB"\s*:\s*\[\s*\w/, '"OBB": [');
fs.mkdirSync('D:/minecraft/modp/Espetro/build/tmp/kjs-edit/data/fcp/sbw/vehicles', { recursive: true });
fs.writeFileSync(OUT, fixed, 'utf8');

const lenient = (t) => JSON.parse(t.replace(/,(\s*[}\]])/g, '$1'));
const j = lenient(fixed);
console.log('修复点: ' + JSON.stringify(probe ? probe[0] : '(未匹配)') + ' → "OBB": [');
console.log('校验: ID=' + j.ID + ' MaxHealth=' + j.MaxHealth + ' OBB条目=' + (j.OBB ? j.OBB.length : 'n/a'));
console.log('字节: ' + text.length + ' → ' + fixed.length);
