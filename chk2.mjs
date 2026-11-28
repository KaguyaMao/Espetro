import fs from "node:fs";
const p="src/main/java/org/espetro/logistics/resupply/ResupplySessionManager.java";
const t=fs.readFileSync(p,"utf8");
console.log("bytes:",Buffer.byteLength(t),"bom:",t.charCodeAt(0)===0xFEFF,"crlf:",t.includes("\r\n"));
for (const s of ["没有可填充的弹匣","枪内弹匣","已领取 1 个满弹匣"]) console.log(s, t.includes(s));
