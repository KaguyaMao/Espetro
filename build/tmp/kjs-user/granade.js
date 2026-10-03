TaCZServerEvents.entityShoot(event => {
    const gunId = event.getGunId();
    const gunItem = event.getGunItem()
    if (gunId.toString() === "tacz:m320") {
        event.cancelShoot();
        const currentAmmo = gunItem.nbt?.GunCurrentAmmoCount || 0;
        //if (currentAmmo > 0) {
        //    gunItem.nbt.GunCurrentAmmoCount = currentAmmo -1;
            const hasbulletinbarrel = gunItem.nbt?.HasBulletInBarrel || 0;
        if (currentAmmo > 0) {
            gunItem.nbt.GunCurrentAmmoCount = currentAmmo -1;
            const { player, level } = event;
        const viewVector = player.getViewVector(1.0);
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
        // EsVehHP ammo match: low damage band (see esvehhp_ammo.js gun_grenade)
        if (projectile.setDamage) projectile.setDamage(30.0)
        projectile.setExplosionDamage(30.0)
        projectile.setExplosionRadius(5.0)
        projectile.setOwner(player)
        projectile.spawn();
        }}
})

TaCZServerEvents.entityShoot(event => {
    const gunId = event.getGunId();
    const gunItem = event.getGunItem()
    if (gunId.toString() === "cib:qlu11") {
        event.cancelShoot();
            const hasbulletinbarrel = gunItem.nbt?.HasBulletInBarrel || 0;
        if (hasbulletinbarrel > 0) {
            gunItem.nbt.HasBulletInBarrel = 0;
            const { player, level } = event;
        const viewVector = player.getViewVector(1.0);
        const length = Math.sqrt(viewVector.x() * viewVector.x() + viewVector.y() * viewVector.y() + viewVector.z() * viewVector.z());
        const normalizedVector = {
            x: viewVector.x() / length,
            y: viewVector.y() / length,
            z: viewVector.z() / length
             };
            const projectile = level.createEntity("superbwarfare:gun_grenade");
        projectile.setPosition(player.x, player.y + 1.6, player.z);
        const velocity = 450 / 20;
        projectile.setMotion(normalizedVector.x * velocity, normalizedVector.y * velocity, normalizedVector.z * velocity);
        // Slightly higher damage for future split profiles (still HE ~45 mm pen)
        if (projectile.setDamage) projectile.setDamage(35.0)
        projectile.setExplosionDamage(30.0)
        projectile.setExplosionRadius(7.0)
        projectile.setOwner(player)
        projectile.spawn();
        }}
})

