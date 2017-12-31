import org.junit.jupiter.api.Test
import wolfsden.system.WolfRNG

class DiceTestSource {
    @Test
    fun testRolls() {
        with(WolfRNG) {
            testRoll(5)
            testRoll(17)
            testRoll(26)
            testRoll(38)
            testRoll(69)
        }
    }

    @Test
    fun testEvenDiffs() {
        with(WolfRNG) {
            testRoll(5, 5)
            testRoll(17, 17)
            testRoll(26, 26)
            testRoll(38, 38)
            testRoll(69, 69)
        }
    }

    @Test
    fun testPcts() {
        with(WolfRNG) {
            extendedRollTest(5, 5)
            extendedRollTest(17, 17)
            extendedRollTest(26, 26)
            extendedRollTest(38, 38)
            extendedRollTest(69, 69)
            extendedRollTest(5, 3)
            extendedRollTest(17, 14)
            extendedRollTest(26, 23)
            extendedRollTest(38, 35)
            extendedRollTest(69, 66)
            extendedRollTest(5, 8)
            extendedRollTest(17, 20)
            extendedRollTest(26, 29)
            extendedRollTest(38, 41)
            extendedRollTest(69, 72)
        }
    }


}