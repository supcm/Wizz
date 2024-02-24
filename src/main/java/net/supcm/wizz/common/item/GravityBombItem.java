package net.supcm.wizz.common.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.supcm.wizz.common.enchantment.Enchantments;

public class GravityBombItem extends Item {
    public GravityBombItem() {
        super(new Properties().stacksTo(16).rarity(Rarity.EPIC).setNoRepair());
    }

    @Override public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override public void inventoryTick(ItemStack stack, Level world, Entity entity,
                                        int tick, boolean flag) {
        if(!world.isClientSide() && !stack.isEnchanted()) {
            stack.enchant(Enchantments.GRAVITY_CORE.get(), 20);
        }
    }
}
