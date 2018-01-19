package wolfsden.system

import com.badlogic.gdx.Gdx
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import wolfsden.screen.PlayScreen
import wolfsden.screen.ui.MenuState

data class DialogOption (
        val text: String = "",
        val result: String = ""
)

data class WolfDialog (
        val caption: String = "",
        val text: String = "",
        var options: List<DialogOption> = listOf()
)

object DialogManager {
    const val convoFile = "data/dialog/sample.convo.json"

    val dialogs: Map<String, WolfDialog> = jacksonObjectMapper().readValue(Gdx.files.internal(convoFile).reader())
    var curDialog: WolfDialog? = null

    init {
        dialogs.values.forEach {
            if (it.options.isEmpty()) {
                it.options = listOf(DialogOption("(End conversation.)", "done"))
            }
        }
    }

    fun select(option: String) {
        if (option == "done") {
            curDialog = null
            PlayScreen.curState.changeState(MenuState.PLAY)
        } else {
            curDialog = dialogs[option]
            PlayScreen.curState.update()
        }
    }

    fun startDialog(dialog: String) {
        select(dialog)
        PlayScreen.curState.changeState(MenuState.DIALOG)
        GameStore.update(false, true)
    }

}