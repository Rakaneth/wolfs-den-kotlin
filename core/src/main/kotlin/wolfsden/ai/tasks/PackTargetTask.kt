package wolfsden.ai.tasks

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import wolfsden.entity.Entity
import wolfsden.log
import wolfsden.system.Scheduler.clock
import wolfsden.system.visibleAllies

class PackTargetTask : LeafTask<Entity>() {
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        return task!!
    }

    override fun execute(): Status {
        val subject = `object`
        subject.visibleAllies().forEach {
            it.ai!!.target = subject.ai!!.target
        }
        log(clock, "AI", "$subject has setpack's target to its own")
        return Status.SUCCEEDED
    }
}