// inspect-blind-vote2.mjs — 失明的施加条件/周期刷新 + 开战移除 + 指挥官投票名单
import fs from 'node:fs';
const dump = (p, a, b, label) => {
  console.log('===== ' + label + ' =====');
  const l = fs.readFileSync(p, 'utf8').split('\n');
  for (let i = a - 1; i < b && i < l.length; i++) {
    const s = (l[i] || '').replace(/\s+$/, '');
    if (s.trim()) console.log((i + 1) + ': ' + s.slice(0, 165));
  }
};
const GSM = 'src/main/java/org/espetro/team/GameStateManager.java';
dump(GSM, 286, 320, 'enforceSpectatorBlindness 实现');
dump(GSM, 1105, 1130, '开战时的失明移除');
dump('src/main/java/org/espetro/bastion/BastionEventHandler.java', 815, 840, '周期刷新失明的调用点');
dump('src/main/java/org/espetro/team/VoteManager.java', 55, 95, '投票初始化名单');
dump('src/main/java/org/espetro/team/VoteManager.java', 130, 150, '投票资格校验');
