package wolfsden.ai.conditions

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import wolfsden.entity.Entity
import wolfsden.log
import wolfsden.system.Scheduler.clock

class PreyAdjacentCondition: LeafTask<Entity>() {
    override fun execute(): Status {
        val subject = `object`
        var target = subject.ai!!.getTarget()

        return when {
            target == null -> {
                log(clock, "AI", "${subject}'s target is null, not adjacent")
                Status.FAILED
            }
            subject.pos!!.coord.isAdjacent(target.pos!!.coord) -> {
                log(clock, "AI", "${subject}'s mark is adjacent")
                Status.SUCCEEDED
            }
            else -> {
                log(clock, "AT", "${subject}'s mark is not adjacent")
                Status.FAILED
            }
        }

    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        return task!!
    }
}