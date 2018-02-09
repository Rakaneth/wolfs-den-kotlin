package wolfsden.entity.skills

import squidpony.squidai.PointAOE
import squidpony.squidmath.Coord
import wolfsden.entity.effects.StunEffect
import wolfsden.entity.effects.TitanStanceEffect
import wolfsden.screen.PlayScreen
import wolfsden.system.attack
import wolfsden.system.describeCombat

private val DEFAULT_POINT: Coord = Coord.get(0, 0)

class TitanStance(userID: String) : WolfSkill(userID, "Hulking Titan Stance",
                                              desc = "Become a hulking titan on the battlefield",
                                              aoe = PointAOE(DEFAULT_POINT),
                                              cd = 20,
                                              cost = 0,
                                              targeting = false,
                                              skillIndex = 0,
                                              isAttack = false
) {
    override fun use(target: Coord): Int {
        user.applyEffect(TitanStanceEffect(user.eID))
        return 0
    }
}

class Stonebreaker(userID: String) : WolfSkill(userID, "Stonebreaker",
                                               desc = "A devastating strike to a single foe",
                                               aoe = PointAOE(DEFAULT_POINT, 1, 1),
                                               cd = 30,
                                               cost = 5,
                                               skillIndex = 1) {

    override val isAvailable: Boolean
        get() = super.isAvailable && user.hasEffect("Hulking Titan Stance")

    override fun use(target: Coord): Int {
        val sucker = targetEntity(target)
        val missMsg = "${user.markupString} swings mightily, but finds nothing there!"

        if (sucker == null) {
            PlayScreen.addMessage(missMsg)
        } else {
            val results = user.attack(sucker, dmgMod = 5)
            if (results.hit) {
                PlayScreen.addMessage(
                    "${user.markupString} brings down a stone-breaking blow upon ${sucker.markupString}!")
                sucker.takeDmg(results.dmg)
            }
            describeCombat(results)
        }
        user.vit!!.curEnd -= cost
        return user.atkDly
    }
}

class Rumble(userID: String) : WolfSkill(userID, "Rumble",
                                         desc = "A rattling blow that stuns enemies, setting them up for other moves in this style",
                                         aoe = PointAOE(DEFAULT_POINT, 1, 1),
                                         cd = 50,
                                         cost = 10,
                                         skillIndex = 2) {

    override val isAvailable: Boolean
        get() = super.isAvailable && user.hasEffect("Hulking Titan Stance")

    override fun use(target: Coord): Int {
        val sucker = targetEntity(target)
        val missMsg = "${user.markupString} pounds the ground!"

        if (sucker == null) {
            PlayScreen.addMessage(missMsg)
        } else {
            val results = user.attack(sucker, user.atk, sucker.sav)
            if (results.hit) {
                PlayScreen.addMessage(
                    "${user.markupString} pounds the ground at ${sucker.markupString}'s feet, stunning them!")
                sucker.takeDmg(results.dmg)
                sucker.applyEffect(StunEffect(sucker.eID, 10))
            }
            describeCombat(results)
        }
        return user.atkDly
    }
}

