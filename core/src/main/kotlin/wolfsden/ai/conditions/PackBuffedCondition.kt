package wolfsden.ai.conditions

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import wolfsden.entity.Entity
import wolfsden.log
import wolfsden.system.Scheduler.clock
import wolfsden.system.visibleAllies

class PackBuffedCondition : LeafTask<Entity>() {
    @TaskAttribute(name="buffName", required=true)
    @JvmField
    var buffName: String = ""

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        (task as PackBuffedCondition).buffName = buffName
        return task
    }

    override fun execute(): Status {
        val subject = `object`
        return if (subject.visibleAllies().all { it.hasEffect(buffName) }) {
            log(clock, "AI", "All allies of $subject have $buffName")
            Status.SUCCEEDED
        } else {
            log(clock, "AI", "Some allies of $subject lack $buffName")
            Status.FAILED
        }
    }


}