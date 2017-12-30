package wolfsden.screen

import squidpony.squidgrid.gui.gdx.*

fun panel(block: PanelBuilder.() -> Unit): SquidPanel = PanelBuilder().apply(block).build()

class PanelBuilder {
    var gw: Int = 0
    var gh: Int = 0
    var x = 0
    var y = 0
    var borderColor = SColor.FLOAT_WHITE
    var borderCaption: String? = null
    private var _tcf: TextCellFactory? = null

    fun tcf(block: TCFBuild.() -> Unit) {
        _tcf = TCFBuild().apply(block).build()
    }

    fun build(): SquidPanel {
        val sqp = SquidPanel(gw, gh, _tcf)
        sqp.setPosition(x * WolfScreen.cellWidth, y * WolfScreen.cellHeight)
        sqp.putBorders(borderColor, borderCaption)
        return sqp
    }
}

class TCFBuild {
    var tweakWidth = 1f
    var tweakHeight = 1f
    var base = DefaultResources.getSlabFamily()

    fun build(): TextCellFactory = base.width(WolfScreen.cellWidth).height(WolfScreen.cellHeight).tweakWidth(tweakWidth * WolfScreen.cellWidth).tweakHeight(tweakHeight * WolfScreen.cellHeight).initBySize()
}