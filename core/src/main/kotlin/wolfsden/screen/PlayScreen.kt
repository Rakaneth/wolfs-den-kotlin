package wolfsden.screen

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import squidpony.squidgrid.gui.gdx.*

object PlayScreen: WolfScreen("main") {
    private val mapW = 80
    private val mapH = 30
    private val statW = 42
    private val statH = 10
    private val msgW = 40
    private val msgH = 10
    private val ttW = 38
    private val ttH = 10
    private val sklW = 40
    private val sklH = 12
    private val invW = 40
    private val invH = 12
    private val eqW = 40
    private val eqH = 6

    private val playLayout = layout {
        layers {
            id = "map"
            gw = mapW
            gh = mapH
            x = 0
            y = msgH
            tcf {
                tweakWidth = 1.2f
                tweakHeight = 1.5f
            }
        }
        messages {
            id = "messages"
            gw = msgW
            gh = msgH
            x = statW
            y = 0
            tcf {
                tweakWidth = 1.2f
                tweakHeight = 1.5f
            }
        }
        panel {
            id = "stats"
            gw = statW
            gh = statH
            x = 0
            y = 0
            tcf {
                tweakWidth = 1.2f
                tweakHeight = 1.5f
            }
        }
        panel {
            id = "tt"
            gw = ttW
            gh = ttH
            x = statW + msgW
            y = 0
            tcf {
                tweakWidth = 1.2f
                tweakHeight = 1.5f
            }
        }
        panel {
            id = "skills"
            gw = sklW
            gh = sklH
            x = mapW
            y = ttH
            tcf {
                tweakWidth = 1.2f
                tweakHeight = 1.5f
            }
        }
        panel {
            id = "inventory"
            gw = invW
            gh = invH
            x = mapW
            y = ttH + sklH
            tcf {
                tweakWidth = 1.2f
                tweakHeight = 1.5f
            }
        }
        panel {
            id = "equip"
            gw = eqW
            gh = eqH
            x = mapW
            y = ttH + sklH + invH
            tcf {
                tweakWidth = 1.2f
                tweakHeight = 1.5f
            }
        }
    }

    private val mapLayers = playLayout.actors["map"] as SparseLayers
    private val statPanel = playLayout.actors["stats"] as SquidPanel
    private val msgPanel = playLayout.actors["messages"] as SquidMessageBox
    private val ttPanel =  playLayout.actors["tt"] as SquidPanel
    private val sklPanel = playLayout.actors["skills"] as SquidPanel
    private val invPanel = playLayout.actors["inventory"] as SquidPanel
    private val eqPanel = playLayout.actors["equip"] as SquidPanel
    private const val FW = SColor.FLOAT_WHITE

    override val stage = playLayout.build()
    override val input = SquidInput({ key, alt, ctrl, shift ->
        TODO("Write keyhandler for main screen")
    })

    init { setInput() }

    private fun drawDungeon() {
        mapLayers.clear()
        mapLayers.put(1, 0, "Map".toICString())
    }

    private fun drawStats() {
        statPanel.erase()
        statPanel.putBorders(FW, "Stats")
    }

    private fun drawMsgs() {
        msgPanel.erase()
        msgPanel.putBorders(FW, "Messages")
    }

    private fun drawTT() {
        ttPanel.erase()
        ttPanel.putBorders(FW, "Info")
    }

    private fun drawSkl() {
        sklPanel.erase()
        sklPanel.putBorders(FW, "Skills")
    }

    private fun drawInv() {
        invPanel.erase()
        invPanel.putBorders(FW, "Inventory")
    }

    private fun drawEQ() {
        eqPanel.erase()
        eqPanel.putBorders(FW, "Equipment")
    }

    private fun drawHUD() {
        drawStats()
        drawMsgs()
        drawTT()
        drawSkl()
        drawInv()
        drawEQ()
    }

    override fun render() {
        drawDungeon()
        drawHUD()
        stage.act()
        stage.draw()
    }
}