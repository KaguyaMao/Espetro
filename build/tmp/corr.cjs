const fs=require('fs');
const L=fs.readFileSync('server-latest.log','utf8').split(/\r?\n/);
const T=L.slice(Math.max(0,L.length-40000));
const ev=[];
const byP={}, cacheP=[];
for(const l of T){
  const ts=(l.match(/\[([^\]]+?)\]/)||[])[1]||'';
  let m;
  if(m=l.match(/TaCZ gun-pack cache prepared: (\d+) bytes in (\d+) chunk/)) { cacheP.push(ts+' prepared '+m[1]+'B/'+m[2]+'ch'); ev.push([ts,'CACHE-PREP '+m[1]+'B']); }
  else if(m=l.match(/^\[[^\]]+\] \[Server thread\/INFO\] \[net\.minecraft\.server\.MinecraftServer\/\]: (\S+) joined the game/)) { ev.push([ts,'JOIN '+m[1]]); byP[m[1]]=(byP[m[1]]||0)+1; }
  else if(m=l.match(/^\[[^\]]+\] \[Server thread\/INFO\] \[net\.minecraft\.server\.network\.ServerGamePacketListenerImpl\/\]: (\S+) lost connection: (.+)$/)) { ev.push([ts,'LOST '+m[1]+' :: '+m[2]]); }
}
console.log('window lines='+T.length);
console.log('cache prepared count='+cacheP.length+' (unique chunks) '+JSON.stringify([...new Set(cacheP.map(s=>s.replace(/^[^ ]+ /,'')))].slice(0,4)));
console.log('join counts by player: '+JSON.stringify(byP));
console.log('--- last 44 events ---');
for(const [ts,e] of ev.slice(-44)) console.log(ts.slice(0,20)+'  '+e.slice(0,150));
const lost=T.filter(l=>/lost connection/.test(l)).map(l=>({p:(l.match(/: (\S+) lost connection: (.+)$/)||[])[1],r:(l.match(/lost connection: (.+)$/)||[])[1]}));
const agg={}; for(const x of lost) agg[x.p+' :: '+x.r]=(agg[x.p+' :: '+x.r]||0)+1;
console.log('--- lost-connection reasons in window ---'); console.log(JSON.stringify(agg,null,1));
