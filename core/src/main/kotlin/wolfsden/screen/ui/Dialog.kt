package wolfsden.screen.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import squidpony.ArrayTools
import squidpony.panel.IColoredString
import squidpony.squidgrid.gui.gdx.DefaultResources
import squidpony.squidgrid.gui.gdx.SColor
import squidpony.squidgrid.gui.gdx.SquidPanel
import squidpony.squidgrid.gui.gdx.TextCellFactory
import wolfsden.nz
import wolfsden.screen.PlayScreen
import wolfsden.screen.WolfScreen
import wolfsden.setUp
import wolfsden.system.DialogManager

class Dialog(var caption: String = "", tcf: TextCellFactory = DefaultResources.getSlabFamily()
        .setUp(tw = 1.2f, th = 1.5f))
    : SquidPanel(10, 10, tcf), WolfSelector {

    val gw
        get() = menuItems.map { it.length }.max().nz() + 2

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
        get() = toWrap.size + menuItems.size + 3

    override var selected = 0

    private val playScreen
        get() = WolfScreen.screens["main"] as PlayScreen

    private fun setDimensions() {
        require(vertSize < playScreen.mapH - 2, { "Dialog is too tall" })
        require(gw < playScreen.mapW - 2, { "Dialog is too long" })
        setGridWidth(gw)
        setGridHeight(vertSize)
        contents = ArrayTools.fill(' ', gw, vertSize)
        colors = ArrayTools.fill(SColor.FLOAT_BLACK, gw, vertSize)
        val left = WolfScreen.cellWidth * maxOf((playScreen.mapW - gw) / 2, 0)
        val pTop = WolfScreen.cellHeight * maxOf((playScreen.mapH - vertSize) / 2, playScreen.msgH)
        setPosition(left, pTop)
    }

    override fun handleSelected() {
        val sl = menuItems[selected]
        DialogManager.select(DialogManager.curDialog!!.options.first { it.text == sl }.result)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        toFront()
        putBorders(SColor.FLOAT_WHITE, caption)
        var offset = 0
        toWrap.forEachIndexed { idx, line ->
            put(1, idx + 1, line)
            offset = idx + 2
        }
        (1 until gridWidth - 1).forEach {
            put(it, offset, "─")
        }
        menuItems.forEachIndexed { idx, s ->
            put(1, idx + offset + 1, s, if (idx == selected) SColor.LIGHT_BLUE else SColor.WHITE)
        }
        super.draw(batch, parentAlpha)
    }
}