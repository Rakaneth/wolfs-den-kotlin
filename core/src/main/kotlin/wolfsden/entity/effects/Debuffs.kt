package wolfsden.entity.effects

class StunEffect(
        override val eID: String,
        override var duration: Int
) : Effect("Stunned", eID, duration = duration, loseTurn = true)