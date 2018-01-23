package wolfsden.ai.tasks

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import wolfsden.entity.Entity
import wolfsden.log
import wolfsden.system.CommandProcessor.process
import wolfsden.system.Faction
import wolfsden.system.Scheduler.clock
import wolfsden.system.isLeader
import wolfsden.system.visibleAllies

class MoveTowardsPreyTask : LeafTask<Entity>() {
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        return task!!
    }

    override fun execute(): Status {
        val dMap = if (`object`.isLeader()){
            Faction.getDMap(`object`.eID)
        } else {
            Faction.getDMap(`object`.ai!!.leader)
        }

        val nextStep = dMap.findPath(1, null, `object`.visibleAllies().map { it.pos!!.coord }, `object`.pos!!.coord, `object`.ai!!.getTarget()!!.pos!!.coord)
        return if (nextStep.isEmpty()) {
            log(clock, "AI", "$`object` cannot find path to prey")
            Status.FAILED
        } else {
            log(clock, "AI", "$`object` moves toward prey")
            process(`object`, "move", `object`.pos!!.coord.toGoTo(nextStep[0]))
            Status.SUCCEEDED
        }
    }

}