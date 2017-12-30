package wolfsden.screen

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import squidpony.squidgrid.gui.gdx.SquidInput

object PlayScreen: WolfScreen("main") {
    override val vport = StretchViewport(fullPixelW, fullPixelH)
    override val stage = Stage(vport, batch)
    override val input = SquidInput({ key, alt, ctrl, shift ->
        TODO("Write keyhandler for main screen")
    })

    private val sqp = panel {
        gw = 80
        gh = 40
        tcf {
            tweakWidth = 1.2f
            tweakHeight = 1.5f
        }
    }
    init {
        if (input.hasNext()) input.next()
        sqp.put(40, 20, "DSL SUCCESS!")
        stage.addActor(sqp)
    }

    override fun render() {
        stage.act()
        stage.draw()
    }





}