package wolfsden.screen

import com.badlogic.gdx.graphics.Colors
import com.badlogic.gdx.utils.viewport.StretchViewport
import squidpony.squidgrid.gui.gdx.SColor
import squidpony.squidgrid.gui.gdx.SquidInput
import wolfsden.CommonColors
import wolfsden.system.GameStore
import wolfsden.toICString

object CCScreen : WolfScreen("character creation") {
    override val vport = StretchViewport(fullPixelW, fullPixelH)
    private val ccLayout = layout(vport) {
        panel {
            id = "choices"
            gw = 30
            gh = 10
            x = 20
            y = 20
            tcf {
                tweakWidth = 1.1f
                tweakHeight = 1.1f
            }
        }
        panel {
            id = "descs"
            gw = 40
            gh = 20
            x = 51
            y = 10
            tcf {
                tweakWidth = 1.1f
                tweakHeight = 1.1f
            }
        }
        sqLayers {
            id = "name"
            gw = 22
            gh = 3
            x = 20
            y = 10
            borderCaption = "Name"
            tcf {
                tweakWidth = 1.1f
                tweakHeight = 1.1f
            }
        }
    }
    override val stage = ccLayout.build()
    override var input = SquidInput { key, _, _, _ ->
        when (key) {
            in ('1'..'3') -> selected = key.toString().toInt()
            SquidInput.ENTER -> {
                val selections = mapOf(
                    1 to "fighter",
                    2 to "rogue",
                    3 to "cleric"
                )
                val charClass = selections[selected]!!
                GameStore.newGame(charClass, playerName)
                WolfScreen.setScreen(PlayScreen)
            }
            in ('A'..'Z'), in ('a'..'z'), ' ' -> {
                val toAdd = nameCursor + 1
                if (toAdd < namePanel.gridWidth - 1) {
                    rawPlayerName[nameCursor] = key
                    nameCursor = toAdd
                }
            }
            SquidInput.BACKSPACE -> {
                val toDelete = nameCursor - 1
                if (toDelete > 0) {
                    rawPlayerName[toDelete] = 0.toChar()
                    nameCursor = toDelete
                }
            }
            else -> {

            }
        }
    }
    private var nameCursor = 1
    private const val info = CommonColors.INFO
    private const val crimson = CommonColors.VIT
    private const val warning = CommonColors.WARNING
    private const val palmyraDesc = "A strong, eager warrior, the [$info][*]Warrior[] has been called upon by his village, " +
                                    "[$info][*]Jommund[], to deal with the threat of the [$crimson][*]Wolflords.[] He prefers to confront enemies " +
                                    "openly, wielding heavy weapons to bring death to his foes and wearing heavy armor for protection. He has " +
                                    "knowledge of various [$info][/]fighting styles[], using that to his advantage on the battlefield."
    private const val poeDesc = "A swift, stealthy scout, the [$info][*]Scoundrel[] hails from [$info][*]Jommund[] as well, having " +
                                "similarly been called upon by the people of his town to face the [$crimson][*]Wolflords.[]  He prefers " +
                                "lighter armor, silent and easier to move around in, as well as light weapons that are easy to conceal. " +
                                "Like the Warrior, he has knowledge of various [$info][/]fighting styles[], though these are underhanded " +
                                "techniques rather than formal martial arts."
    private const val barnaDesc = "A sturdy, faithful [$info][*]Oathsworn[] of [$warning][*]the Raven, Oath of Death " +
                                  "and War,[] the [$info]Oathsworn[] does not hail from [$info][*]Jommund[], being a missionary from the capital " +
                                  "of [$info][*]Salaban[]. His crusade against the [$crimson][*]Wolflords[] is a holy one. He prefers defense " +
                                  "over offense, wearing heavy armor and shields. He has knowledge of a few [$info][/]martial techniques[], " +
                                  "but draws his might directly from the Raven, [$info][/]calling upon the Oath[] to wreak destruction on his " +
                                  "enemies or bless the Oathsworn in battle."
    private val choicePanel = ccLayout.toSquidPanel("choices")
    private val descPanel = ccLayout.toSquidPanel("descs")
    private val namePanel = ccLayout.toSquidLayers("name")
    private var rawPlayerName = CharArray(namePanel.gridWidth)
    private val playerName
        get(): String {
            val cand = String(rawPlayerName.filter { it != 0.toChar() }.toCharArray())
            return if (cand.isEmpty()) "Nameless" else cand
        }
    private var selected = 1
    private val choices = mapOf(
        1 to ("[1]: The Warrior" to palmyraDesc.toICString()),
        2 to ("[2]: The Scoundrel" to poeDesc.toICString()),
        3 to ("[3]: The Oathsworn" to barnaDesc.toICString())
    )

    init {
        activateInput(stage, input)
    }

    private fun drawChoices() {
        with(choicePanel) {
            erase()
            val printChoice: (Int, Int, Int, String) -> Unit = { selection, x, y, text ->
                val color = if (selection == selected) CommonColors.WARNING else "White"
                put(x, y, text, Colors.get(color))
            }
            putBorders(SColor.FLOAT_WHITE, "Choose a Hero (number)")
            for (i in 1..3) {
                printChoice(i, 1, i, choices[i]!!.first)
            }
        }
    }

    private fun drawDesc() {
        with(descPanel) {
            erase()
            putBorders(SColor.FLOAT_WHITE, "Description (Enter to select)")
            val descText = choices[selected]!!.second.wrap(descPanel.gridWidth - 2)
            for ((idx, line) in descText.withIndex()) {
                put(1, idx + 1, line)
            }
        }
    }

    private fun drawName() {
        namePanel.erase()
        namePanel.backgroundLayer.putBorders(SColor.FLOAT_WHITE, "Name")
        namePanel.backgroundLayer.put(nameCursor, 1, '_')
        namePanel.foregroundLayer.put(1, 1, playerName)
    }

    override fun render() {
        if (input.hasNext()) input.next()
        drawChoices()
        drawDesc()
        drawName()
        stage.act()
        stage.draw()
    }
}