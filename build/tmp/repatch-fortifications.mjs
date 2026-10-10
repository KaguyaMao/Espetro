// repatch-fortifications.mjs — 以线上最新文件为准重做电台 usable_by 修改
import fs from 'node:fs';
const online = fs.readFileSync('build/tmp/fortifications-online-before.json', 'utf8');
const mine = fs.readFileSync('build/tmp/fortifications-before.json', 'utf8');
console.log('线上(现在)=' + online.length + '  我先前抓的=' + mine.length);
console.log('线上行数=' + online.split('\n').length + '  先前=' + mine.split('\n').length);
console.log('线上含 CRLF=' + online.includes('\r\n') + '  先前含 CRLF=' + mine.includes('\r\n'));
// 找出第一处差异
let i = 0;
while (i < Math.min(online.length, mine.length) && online[i] === mine[i]) i++;
console.log('首处差异位置=' + i);
console.log('  线上: ' + JSON.stringify(online.slice(Math.max(0, i - 60), i + 60)));
console.log('  先前: ' + JSON.stringify(mine.slice(Math.max(0, i - 60), i + 60)));

// 以线上版本重做修改
const start = online.indexOf('"id": "espetro:radio"');
const end = online.indexOf('"id": "espetro:hab"', start);
if (start < 0 || end < 0) { console.error('❌ 定位失败'); process.exit(1); }
const seg = online.slice(start, end);
const out = seg.replace(/"squad_leader",(\s*)"fireteam_leader"/, '"squad_leader"$1');
if (out === seg) { console.error('❌ 未匹配 fireteam_leader'); process.exit(1); }
const result = online.slice(0, start) + out + online.slice(end);
fs.writeFileSync('build/tmp/fortifications-after.json', result, 'utf8');
const j = JSON.parse(result);
const radio = j.fortifications.find((d) => d.id === 'espetro:radio');
const others = j.fortifications.filter((d) => d.id !== 'espetro:radio');
const othersOk = others.every((d) => (d.requirements?.usable_by ?? []).includes('fireteam_leader'));
console.log('\n新文件 ' + result.length + ' 字符');
console.log('radio.usable_by = ' + JSON.stringify(radio.requirements.usable_by));
console.log('其余 ' + others.length + ' 个定义仍含 fireteam_leader: ' + (othersOk ? '是 ✓' : '否 ❌'));
if (radio.requirements.usable_by.includes('fireteam_leader') || !othersOk) process.exit(1);
