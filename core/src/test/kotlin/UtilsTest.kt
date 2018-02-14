import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import wolfsden.entity.CreatureBuilder
import wolfsden.entity.ItemBuilder
import wolfsden.joinWithAnd
import wolfsden.system.hasResistance
import wolfsden.system.hasWeakness

class UtilsTestSource {

    init {
        CreatureBuilder.initBP(false)
        ItemBuilder.initBP(false)
    }

    @Test
    fun testOxfordComma() {
        val lst1 = listOf("apples", "oranges", "bananas")
        val lst2 = listOf("apples", "bananas")
        val lst3 = listOf("apples")
        assertEquals("apples, oranges, and bananas", lst1.joinWithAnd())
        assertEquals("apples and bananas", lst2.joinWithAnd())
        assertEquals("apples", lst3.joinWithAnd())
    }

    @Test
    fun testWeaknessAndResistance() {
        val revenant = CreatureBuilder.build("revenant")
        val weaks = revenant.hasWeakness("holy")
        val res = revenant.hasResistance("natural")
        val nonexistent = revenant.hasWeakness("nonexistent")
        val alsoNonexistent = revenant.hasResistance("nonexistent")
        assertEquals(2, weaks)
        assertEquals(2, res)
        assertEquals(0, nonexistent)
        assertEquals(0, alsoNonexistent)
    }
}