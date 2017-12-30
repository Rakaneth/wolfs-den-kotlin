package wolfsden.system

import wolfsden.entity.BESTIARY
import wolfsden.entity.Slot

object Equip {
    fun unequip(eID: String, slot: Slot) {
        val entity = BESTIARY[eID]!!
        when (slot) {
            Slot.MH -> entity.mh = null
            Slot.OH -> entity.oh = null
            Slot.ARMOR -> entity.armor = null
            Slot.TRINKET -> entity.trinket = null
            Slot.TWOH -> {
                entity.mh = null; entity.oh = null
            }
        }
    }
}