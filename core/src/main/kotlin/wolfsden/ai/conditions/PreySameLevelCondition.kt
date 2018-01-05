package wolfsden.ai.conditions

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import wolfsden.entity.Entity
import wolfsden.log
import wolfsden.system.Location.sameMap
import wolfsden.system.Scheduler.clock

class PreySameLevelCondition : LeafTask<Entity>() {
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        return task!!
    }

    override fun execute(): Status {
        val subject = `object`
        val target = subject.ai!!.getTarget()
        return when {
            target == null -> {
                log(clock, "AI", "$subject's target is null, not on same level")
                Status.FAILED
            }
            sameMap(subject, target) -> {
                log(clock, "AI", "$subject's target $target is on the same level")
                Status.SUCCEEDED
            }
            else -> {
                log(clock, "AI", "$subject's target $target is not on the same level")
                Status.FAILED
            }
        }
    }
}