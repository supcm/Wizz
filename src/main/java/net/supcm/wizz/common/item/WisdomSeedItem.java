package net.supcm.wizz.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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

public class WisdomSeedItem extends Item {
    public WisdomSeedItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }
    @Override public void appendHoverText(ItemStack stack, @Nullable Level world,
                                          List<Component> list, TooltipFlag flag) {
        list.add(Component.translatable("item.wisdom_seed.info").withStyle(ChatFormatting.BLUE));
        if(stack.getTag() != null && !stack.getTag().isEmpty() && stack.getTag().contains("Mode")) {
            Component text = Component.translatable("item.wisdom_seed.giving_mode");
            if(stack.getTag().getBoolean("Mode"))
                text = Component.translatable("item.wisdom_seed.taking_mode");
            list.add(text);
            Component stored = Component.empty()
                    .append(Component.translatable("item.wisdom_seed.stored"))
                    .append(": " + stack.getTag().getInt("Stored"));
            list.add(stored);
        }
    }
    @Override public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if(!entity.level().isClientSide) {
            stack.getTag().putBoolean("Mode", !stack.getTag().getBoolean("Mode"));
            if(entity instanceof Player player){
                Component text = Component.translatable("item.wisdom_seed.giving_mode");
                if (stack.getTag().getBoolean("Mode"))
                    text = Component.translatable("item.wisdom_seed.taking_mode");
                player.displayClientMessage(text, true);
            }
        }
        return super.onEntitySwing(stack, entity);
    }
    @Override public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if(!world.isClientSide && hand == InteractionHand.MAIN_HAND) {
            ItemStack stack = player.getItemInHand(hand);
            if(stack.getTag().getBoolean("Mode")) { // Taking mode
                if(player.experienceLevel > 0) {
                    int store = 1;
                    if (player.isShiftKeyDown())
                        store = player.experienceLevel;
                    stack.getTag().putInt("Stored", stack.getTag().getInt("Stored") + store);
                    player.onEnchantmentPerformed(stack, store);
                    InteractionResultHolder.success(stack);
                } else {
                    player.displayClientMessage(Component.translatable("item.wisdom_seed.not_enough_player"), true);
                    InteractionResultHolder.fail(stack);
                }
            } else { // Giving Mode
                if(stack.getTag().getInt("Stored") > 0) {
                    int stored = 1;
                    if (player.isShiftKeyDown() && stack.getTag().getInt("Stored") > 1)
                        stored = stack.getTag().getInt("Stored");
                    stack.getTag().putInt("Stored", stack.getTag().getInt("Stored") - stored);
                    player.onEnchantmentPerformed(stack, -stored);
                    InteractionResultHolder.success(stack);
                } else {
                    player.displayClientMessage(Component.translatable("item.wisdom_seed.not_enough_seed"), true);
                    InteractionResultHolder.fail(stack);
                }
            }
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }
    @Override public void inventoryTick(ItemStack stack, Level world, Entity entity, int integer, boolean flag) {
        if(stack.getTag() == null || stack.getTag().isEmpty() || !stack.getTag().contains("Mode")) {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("Mode", true);
            tag.putInt("Stored", 0);
            stack.setTag(tag);
        }
    }
}
