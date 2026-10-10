const fs=require('fs');
for(const [tag,file] of [['SERVER','server-latest.log'],['CLIENT',process.env.CLI]]){
  const L=fs.readFileSync(file,'utf8').split(/\r?\n/);
  const packs=[], errs=[];
  for(const l of L){
    const i=l.indexOf('Main namespace:');
    if(i>=0){ packs.push(l.slice(i+15).trim()+'  <-  '+l.slice(l.indexOf(']: - ')+5, i).trim()); }
    if(l.indexOf('GunPackFinder')>=0 && /(Failed|Error|corrupt|not found)/.test(l)) errs.push(l.slice(l.indexOf('GunPackFinder')+16, l.indexOf('GunPackFinder')+150));
  }
  console.log('=== '+tag+' : '+packs.length+' packs ===');
  [...new Set(packs)].sort().forEach(p=>console.log('   '+p));
  if(errs.length){ console.log('   -- ???? --'); [...new Set(errs)].forEach(e=>console.log('   !! '+e)); }
}
