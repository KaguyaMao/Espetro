function modifyProperty(pmc, level, perkTag, gunDataProxy) {
    pmc.add("UnderwaterMotionScale", Math.max(0, 0.02 * level))
    if (level > 10) {
        pmc.add("Velocity", 0.02 * level)
    }
}

function getModifiedDamage(damage, target, level, perkTag, sourceProxy) {
    if (target.isAquatic()) {
        return damage * (1 + 0.2 * level)
    }
    return damage
}