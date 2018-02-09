package wolfsden.screen.ui

import com.badlogic.gdx.graphics.g2d.Batch
import squidpony.squidgrid.gui.gdx.SColor
import squidpony.squidgrid.gui.gdx.SquidPanel
import squidpony.squidgrid.gui.gdx.TextCellFactory
import wolfsden.screen.WolfScreen

abstract class WolfMenu(
    tcf: TextCellFactory,
    private val caption: String? = null,
    override var menuItems: List<String> = listOf()
) : SquidPanel(10, 10, tcf), WolfSelector {

    protected fun setGridPos(x: Int, y: Int) {
        setPosition(WolfScreen.cellWidth * x, WolfScreen.cellHeight * y)
    }

    override var selected = 0

    override abstract fun handleSelected()

    override fun draw(batch: Batch?, parentAlpha: Float) {
        require(menuItems.isNotEmpty(), { "menuItems must contain at least one item" })
        //erase()
        toFront()
        if (caption == null) putBorders() else putBorders(SColor.FLOAT_WHITE, caption)
        menuItems.forEachIndexed { index, s ->
            put(1, 1 + index, s, if (selected == index) SColor.LIGHT_BLUE else SColor.WHITE)
        }
        super.draw(batch, parentAlpha)
    }
}

