package wolfsden

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import squidpony.squidgrid.gui.gdx.SColor
import wolfsden.entity.CreatureBuilder
import wolfsden.entity.ItemBuilder
import wolfsden.map.MapBuilder
import wolfsden.screen.CCScreen
import wolfsden.screen.PlayScreen
import wolfsden.screen.TitleScreen
import wolfsden.screen.WolfScreen

class WolfGame : ApplicationAdapter() {
    private val bgColor = SColor.DARK_SLATE_GRAY
    lateinit var batch: SpriteBatch

    override fun create() {
        batch = SpriteBatch()
        ItemBuilder.initBP()
        CreatureBuilder.initBP()
        MapBuilder.initBP()
        WolfScreen.register(TitleScreen(batch))
        WolfScreen.register(CCScreen(batch))
        WolfScreen.register(PlayScreen(batch))
        WolfScreen.setScreen("title")
    }

    override fun render() {
        Gdx.gl.glClearColor(bgColor.r / 255f, bgColor.g / 255f, bgColor.b / 255f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        WolfScreen.curScreen?.render()
    }

    override fun resize(width: Int, height: Int) {
        WolfScreen.curScreen?.vport?.update(width, height, false);

    }
}