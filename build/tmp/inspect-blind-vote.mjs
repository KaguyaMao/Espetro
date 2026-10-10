// inspect-blind-vote.mjs — 失明效果链路 + 指挥官投票名单
import fs from 'node:fs';
const files = [];
(function walk(d) { for (const e of fs.readdirSync(d, { withFileTypes: true })) { const p = d + '/' + e.name; if (e.isDirectory()) walk(p); else if (e.name.endsWith('.java')) files.push(p); } })('src/main/java');

console.log('===== 1) enforceSpectatorBlindness 实现与所有调用点 =====');
{
  const GSM = 'src/main/java/org/espetro/team/GameStateManager.java';
  const l = fs.readFileSync(GSM, 'utf8').split('\n');
  for (let i = 0; i < l.length; i++) {
    if (/enforceSpectatorBlindness|BLINDNESS|Blindness|removeEffect|addEffect|hasEffect/.test(l[i])) {
      console.log((i + 1) + ': ' + l[i].trim().slice(0, 150));
    }
  }
}
console.log('\n===== 1b) 其它文件里的失明调用 =====');
for (const f of files) {
  const l = fs.readFileSync(f, 'utf8').split('\n');
  for (let i = 0; i < l.length; i++) {
    if (/enforceSpectatorBlindness|MobEffects\.BLINDNESS/.test(l[i]) && !/GameStateManager\.java$/.test(f)) {
      console.log(f.replace('src/main/java/', '') + ':' + (i + 1) + '  ' + l[i].trim().slice(0, 140));
    }
  }
}
console.log('\n===== 2) 指挥官投票：候选/投票名单构造 =====');
for (const f of files) {
  if (!/Vote|vote/.test(f)) continue;
  const l = fs.readFileSync(f, 'utf8').split('\n');
  for (let i = 0; i < l.length; i++) {
    if (/isObserver|observers|candidates|voters|eligible|getPlayers\(\)|canVote|addVoter|投票名单|候选人/.test(l[i])) {
      console.log(f.replace('src/main/java/', '') + ':' + (i + 1) + '  ' + l[i].trim().slice(0, 140));
    }
  }
}
