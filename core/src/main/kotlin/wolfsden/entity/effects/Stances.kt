package wolfsden.entity.effects

interface Stance

class TitanStance(override val eID: String)
    : Effect("Hulking Titan Stance", eID, duration = 0, permanent = true, dmg = 2, atkDly = 2, buff = true), Stance

class BulwarkStance(override val eID: String)
    : Effect("Iron Bulwark Stance", eID, duration = 0, permanent = true, dfp = 2, movDly = 2, buff = true), Stance

class SnakeStance(override val eID: String)
    : Effect("Cunning Snake Stance", eID, duration = 0, permanent = true, atk = 2, dmg = -1, buff = true), Stance

class WolfSlayerStance(override val eID: String)
    : Effect("Wolf Slayer Stance", eID, duration = 0, permanent = true, resistance = listOf("natural", "wolf")), Stance

class WitchHunterStance(override val eID: String)
    : Effect("Witch Hunter Stance", eID, duration = 0, permanent = true,
        resistance = listOf("dark", "magic"), tags = listOf("holy")), Stance