package wolfsden.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.StretchViewport
import squidpony.panel.IColoredString
import squidpony.squidgrid.gui.gdx.SquidInput
import wolfsden.toICString

abstract class WolfScreen(val name: String) : Disposable {
    companion object Params {
        const val cellWidth = 15f
        const val cellHeight = 20f
        const val fullGridW = 120
        const val fullGridH = 40
        const val fullPixelW = fullGridW * cellWidth
        const val fullPixelH = fullGridH * cellHeight
        var curScreen: WolfScreen? = null
        val batch = SpriteBatch()
        fun setScreen(screen: WolfScreen) {
            curScreen?.exit()
            curScreen = screen
            curScreen?.enter()
        }
    }

    abstract val stage: Stage
    abstract var input: SquidInput
    abstract val vport: StretchViewport

    abstract fun render()
    open fun enter() {
        println("Entered $name screen.")
    }

    open fun exit() {
        println("Exited $name screen")
    }

    protected fun markup(text: String, color: String): IColoredString<out Color> = "[$color]$text[]".toICString()

    fun activateInput(stage: Stage, input: InputProcessor) {
        Gdx.input.inputProcessor = InputMultiplexer(stage, input)
    }

    override fun dispose() {
        stage.dispose()
        batch.dispose()
    }
}
