package wolfsden.screen.ui

import com.badlogic.gdx.scenes.scene2d.Actor

interface WolfSelector {
    var selected: Int
    var menuItems: List<String>
    fun nextItem() {
        val nxt = selected + 1
        selected = if (nxt >= menuItems.size) 0 else nxt
    }

    fun prevItem() {
        val prev = selected - 1
        selected = if (prev < 0) menuItems.size - 1 else prev
    }

    fun getSelected() = menuItems[selected]
    fun asActor() = this as Actor
    fun handleSelected()
}