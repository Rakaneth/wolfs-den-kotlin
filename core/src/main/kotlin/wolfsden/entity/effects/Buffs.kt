package wolfsden.entity.effects

class RegenEffect(
        override val eID: String,
        override var duration: Int,
        val effPowa: Double = 0.0
) : Effect("Regen", eID, duration = duration, buff = true) {
    var accVit = 0.0
    override fun tick() {
        accVit += effPowa
        if (accVit > 1.0) {
            accVit -= 1.0
            entity.heal(1)
        }
        super.tick()
    }
}

class HasteEffect(
        override val eID: String,
        override var duration: Int
) : Effect("Haste", eID, duration = duration, atkDly = -5, movDly = -5, buff = true)