// roundtrip-check.mjs — 检查 JSON 往返序列化是否能字节级还原（决定改写方式）
import fs from 'fs';

const files = process.argv.slice(2);
for (const f of files) {
  const raw = fs.readFileSync(f, 'utf8');
  const rt = JSON.stringify(JSON.parse(raw), null, 2) + '\n';
  const rtNoNl = JSON.stringify(JSON.parse(raw), null, 2);
  console.log(`${f.split('/').pop()}  size=${raw.length}  CRLF=${/\r\n/.test(raw)}  末尾换行=${raw.endsWith('\n')}`);
  console.log(`   往返(带\\n) 一致=${rt === raw}   往返(不带\\n) 一致=${rtNoNl === raw}`);
  if (rt !== raw && rtNoNl !== raw) {
    // 找出第一处差异
    for (let i = 0; i < Math.max(raw.length, rt.length); i++) {
      if (raw[i] !== rt[i]) {
        console.log(`   首个差异 @${i}: 原 '${JSON.stringify(raw.slice(Math.max(0, i - 30), i + 30))}' vs 新 '${JSON.stringify(rt.slice(Math.max(0, i - 30), i + 30))}'`);
        break;
      }
    }
  }
}
