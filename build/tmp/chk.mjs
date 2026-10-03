import fs from 'fs';
const j = JSON.parse(fs.readFileSync('D:/minecraft/modp/Espetro/build/tmp/server-vehicles-fixed/ztz99a.json', 'utf8'));
console.log('DM 前3行: ' + JSON.stringify(j.DamageModifiers.slice(0, 3)));
console.log('Cannon: D=' + j.Weapons.Cannon.Damage + ' Expl=' + j.Weapons.Cannon.ExplosionDamage + '/' + j.Weapons.Cannon.ExplosionRadius);
console.log('HE: D=' + j.Weapons.Cannon.AmmoType[1].Override.Damage + ' Expl=' + j.Weapons.Cannon.AmmoType[1].Override.ExplosionDamage + '/' + j.Weapons.Cannon.AmmoType[1].Override.ExplosionRadius);
console.log('MG=' + j.Weapons.MachineGun.Damage + ' PMG=' + j.Weapons.PassengerMachineGun.Damage);