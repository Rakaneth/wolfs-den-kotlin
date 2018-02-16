package wolfsden.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import squidpony.panel.IColoredString
import squidpony.squidgrid.gui.gdx.SquidInput
import wolfsden.entity.Entity
import wolfsden.toICString

abstract class WolfScreen(val name: String) {
    companion object {
        const val cellWidth = 15f
        const val cellHeight = 20f
        const val fullGridW = 120
        const val fullGridH = 40
        const val fullPixelW = fullGridW * cellWidth
        const val fullPixelH = fullGridH * cellHeight
        var curScreen: WolfScreen? = null
        var screens: MutableMap<String, WolfScreen> = mutableMapOf()
        fun setScreen(screenName: String) {
            curScreen?.exit()
            curScreen = screens[screenName]
            curScreen!!.activateInput(curScreen!!.stage, curScreen!!.input)
            curScreen?.enter()
        }

        fun register(screen: WolfScreen) {
            screens[screen.name] = screen
        }

        fun addMessage(msg: String) {
            val playScreen = screens["main"]!! as PlayScreen
            playScreen.addMessage(msg)
        }

        fun addMessageVisible(other: Entity, msg: String) {
            val playScreen = screens["main"]!! as PlayScreen
            playScreen.addMessageVisible(other, msg)
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
}
