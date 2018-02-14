import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import squidpony.squidgrid.mapping.DungeonGenerator
import wolfsden.entity.*
import wolfsden.map.MapBuilder
import wolfsden.map.WolfMap
import wolfsden.system.WolfRNG


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
    @Disabled
    fun equipFunctions() {
        e1.armor = EquipStats(e1.eID, Slot.ARMOR, 1, 2, 3, 4, 5)
        e1.mh = EquipStats(e1.eID, Slot.MH, 2, 0, 2, 0, 5)
        e1.oh = EquipStats(e1.eID, Slot.OH, 0, 1, 1, 0, 8)
        e1.addStats(1, 1, 1, 1)
        assertEquals(4, e1.atk)
        assertEquals(4, e1.dfp)
        assertEquals(7, e1.dmg)
        assertEquals(5, e1.sav)
        assertEquals(9, e1.movDly)
        assertEquals(12, e1.atkDly)
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

    fun testMapBuilder() {
        val mine = MapBuilder.build("mine")
    }

}