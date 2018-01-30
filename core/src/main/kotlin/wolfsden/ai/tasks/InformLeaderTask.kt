package wolfsden.ai.tasks

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import wolfsden.entity.Entity
import wolfsden.log
import wolfsden.system.Scheduler.clock

class InformLeaderTask : LeafTask<Entity>() {
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        return task!!
    }

    override fun execute(): Status {
        val subject = `object`
        val leader = subject.ai!!.getLeader()
        return if (leader!!.ai!!.getTarget() == null) {
            leader.ai!!.target = subject.ai!!.target
            log(clock, "AI", "$subject informs leader of its target")
            Status.SUCCEEDED
        } else {
            log(clock, "AI", "$subject informs leader of its target but the pack has one")
            Status.FAILED
        }
    }
}