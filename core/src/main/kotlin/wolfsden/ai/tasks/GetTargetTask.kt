package wolfsden.ai.tasks

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import wolfsden.entity.Entity
import wolfsden.log
import wolfsden.system.Scheduler.clock
import wolfsden.system.visibleEnemies

class GetTargetTask: LeafTask<Entity>() {
    override fun execute(): Status {
        val prey = `object`.visibleEnemies()
        val closest = prey.minBy { `object`.pos!!.coord.distance(it.pos?.coord)}
        return if (closest == null) {
            log(clock, "AI", "${`object`.eID} cannot get target")
            Status.FAILED
        } else {
            `object`.ai!!.target = closest.eID
            log(clock, "AI", "${`object`.eID} gets target ${closest.eID}")
            Status.SUCCEEDED
        }
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        return task!!
    }

}