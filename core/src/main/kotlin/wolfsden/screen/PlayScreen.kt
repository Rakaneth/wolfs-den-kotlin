package wolfsden.screen

import com.badlogic.gdx.graphics.Colors
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.viewport.StretchViewport
import squidpony.squidgrid.Direction
import squidpony.squidgrid.gui.gdx.*
import squidpony.squidmath.Coord
import wolfsden.CommonColors
import wolfsden.entity.CreatureBuilder
import wolfsden.entity.EquipStats
import wolfsden.map.MapBuilder
import wolfsden.system.CommandProcessor
import wolfsden.system.GameStore
import wolfsden.system.GameStore.curMap
import wolfsden.system.Location
import wolfsden.system.Scheduler
import wolfsden.toICString

object PlayScreen : WolfScreen("main") {
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

    override val vport = StretchViewport(fullPixelW, fullPixelH)

    private val playLayout = layout(vport) {
        layers {
            id = "map"
            gw = mapW
            gh = mapH
            x = 0
            y = msgH
            tcf {
                base = DefaultResources.getStretchableWideFont()
                tweakWidth = 1.1f
                tweakHeight = 1.1f
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
    private val ttPanel = playLayout.actors["tt"] as SquidPanel
    private val sklPanel = playLayout.actors["skills"] as SquidPanel
    private val invPanel = playLayout.actors["inventory"] as SquidPanel
    private val eqPanel = playLayout.actors["equip"] as SquidPanel
    private const val FW = SColor.FLOAT_WHITE
    private val player
        get() = GameStore.player

    override val stage = playLayout.build()
    override val input = SquidInput({ key, alt, ctrl, shift ->
        val player = GameStore.player
        when (key) {
            SquidInput.UP_ARROW -> CommandProcessor.process(player, "move", Direction.UP)
            SquidInput.UP_RIGHT_ARROW -> CommandProcessor.process(player, "move", Direction.UP_RIGHT)
            SquidInput.RIGHT_ARROW -> CommandProcessor.process(player, "move", Direction.RIGHT)
            SquidInput.DOWN_RIGHT_ARROW -> CommandProcessor.process(player, "move", Direction.DOWN_RIGHT)
            SquidInput.DOWN_ARROW -> CommandProcessor.process(player, "move", Direction.DOWN)
            SquidInput.DOWN_LEFT_ARROW -> CommandProcessor.process(player, "move", Direction.DOWN_LEFT)
            SquidInput.LEFT_ARROW -> CommandProcessor.process(player, "move", Direction.LEFT)
            SquidInput.UP_LEFT_ARROW -> CommandProcessor.process(player, "move", Direction.UP_LEFT)
        }
    })
    val cam: Coord
        get() {
            val m = GameStore.curMap
            val c = GameStore.player.pos!!.coord
            val calc: (Int, Int, Int) -> Int = { p, m, s -> MathUtils.clamp(p - s / 2, 0, maxOf(m - s, 0)) }
            val camX = calc(c.x, m.width, mapW)
            val camY = calc(c.y, m.height, mapH)
            return Coord.get(camX, camY)
        }

    init {
        setInput()
    }

    private fun drawDungeon() {
        mapLayers.clear()
        val m = GameStore.curMap
        for (x in 0.until(mapW)) {
            for (y in 0.until(mapH)) {
                val wx = x + cam.x
                val wy = y + cam.y
                val wc = Coord.get(wx, wy)
                when {
                    !m.oob(wc) && (m.light || Location.visible(GameStore.player, wc)) -> {
                        mapLayers.put(x, y, m.displayMap[wx][wy], m.fgFloats[wx][wy], m.bgFloats[wx][wy])
                    }
                    m.oob(wc) -> mapLayers.put(x, y, ' ', SColor.FLOAT_BLACK)
                }
            }
        }
        drawEntities()
    }

    private fun drawStats() {
        val markupStat = {label: String, stat: Number, col: Int, y: Int ->
            var placement = Pair(0, 0)
            when (col) {
                1 -> placement = Pair(1, 5)
                2 -> placement = Pair(8, 12)
                3 -> placement = Pair(30, 34)
            }
            statPanel.put(placement.first, y, markup(label, CommonColors.INFO))
            statPanel.put(placement.second, y, stat.toString())
        }
        val markupTemper = {label: String, tMin: Number, tMax: Number, startx: Int, y: Int, color: String ->
            val numFormat = when (tMin) {
                is Int -> "%4d/%4d"
                else -> "%5.0f/%5.0f"
            }
            statPanel.put(startx, y, "[$color]$label[] $numFormat".format(tMin, tMax).toICString())
        }
        with(statPanel) {
            erase()
            putBorders(FW, "Stats")
            put(1, 1, player.markupString!!.toICString())
            put(1, 2, "${curMap.name} ${player.pos!!.coord}")
            markupStat("Str", player.stats!!.str, 1, 3)
            markupStat("Sta", player.stats!!.stam, 1, 4)
            markupStat("Spd", player.stats!!.spd, 1, 5)
            markupStat("Skl", player.stats!!.skl, 1, 6)
            markupStat("Dmg", player.dmg, 2, 3)
            markupStat("Sav", player.sav, 2, 4)
            markupStat("Dfp", player.dfp, 2, 5)
            markupStat("Atk", player.atk, 2, 6)
            markupTemper("Vit", player.vit!!.curVit, player.maxVit, 15, 3, CommonColors.VIT)
            markupTemper("End", player.vit!!.curEnd, player.maxEnd, 15, 4, CommonColors.WARNING)
            markupTemper("Arm", player.curArmor, player.maxArmor, 15, 5, CommonColors.METAL)
            markupTemper("Shd", player.curShield, player.maxShield, 15, 6, CommonColors.SHIELD)
            markupStat("MDl", player.movDly, 3, 3)
            markupStat("ADl", player.atkDly, 3, 4)
            markupTemper("XP", player.xp!!.curXP, player.xp!!.totXP, 1, 7, CommonColors.XP)
        }
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
        with(eqPanel) {
            erase()
            putBorders(FW, "EquipStats")
            val markupEQ = { label: String, y: Int, eq: EquipStats?  ->
                eqPanel.put(1, y, "[${CommonColors.INFO}]%8s[]: ${eq?.name ?: "Nothing"}".format(label).toICString())
            }
            markupEQ("Mainhand", 1, player.mh)
            markupEQ("Offhand", 2, player.oh)
            markupEQ("Armor",3, player.armor)
            markupEQ("Trinket", 4, player.trinket)
        }
    }

    private fun drawHUD() {
        drawStats()
        drawMsgs()
        drawTT()
        drawSkl()
        drawInv()
        drawEQ()
    }

    private fun drawEntities() {
        var ec: Coord
        for (entity in GameStore.curEntities.filter{ it.pos != null}) {
            ec = entity.pos!!.coord
            val color = entity.draw!!.color
            if (Location.visible(GameStore.player, entity.pos!!.coord)) {
                mapLayers.put(ec.x - cam.x, ec.y - cam.y, entity.draw!!.glyph, Colors.get(color))
            }
        }
    }

    fun addMessage(msg: String) {
        msgPanel.appendWrappingMessage(msg.toICString())
    }

    override fun enter() {
        MapBuilder.build("mine")
        with(CreatureBuilder) {
            build("fighter", true, null, "mine", "Palmyra")
            build("wolf")
        }
        Scheduler.resume()
        addMessage("Welcome to [Green][*]Wolf's Den II![]")
        super.enter()
    }

    override fun render() {
        if (GameStore.mapDirty) {
            drawDungeon()
            GameStore.mapDirty = false
        }
        if (GameStore.hudDirty) {
            drawHUD()
            GameStore.hudDirty = false
        }
        Scheduler.tick()
        if (input.hasNext()) input.next()
        stage.act()
        stage.draw()
    }
}