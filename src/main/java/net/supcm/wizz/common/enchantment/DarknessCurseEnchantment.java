package net.supcm.wizz.common.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class DarknessCurseEnchantment extends Enchantment {
    protected DarknessCurseEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.VANISHABLE, EquipmentSlot.values());
    }
    @Override public boolean isCurse() { return true; }
}
