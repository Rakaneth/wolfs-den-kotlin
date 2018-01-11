package wolfsden.screen

import squidpony.squidgrid.gui.gdx.TextCellFactory
import wolfsden.entity.Entity
import wolfsden.system.CommandProcessor
import wolfsden.system.GameStore

class ItemMenu (
        private val theItem: Entity,
        tcf: TextCellFactory
) : WolfMenu(menuItems = if (theItem.hasTag("item")) listOf("Use", "Describe") else listOf("Equip", "Describe"),
        tcf = tcf) {

    init {
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