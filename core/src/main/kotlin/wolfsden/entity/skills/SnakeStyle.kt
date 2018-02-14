package wolfsden.entity.skills

import squidpony.squidai.PointAOE
import squidpony.squidmath.Coord
import wolfsden.entity.effects.PoisonerEffect
import wolfsden.entity.effects.SnakeStanceEffect
import wolfsden.screen.PlayScreen
import wolfsden.system.attack
import wolfsden.system.describeCombat
import wolfsden.system.hasTag

private val DEFAULT_POINT = Coord.get(0, 0)

class CunningSnakeStance(userID: String) : WolfSkill(
    userID,
    "Cunning Snake Stance",
    desc = "Assume the attitude of a snake looking to strike.",
    skillType = SkillType.SNAKE,
    cd = 20,
    cost = 0,
    targeting = false,
    skillIndex = 0,
    isAttack = false
) {
    override fun use(target: Coord): Int {
        user.applyEffect(SnakeStanceEffect(user.eID))
        return 0
    }
}

class SwiftFangStrike(userID: String) : WolfSkill(
    userID,
    "Swift Fang Strike",
    "A cunning strike that takes advantage of speed.",
    skillType = SkillType.SNAKE,
    aoe = PointAOE(DEFAULT_POINT, 1, 1),
    cd = 50,
    cost = 2,
    skillIndex = 1
) {
    override val isAvailable: Boolean
        get() = super.isAvailable && user.dualWield && user.atkDly < 10

    override fun use(target: Coord): Int {
        val sucker = targetEntity(target)
        val missMsg = "${user.markupString} quickly stabs at the air!"

        if (sucker == null) {
            PlayScreen.addMessage(missMsg)
        } else {
            val results = user.attack(sucker, dmgMod = (10 - user.atkDly))
            if (results.hit) {
                PlayScreen.addMessage("${user.markupString} strikes like a snake!")
                sucker.takeDmg(results.dmg)
            }
            describeCombat(results)
        }
        return user.atkDly
    }
}

/*
 * Posssibly overpowered skill. Resets CDs for all other Cunning Snake skills.
 */
class Preparation(userID: String) : WolfSkill(
    userID,
    "Preparation",
    skillType = SkillType.SNAKE,
    desc = "Take a moment to breathe and prepare for the coming slaughter.",
    cd = 500,
    cost = 0,
    skillIndex = 2,
    targeting = false
) {
    override fun use(target: Coord): Int {
        PlayScreen.addMessage("${user.markupString} breathes and reflects on the grim tasks before him.")
        user.skillStack!!.getSkillsOfType(SkillType.SNAKE).filter { it.name != this.name }.forEach {
            it.resetCD()
        }
        return 0
    }
}

class PoisonedBlades(userID: String) : WolfSkill(
    userID,
    "Poisoned Blades",
    skillType = SkillType.SNAKE,
    desc = "Poison your blades to weaken foes.",
    cd = 100,
    cost = 0,
    skillIndex = 1,
    targeting = false
) {
    override val isAvailable: Boolean
        get() = super.isAvailable && (listOf(user.mh, user.oh).any {
            it?.getEntity?.hasTag("slash") == true || it?.getEntity?.hasTag("stab") == true
        })

    override fun use(target: Coord): Int {
        user.applyEffect(PoisonerEffect(user.eID, 50))
        return 10
    }
}