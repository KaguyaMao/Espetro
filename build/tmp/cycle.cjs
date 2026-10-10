const fs=require('fs');
const L=fs.readFileSync('server-latest.log','utf8').split(/\r?\n/);
let j=-1;
for(let i=L.length-1;i>=0;i--){ if(/Tan_Yi_ joined the game/.test(L[i])){ j=i; break; } }
console.log('total='+L.length+' lastJoinIdx='+j);
const skip=/voicechat|Chunk |chunk |\[Server thread\/INFO\] \[net.minecraft.server.MinecraftServer\/\]: (Saving|Saved|Running|Preparing|Time elapsed)/;
for(let i=Math.max(0,j-6);i<Math.min(L.length,j+60);i++){ if(!skip.test(L[i])) console.log((i-j>=0?'+':'')+(i-j)+' | '+L[i].replace(/\x1b\[[0-9;]*m/g,'').slice(0,240)); }
