/*
TaCZServerEvents.entityShoot(event => {
    const gunId = event.getGunId();
    const gunItem = event.getGunItem()
    const under = gunItem.nbt?.GunId || "";
    if (gunId.toString() === "ccrp:lmt_m203") {
        if (extraAmmo === "ts:40mm_m680") {
            return;
        }
        event.cancelShoot();
        if (under === "tacz:m4a1") {
        const currentAmmo = gunItem.nbt?.Extras?.UnderBarrelRoot?.GunCurrentAmmoCount || 0;
            if (currentAmmo > 0) {
                gunItem.nbt.Extras.UnderBarrelRoot.GunCurrentAmmoCount = currentAmmo -1;
                const { entity, level } = event;
            const viewVector = entity.getViewVector(1.0);
            const length = Math.sqrt(viewVector.x() * viewVector.x() + viewVector.y() * viewVector.y() + viewVector.z() * viewVector.z());
            const normalizedVector = {
                x: viewVector.x() / length,
                y: viewVector.y() / length,
                z: viewVector.z() / length
                };
                const projectile = level.createEntity("superbwarfare:gun_grenade");
            projectile.setPosition(player.x, player.y + 1.6, player.z);
            const velocity = 50 / 20;
            projectile.setMotion(normalizedVector.x * velocity, normalizedVector.y * velocity, normalizedVector.z * velocity);
            projectile.setExplosionDamage(30.0)
            projectile.setExplosionRadius(50.0)
            projectile.setOwner(player)
            projectile.spawn();
            }}
        else if (under === "ccrp:lmt_m203") {
            const currentAmmo = gunItem.nbt?.GunCurrentAmmoCount || 0;
            if (currentAmmo > 0) {
                gunItem.nbt.GunCurrentAmmoCount = currentAmmo -1;
                const { entity, level } = event;
            const viewVector = entity.getViewVector(1.0)        ;
            const length = Math.sqrt(viewVector.x() * viewVector.x() + viewVector.y() * viewVector.y() + viewVector.z() * viewVector.z());
            const normalizedVector = {
                x: viewVector.x() / length,
                y: viewVector.y() / length,
                z: viewVector.z() / length
                };
                const projectile = level.createEntity("superbwarfare:gun_grenade");
            projectile.setPosition(player.x, player.y + 1.6, player.z);
            const velocity = 76 / 20;
            projectile.setMotion(normalizedVector.x * velocity, normalizedVector.y * velocity, normalizedVector.z * velocity);
            projectile.setExplosionDamage(30.0)
            projectile.setExplosionRadius(5.0)
            projectile.setOwner(player)
            projectile.spawn();
            }}
        }
})
*/

TaCZServerEvents.entityShoot(event => {
    const gunId = event.getGunId();
    const gunItem = event.getGunItem()
    const extraAmmo = gunItem.nbt?.Extras?.UnderBarrelRoot?.ExtraAmmo || "";
    const under = gunItem.nbt?.GunId || "";
    if (gunId.toString() === "ts:m320") {
        if (extraAmmo === "ts:40mm_m680") {
            return;
        }
        event.cancelShoot();
        const currentAmmo = gunItem.nbt?.Extras?.UnderBarrelRoot?.GunCurrentAmmoCount || 0;
            if (currentAmmo > 0) {
                gunItem.nbt.Extras.UnderBarrelRoot.GunCurrentAmmoCount = currentAmmo -1;
                const { entity, level } = event;
            const viewVector = entity.getViewVector(1.0);
            const length = Math.sqrt(viewVector.x() * viewVector.x() + viewVector.y() * viewVector.y() + viewVector.z() * viewVector.z());
            const normalizedVector = {
                x: viewVector.x() / length,
                y: viewVector.y() / length,
                z: viewVector.z() / length
                };
            let projectileId = "superbwarfare:gun_grenade";
            let velocity = 50 / 20;
            let damage = 10.0;
            let explosionDamage = 30.0;
            let explosionRadius = 5.0;
            if (extraAmmo === "ts:40mm_m1060") {
                //HE
                projectileId = "superbwarfare:gun_grenade";
                velocity = 50 / 20;
                damage = 10.0;
                explosionDamage = 50.0;
                explosionRadius = 7.0;
            }
            const projectile = level.createEntity(projectileId);
            projectile.setPosition(entity.x, entity.y + 1.6, entity.z);
            projectile.setMotion(normalizedVector.x * velocity, normalizedVector.y * velocity, normalizedVector.z * velocity);
            projectile.setDamage(damage)
            projectile.setExplosionDamage(explosionDamage)
            projectile.setExplosionRadius(explosionRadius)
            projectile.setOwner(entity)
            projectile.spawn();
            }
        }
}) 

TaCZServerEvents.entityShoot(event => {
    const gunId = event.getGunId();
    const gunItem = event.getGunItem()
    const extraAmmo = gunItem.nbt?.Extras?.UnderBarrelRoot?.ExtraAmmo || "";
    const under = gunItem.nbt?.GunId || "";
    if (gunId.toString() === "ts:gp25") {
/*        if (extraAmmo === "ts:40mm_m680") {
            return;
        }
            */
        event.cancelShoot();
        const currentAmmo = gunItem.nbt?.Extras?.UnderBarrelRoot?.GunCurrentAmmoCount || 0;
            if (currentAmmo > 0) {
                gunItem.nbt.Extras.UnderBarrelRoot.GunCurrentAmmoCount = currentAmmo -1;
                const { entity, level } = event;
            const viewVector = entity.getViewVector(1.0);
            const length = Math.sqrt(viewVector.x() * viewVector.x() + viewVector.y() * viewVector.y() + viewVector.z() * viewVector.z());
            const normalizedVector = {
                x: viewVector.x() / length,
                y: viewVector.y() / length,
                z: viewVector.z() / length
                };
            let projectileId = "superbwarfare:gun_grenade";
            let velocity = 50 / 20;
            let damage = 10.0;
            let explosionDamage = 30.0;
            let explosionRadius = 5.0;
            /*if (extraAmmo === "ts:40mm_m1060") {
                //HE
                projectileId = "superbwarfare:gun_grenade";
                velocity = 50 / 20;
                damage = 10.0;
                explosionDamage = 50.0;
                explosionRadius = 7.0;
            }*/
            const projectile = level.createEntity(projectileId);
            projectile.setPosition(entity.x, entity.y + 1.6, entity.z);
            projectile.setMotion(normalizedVector.x * velocity, normalizedVector.y * velocity, normalizedVector.z * velocity);
            projectile.setDamage(damage)
            projectile.setExplosionDamage(explosionDamage)
            projectile.setExplosionRadius(explosionRadius)
            projectile.setOwner(entity)
            projectile.spawn();
            }
        }
})