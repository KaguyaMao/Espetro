// analyze-client-latest.mjs — 分析客户端最新会话日志（连接/断线/建队/运行期异常）
import fs from 'node:fs';

const f = process.argv[2] ?? 'client-latest.log';
const lines = fs.readFileSync(f, 'utf8').split('\n');
const ts = (l) => {
  const m = /^\[(\d{1,2})\S{0,4}(\d{4}) (\d{2}:\d{2}:\d{2})\./.exec(l);
  return m ? m[3] : null;
};

console.log('=== 连接 / 断线 / 小队 相关 ===');
const reConn = /Connecting to|lost connection|Disconnected|Timed out|连接中断|小队|squad|SquadCommand|/i;
for (const x of lines) {
  if (/Squad预发布测试|Squad%e9/.test(x)) continue;
  if (!/Connecting to|lost connection|Disconnected|Timed out|连接中断|小队|squad|SquadCommand/i.test(x)) continue;
  const s = x.replace(/\s+/g, ' ').trim();
  console.log(s.length > 230 ? s.slice(0, 230) + ' …' : s);
}

console.log('\n=== 运行期异常（带时间戳、排除启动期噪音）===');
let n = 0;
for (let i = 0; i < lines.length; i++) {
  const x = lines[i];
  if (!ts(x)) continue;
  if (!/Exception|Error:|Caused by/.test(x)) continue;
  if (/mixin\/\]|ClassNotFound|ModFileParser|JarSelector|RuntimeDistCleaner|NoSuchMethodException: net.minecraft.nbt/.test(x)) continue;
  n++;
  console.log(`行${i + 1}: ` + x.replace(/\s+/g, ' ').trim().slice(0, 220));
  for (let k = 1; k <= 3; k++) if (lines[i + k]?.trim().startsWith('at ')) console.log('        ' + lines[i + k].trim().slice(0, 180));
  if (n >= 20) break;
}
if (!n) console.log('（无）');

console.log('\n=== 末尾 60 行 ===');
for (const x of lines.slice(-60)) { const s = x.replace(/\s+/g, ' ').trim(); if (s) console.log(s.slice(0, 200)); }
