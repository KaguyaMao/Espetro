const fs=require('fs');
function scan(file,tag){
  const L=fs.readFileSync(file,'utf8').split(/\r?\n/);
  let on=false,n=0;const packs=[];
  for(const l of L){
    if(/Start scanning for gun packs/.test(l)){ on=true; packs.length=0; continue; }
    if(on){
      const m=l.match(/GunPackFinder\/\]: - (.+), Main namespace: (\S+)/);
      if(m){ packs.push(m[1]+'  ['+m[2]+']'); n++; continue; }
      const b=l.match(/GunPackFinder\/\]: (\S.*(Failed|Error|skip).*)/);
      if(b && n<200){ packs.push('!! '+b[1].slice(0,120)); continue; }
      if(/GunPackFinder/.test(l) && /(finish|total|end|Found)/i.test(l)){ on=false; }
      else if(/GunPackFinder\]: -/.test(l)) {}
    }
  }
  console.log('=== '+tag+' : '+packs.length+' pack lines ===');
  for(const p of packs.slice(-60)) console.log('  '+p);
}
scan('server-latest.log','SERVER ??');
scan(process.env.CLI,'CLIENT ??');
