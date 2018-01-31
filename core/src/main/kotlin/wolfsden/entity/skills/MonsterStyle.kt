package wolfsden.entity.skills

import squidpony.squidai.AimLimit
import squidpony.squidai.BurstAOE
import squidpony.squidgrid.Radius
import squidpony.squidmath.Coord
import wolfsden.entity.effects.MightEffect
import wolfsden.entity.effects.WeakEffect
import wolfsden.screen.PlayScreen
import wolfsden.system.GameStore
import wolfsden.system.isAlly

private val DEFAULT_POINT = Coord.get(0, 0)

class WolfHowl(userID: String) : WolfSkill(userID, "Howl",
        desc = "A howl that rallies packmates and disquiets foes",
        aoe = BurstAOE(DEFAULT_POINT, 5, Radius.SQUARE, 0, 5),
        cd = 100,
        cost = 0,
        targeting = false,
        isAttack = false) {

    override fun use(target: Coord): Int {
        allTargets(user.pos!!.coord).forEach {
            if (it.isAlly(user))
                it.applyEffect(MightEffect(it.eID, 100))
            else
                it.applyEffect(WeakEffect(it.eID, 50))
        }
        PlayScreen.addMessage("${user.markupString} howls, bolstering the pack and chilling foes!")
        return 10
    }
}