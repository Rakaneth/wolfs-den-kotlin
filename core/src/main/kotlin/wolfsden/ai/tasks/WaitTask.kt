package wolfsden.ai.tasks

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import wolfsden.entity.Entity
import wolfsden.log
import wolfsden.system.CommandProcessor.process
import wolfsden.system.Scheduler.clock

class WaitTask : LeafTask<Entity>() {
    override fun execute(): Status {
        process(`object`, "wait")
        log(clock, "AI", "$`object` has nothing to do, so waits")
        return Status.SUCCEEDED
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        return task!!
    }
}