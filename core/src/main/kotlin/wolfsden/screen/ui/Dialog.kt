package wolfsden.screen.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import squidpony.panel.IColoredString
import squidpony.squidgrid.gui.gdx.DefaultResources
import squidpony.squidgrid.gui.gdx.SColor
import squidpony.squidgrid.gui.gdx.SquidPanel
import squidpony.squidgrid.gui.gdx.TextCellFactory
import wolfsden.screen.WolfScreen
import wolfsden.system.DialogTreeManager

class Dialog(val caption: String = "", tcf: TextCellFactory = DefaultResources.getSlabFamily())
    : SquidPanel(10, 10, tcf), WolfSelector {
    companion object {
        const val gw = (WolfScreen.fullGridW * 0.75).toInt()
    }

    init {
        tcf.width(WolfScreen.cellWidth)
                .height(WolfScreen.cellHeight)
                .tweakWidth(1.1f * WolfScreen.cellWidth)
                .tweakHeight(1.5f * WolfScreen.cellHeight)
                .initBySize()
    }

    override var menuItems: List<String> = listOf()
        set(value) {
            field = value
            setDimensions()
        }
    var dialog: IColoredString<Color> = IColoredString.Impl()
        set(value) {
            field = value
            setDimensions()
        }
    private val toWrap
        get() = dialog.wrap(gw - 2)

    private val vertSize
        get() = toWrap.size + menuItems.size + 4

    override var selected = 0

    private fun setDimensions() {
        require(vertSize < WolfScreen.fullGridH, { "Dialog is too large" })
        setGridWidth(gw)
        setGridHeight(vertSize)
        setPosition(WolfScreen.cellWidth * ((WolfScreen.fullGridW - gw) / 2), WolfScreen.cellHeight * vertSize)
    }

    override fun handleSelected() {
        DialogTreeManager.select(menuItems[selected])
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        putBorders(SColor.FLOAT_WHITE, caption)
        var offset = 0
        toWrap.forEachIndexed { idx, line ->
            put(1, idx + 1, line)
            offset = idx + 1
        }
        (1 until gridWidth).forEach {
            put(it, offset, "-")
        }
        menuItems.forEachIndexed { idx, s ->
            put(1, idx + offset + 1, "$idx) $s", if (idx == selected) SColor.LIGHT_BLUE else SColor.WHITE)
        }
        super.draw(batch, parentAlpha)
    }
}