package wolfsden.ai.tasks

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import wolfsden.entity.Entity
import wolfsden.getCoords
import wolfsden.log
import wolfsden.system.CommandProcessor
import wolfsden.system.Faction
import wolfsden.system.Scheduler.clock
import wolfsden.system.visibleAllies

class FollowLeaderTask : LeafTask<Entity>() {
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        return task!!
    }

    override fun execute(): Status {
        val subject = `object`
        val userC = subject.pos!!.coord
        val alphaC = subject.ai!!.getLeader()!!.pos!!.coord
        val allyCoords = subject.visibleAllies().getCoords()
        val dMap = Faction.getDMap(subject.eID)
        val path = dMap.findPath(1, null, allyCoords, userC, alphaC)
        return if (path.isEmpty()) {
            CommandProcessor.process(subject, "wait")
            log(clock, "AI", "$subject cannot find path to leader")
            Status.FAILED
        } else {
            CommandProcessor.process(subject, "move", userC.toGoTo(path[0]))
            log(clock, "AI", "$subject moves toward leader")
            Status.SUCCEEDED
        }
    }
}