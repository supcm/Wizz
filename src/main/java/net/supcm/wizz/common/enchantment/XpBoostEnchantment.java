package net.supcm.wizz.common.enchantment;


import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class XpBoostEnchantment extends Enchantment {

    public XpBoostEnchantment() { this(EquipmentSlot.MAINHAND); }
    protected XpBoostEnchantment(EquipmentSlot... slots) { super(Rarity.RARE, EnchantmentCategory.WEAPON, slots); }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof DiggerItem ||
                stack.getItem() instanceof SwordItem ||
                stack.getItem() instanceof BowItem ||
                stack.getItem() instanceof TridentItem;
    }
    @Override public int getMaxLevel() { return 5; }
}
