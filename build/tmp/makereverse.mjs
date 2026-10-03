import { readFileSync, writeFileSync } from 'fs';
const lines = readFileSync('D:/minecraft/modp/Espetro/build/createMcpToSrg/output.tsrg', 'utf8').split('\n');
const out = ['tsrg2 right left'];
for (const line of lines) {
  if (!line.trim() || line.startsWith('tsrg2')) continue;
  if (line.startsWith('\t')) {
    const parts = line.trim().split(/\s+/);
    if (/^\d+$/.test(parts[0])) continue; // 参数行跳过（CFR 不依赖参数名）
    if (parts.length === 3) out.push('\t' + parts[2] + ' ' + parts[1] + ' ' + parts[0]);
    else if (parts.length === 2) out.push('\t' + parts[1] + ' ' + parts[0]);
  } else {
    const parts = line.trim().split(/\s+/);
    if (parts.length === 2) out.push(parts[1] + ' ' + parts[0]);
  }
}
writeFileSync('D:/minecraft/modp/Espetro/build/tmp/reverse.tsrg', out.join('\n'), 'utf8');
console.log('lines=' + out.length);