TaCZServerEvents.entityShoot(event => {
    const gunId = event.getGunId();
    const gunItem = event.getGunItem()
    if (gunId.toString() === "ts:at4") {
        event.cancelShoot();
        const currentAmmo = gunItem.nbt?.GunCurrentAmmoCount || 0;
        if (currentAmmo > 0) {
            // 扣除弹药
            gunItem.nbt.GunCurrentAmmoCount = currentAmmo - 1;
            const { entity, level } = event;

            // 获取玩家主手容器
            const mainHandItemHandler = entity.getMainHandItem();

            // 计算发射向量
            const viewVector = entity.getViewVector(1.0);
            const length = Math.sqrt(viewVector.x() * viewVector.x() + viewVector.y() * viewVector.y() + viewVector.z() * viewVector.z());
            const normalizedVector = {
                x: viewVector.x() / length,
                y: viewVector.y() / length,
                z: viewVector.z() / length
            };

            // 生成火箭弹
            const projectile = level.createEntity("superbwarfare:rpg_rocket_standard");
            projectile.setPosition(entity.x, entity.y + 1.6, entity.z);
            const velocity = 300 / 20;
            projectile.setMotion(normalizedVector.x * velocity, normalizedVector.y * velocity, normalizedVector.z * velocity);
            projectile.setDamage(200.0)
            projectile.setExplosionDamage(25.0)
            projectile.setExplosionRadius(7.0)
            projectile.setOwner(entity)
            projectile.spawn();

            // 发射成功后，把主手替换为空物品air
            entity.setMainHandItem(Item.of("minecraft:air"));
        }
    }
})
TaCZServerEvents.entityShoot(event => {
    const gunId = event.getGunId();
    const gunItem = event.getGunItem()
    if (gunId.toString() === "cib:dzj08") {
        event.cancelShoot();
        const currentAmmo = gunItem.nbt?.GunCurrentAmmoCount || 0;
        if (currentAmmo > 0) {
            // 扣除弹药
            gunItem.nbt.GunCurrentAmmoCount = currentAmmo - 1;
            const { entity, level } = event;

            // 获取玩家主手容器
            const mainHandItemHandler = entity.getMainHandItem();

            // 计算发射向量
            const viewVector = entity.getViewVector(1.0);
            const length = Math.sqrt(viewVector.x() * viewVector.x() + viewVector.y() * viewVector.y() + viewVector.z() * viewVector.z());
            const normalizedVector = {
                x: viewVector.x() / length,
                y: viewVector.y() / length,
                z: viewVector.z() / length
            };

            // 生成火箭弹
            const projectile = level.createEntity("superbwarfare:rpg_rocket_standard");
            projectile.setPosition(entity.x, entity.y + 1.6, entity.z);
            const velocity = 300 / 20;
            projectile.setMotion(normalizedVector.x * velocity, normalizedVector.y * velocity, normalizedVector.z * velocity);
            projectile.setDamage(200.0)
            projectile.setExplosionDamage(25.0)
            projectile.setExplosionRadius(7.0)
            projectile.setOwner(entity)
            projectile.spawn();

            // 发射成功后，把主手替换为空物品air
            entity.setMainHandItem(Item.of("minecraft:air"));
        }
    }
})

TaCZServerEvents.entityShoot(event => {
    const gunId = event.getGunId();
    const gunItem = event.getGunItem()
    if (gunId.toString() === "ts:gustavm4") {
        event.cancelShoot();
        const currentAmmo = gunItem.nbt?.GunCurrentAmmoCount || 0;
        if (currentAmmo > 0) {
            // 扣除弹药
            gunItem.nbt.GunCurrentAmmoCount = currentAmmo - 1;
            const { entity, level } = event;

            // 读取当前装填的弹药类型（位于 Extras.ExtraAmmo）
            const extraAmmo = gunItem.nbt?.Extras?.ExtraAmmo || "";

            // 获取玩家主手容器
            //const mainHandItemHandler = entity.getMainHandItem();

            // 计算发射向量
            const viewVector = entity.getViewVector(1.0);
            const length = Math.sqrt(viewVector.x() * viewVector.x() + viewVector.y() * viewVector.y() + viewVector.z() * viewVector.z());
            const normalizedVector = {
                x: viewVector.x() / length,
                y: viewVector.y() / length,
                z: viewVector.z() / length
            };

            // 根据装填的弹药类型决定弹射物数据
            // 默认弹种配置（可在此处扩展更多弹药类型）
            let projectileId = "superbwarfare:rpg_rocket_standard";
            let velocity = 300 / 20;
            let damage = 200.0;
            let explosionDamage = 20.0;
            let explosionRadius = 5.0;

            if (extraAmmo === "ts:84mm_ffv441") {
                //HE
                projectileId = "superbwarfare:rpg_rocket_standard";
                velocity = 300 / 20;
                damage = 30.0;
                explosionDamage = 30.0;
                explosionRadius = 10.0;
            }

            if (extraAmmo === "ts:84mm_ffv751") {
                //破甲弹
                projectileId = "superbwarfare:rpg_rocket_standard";
                velocity = 300 / 20;
                damage = 1000.0;
                explosionDamage = 20.0;
                explosionRadius = 2.0;
            }

            // 生成火箭弹
            const projectile = level.createEntity(projectileId);
            projectile.setPosition(entity.x, entity.y + 1.6, entity.z);
            projectile.setMotion(normalizedVector.x * velocity, normalizedVector.y * velocity, normalizedVector.z * velocity);
            projectile.setDamage(damage)
            projectile.setExplosionDamage(explosionDamage)
            projectile.setExplosionRadius(explosionRadius)
            projectile.setOwner(entity)
            projectile.spawn();

            // 发射成功后，把主手替换为空物品air
            //entity.setMainHandItem(Item.of("minecraft:air"));
        }
    }
})

