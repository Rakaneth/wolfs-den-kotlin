package wolfsden.entity.effects

import wolfsden.screen.PlayScreen

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

class MightEffect(
    override val eID: String,
    override var duration: Int
) : Effect("Might", eID, duration = duration, atk = 2, dmg = 2, buff = true)

class AgilityEffect(
    override val eID: String,
    override var duration: Int
) : Effect("Agility", eID, duration = duration, dfp = 2, sav = 2, buff = true)

class PoisonerEffect(
    override val eID: String,
    override var duration: Int
) : Effect("Poisoned Weapons",
           eID,
           duration = duration,
           tags = listOf("poison"),
           buff = true) {

    override fun onApply() {
        PlayScreen.addMessageVisible(entity, "${entity.markupString} poisons their weapons.")
        super.onApply()
    }

    override fun onExpire() {
        PlayScreen.addMessageVisible(entity, "${entity.markupString}'s weapons are no longer poisoned.")
        super.onExpire()
    }
}