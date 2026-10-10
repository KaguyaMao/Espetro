// EsVehHP + TaCZ caliber tiers for vehicle damage.
// Requires EsVehHP with selectors.ammoId (EntityKineticBullet.getAmmoId).
//
// IDs match the Supr datapack (esvehhp:tacz_*). When both are present, THIS SCRIPT
// overrides the pack entries with the same id. Edit numbers here if you use KubeJS;
// otherwise edit Supr data/esvehhp/esvehhp/ammunition/tacz_*.json and remove/disable this file.
//
// Small arms cannot damage vehicles.
// 12.7/.50: pen IFV rear/top (not front/side — those stay thick for LAW).
// After changes: /reload or KubeJS reload, then /esvehhp ammo list.

EsVehEvents.ammunition(event => {
  // --- Catch-all: all tacz:bullet = no vehicle effect ---
  event.register('esvehhp:tacz_bullet_null', b => {
    b.entity('tacz:bullet', '', 10)
      .type('KINETIC')
      .baseDamage(0)
      .penetrationMm(0)
      .ricochet(60, 75)
      .emptyPenStructure(false)
      .emptyPenStructureMultiplier(0)
  })

  // --- 12.7 / .50 ---
  event.register('esvehhp:tacz_hmg_127', b => {
    const ammos = [
      'tacz:50bmg',
      'tacz:50ae',
      'tacz:416barrett',
      'cib:127x108',
      'suffuse:12.7x108mm',
      'suffuse:12.7x55',
      'ccrp:127x55'
    ]
    for (const ammo of ammos) {
      b.entityAmmo('tacz:bullet', ammo, 40)
    }
    b.type('KINETIC')
      .caliber(12.7)
      .baseDamage(26)
      // 290mm: IFV rear ~220–270 / top ~55–70 可穿；正/侧 ~570+ 不穿（轻筒仍挡在正侧）
      .penetrationMm(290, 200, 200)
      .normalization(2)
      .ricochet(65, 80)
      .emptyPenStructure(true)
      .emptyPenStructureMultiplier(0.55)
  })

  // --- 14.5×114 ---
  event.register('esvehhp:tacz_hmg_145', b => {
    b.entityAmmo('tacz:bullet', 'suffuse:14.5x114mm', 45)
      .type('KINETIC')
      .caliber(14.5)
      .baseDamage(34)
      .penetrationMm(360, 250, 200)
      .normalization(2)
      .ricochet(65, 80)
      .emptyPenStructure(true)
      .emptyPenStructureMultiplier(0.6)
  })

  // --- 20–27 mm class (when still tacz:bullet) ---
  event.register('esvehhp:tacz_autocannon', b => {
    for (const ammo of [
      'suffuse:23mm',
      'ts:27x58',
      'ts:27x58_ap',
      'ts:27x58_hp',
      'ts:25mm_hedp',
      'ts:25mm_heab'
    ]) {
      b.entityAmmo('tacz:bullet', ammo, 50)
    }
    b.type('KINETIC')
      .caliber(25)
      .baseDamage(30)
      .penetrationMm(100, 70, 250)
      .normalization(3)
      .ricochet(68, 82)
      .emptyPenStructure(true)
      .emptyPenStructureMultiplier(0.6)
  })
})
