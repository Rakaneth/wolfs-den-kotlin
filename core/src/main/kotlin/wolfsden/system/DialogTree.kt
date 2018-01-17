package wolfsden.system

import com.badlogic.gdx.Gdx
import java.awt.Dialog

open class DialogTree {
    open var caption = ""
    open var dialogText = ""
    open var children: Map<String, String> = mapOf()
    val isLeaf
        get() = children.isEmpty() || children.all { it.value == "continue" }
}

class DefaultTree : DialogTree() {
    override var caption = ""
    override var dialogText = ""
    override var children: Map<String, String> = mapOf("(Continue)" to "continue")
}

object DialogTreeManager {
    var curTree: DialogTree = DialogTree()
    private var registeredTrees: MutableMap<String, DialogTree> = mutableMapOf()

    fun select(option: String) {
        val result = curTree.children[option]!!
        when {
            result == "continue" -> curTree = DialogTree()
            registeredTrees.containsKey(result) -> curTree = registeredTrees[result]!!
            else -> {}
        }
    }

    fun register(ref: String, tree: DialogTree) {
        registeredTrees[ref] = tree
    }

    fun unregister(ref: String) {
        registeredTrees.remove(ref)
    }

    fun parseConvoFile(fileName: String) {
        //TODO: Unfinished
        val regNamePattern = Regex("""\[(.*)]""")
        Gdx.files.internal(fileName).reader().readLines().forEach {
            val dTree = DialogTree()
            val match = regNamePattern.matchEntire(it)

            when  {
                match != null -> {
                    val (id) = match.destructured
                    register(id, dTree)
                }
               // it.split(":")[0] == "option"
            }
        }
    }




}



