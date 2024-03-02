package net.supcm.wizz.common.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class RevelationMirrorItem extends Item {
    public RevelationMirrorItem() {
        super(new Properties().stacksTo(1).setNoRepair().rarity(Rarity.RARE));
    }

    @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int tick, boolean flag) {
        //TODO: create broken state
    }
    @Override public UseAnim getUseAnimation(ItemStack stack) {
        //TODO: choose anim
        return UseAnim.BLOCK;
    }
    @Override public int getUseDuration(ItemStack stack) {
        //TODO: smth about 2-3 seconds, I think??? (40, 60)
        return 72000;
    }

    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int tick) {
        //TODO: add action
    }
}
