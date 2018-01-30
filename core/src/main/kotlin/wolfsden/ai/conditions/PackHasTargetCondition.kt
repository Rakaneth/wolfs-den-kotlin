package wolfsden.ai.conditions

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import wolfsden.entity.Entity
import wolfsden.log
import wolfsden.system.Scheduler.clock
import wolfsden.system.visibleAllies

class PackHasTargetCondition : LeafTask<Entity>() {
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        return task!!
    }

    override fun execute(): Status {
        val subject = `object`
        return if (subject.visibleAllies().all { it.ai!!.target == subject.ai!!.target }) {
            log(clock, "AI", "All of $subject's pack has $subject's target")
            Status.SUCCEEDED
        } else {
            log(clock, "AI", "Some of $subject's pack lack $subject's target")
            Status.FAILED
        }
    }
}