package wolfsden.ai.tasks

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import wolfsden.entity.Entity
import wolfsden.log
import wolfsden.system.CommandProcessor
import wolfsden.system.Scheduler.clock

class HowlTask : LeafTask<Entity>() {
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        return task!!
    }

    override fun execute(): Status {
        val subject = `object`
        val howlSkill = subject.getSkillByName("Howl")
        subject.ai!!.skillInUse = howlSkill
        return if (howlSkill.isAvailable) {
            CommandProcessor.process(subject, "skill", subject.pos!!.coord)
            log(clock, "AI", "$subject howls for its pack")
            Status.SUCCEEDED
        } else {
            log(clock, "AI", "$subject wants to howl, but it is on CD")
            CommandProcessor.process(subject, "wait")
            Status.RUNNING
        }
    }
}