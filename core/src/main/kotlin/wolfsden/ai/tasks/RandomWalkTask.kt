package wolfsden.ai.tasks

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import squidpony.squidgrid.Direction
import wolfsden.entity.Entity
import wolfsden.log
import wolfsden.system.CommandProcessor.process
import wolfsden.system.Scheduler.clock
import wolfsden.system.WolfRNG

class RandomWalkTask : LeafTask<Entity>() {

    override fun execute(): Status {
        val d = WolfRNG.wolfRNG.getRandomElement(Direction.values())
        process(`object`, "move", d)
        log(clock, "AI", "$`object` has nothing to do, so walks randomly")
        return Status.SUCCEEDED
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        return task!!
    }
}