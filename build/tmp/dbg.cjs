const fs=require('fs');
const L=fs.readFileSync(process.env.CLI,'utf8').split(/\r?\n/);
let n=0;
for(const l of L){ if(/GunPackFinder/.test(l)){ console.log(JSON.stringify(l).slice(0,220)); if(++n>=6) break; } }
console.log('total GunPackFinder lines='+L.filter(l=>/GunPackFinder/.test(l)).length);
