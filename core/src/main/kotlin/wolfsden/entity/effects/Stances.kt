package wolfsden.entity.effects

import wolfsden.screen.PlayScreen

interface Stance

class TitanStanceEffect(override val eID: String)
    : Effect("Hulking Titan Stance", eID, duration = 0, permanent = true, dmg = 2, atkDly = 2, buff = true), Stance {

    override fun onApply() {
        PlayScreen.addMessage("${entity.markupString} assumes the stance of a hulking titan!")
    }

    override fun onExpire() {
        PlayScreen.addMessage("${entity.markupString} leaves the Hulking Titan stance.")
    }
}

class BulwarkStanceEffect(override val eID: String)
    : Effect("Iron Bulwark Stance", eID, duration = 0, permanent = true, dfp = 2, movDly = 2, buff = true), Stance {

    override fun onApply() {
        PlayScreen.addMessage("${entity.markupString} assumes the stance of an immovable bulwark!")
    }

    override fun onExpire() {
        PlayScreen.addMessage("${entity.markupString} leaves the Iron Bulwark stance.")
    }
}

class SnakeStanceEffect(override val eID: String)
    : Effect("Cunning Snake Stance", eID, duration = 0, permanent = true, atk = 2, dmg = -1, buff = true), Stance {

    override fun onApply() {
        PlayScreen.addMessage("${entity.markupString} assumes the stance of a cunning snake!")
    }

    override fun onExpire() {
        PlayScreen.addMessage("${entity.markupString} leaves the Cunning Snake stance.")
    }
}

class WolfSlayerStanceEffect(override val eID: String)
    : Effect("Wolf Slayer Stance", eID, duration = 0, permanent = true, buff = true, tags = listOf("O-natural")),
      Stance {

    override fun onApply() {
        PlayScreen.addMessage("${entity.markupString} assumes the stance of a hunter of beasts!")
    }

    override fun onExpire() {
        PlayScreen.addMessage("${entity.markupString} leaves the Wolf Hunter stance.")
    }
}

class WitchHunterStanceEffect(override val eID: String)
    : Effect("Witch Hunter Stance", eID, duration = 0, permanent = true, buff = true,
             tags = listOf("holy", "O-dark", "O-magic")), Stance {

    override fun onApply() {
        PlayScreen.addMessage("${entity.markupString} assumes the stance of a determined inquisitor!")
    }

    override fun onExpire() {
        PlayScreen.addMessage("${entity.markupString} leaves the Witch Hunter stance.")
    }
}