package net.supcm.wizz.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class EnchantedFeatherItem extends Item {
    public EnchantedFeatherItem() {
        super(new Properties().stacksTo(1)
                .rarity(Rarity.RARE));
    }
    @Override public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list,
                                          TooltipFlag flag) {
        list.add(Component.translatable("item.enchanted_feather.info"));
    }
    @Override public void inventoryTick(ItemStack stack, Level world, Entity entity, int ticks, boolean flag) {
        if(!world.isClientSide && entity.fallDistance >= 3.25f) {
            if(entity instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20, 1));
            }
        }
    }
    @Override public boolean isFoil(ItemStack stack) { return true; }
}
