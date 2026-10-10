const fs=require('fs');
for(const [tag,file] of [['SERVER','server-latest.log'],['CLIENT',process.env.CLI]]){
  const txt=fs.readFileSync(file,'utf8').split(/\r?\n/);
  const packs=new Set(), errs=new Set();
  for(const l of txt){
    const m=l.match(/GunPackFinder\/\]: - (.+?), Main namespace: (\S+)/);
    if(m) packs.add(m[2]+'  <-  '+m[1]);
    const e=l.match(/GunPackFinder\/\]: (.*(?:Failed|Error|not found|corrupt).*)/);
    if(e) errs.add(e[1].slice(0,130));
  }
  console.log('=== '+tag+' : '+packs.size+' packs ===');
  [...packs].sort().forEach(p=>console.log('   '+p));
  if(errs.size){ console.log('   -- ????/?? --'); [...errs].forEach(e=>console.log('   !! '+e)); }
}
