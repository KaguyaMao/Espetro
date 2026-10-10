// inspect-build-phase.mjs — 工事建造的阶段门禁
import fs from 'node:fs';
const files = [];
(function walk(d) { for (const e of fs.readdirSync(d, { withFileTypes: true })) { const p = d + '/' + e.name; if (e.isDirectory()) walk(p); else if (e.name.endsWith('.java')) files.push(p); } })('src/main/java');

console.log('===== 1) allowsPhase / allowed_phases / 阶段限制点 =====');
for (const f of files) {
  const l = fs.readFileSync(f, 'utf8').split('\n');
  for (let i = 0; i < l.length; i++) {
    if (/allowsPhase|allowedPhases|allowed_phases|BATTLE\.name|GamePhase\.BATTLE|currentPhase ==|getCurrentPhase\(\)/.test(l[i])
        && /bastion|logistics|Fortification|network|client/.test(f)) {
      console.log(f.replace('src/main/java/', '') + ':' + (i + 1) + '  ' + l[i].trim().slice(0, 140));
    }
  }
}

console.log('\n===== 2) FortificationManager.validateCommon 实现 =====');
{
  const f = 'src/main/java/org/espetro/bastion/FortificationManager.java';
  const l = fs.readFileSync(f, 'utf8').split('\n');
  let s = -1;
  for (let i = 0; i < l.length; i++) if (/String validateCommon\(/.test(l[i])) { s = i; break; }
  if (s < 0) console.log('  (未找到)');
  else for (let i = s; i < s + 30; i++) { const x = (l[i] || '').replace(/\s+$/, ''); if (x.trim()) console.log((i + 1) + ': ' + x.slice(0, 150)); }
}

console.log('\n===== 3) LogisticsConfig 的 allowed_phases 解析 =====');
{
  const f = 'src/main/java/org/espetro/logistics/LogisticsConfig.java';
  const l = fs.readFileSync(f, 'utf8').split('\n');
  for (let i = 0; i < l.length; i++) {
    if (/allowedPhases|allowed_phases|allowsPhase|class RadioPlacementSettings/.test(l[i])) {
      console.log((i + 1) + ': ' + l[i].trim().slice(0, 150));
    }
  }
}

console.log('\n===== 4) 客户端：建造菜单是否受阶段限制 =====');
{
  const f = 'src/main/java/org/espetro/client/gui/AuraTipRadialController.java';
  const l = fs.readFileSync(f, 'utf8').split('\n');
  for (let i = 0; i < l.length; i++) {
    if (/GamePhase|Phase|BATTLE|DEPLOYING|cachedPhase/.test(l[i])) console.log((i + 1) + ': ' + l[i].trim().slice(0, 140));
  }
}
