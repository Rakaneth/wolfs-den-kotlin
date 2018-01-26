package wolfsden.entity.skills

import squidpony.squidai.PointAOE
import squidpony.squidmath.Coord
import wolfsden.CommonColors
import wolfsden.screen.PlayScreen
import wolfsden.system.Location
import wolfsden.system.attack

class Stonebreaker(override val userID: String) : WolfSkill(userID, "Stonebreaker",
        "A devastating strike to a single foe",
        PointAOE(Coord.get(0, 0)),
        20) {

    override val isAvailable: Boolean
        get() = super.isAvailable && user.hasEffect("Hulking Titan Stance")

    override fun use(target: Coord): Int {
        val affected = apply(user.pos!!.coord, target)
        val spot = affected.filter { it.value > 0.0 }.keys.first()
        val sucker = Location.thingsAt(spot, user.pos!!.mapID).firstOrNull() { it.isCreature }
        val missMsg = "${user.markupString} swings mightily, but finds nothing there!"

        if (sucker == null) {
            PlayScreen.addMessage(missMsg)
        } else {
            val results = user.attack(sucker, dmgMod = 4)
            if (results.hit) {
                PlayScreen.addMessage("${user.markupString} brings down a crashing blow upon ${sucker.markupString}," +
                        "dealing [${CommonColors.VIT}]${results.dmg} damage![]")
                sucker.takeDmg(results.dmg)
            } else {
                PlayScreen.addMessage(missMsg)
            }
        }
        return user.atkDly + 5
    }
}