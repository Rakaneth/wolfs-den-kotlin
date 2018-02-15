import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import squidpony.squidgrid.mapping.DungeonGenerator
import wolfsden.entity.CreatureBuilder
import wolfsden.entity.Entity
import wolfsden.entity.ItemBuilder
import wolfsden.map.MapBuilder
import wolfsden.map.WolfMap
import wolfsden.system.WolfRNG
import wolfsden.system.isAlly
import wolfsden.system.isEnemy


class EntityTestSource {
    private val e1 = Entity("e1")
    private val e2 = Entity("e2")
    private val gen = DungeonGenerator(30, 30, WolfRNG.wolfRNG)
    private val m1 = WolfMap("test", "Test Map 1", gen.generate(), true)
    private val m2 = WolfMap("test", "Test Map 2", gen.generate(), true)

    init {
        CreatureBuilder.initBP(false)
        ItemBuilder.initBP(false)
        MapBuilder.initBP(false)
    }

    @Test
    fun testDraw() {
        e1.addDraw('$', "Silver", 1)
        assertEquals('$', e1.draw?.glyph)
        assertEquals("Silver", e1.draw?.color)
    }

    @Test
    fun testCreatureBuilder() {
        val wolf = CreatureBuilder.build("wolf")
        assertNotNull(wolf)
        assertNotNull(wolf.mh)
        assertEquals("Wolf", wolf.id?.name)
        assertEquals(1, wolf.stats?.skl)
        assertEquals(0, wolf.atk)
    }

    @Test
    fun testRawFactions() {
        val wolf = CreatureBuilder.build("wolf")
        with(wolf.factionStack!!) {
            assertNotNull(this)
            assertTrue(hostile.contains("hero"))
            assertTrue(neutral.contains("wolf"))
            assertTrue(hostile.contains("undead"))
            assertTrue(ally.isEmpty())
        }
    }

    @Test
    fun testFactionManager() {
        val wolf = CreatureBuilder.build("wolf")
        val zmobi = CreatureBuilder.build("revenant")
        val otherWolf = CreatureBuilder.build("wolf")
        val wolfLord = CreatureBuilder.build("wolfLord")
        assertTrue(wolf.isEnemy(zmobi))
        assertTrue(!wolf.isEnemy(otherWolf))
        assertTrue(wolfLord.isAlly(wolf))
        assertTrue(wolfLord.isAlly(otherWolf))
    }
}