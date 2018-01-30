package wolfsden.ai.tasks

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import squidpony.squidgrid.LOS
import wolfsden.entity.Entity
import wolfsden.getCoords
import wolfsden.log
import wolfsden.nearest
import wolfsden.system.CommandProcessor
import wolfsden.system.Faction
import wolfsden.system.Scheduler.clock
import wolfsden.system.visibleAllies
import wolfsden.system.visibleEnemies

class UseSkillTask : LeafTask<Entity>() {
    @TaskAttribute(required = true) var skillName: String = ""
    private val los: LOS = LOS()

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        (task as UseSkillTask).skillName = ""
        return task
    }

    override fun execute(): Status {
        val subject = `object`
        val userC = subject.pos!!.coord
        val dMap = Faction.getDMap(subject.eID)
        val theSkill = subject.getSkillByName(skillName)
        val allyCoords = subject.visibleAllies().getCoords()
        val enemyCoords = subject.visibleEnemies().getCoords()
        val iLocs = if (theSkill.isAttack) {
            theSkill.idealLocations(userC,
                    enemyCoords,
                    allyCoords)
        } else {
            theSkill.idealLocations(userC,
                    setOf(userC),
                    allyCoords.toSet(),
                    enemyCoords.toSet())
        }
        val possTargets = theSkill.possibleTargets(userC)
        return if (possTargets.any { iLocs.containsKey(it) }) {
            subject.ai!!.skillInUse = theSkill
            val bestTargets = possTargets.filter { iLocs.containsKey(it) }
            CommandProcessor.process(subject, "skill", bestTargets.nearest(userC))
            log(clock, "AI", "$subject uses $theSkill")
            Status.SUCCEEDED
        } else {
            val path = dMap.findAttackPath(1, theSkill.aoe.maxRange, los, null, allyCoords, userC, *iLocs.keys.toTypedArray())
            CommandProcessor.process(subject, "move", userC.toGoTo(path[0]))
            log(clock, "AI", "$subject moves in range to use $theSkill")
            Status.RUNNING
        }
    }
}