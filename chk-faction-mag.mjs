import fs from "node:fs";
const dir="D:/minecraft/squadMC预发布测试/versions/Squad预发布测试/EsFactions";
const files=fs.readdirSync(dir).filter(f=>f.endsWith(".json"));
for (const f of files.slice(0,3)) {
  const j=JSON.parse(fs.readFileSync(dir+"/"+f,"utf8"));
  const s=JSON.stringify(j);
  const mags=[...s.matchAll(/\{"[^{}]*taczmagazines:magazine[^{}]*\}/g)].map(m=>m[0]);
  console.log("== "+f+" magazine entries: "+mags.length);
  for(const m of mags.slice(0,4)) console.log("   "+m.slice(0,220));
}
