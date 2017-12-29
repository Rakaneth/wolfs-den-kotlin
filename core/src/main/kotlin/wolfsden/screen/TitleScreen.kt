package wolfsden.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import squidpony.squidgrid.gui.gdx.DefaultResources
import squidpony.squidgrid.gui.gdx.SColor
import squidpony.squidgrid.gui.gdx.SquidInput
import squidpony.squidgrid.gui.gdx.SquidPanel

object TitleScreen : WolfScreen("title") {
    override val vport = StretchViewport(fullPixelW, fullPixelH)
    override val stage = Stage(vport, batch)
    override val input = SquidInput({ key, alt, ctrl, shift ->
        when (key) {
            'n', 'N' -> TODO("New game procedures in progress")
            else -> {
            }
        }
    })
    private val slab = DefaultResources.getStretchableSlabFont().width(cellWidth).height(cellHeight).initBySize()
    private val display = SquidPanel(120, 40, slab)

    init {
        display.setBounds(0f, 0f, fullPixelW, fullPixelH)
        stage.addActor(display)
        setInput()
    }

    override fun render() {
        display.put(54, 18, "Wolf's Den II", SColor.WHITE)
        display.put(54, 19, "by Rakaneth", SColor.WHITE)
        display.put(54, 20, "[N]ew Game", SColor.WHITE)
        if (input.hasNext()) input.next()
        stage.act()
        stage.draw()
    }
}