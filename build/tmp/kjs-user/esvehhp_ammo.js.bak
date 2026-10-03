// EsVehHP ammunition for TaCZ → SBW projectiles (see granade.js).
// Requires EsVehHP + KubeJS. MBT baseline: front 800/850, side 700, hull/turret rear 600, barrel 25.
//
// granade.js mapping:
//   tacz:m320, cib:qlu11          → superbwarfare:gun_grenade
//   ts:at4, cib:dzj08             → rpg_rocket_standard + setDamage(200)
//   ts:gustavm4, suffuse:pf98a    → rpg_rocket_standard + setDamage(400)

EsVehEvents.ammunition(event => {
  // --- 40mm / 35mm HE grenades (m320 + qlu11 share gun_grenade; no setDamage in granade.js) ---
  event.register('kubejs:tacz_gun_grenade', b => {
    b.entity('superbwarfare:gun_grenade', '', 50)
      .type('HE')
      .caliber(40)
      .baseDamage(30)
      .penetrationMm(45)
      .explosiveMass(2.5)
      .overpressureRadius(5)
      .fragmentCount(16)
      .fragmentCone(35)
      .ricochet(68, 84)
      .emptyPenStructure(false)
  })

  // --- LAW tier: AT4 + DZJ08 (damage ~200) — pen side/rear, not reliable MBT front ---
  event.register('kubejs:tacz_law_heat', b => {
    b.entity('superbwarfare:rpg_rocket_standard', '', 60, 150, 299, '')
      .type('HEAT')
      .caliber(84)
      .baseDamage(200)
      .penetrationMm(560)
      .explosiveMass(3)
      .overpressureRadius(2.5)
      .fragmentCount(12)
      .fragmentCone(12)
      .fireChance(0.2)
      .ricochet(82, 88)
      .emptyPenStructure(false)
  })

  // --- Recoilless / heavy rocket: Gustav M4 + PF98A (setDamage ~400)
  // Pen 1000 + baseDamage 360: one hit through turret rear (400) kills ammo rack and detonates.
  event.register('kubejs:tacz_recoilless_heat', b => {
    b.entity('superbwarfare:rpg_rocket_standard', '', 60, 300, null, '')
      .type('HEAT')
      .caliber(84)
      .baseDamage(360)
      .penetrationMm(1000)
      .explosiveMass(3)
      .overpressureRadius(2)
      .fragmentCount(12)
      .fragmentCone(12)
      .fireChance(0.2)
      .ricochet(80, 87)
      .emptyPenStructure(false)
  })

  // Keep tank AP reference for vehicle cannons (optional pack balance)
  event.modify('esvehhp:cannon_ap', b => {
    b.penetrationMm(1200)
      .ricochet(70, 82)
      .baseDamage(180)
      .emptyPenStructure(true)
      .emptyPenStructureMultiplier(1.25)
  })
})