TaCZServerEvents.entityShoot(event => {
    const gunId = event.getGunId();
    const gunItem = event.getGunItem()
    if (gunId.toString() === "suffuse:pf98a") {
        event.cancelShoot();
        const currentAmmo = gunItem.nbt?.GunCurrentAmmoCount || 0;
        if (currentAmmo > 0) {
            // 扣除弹药
            gunItem.nbt.GunCurrentAmmoCount = currentAmmo - 1;
            const { entity, level } = event;

            // 获取玩家主手容器
            //const mainHandItemHandler = entity.getMainHandItem();

            // 计算发射向量
            const viewVector = entity.getViewVector(1.0);
            const length = Math.sqrt(viewVector.x() * viewVector.x() + viewVector.y() * viewVector.y() + viewVector.z() * viewVector.z());
            const normalizedVector = {
                x: viewVector.x() / length,
                y: viewVector.y() / length,
                z: viewVector.z() / length
            };

            // 生成火箭弹
            const projectile = level.createEntity("superbwarfare:rpg_rocket_standard");
            projectile.setPosition(entity.x, entity.y + 1.6, entity.z);
            const velocity = 300 / 20;
            projectile.setMotion(normalizedVector.x * velocity, normalizedVector.y * velocity, normalizedVector.z * velocity);
            projectile.setDamage(1000.0)
            projectile.setExplosionDamage(20.0)
            projectile.setExplosionRadius(2.0)
            projectile.setOwner(entity)
            projectile.spawn();

            // 发射成功后，把主手替换为空物品air
            //entity.setMainHandItem(Item.of("minecraft:air"));
        }
    }
})

TaCZServerEvents.entityShoot(event => {
    const gunId = event.getGunId();
    const gunItem = event.getGunItem()
    if (gunId.toString() === "murasamet:rpg7_pg7vr_tandem_heat") {
        event.cancelShoot();
        const currentAmmo = gunItem.nbt?.GunCurrentAmmoCount || 0;
        if (currentAmmo > 0) {
            // 扣除弹药
            gunItem.nbt.GunCurrentAmmoCount = currentAmmo - 1;
            const { entity, level } = event;

            // 获取玩家主手容器
            //const mainHandItemHandler = entity.getMainHandItem();

            // 计算发射向量
            const viewVector = entity.getViewVector(1.0);
            const length = Math.sqrt(viewVector.x() * viewVector.x() + viewVector.y() * viewVector.y() + viewVector.z() * viewVector.z());
            const normalizedVector = {
                x: viewVector.x() / length,
                y: viewVector.y() / length,
                z: viewVector.z() / length
            };

            // 生成火箭弹
            const projectile = level.createEntity("superbwarfare:rpg_rocket_standard");
            projectile.setPosition(entity.x, entity.y + 1.6, entity.z);
            const velocity = 300 / 20;
            projectile.setMotion(normalizedVector.x * velocity, normalizedVector.y * velocity, normalizedVector.z * velocity);
            projectile.setDamage(400.0)
            projectile.setExplosionDamage(20.0)
            projectile.setExplosionRadius(2.0)
            projectile.setOwner(entity)
            projectile.spawn();

            // 发射成功后，把主手替换为空物品air
            //entity.setMainHandItem(Item.of("minecraft:air"));
        }
    }
})

