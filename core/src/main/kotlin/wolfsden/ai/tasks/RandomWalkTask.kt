package wolfsden.ai.tasks

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import squidpony.squidgrid.Direction
import wolfsden.entity.Entity
import wolfsden.system.CommandProcessor.process
import wolfsden.system.WolfRNG

class RandomWalkTask : LeafTask<Entity>() {

    override fun execute(): Status {
        val d = WolfRNG.wolfRNG.getRandomElement(Direction.values())
        process(`object`, "move", d)
        return Status.SUCCEEDED
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        return task!!
    }
}