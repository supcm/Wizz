package net.supcm.wizz.common.item;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import java.util.List;

public class CatsEyeItem extends Item {
    public CatsEyeItem() {
        super(new Properties().stacksTo(1).durability(240)
                .defaultDurability(240).rarity(Rarity.RARE));
    }
    @Override public void appendHoverText(ItemStack stack, @Nullable Level world,
                                          List<Component> text, TooltipFlag flag) {
        text.add(Component.translatable("item.cats_eye.info"));
        super.appendHoverText(stack, world, text, flag);
    }
    @Override public void inventoryTick(ItemStack stack, Level world, Entity entity,
                                        int tick, boolean flag) {
        if (!world.isClientSide) {
            if (stack.getTag() == null) {
                CompoundTag tag = new CompoundTag();
                tag.putBoolean("Activated", false);
                stack.setTag(tag);
            }
            if(stack.getTag().getBoolean("Activated")) {
                if(world.getGameTime() % 20 == 0) {
                    if (entity instanceof LivingEntity living) {
                        living.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 225, 0));
                        if(world.getGameTime() % 200 == 0)
                            stack.hurtAndBreak(1, living, e -> e.broadcastBreakEvent(living.swingingArm));
                    }
                }
            }
        }
    }
    @Override public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if(!world.isClientSide) {
            if(hand == InteractionHand.MAIN_HAND && player.getItemInHand(hand).getItem() == this)
                changeActivity(player.getItemInHand(hand));
            else if(hand == InteractionHand.OFF_HAND && player.getItemInHand(hand).getItem() == this)
                changeActivity(player.getItemInHand(hand));
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }
    private InteractionResultHolder<ItemStack> changeActivity(ItemStack stack) {
        if(stack.getTag() != null) {
            stack.getTag().putBoolean("Activated", !stack.getTag().getBoolean("Activated"));
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }
}