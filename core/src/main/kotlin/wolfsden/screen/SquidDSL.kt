package wolfsden.screen

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import squidpony.squidgrid.gui.gdx.*
import wolfsden.screen.WolfScreen.Params.batch
import wolfsden.screen.WolfScreen.Params.cellHeight
import wolfsden.screen.WolfScreen.Params.cellWidth

fun layout(vport: StretchViewport, block: Layout.() -> Unit): Layout = Layout(vport).apply(block)

class Layout(vport: StretchViewport) {
    private val stage = Stage(vport, batch)
    private val actors: MutableMap<String, Actor> = mutableMapOf()
    fun panel(block: PanelBuilder.() -> Unit) {
        val pb = PanelBuilder().apply(block)
        actors[pb.id] = pb.build()
        stage.addActor(actors[pb.id])
    }

    fun layers(block: SparseBuilder.() -> Unit) {
        val sb = SparseBuilder().apply(block)
        actors[sb.id] = sb.build()
        stage.addActor(actors[sb.id])
    }

    fun messages(block: MessageBuilder.() -> Unit) {
        val mb = MessageBuilder().apply(block)
        actors[mb.id] = mb.build()
        stage.addActor(actors[mb.id])
    }

    fun sqLayers(block: SquidLayersBuilder.() -> Unit) {
        val sqb = SquidLayersBuilder().apply(block)
        actors[sqb.id] = sqb.build()
        stage.addActor(actors[sqb.id])
    }

    fun toSquidPanel(id: String) = actors[id] as SquidPanel
    fun toMessageBox(id: String) = actors[id] as SquidMessageBox
    fun toSparseLayers(id: String) = actors[id] as SparseLayers
    fun toSquidLayers(id: String) = actors[id] as SquidLayers

    fun build(): Stage = stage
}

class SparseBuilder {
    var id: String = ""
    var gw = 0
    var gh = 0
    var x = 0
    var y = 0
    private var _tcf: TextCellFactory? = null
    fun tcf(block: TCFBuild.() -> Unit) {
        _tcf = TCFBuild().apply(block).build()
    }

    fun build(): SparseLayers {
        val spl = SparseLayers(gw, gh, cellWidth, cellHeight, _tcf)
        spl.setPosition(x * cellWidth, y * cellHeight)
        return spl
    }
}

class MessageBuilder {
    var gw = 0
    var gh = 0
    var x = 0
    var y = 0
    var id: String = ""
    private var _tcf: TextCellFactory? = null
    fun tcf(block: TCFBuild.() -> Unit) {
        _tcf = TCFBuild().apply(block).build()
    }

    fun build(): SquidMessageBox {
        val msg = SquidMessageBox(gw, gh, _tcf)
        msg.setBounds(x * cellWidth, y * cellHeight, gw * cellWidth, gh * cellHeight)
        return msg
    }
}

class PanelBuilder {
    var gw: Int = 0
    var gh: Int = 0
    var x = 0
    var y = 0
    var borderColor = SColor.FLOAT_WHITE
    var borderCaption: String? = null
    var id: String = ""
    private var _tcf: TextCellFactory? = null

    fun tcf(block: TCFBuild.() -> Unit) {
        _tcf = TCFBuild().apply(block).build()
    }

    fun build(): SquidPanel {
        val sqp = SquidPanel(gw, gh, _tcf)
        sqp.setPosition(x * cellWidth, y * cellHeight)
        sqp.putBorders(borderColor, borderCaption)
        return sqp
    }
}

class SquidLayersBuilder {
    var gw: Int = 0
    var gh: Int = 0
    var x = 0
    var y = 0
    var borderColor = SColor.FLOAT_WHITE
    var borderCaption: String? = null
    var id: String = ""
    private lateinit var _tcf: TextCellFactory

    fun tcf(block: TCFBuild.() -> Unit) {
        _tcf = TCFBuild().apply(block).build()
    }

    fun build(): SquidLayers {
        val sql = SquidLayers(gw, gh, cellWidth.toInt(), cellHeight.toInt(), _tcf)
        sql.setPosition(x * cellWidth, y * cellHeight)
        sql.backgroundLayer.putBorders(borderColor, borderCaption)
        return sql
    }
}

class TCFBuild {
    var tweakWidth = 1f
    var tweakHeight = 1f
    var base: TextCellFactory = DefaultResources.getSlabFamily()

    fun build(): TextCellFactory = base.width(WolfScreen.cellWidth).height(WolfScreen.cellHeight)
            .tweakWidth(tweakWidth * WolfScreen.cellWidth).tweakHeight(tweakHeight * WolfScreen.cellHeight)
            .initBySize()
}