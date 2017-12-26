package wolfsden.system

import wolfsden.entity.Slot
import wolfsden.entity.BESTIARY

object EquipSystem {
    fun unequip(eID: String, slot: Slot) {
        val entity = BESTIARY[eID]!!
        when (slot) {
            Slot.MH -> entity.mh = null
            Slot.OH -> entity.oh= null
            Slot.ARMOR -> entity.armor = null
            Slot.TRINKET -> entity.trinket = null
        }
    }
}