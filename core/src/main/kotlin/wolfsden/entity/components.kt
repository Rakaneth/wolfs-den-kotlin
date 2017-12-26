package wolfsden.entity

enum class Slot { MH, OH, ARMOR, TRINKET, TWOH}

open class Component(open val entity: String)

data class Identity(
        override val entity: String,
        val name: String = "No Name",
        val desc: String = "No description"
): Component(entity)

data class Stats (
        override val entity: String,
        var str: Int = 1,
        var stam: Int = 1,
        var spd: Int = 1,
        var skl: Int = 1
): Component(entity)

data class Vitals (
        override val entity: String,
        var alive: Boolean = true,
        var curVit: Int = 0,
        var maxVit: Int = 0,
        var curEnd: Int = 0,
        var maxEnd: Int = 0
): Component(entity)

data class Drawing (
        override val entity: String,
        val glyph: Char = '@',
        val color: String = "White"
): Component(entity)

data class Position (
        override val entity: String,
        var mapID: String,
        var x: Int = -1,
        var y: Int = -1
): Component(entity)

data class Equipment (
        override val entity: String,
        val slot: Slot,
        val atk: Int = 0,
        val dfp: Int = 0,
        val dmg: Int = 0,
        val sav: Int = 0,
        val dly: Int = 0
): Component(entity)

data class HealingItem(
        override val entity: String,
        val pctAmt: Float = 0f,
        val flatAmt: Int = 0
): Component(entity)

data class RepairItem(
        override val entity: String,
        val pctAmt: Float = 0f,
        val flatAmt: Int = 0
): Component(entity)

data class XPGainer(
        override val entity: String,
        var curXP: Float = 0f,
        var totXP: Float = 0f
): Component(entity)