import fs from "node:fs";
const p="src/main/java/org/espetro/logistics/resupply/ResupplySessionManager.java";
const t=fs.readFileSync(p,"utf8");
console.log("bytes:", Buffer.byteLength(t), "bom:", t.charCodeAt(0)===0xFEFF, "crlf:", t.includes("\r\n"));
const idx=t.indexOf("没有可填充的弹匣");
console.log("cn ok:", idx>=0, JSON.stringify(t.slice(idx-30, idx+20)));
const idx2=t.indexOf("枪内弹匣");
console.log("cn2:", idx2>=0, JSON.stringify(t.slice(idx2-40, idx2+30)));
