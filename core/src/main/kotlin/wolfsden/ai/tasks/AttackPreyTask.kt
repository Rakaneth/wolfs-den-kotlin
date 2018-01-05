package wolfsden.ai.tasks

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import wolfsden.entity.Entity
import wolfsden.system.CommandProcessor.process

class AttackPreyTask : LeafTask<Entity>() {
    override fun execute(): Status {
        val subject = `object`
        val target = subject.ai!!.getTarget()!!
        process(subject, "attack", target)
        return Status.SUCCEEDED
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        return task!!
    }
}