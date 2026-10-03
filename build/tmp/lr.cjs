const fs=require('fs');
for(const [tag,file] of [['SERVER','server-latest.log'],['CLIENT',process.env.CLI]]){
  const L=fs.readFileSync(file,'utf8').split(/\r?\n/);
  const hits=new Set();
  for(const l of L){ if(/lrtactical|ciblr/i.test(l)) hits.add(l.replace(/\s+/g,' ').slice(0,200)); }
  console.log('=== '+tag+' : '+hits.size+' ?? lrtactical/ciblr ===');
  [...hits].slice(0,12).forEach(h=>console.log('   '+h));
}
