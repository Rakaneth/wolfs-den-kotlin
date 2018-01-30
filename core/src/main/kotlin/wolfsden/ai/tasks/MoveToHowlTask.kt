package wolfsden.ai.tasks

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import squidpony.squidai.BurstAOE
import squidpony.squidgrid.LOS
import wolfsden.entity.Entity
import wolfsden.getCoords
import wolfsden.log
import wolfsden.system.CommandProcessor
import wolfsden.system.Faction
import wolfsden.system.Scheduler.clock
import wolfsden.system.visibleAllies

class MoveToHowlTask : LeafTask<Entity>() {
    private val los = LOS()

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        return task!!
    }

    override fun execute(): Status {
        val subject = `object`
        val howlSkill = subject.getSkillByName("Howl")
        val userC = subject.pos!!.coord
        val allyCoords = subject.visibleAllies().getCoords()
        val bestHowlLocs = howlSkill.idealLocations(userC, allyCoords, null)
        return if (bestHowlLocs.containsKey(userC) || howlSkill.possibleTargets(userC).containsAll(allyCoords)) {
            log(clock, "AI", "$subject is in range to howl")
            Status.SUCCEEDED
        } else {
            log(clock, "AI", "$subject is moving in range to howl")
            val dMap = Faction.getDMap(subject.eID)
            val radius = (howlSkill.aoe as BurstAOE).radius
            val path = dMap.findAttackPath(1,
                    radius,
                    los,
                    allyCoords,
                    allyCoords,
                    userC,
                    *allyCoords.toTypedArray())
            if (path.isNotEmpty()) {
                CommandProcessor.process(subject, "move", userC.toGoTo(path[0]))
                Status.RUNNING
            } else {
                log(clock, "AI", "$subject cannot find path to get in range to howl")
                CommandProcessor.process(subject, "wait")
                Status.RUNNING
            }
        }
    }
}