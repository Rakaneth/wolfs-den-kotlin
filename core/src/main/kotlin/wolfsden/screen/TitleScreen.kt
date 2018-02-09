package wolfsden.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import squidpony.squidgrid.gui.gdx.DefaultResources
import squidpony.squidgrid.gui.gdx.SColor
import squidpony.squidgrid.gui.gdx.SquidInput
import squidpony.squidgrid.gui.gdx.SquidPanel
import wolfsden.system.GameStore
import java.io.File

object TitleScreen : WolfScreen("title") {
    override val vport = StretchViewport(fullPixelW, fullPixelH)
    override val stage = Stage(vport, batch)
    override var input = SquidInput({ key, _, _, _ ->
                                        when (key) {
                                            'n', 'N' -> WolfScreen.setScreen(CCScreen)
                                            SquidInput.ESCAPE -> Gdx.app.exit()
                                            '0', '1', '2' -> {
                                                val selected = key.toString().toInt()
                                                if (fileList.size >= selected + 1) {
                                                    val sFile = fileList[selected].name
                                                    GameStore.loadGame(sFile)
                                                    WolfScreen.setScreen(PlayScreen)
                                                }
                                            }
                                            ')' -> if (fileList.isNotEmpty()) GameStore.deleteGame(fileList[0].name)
                                            '!' -> if (fileList.size >= 2) GameStore.deleteGame(fileList[1].name)
                                            '@' -> if (fileList.size >= 3) GameStore.deleteGame(fileList[2].name)
                                            else -> {
                                            }
                                        }
                                    })
    private val slab = DefaultResources.getSlabFamily().width(cellWidth).height(cellHeight)
            .tweakHeight(cellHeight * 1.25f).initBySize()
    private val display = SquidPanel(120, 40, slab)
    private val fileList: Array<File>
        get() {
            val savePath = "${System.getProperty("user.home")}/WolfsDenKotlin"
            val handler = File(savePath)
            return if (handler.exists() && handler.isDirectory) {
                handler.listFiles()
            } else {
                arrayOf()
            }
        }

    init {
        display.setBounds(0f, 0f, fullPixelW, fullPixelH)
        stage.addActor(display)
        activateInput(stage, input)
    }

    private fun drawBase() {
        display.erase()
        display.put(54, 18, "Wolf's Den II", SColor.WHITE)
        display.put(54, 19, "by Rakaneth", SColor.WHITE)
        display.put(54, 20, "[N]ew Game", SColor.WHITE)
    }

    private fun drawSaves() {
        if (fileList.isNotEmpty()) {
            display.put(54, 22, "Continue a quest:")
            display.put(54, 23, "([Shift + number] to delete")
            for ((idx, file) in fileList.withIndex()) {
                display.put(54, idx + 24, "$idx: ${file.nameWithoutExtension}")
            }
        }
    }

    override fun render() {
        drawBase()
        drawSaves()
        if (input.hasNext()) input.next()
        stage.act()
        stage.draw()
    }
}