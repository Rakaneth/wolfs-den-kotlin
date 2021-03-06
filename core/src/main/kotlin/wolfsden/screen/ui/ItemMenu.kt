package wolfsden.screen.ui

import squidpony.squidgrid.gui.gdx.DefaultResources
import squidpony.squidgrid.gui.gdx.TextCellFactory
import wolfsden.entity.Entity
import wolfsden.screen.PlayScreen
import wolfsden.screen.WolfScreen
import wolfsden.setUp
import wolfsden.system.CommandProcessor
import wolfsden.system.GameStore
import wolfsden.system.hasTag

class ItemMenu(tcf: TextCellFactory = DefaultResources.getSlabFamily()
        .setUp(tw = 1.2f, th = 1.5f))
    : WolfMenu(tcf) {

    private lateinit var theItem: Entity

    fun setItem(item: Entity) {
        theItem = item
        menuItems = if (item.hasTag("item")) {
            listOf("Use", "Describe")
        } else {
            listOf("Equip", "Describe")
        }
        val maxLength = (menuItems.maxBy { it.length })!!.length + 2
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
            (WolfScreen.screens["main"] as PlayScreen).curState.changeState(MenuState.PLAY)
        }
    }
}