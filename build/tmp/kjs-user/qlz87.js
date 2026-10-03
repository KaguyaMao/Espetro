EntityEvents.spawned(event => {
    const e = event.entity;
    if (e.getType().toString() !== "tacz:bullet") return;

    const owner = e.getOwner();
    if (!owner) return;

    const gun = owner.getMainHandItem();
    if (!gun || String(gun.nbt?.GunId || "") !== "suffuse:qlz87") return;
    const vx = Number(e.getMotionX()) || 0;
    const vy = Number(e.getMotionY()) || 0;
    const vz = Number(e.getMotionZ()) || 0;
    const g = event.level.createEntity("superbwarfare:gun_grenade");
    g.setPosition(e.x, e.y, e.z);
    g.setMotion(vx, vy, vz);
    g.setExplosionDamage(20.0);
    g.setExplosionRadius(4.0);
    g.setOwner(owner);
    g.spawn();
    event.cancel();
});
