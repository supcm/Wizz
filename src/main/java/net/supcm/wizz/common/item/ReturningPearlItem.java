package net.supcm.wizz.common.item;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ReturningPearlItem extends Item {
    public ReturningPearlItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.RARE));
    }
    @Override public void appendHoverText(ItemStack stack, @Nullable Level world,
                                          List<Component> text, TooltipFlag flag) {
        text.add(Component.translatable("item.returning_pearl.info"));
        text.add(Component.translatable("item.returning_pearl.info1"));
        super.appendHoverText(stack, world, text, flag);
    }
    @Override public boolean isFoil(ItemStack stack) {
        if(stack.getTag() != null && stack.getTag().contains("x"))
            return true;
        return false;
    }
    @Override public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if(!world.isClientSide && hand == InteractionHand.MAIN_HAND) {
            ItemStack stack = player.getItemInHand(hand);
            if(!player.isCrouching()) {
                if (stack.getTag() != null) {
                    if (stack.getTag().getString("dim").equals(world.dimension().location().toString())) {
                        player.fallDistance = 0.0f;
                        player.teleportTo(stack.getTag().getInt("x"), stack.getTag().getInt("y"),
                                stack.getTag().getInt("z"));
                        if (!player.isCreative())
                            stack.shrink(1);
                        world.playSound(null, player.blockPosition(),
                                SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
                        return InteractionResultHolder.success(player.getItemInHand(hand));
                    } else
                        player.displayClientMessage(
                                Component.translatable("item.returning_pearl.not_the_same_dim"),
                                true);
                } else
                    player.displayClientMessage(Component.translatable("item.returning_pearl.no_pos"),
                            true);
            } else {
                if(stack.getTag() == null) {
                    CompoundTag tag = new CompoundTag();
                    tag.putInt("x", player.blockPosition().getX());
                    tag.putInt("y", player.blockPosition().getY());
                    tag.putInt("z", player.blockPosition().getZ());
                    tag.putString("dim", world.dimension().location().toString());
                    stack.setTag(tag);
                } else {
                    stack.getTag().putInt("x", player.blockPosition().getX());
                    stack.getTag().putInt("y", player.blockPosition().getY());
                    stack.getTag().putInt("z", player.blockPosition().getZ());
                    stack.getTag().putString("dim",
                            world.dimension().location().toString());
                    player.displayClientMessage(Component.translatable("item.returning_pearl.changed_pos"),
                            true);
                }
                return InteractionResultHolder.success(player.getItemInHand(hand));
            }
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override public InteractionResult useOn(UseOnContext ctx) {
        if(!ctx.getLevel().isClientSide && ctx.getHand() == InteractionHand.MAIN_HAND && ctx.getPlayer().isCrouching()) {
            if(ctx.getItemInHand().getTag() == null) {
                CompoundTag tag = new CompoundTag();
                tag.putInt("x", ctx.getPlayer().blockPosition().getX());
                tag.putInt("y", ctx.getPlayer().blockPosition().getY());
                tag.putInt("z", ctx.getPlayer().blockPosition().getZ());
                tag.putString("dim", ctx.getLevel().dimension().location().toString());
                ctx.getItemInHand().setTag(tag);
            } else {
                ctx.getItemInHand().getTag().putInt("x", ctx.getPlayer().blockPosition().getX());
                ctx.getItemInHand().getTag().putInt("y", ctx.getPlayer().blockPosition().getY());
                ctx.getItemInHand().getTag().putInt("z", ctx.getPlayer().blockPosition().getZ());
                ctx.getItemInHand().getTag().putString("dim",
                        ctx.getLevel().dimension().location().toString());
                ctx.getPlayer().displayClientMessage(Component.translatable("item.returning_pearl.changed_pos"),
                        true);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(ctx);
    }
}