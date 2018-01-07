import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import wolfsden.joinWithAnd

class UtilsTestSource {

    @Test
    fun testOxfordComma() {
        val lst1 = listOf("apples", "oranges", "bananas")
        val lst2 = listOf("apples", "bananas")
        val lst3 = listOf("apples")
        assertEquals("apples, oranges, and bananas", lst1.joinWithAnd())
        assertEquals("apples and bananas", lst2.joinWithAnd())
        assertEquals("apples", lst3.joinWithAnd())
    }
}