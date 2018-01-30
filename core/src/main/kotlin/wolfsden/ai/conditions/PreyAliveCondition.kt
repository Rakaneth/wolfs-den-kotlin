package wolfsden.ai.conditions

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import wolfsden.entity.Entity
import wolfsden.log
import wolfsden.system.Scheduler.clock

class PreyAliveCondition : LeafTask<Entity>() {
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        return task!!
    }

    override fun execute(): Status {
        val subject = `object`
        val target = subject.ai!!.getTarget()
        return when {
            target == null -> {
                log(clock, "AI", "$subject's target is null, not alive")
                Status.FAILED
            }
            target.vit!!.alive -> {
                log(clock, "AI", "$subject's target $target is alive")
                Status.SUCCEEDED
            }
            else -> {
                log(clock, "AI", "$subject's target $target is dead")
                subject.ai!!.target = ""
                Status.FAILED
            }
        }
    }
}