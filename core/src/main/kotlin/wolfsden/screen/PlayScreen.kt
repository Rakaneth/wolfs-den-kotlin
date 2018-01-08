package wolfsden.screen

import com.badlogic.gdx.graphics.Colors
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.viewport.StretchViewport
import squidpony.squidgrid.Direction
import squidpony.squidgrid.gui.gdx.SColor
import squidpony.squidgrid.gui.gdx.SquidInput
import squidpony.squidmath.Coord
import wolfsden.CommonColors
import wolfsden.entity.HasteEffect
import wolfsden.entity.ItemBuilder
import wolfsden.entity.RegenEffect
import wolfsden.entity.StunEffect
import wolfsden.system.CommandProcessor
import wolfsden.system.GameStore
import wolfsden.system.GameStore.curMap
import wolfsden.system.Scheduler
import wolfsden.system.playerVisible
import wolfsden.toICString

object PlayScreen : WolfScreen("main") {
    private const val mapW = 80
    private const val mapH = 30
    private const val statW = 40
    private const val statH = 10
    private const val msgW = 40
    private const val msgH = 10
    private const val ttW = 40
    private const val ttH = 10
    private const val sklW = 40
    private const val sklH = 12
    private const val invW = 40
    private const val invH = 12
    private const val eqW = 40
    private const val eqH = 6

    override val vport = StretchViewport(fullPixelW, fullPixelH)

    private val playLayout = layout(vport) {
        layers {
            id = "map"
            gw = mapW
            gh = mapH
            x = 0
            y = msgH
            tcf {
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

    private val mapLayers = playLayout.toSparseLayers("map")
    private val statPanel = playLayout.toSquidPanel("stats")
    private val msgPanel = playLayout.toMessageBox("messages")
    private val ttPanel = playLayout.toSquidPanel("tt")
    private val sklPanel = playLayout.toSquidPanel("skills")
    private val invPanel = playLayout.toSquidPanel("inventory")
    private val eqPanel = playLayout.toSquidPanel("equip")
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
            'G' -> CommandProcessor.process(player, "pickup")
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                val numSlot = key.toString().toInt()
                if (player.inventory.size >= (numSlot + 1)) CommandProcessor.process(player, "use", player.inventory[numSlot])
                else addMessage("No item to use/equip in that slot.")
            }
            't' -> {
                player.applyEffect(StunEffect(player.eID, 25))
                GameStore.update(false, true)
            }
            'r' -> {
                player.applyEffect(RegenEffect(player.eID, 50, 0.1))
                GameStore.update(false, true)
            }
            'h' -> {
                player.applyEffect(HasteEffect(player.eID, 75))
                GameStore.update(false, true)
            }
        }
    })
    private val cam: Coord
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
        val GF = SColor.GRAY.toFloatBits()
        for (x in 0.until(mapW)) {
            for (y in 0.until(mapH)) {
                val wx = x + cam.x
                val wy = y + cam.y
                val wc = Coord.get(wx, wy)
                if (!m.oob(wc)) {
                    val c = m.displayMap[wx][wy]
                    val fg = m.fgFloats[wx][wy]
                    val bg = m.bgFloats[wx][wy]
                    when {
                        player.visible(wc) -> {
                            mapLayers.put(x, y, c, fg, bg)
                        }
                        m.light -> {
                            mapLayers.put(x, y, c, fg, SColor.lerpFloatColors(bg, GF, -0.5f))
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    private fun drawStats() {
        val markupStat = { label: String, stat: Number, col: Int, y: Int ->
            var placement = Pair(0, 0)
            when (col) {
                1 -> placement = Pair(1, 5)
                2 -> placement = Pair(8, 12)
                3 -> placement = Pair(30, 34)
            }
            statPanel.put(placement.first, y, markup(label, CommonColors.INFO))
            statPanel.put(placement.second, y, stat.toString())
        }
        val markupTemper = { label: String, tMin: Number, tMax: Number, startx: Int, y: Int, color: String ->
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
        var idx = 1
        player.effectStack!!.effects.forEach {
            when (idx) {
                in (1 until 8) -> ttPanel.put(1, idx, it.toString().toICString())
                8 -> ttPanel.put(1, 8, "...")
                else -> {}
            }
            idx++
        }
    }

    private fun drawSkl() {
        sklPanel.erase()
        sklPanel.putBorders(FW, "Skills(Shift: use)")
    }

    private fun drawInv() {
        invPanel.erase()
        invPanel.putBorders(FW, "Inventory(number: use, Alt: describe)")
        for ((idx, item) in player.inventory.withIndex()) {
            invPanel.put(1, 1 + idx, "$idx: ${item.markupString}".toICString())
        }
    }

    private fun drawEQ() {
        with(eqPanel) {
            erase()
            putBorders(FW, "Equipment")
            put(1, 1, player.mhMarkup)
            put(1, 2, player.ohMarkup)
            put(1, 3, player.armorMarkup)
            put(1, 4, player.trinketMarkup)
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
        val toDraw = GameStore.curEntities.filter { it.pos != null && it.playerVisible() }
        for (entity in toDraw.sortedBy { it.draw!!.layer }) {
            ec = entity.pos!!.coord
            val color = entity.draw!!.color
            mapLayers.put(ec.x - cam.x, ec.y - cam.y, entity.draw!!.glyph, Colors.get(color).toFloatBits())
        }
    }

    fun addMessage(msg: String) {
        msgPanel.appendWrappingMessage("-$msg".toICString())
    }

    override fun render() {
        if (GameStore.mapDirty) {
            drawDungeon()
            drawEntities()
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