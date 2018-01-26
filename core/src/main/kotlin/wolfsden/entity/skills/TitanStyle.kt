package wolfsden.entity.skills

import squidpony.squidai.AimLimit
import squidpony.squidai.PointAOE
import squidpony.squidmath.Coord
import wolfsden.CommonColors
import wolfsden.screen.PlayScreen
import wolfsden.system.Location
import wolfsden.system.attack
import wolfsden.system.describeCombat

class Stonebreaker(userID: String) : WolfSkill(userID, "Stonebreaker",
        "A devastating strike to a single foe",
        PointAOE(Coord.get(0, 0), 1, 1),
        20,
        5) {

    init {
        aoe.limitType = AimLimit.EIGHT_WAY
    }

    override val isAvailable: Boolean
        get() = super.isAvailable && user.hasEffect("Hulking Titan Stance")

    override fun use(target: Coord): Int {
        val sucker = targetEntity(target)
        val missMsg = "${user.markupString} swings mightily, but finds nothing there!"

        if (sucker == null) {
            PlayScreen.addMessage(missMsg)
        } else {
            val results = user.attack(sucker, dmgMod = 4)
            if (results.hit) {
                PlayScreen.addMessage("${user.markupString} brings down a stone-breaking blow upon ${sucker.markupString}!")
                sucker.takeDmg(results.dmg)
            }
            describeCombat(results)
        }
        user.vit!!.curEnd -= cost
        return user.atkDly + 5
    }
}