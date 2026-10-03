// check-rule-keys-vs-entityids.mjs — 校验规则键（JSON 路径）是否对应真实注册的载具实体 id
//
// 键取自 veh-rules-effective.json 的生效集；实体 id 从各 mod jar 的 *Entities.class /
// ModEntities*.class 常量池里抽取（Forge 注册名就是字符串字面量）。
// 若某个键的 path 在所有 mod 的类里都找不到，说明该 JSON 的名字与实体 id 不一致 ——
// mixin 按 <ns>:<path> 查表，会永远查不到 → 方向抗性静默失效。
import fs from 'node:fs';
import path from 'node:path';

const MODS = 'D:\\minecraft\\squadMC预发布测试\\versions\\Squad预发布测试\\mods';
const eff = JSON.parse(fs.readFileSync('veh-rules-effective.json', 'utf8'));
const keys = Object.keys(eff.effective);

// 1) 收集所有 jar 里 .class 的字符串常量（只取 init/entity 相关包，控制体量）
const idPool = new Map(); // id -> jar
const { execSync } = await import('node:child_process');
