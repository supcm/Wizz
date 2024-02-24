package net.supcm.wizz.common.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class GravityCoreEnchantment extends Enchantment {
    public GravityCoreEnchantment() { this(EquipmentSlot.MAINHAND); }
    protected GravityCoreEnchantment(EquipmentSlot... p_i46731_3_) {
        super(Rarity.RARE, EnchantmentCategory.DIGGER, p_i46731_3_);
    }
    @Override public boolean canEnchant(ItemStack stack) { return true; }
    @Override public int getMaxLevel() { return 4; }
}
