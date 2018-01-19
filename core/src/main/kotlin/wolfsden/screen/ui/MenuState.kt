package wolfsden.screen.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import squidpony.squidgrid.Direction
import squidpony.squidgrid.gui.gdx.SquidInput
import wolfsden.screen.PlayScreen
import wolfsden.system.CommandProcessor
import wolfsden.system.DialogManager
import wolfsden.system.GameStore
import wolfsden.toICString

enum class MenuState(val theMenu: WolfSelector?) : State<PlayScreen> {
    NULL(null) {
        override fun enter(entity: PlayScreen?) {}
        override fun update(entity: PlayScreen?) {}
        override fun exit(entity: PlayScreen?) {}
    },

    PLAY(null) {
        override fun enter(entity: PlayScreen?) {
            entity!!.input = SquidInput({ key, alt, ctrl, shift ->
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
                    SquidInput.CENTER_ARROW -> CommandProcessor.process(player, "wait")
                    'G' -> CommandProcessor.process(player, "pickup")
                    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                        val numSlot = key.toString().toInt()
                        if (player.inventory.size >= (numSlot + 1)) {
                            PlayScreen.itemSelected = player.inventory[numSlot]
                            PlayScreen.curState.changeState(ITEM_MENU)

                        } else PlayScreen.addMessage("No item to use/equip in that slot.")
                    }
                    '>', '<' -> {
                        val pc = player.pos!!.coord
                        val curMap = GameStore.curMap
                        if (curMap.connections.containsKey(pc)) {
                            CommandProcessor.process(player, "stairs", curMap.connection(pc))
                        } else {
                            PlayScreen.addMessage("No stairs here.")
                        }
                    }
                    'd' -> {
                        DialogManager.startDialog("joe")
                    }
                    'Q' -> {
                        GameStore.saveGame()
                        Gdx.app.exit()
                    }
                }
            })
            entity.activateInput(entity.stage, entity.input)
        }

        override fun update(entity: PlayScreen?) {
        }

        override fun exit(entity: PlayScreen?) {
        }
    },

    ITEM_MENU(ItemMenu()) {
        override fun enter(entity: PlayScreen?) {
            PlayScreen.menuStage.addActor(theMenu!!.asActor())
            (theMenu as ItemMenu).setItem(PlayScreen.itemSelected!!)
            PlayScreen.input = SquidInput({ key, _, _, _ ->
                when (key) {
                    SquidInput.UP_ARROW -> theMenu.prevItem()
                    SquidInput.DOWN_ARROW -> theMenu.nextItem()
                    SquidInput.ENTER -> theMenu.handleSelected()
                    SquidInput.ESCAPE -> PlayScreen.curState.changeState(PLAY)
                    else -> {
                    }
                }
            })
            entity!!.activateInput(PlayScreen.menuStage, PlayScreen.input)
        }

        override fun update(entity: PlayScreen?) {
        }

        override fun exit(entity: PlayScreen?) {
            theMenu!!.asActor().remove()
        }
    },

    DIALOG(Dialog()) {
        override fun update(entity: PlayScreen?) {
            val curDialog = DialogManager.curDialog!!
            with(theMenu as Dialog) {
                theMenu.caption = curDialog.caption
                theMenu.dialog = curDialog.text.toICString()
                theMenu.menuItems = curDialog.options.map { it.text }
            }
        }

        override fun enter(entity: PlayScreen?) {
            update(entity)
            PlayScreen.menuStage.addActor(theMenu!!.asActor())
            entity!!.input = SquidInput({ key, _, _, _ ->
                when (key) {
                    SquidInput.UP_ARROW -> theMenu.prevItem()
                    SquidInput.DOWN_ARROW -> theMenu.nextItem()
                    SquidInput.ENTER -> theMenu.handleSelected()
                    SquidInput.ESCAPE -> PlayScreen.curState.changeState(PLAY)
                    else -> {
                    }
                }
            })
            entity.activateInput(PlayScreen.menuStage, PlayScreen.input)
        }

        override fun exit(entity: PlayScreen?) {
            theMenu!!.asActor().remove()
        }

    };

    override fun onMessage(entity: PlayScreen?, telegram: Telegram?) = false
}