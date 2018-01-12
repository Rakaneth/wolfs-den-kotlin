package wolfsden.screen

import com.badlogic.gdx.graphics.g2d.Batch
import squidpony.squidgrid.gui.gdx.DefaultResources
import squidpony.squidgrid.gui.gdx.SColor
import squidpony.squidgrid.gui.gdx.SquidPanel
import squidpony.squidgrid.gui.gdx.TextCellFactory

abstract class WolfMenu (
        private val caption: String? = null,
        var menuItems: List<String> = listOf(),
        private val tcf: TextCellFactory = DefaultResources.getSlabFamily()
) : SquidPanel(10, 10, tcf) {

    init {
        tcf.width(WolfScreen.cellWidth)
                .height(WolfScreen.cellHeight)
                .tweakWidth((1.1f * WolfScreen.cellWidth))
                .tweakHeight((1.5f * WolfScreen.cellHeight))
                .initBySize()
    }

    protected fun setGridPos(x: Int, y: Int) {
        setPosition(WolfScreen.cellWidth * x, WolfScreen.cellHeight * y)
    }

    var selected = 0

    fun nextItem() {
        val nxt = selected + 1
        selected = if (nxt >= menuItems.size) 0 else nxt
    }

    fun prevItem() {
        val prev = selected - 1
        selected = if (prev < 0) menuItems.size - 1 else prev
    }

    fun getSelected() = menuItems[selected]

    abstract fun handleSelected()

    override fun draw(batch: Batch?, parentAlpha: Float) {
        require (menuItems.isNotEmpty(), { "menuItems must contain at least one item"})
        toFront()
        if (caption == null) putBorders() else putBorders(SColor.FLOAT_WHITE, caption)
        menuItems.forEachIndexed { index, s ->
            put(1, 1+index, s, if (selected == index) SColor.LIGHT_BLUE else SColor.WHITE)
        }
        super.draw(batch, parentAlpha)
    }
}

