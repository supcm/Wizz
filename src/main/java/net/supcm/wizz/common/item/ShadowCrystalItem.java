package net.supcm.wizz.common.item;

import net.minecraft.nbt.CompoundTag;
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

public class ShadowCrystalItem extends Item {
    public ShadowCrystalItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.RARE));
    }
    @Override public boolean isFoil(ItemStack stack) {
        if(stack.getTag() != null && stack.getTag().contains("InShadow"))
            return stack.getTag().getBoolean("InShadow");
        return false;
    }
    @Override public void appendHoverText(ItemStack stack, @Nullable Level world,
                                          List<Component> text, TooltipFlag flag) {
        text.add(Component.translatable("item.shadow_crystal.info"));
        if(stack.getTag() != null && stack.getTag().getBoolean("InShadow"))
            text.add(Component.translatable("item.shadow_crystal.active"));
        super.appendHoverText(stack, world, text, flag);
    }
    @Override public void inventoryTick(ItemStack stack, Level world, Entity entity,
                                        int tick, boolean flag) {
        if (!world.isClientSide) {
            if (stack.getTag() == null) {
                CompoundTag tag = new CompoundTag();
                tag.putBoolean("InShadow", false);
                stack.setTag(tag);
            }
            boolean isInShadow = world.isDay() ?
                    world.getLightEngine().getRawBrightness(entity.blockPosition(), 0) < 7 :
                    world.getLightEngine().getRawBrightness(entity.blockPosition(), 15) < 7;
            stack.getTag().putBoolean("InShadow",
                    isInShadow);
            if (isInShadow) {
                if (entity instanceof LivingEntity living) {
                    if (!living.hasEffect(MobEffects.INVISIBILITY))
                        living.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 100, 0));
                    else if(living.getEffect(MobEffects.INVISIBILITY).getDuration() < 20)
                        living.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 100, 0));
                }
            }
        }
    }
}
