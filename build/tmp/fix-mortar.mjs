import fs from 'fs';
const SRCDIR = 'D:/minecraft/modp/Espetro/build/tmp/server-vehicles/';
const OUT = 'D:/minecraft/modp/Espetro/build/tmp/server-vehicles-fixed/';
const DM_IFV = [
	"All - 13", "minecraft:lava + 13", "minecraft:lava * 10",
	"@minecraft:tnt * 3", "@minecraft:tnt_minecart * 3", "All * 0.2",
	"minecraft:arrow * 1.5", "minecraft:trident * 1.5",
	"minecraft:mob_attack * 2.5", "minecraft:mob_attack_no_aggro * 2",
	"minecraft:mob_projectile * 1.5", "minecraft:explosion * 6",
	"minecraft:player_explosion * 6", "superbwarfare:custom_explosion * 2",
	"superbwarfare:projectile_explosion * 2", "superbwarfare:mine * 0.7",
	"superbwarfare:lunge_mine * 0.9", "superbwarfare:projectile_hit * 1.35",
	"superbwarfare:grapeshot_hit * 0.25", "superbwarfare:laser * 1.25",
	"@#superbwarfare:aerial_bomb * 3", "@#superbwarfare:aa_missile * 0.5",
	"#superbwarfare:projectile * 0.1", "#superbwarfare:projectile_absolute * 0.7",
	"#superbwarfare:vehicle_strike * 13", "@superbwarfare:mortar_shell * 1.1",
	"@superbwarfare:gun_grenade * 1.5", "@superbwarfare:javelin_missile * 0.8"
];
const j = JSON.parse(fs.readFileSync(SRCDIR + 'stryker_mortar.json', 'utf8'));
j.DamageModifiers = DM_IFV;
fs.writeFileSync(OUT + 'stryker_mortar.json', JSON.stringify(j, null, 2), 'utf8');
console.log('stryker_mortar 抗性已补');