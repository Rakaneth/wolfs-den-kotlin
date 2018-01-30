package wolfsden.ai.conditions

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import wolfsden.entity.Entity
import wolfsden.log
import wolfsden.system.Scheduler.clock

class BuffedCondition : LeafTask<Entity>() {
    @TaskAttribute(required = true)
    @JvmField
    var buffName: String = ""

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        (task as BuffedCondition).buffName = buffName
        return task
    }

    override fun execute(): Status {
        return if (`object`.hasEffect(buffName)) {
            log(clock, "AI", "$`object` has buff: $buffName")
            Status.SUCCEEDED
        } else {
            log(clock, "AI", "$`object` does not have buff: $buffName")
            Status.FAILED
        }
    }
}