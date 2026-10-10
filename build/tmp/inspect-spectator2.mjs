// inspect-spectator2.mjs — 找玩家名参数、投票发包点、自动分配点、观战者排除点
import fs from 'node:fs';
const files = [];
function walk(d) {
  for (const e of fs.readdirSync(d, { withFileTypes: true })) {
    const p = d + '/' + e.name;
    if (e.isDirectory()) walk(p);
    else if (e.name.endsWith('.java')) files.push(p);
  }
}
walk('src/main/java');

console.log('===== 1) 所有"玩家名"字符串参数（需换 EntityArgument.player）=====');
for (const f of files) {
  const l = fs.readFileSync(f, 'utf8').split('\n');
  for (let i = 0; i < l.length; i++) {
    const s = l[i];
    if (/getPlayerByName|StringArgumentType\.string\(\)/.test(s) && /player|Player|玩家/.test(l.slice(Math.max(0, i - 3), i + 3).join(' '))) {
      console.log(f.replace('src/main/java/', '') + ':' + (i + 1) + '  ' + s.trim().slice(0, 130));
    }
  }
}

console.log('\n===== 2) 投票/揭晓界面发包点（服务端）=====');
for (const f of files) {
  const l = fs.readFileSync(f, 'utf8').split('\n');
  for (let i = 0; i < l.length; i++) {
    if (/(sendMapVote|sendCommanderVote|sendFactionSelection|sendFactionReveal|sendTeamSelectState|sendMapReveal|queueUnifiedDeployScreen|sendCloseModScreens)/.test(l[i]) && /void |public static|private static/.test(l[i])) {
      console.log(f.replace('src/main/java/', '') + ':' + (i + 1) + '  ' + l[i].trim().slice(0, 140));
    }
  }
}

console.log('\n===== 3) 自动分配队伍 / 参战名单构建点 =====');
const gsm = 'src/main/java/org/espetro/team/GameStateManager.java';
{
  const l = fs.readFileSync(gsm, 'utf8').split('\n');
  for (let i = 0; i < l.length; i++) {
    if (/自动分配|randomAssign|assignTeams|shuffle|isObserver|observers\.|getPlayers\(\)|waitingForTeam/.test(l[i])) {
      console.log((i + 1) + ': ' + l[i].trim().slice(0, 140));
    }
  }
}
