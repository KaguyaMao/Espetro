// fix-humveem2.mjs — 修正 fcp 内置 humveem2.json 的 "OBB": [w 语法错误，生成 kubejs 覆盖文件
import fs from 'node:fs';
import path from 'node:path';

const SRC = 'obb-all/fcp-1.1.1-更改特化版.jar__data_fcp_sbw_vehicles_humveem2.json';
const OUT = 'kjs-edit/data/fcp/sbw/vehicles/humveem2.json';

const text = fs.readFileSync(SRC, 'utf8');
console.log('=== 原文前 9 行 ===');
text.split('\n').slice(0, 9).forEach((l, i) => console.log('L' + (i + 1) + ': ' + l));

const fixed = text.replace('"OBB": [w', '"OBB": [');
if (fixed === text) {
  console.log('!! 未找到 "OBB": [w，未做修改');
  process.exit(1);
}
fs.mkdirSync(path.dirname(OUT), { recursive: true });
fs.writeFileSync(OUT, fixed, 'utf8');
console.log(`\n已生成修正版 → ${OUT} (${fixed.length} B)`);
console.log('=== 修正后前 8 行 ===');
fixed.split('\n').slice(0, 8).forEach((l, i) => console.log('L' + (i + 1) + ': ' + l));

// 自检：用 Gson 同款调用（宽松）验证修好的文件能解析
try {
  JSON.parse(fixed.replace(/,(\s*[}\]])/g, '$1')); // 粗检（宽松 JSON 会容忍尾逗号）
  console.log('\n本地粗检通过（严格 JSON.parse，已去掉尾逗号）');
} catch (e) {
  console.log('\n本地粗检失败: ' + e.message);
}
