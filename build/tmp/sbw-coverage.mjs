// sbw-coverage.mjs — 编制配置引用的载具实体 与 kubejs 可改数据 的覆盖情况
import fs from 'fs';

const CFG = 'D:/minecraft/modp/Espetro/build/tmp/srv-final/';       // 服务器现有 12 个编制
const KJS = ['fcp', 'dr'].map((n) => ['fcp', 'dr'].includes(n) ? [n, `D:/minecraft/modp/Espetro/build/tmp/sbw-all/${n}/`] : null).filter(Boolean);

// kubejs 覆盖的实体 ID（文件名 -> 数据里的 ID）
const covered = new Map();
for (const [tag, dir] of KJS) {
  for (const f of fs.readdirSync(dir).filter((x) => x.endsWith('.json'))) {
    try {
      const j = JSON.parse(fs.readFileSync(dir + f, 'utf8'));
      if (j.ID) covered.set(j.ID, `${tag}/${f}`);
    } catch { /* ignore */ }
  }
}

const used = new Map();
for (const f of fs.readdirSync(CFG).filter((x) => x.endsWith('.json'))) {
  const j = JSON.parse(fs.readFileSync(CFG + f, 'utf8'));
  for (const [t, v] of Object.entries(j.vehicles ?? {})) {
    for (const e of v.entity ?? []) {
      if (!used.has(e)) used.set(e, []);
      used.get(e).push(`${f.replace('.json', '')}:${t}`);
    }
  }
}

console.log(`编制引用的载具实体 ${used.size} 种：`);
for (const [e, refs] of [...used.entries()].sort()) {
  const cov = covered.get(e);
  console.log(`${cov ? '✔ kubejs可改' : '✘ 仅存在于模组jar'}  ${e.padEnd(38)} ${cov ? cov.padEnd(22) : ''.padEnd(22)} (${refs.length}处: ${refs.slice(0, 3).join(', ')}${refs.length > 3 ? '…' : ''})`);
}
console.log(`\nkubejs 覆盖文件 ${covered.size} 个（fcp ${fs.readdirSync(KJS[0][1]).filter((f) => f.endsWith('.json')).length} + dr ${fs.readdirSync(KJS[1][1]).filter((f) => f.endsWith('.json')).length}）`);
