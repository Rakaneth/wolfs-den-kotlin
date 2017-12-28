import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.XmlReader
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import squidpony.squidgrid.mapping.DungeonGenerator
import wolfsden.entity.Entity
import wolfsden.entity.Equipment
import wolfsden.entity.Slot
import wolfsden.map.WolfMap
import java.io.*


class EntityTestSource {
    private val e1 = Entity()
    private val e2 = Entity()
    private val gen = DungeonGenerator()
    private val m1 = WolfMap("test", gen.generate())
    private val m2 = WolfMap("test2", gen.generate())

    @Test
    fun testDraw() {
        e1.addDraw('$', "Silver")
        assertEquals('$', e1.draw?.glyph)
        assertEquals("Silver", e1.draw?.color)
    }

    @Test
    fun equipFunctions() {
        e1.armor = Equipment(e1.eID, Slot.ARMOR, 1, 2, 3, 4, 5)
        e1.mh = Equipment(e1.eID, Slot.MH, 2, 0, 2, 0, 5)
        e1.oh = Equipment(e1.eID, Slot.OH, 0, 1, 1, 0, 8)
        e1.addStats(1, 1, 1, 1)
        assertEquals(4, e1.atk)
        assertEquals(4, e1.dfp)
        assertEquals(7, e1.dmg)
        assertEquals(5, e1.sav)
        assertEquals(9, e1.movDly)
        assertEquals(12, e1.atkDly)
    }

    @Test
    fun serializeTest() {
        e1.addPos(2, 5, "mapID")
        e1.addVitals(true, 30, 55, 10, 78)
        e1.addXP(125f, 1750f)
        ObjectOutputStream(FileOutputStream("test.wlf")).use { it ->
            it.writeObject(e1)
        }
        ObjectInputStream(FileInputStream("test.wlf")).use { it ->
            val newEntity = it.readObject()
            when (newEntity) {
                is Entity -> {
                    newEntity.addEQ(Slot.TRINKET, 4, 2, 3, 3)
                    assertEquals(e1.eID, newEntity.eID)
                    assertEquals(e1.draw?.glyph, newEntity.draw?.glyph)
                    assertEquals(4, newEntity.eq?.atk)
                }
                else -> fail("Deserialization failed")
            }
        }
        File("test.wlf").delete()
    }

    @Test
    fun serializeMapOfEntitiesTest() {
        e1.addID("Entity", "First Entity")
        e2.addID("Entity 2", "Second Entity")
        val testMap: MutableMap<String, Entity> = mutableMapOf()
        val mapTestMap: MutableMap<String, WolfMap> = mutableMapOf()
        testMap[e1.eID] = e1
        testMap[e2.eID] = e2
        mapTestMap[m1.id] = m1
        mapTestMap[m2.id] = m2

        val fos = FileOutputStream("test2.wlf")
        ObjectOutputStream(fos).use { it ->
            it.writeObject(testMap)
            it.writeObject(mapTestMap)
        }
        val fis = FileInputStream("test2.wlf")
        ObjectInputStream(fis).use { it ->
            val newMap = it.readObject()
            val newMapTestMap = it.readObject()

            when (newMap) {
                is MutableMap<*, *> -> {
                    val newE1 = newMap[e1.eID]!! as Entity
                    val newE2 = newMap[e2.eID]!! as Entity
                    assertEquals(e1.eID, newE1.eID)
                    assertEquals(e2.eID, newE2.eID)
                }
                else -> fail("Deserialization of map failed")
            }

            when (newMapTestMap) {
                is MutableMap<*, *> -> {
                    val newM1 = newMapTestMap[m1.id] as WolfMap
                    val newM2 = newMapTestMap[m2.id] as WolfMap
                    assertEquals(newM1.id, m1.id)
                    assertEquals(newM2.id, m2.id)
                }
            }
        }
        File("test2.wlf").delete()
    }

    @Test
    fun readXMLTest() {
        val reader = XmlReader()
        try {
            val root = reader.parse(FileHandle("src/test/res/data/test2.xml"))
            val wolf = root.getChildrenByName("EntityType").filter { it.attributes["meta:RefKey"] == "creature" }.first()
            val rations = root.getChildrenByName("EntityType").filter {it.attributes["meta:RefKey"] == "item"}.first()
            val fangs = root.getChildrenByName("EntityType").filter {it.attributes["meta:RefKey"] == "equipment"}.first()
            assertEquals("Wolf", wolf.getChildByName("identity").getAttribute("name"))
            assertEquals(0.2f, rations.getChildByName("recoverData").attributes["pctAmt"].toFloat())
            assertEquals(1, fangs.getChildByName("eqData").attributes["dmg"].toInt())
        } catch (err: IOException) {
            fail<XmlReader>("Failed to read XML: ${err.stackTrace}")
        }
    }
}