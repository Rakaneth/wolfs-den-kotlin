package wolfsden.screen.ui

import squidpony.squidgrid.gui.gdx.TextCellFactory
import wolfsden.entity.Entity
import wolfsden.screen.PlayScreen
import wolfsden.screen.WolfScreen
import wolfsden.system.CommandProcessor
import wolfsden.system.GameStore

class ItemMenu (tcf: TextCellFactory) : WolfMenu(tcf = tcf) {

    lateinit private var theItem: Entity

    fun setItem(item: Entity) {
        theItem = item
        menuItems = if (item.hasTag("item")) {
            listOf("Use", "Describe")
        } else {
            listOf("Equip", "Describe")
        }
        val maxLength = (menuItems.maxBy { it.length})!!.length + 2
        val maxHeight = menuItems.size + 2
        val baseX = (WolfScreen.fullGridW - maxLength) / 2
        val baseY = (WolfScreen.fullGridH - maxHeight) / 2
        setGridWidth(maxLength)
        setGridHeight(maxHeight)
        setGridPos(baseX, baseY)
    }

    override fun handleSelected() {
        if (getSelected() == "Describe") {
            TODO("Need to implement Dialog for descriptions")
        } else {
            CommandProcessor.process(GameStore.player, "use", theItem)
            PlayScreen.curState.changeState(MenuState.PLAY)
        }
    }
}