TaCZServerEvents.entityShoot(event => {
    const gunId = event.getGunId();
    const gunItem = event.getGunItem()
    if (gunId.toString() === "murasamet:rpg7_og7he") {
        event.cancelShoot();
        const currentAmmo = gunItem.nbt?.GunCurrentAmmoCount || 0;
        if (currentAmmo > 0) {
            // 扣除弹药
            gunItem.nbt.GunCurrentAmmoCount = currentAmmo - 1;
            const { entity, level } = event;

            // 获取玩家主手容器
            //const mainHandItemHandler = entity.getMainHandItem();

            // 计算发射向量
            const viewVector = entity.getViewVector(1.0);
            const length = Math.sqrt(viewVector.x() * viewVector.x() + viewVector.y() * viewVector.y() + viewVector.z() * viewVector.z());
            const normalizedVector = {
                x: viewVector.x() / length,
                y: viewVector.y() / length,
                z: viewVector.z() / length
            };

            // 生成火箭弹
            const projectile = level.createEntity("superbwarfare:rpg_rocket_standard");
            projectile.setPosition(entity.x, entity.y + 1.6, entity.z);
            const velocity = 300 / 20;
            projectile.setMotion(normalizedVector.x * velocity, normalizedVector.y * velocity, normalizedVector.z * velocity);
            projectile.setDamage(30.0)
            projectile.setExplosionDamage(30.0)
            projectile.setExplosionRadius(10.0)
            projectile.setOwner(entity)
            projectile.spawn();

            // 发射成功后，把主手替换为空物品air
            //entity.setMainHandItem(Item.of("minecraft:air"));
        }
    }
})

TaCZServerEvents.entityShoot(event => {
    const gunId = event.getGunId();
    const gunItem = event.getGunItem()
    if (gunId.toString() === "murasamet:rpg7_pg7heat") {
        event.cancelShoot();
        const currentAmmo = gunItem.nbt?.GunCurrentAmmoCount || 0;
        if (currentAmmo > 0) {
            // 扣除弹药
            gunItem.nbt.GunCurrentAmmoCount = currentAmmo - 1;
            const { entity, level } = event;

            // 获取玩家主手容器
            //const mainHandItemHandler = entity.getMainHandItem();

            // 计算发射向量
            const viewVector = entity.getViewVector(1.0);
            const length = Math.sqrt(viewVector.x() * viewVector.x() + viewVector.y() * viewVector.y() + viewVector.z() * viewVector.z());
            const normalizedVector = {
                x: viewVector.x() / length,
                y: viewVector.y() / length,
                z: viewVector.z() / length
            };

            // 生成火箭弹
            const projectile = level.createEntity("superbwarfare:rpg_rocket_standard");
            projectile.setPosition(entity.x, entity.y + 1.6, entity.z);
            const velocity = 300 / 20;
            projectile.setMotion(normalizedVector.x * velocity, normalizedVector.y * velocity, normalizedVector.z * velocity);
            projectile.setDamage(200.0)
            projectile.setExplosionDamage(20.0)
            projectile.setExplosionRadius(3.0)
            projectile.setOwner(entity)
            projectile.spawn();

            // 发射成功后，把主手替换为空物品air
            //entity.setMainHandItem(Item.of("minecraft:air"));
        }
    }
})

TaCZServerEvents.entityShoot(event => {
    const gunId = event.getGunId();
    const gunItem = event.getGunItem()
    if (gunId.toString() === "rcp:rpg26") {
        event.cancelShoot();
        const currentAmmo = gunItem.nbt?.GunCurrentAmmoCount || 0;
        if (currentAmmo > 0) {
            // 扣除弹药
            gunItem.nbt.GunCurrentAmmoCount = currentAmmo - 1;
            const { entity, level } = event;

            // 获取玩家主手容器
            const mainHandItemHandler = entity.getMainHandItem();

            // 计算发射向量
            const viewVector = entity.getViewVector(1.0);
            const length = Math.sqrt(viewVector.x() * viewVector.x() + viewVector.y() * viewVector.y() + viewVector.z() * viewVector.z());
            const normalizedVector = {
                x: viewVector.x() / length,
                y: viewVector.y() / length,
                z: viewVector.z() / length
            };

            // 生成火箭弹
            const projectile = level.createEntity("superbwarfare:rpg_rocket_standard");
            projectile.setPosition(entity.x, entity.y + 1.6, entity.z);
            const velocity = 300 / 20;
            projectile.setMotion(normalizedVector.x * velocity, normalizedVector.y * velocity, normalizedVector.z * velocity);
            projectile.setDamage(200.0)
            projectile.setExplosionDamage(25.0)
            projectile.setExplosionRadius(7.0)
            projectile.setOwner(entity)
            projectile.spawn();

            // 发射成功后，把主手替换为空物品air
            entity.setMainHandItem(Item.of("minecraft:air"));
        }
    }
})