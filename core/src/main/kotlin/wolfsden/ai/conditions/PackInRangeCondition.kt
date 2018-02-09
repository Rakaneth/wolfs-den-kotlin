package wolfsden.ai.conditions

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import wolfsden.entity.Entity
import wolfsden.getCoords
import wolfsden.log
import wolfsden.system.CommandProcessor
import wolfsden.system.Scheduler.clock
import wolfsden.system.getMap
import wolfsden.system.visibleAllies

class PackInRangeCondition : LeafTask<Entity>() {
    @TaskAttribute(required = true, name = "skillName")
    @JvmField
    var skillName: String = ""

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        (task as PackInRangeCondition).skillName = skillName
        return task
    }

    override fun execute(): Status {
        val subject = `object`
        val allyCoords = subject.visibleAllies().getCoords()
        val theSkill = subject.getSkillByName(skillName)
        val resistances = subject.getMap().resistances
        val possTargets = theSkill.possibleTargets(subject.pos!!.coord, resistances)
        return if (possTargets.containsAll(allyCoords)) {
            log(clock, "AI", "All allies of $subject are in range for $skillName")
            Status.SUCCEEDED
        } else {
            log(clock, "AI", "Some allies of $subject are not in range for $skillName")
            CommandProcessor.process(subject, "wait")
            Status.FAILED
        }
    }
}