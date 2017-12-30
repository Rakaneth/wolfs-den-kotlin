package wolfsden.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import squidpony.panel.IColoredString
import squidpony.squidgrid.gui.gdx.GDXMarkup
import squidpony.squidgrid.gui.gdx.SquidInput

abstract class WolfScreen(val name: String) {
    companion object Params {
        const val cellWidth = 10f
        const val cellHeight = 20f
        const val fullGridW = 120
        const val fulllGridH = 40
        const val fullPixelW = 120f * cellWidth
        const val fullPixelH = 40f * cellHeight
        val batch = SpriteBatch()
        val vport = StretchViewport(fullPixelW, fullPixelH)
    }

    abstract val stage: Stage
    abstract val input: SquidInput

    abstract fun render()
    open fun enter() {
        println("Entered $name screen.")
    }

    open fun exit() {
        println("Exited $name screen")
    }

    fun String.toICString(): IColoredString<Color> {
        return GDXMarkup.instance.colorString(this)
    }

    fun setInput() {
        Gdx.input.inputProcessor = InputMultiplexer(stage, input)
    }
}
