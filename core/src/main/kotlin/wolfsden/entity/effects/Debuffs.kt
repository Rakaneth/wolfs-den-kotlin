package wolfsden.entity.effects

class StunEffect(
    override val eID: String,
    override var duration: Int
) : Effect("Stunned", eID, duration = duration, loseTurn = true)

class WolfCurseEffect(
    override val eID: String,
    override var duration: Int
) : Effect("Wolf Curse", eID, duration = duration, weakness = listOf("wolf", "natural"))

class DarkCurseEffect(
    override val eID: String,
    override var duration: Int
) : Effect("Curse of Darkness", eID, duration = duration, weakness = listOf("dark", "magic"))

class SlowEffect(
    override val eID: String,
    override var duration: Int
) : Effect("Slowed", eID, duration = duration, movDly = 5, atkDly = 5)

class WeakEffect(
    override val eID: String,
    override var duration: Int
) : Effect("Weakened", eID, duration = duration, atk = -2, dmg = -2)

class BreachEffect(
    override val eID: String,
    override var duration: Int
) : Effect("Breached", eID, duration = duration, dfp = -2, sav = -2)
