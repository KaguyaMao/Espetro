const fs=require('fs');
const p=process.env.ATT;
console.log('size='+fs.statSync(p).size);
const L=fs.readFileSync(p,'utf8').split(/\r?\n/);
let start=-1;
for(let i=L.length-1;i>=0;i--){ if(/16:29:1[5-9]/.test(L[i])){ start=i; break; } }
console.log('lines='+L.length+' start='+start);
const pats=/Exception|error|ERROR|WARN|warn|tacz|TaCZ|TACZ|isconnect|Kick|kick|EsPoints|Espetro|Oculus|Iris|shader|reload|Reload|Setting user|Connecting|voicechat|Lost|abort/i;
for(let i=start;i<L.length;i++){ if(pats.test(L[i])) console.log(i+' | '+L[i].slice(0,300)); }
console.log('--- TAIL 18 ---');
for(let i=Math.max(0,L.length-18);i<L.length;i++) console.log(i+' | '+L[i].slice(0,300));
