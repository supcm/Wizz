package net.supcm.wizz.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.supcm.wizz.WizzMod;
import net.supcm.wizz.common.handler.EnchantmentsHandler;

import javax.annotation.Nullable;
import java.util.List;

public class GlyphsCasketItem extends Item {
    public GlyphsCasketItem() {
        super(new Properties().rarity(Rarity.RARE).stacksTo(8));
    }
    @Override public void appendHoverText(ItemStack stack, @Nullable Level world,
                                          List<Component> text, TooltipFlag flag) {
        text.add(1, Component.translatable("item.glyphs_casket.info"));
        super.appendHoverText(stack, world, text, flag);
    }
    @Override public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if(!world.isClientSide) {
            if(player.experienceLevel >= 2 || player.isCreative()) {
                RandomSource r = world.getRandom();
                ItemStack item = new ItemStack(ForgeRegistries.ITEMS.getValue(
                        new ResourceLocation(WizzMod.MODID, EnchantmentsHandler.GLYPHS_LIST
                                .get(r.nextInt(EnchantmentsHandler.GLYPHS_LIST.size())))),
                        r.nextInt(2) + 1);
                if(player.getInventory().getFreeSlot() != -1)
                    player.addItem(item);
                else
                    world.addFreshEntity(new ItemEntity(world,
                            player.blockPosition().getX(),
                            player.blockPosition().getY(),
                            player.blockPosition().getZ(),
                            item));
                if(!player.isCreative())
                    player.getItemInHand(hand).shrink(1);
                world.playSound(null, player.blockPosition(), SoundEvents.ENDER_CHEST_OPEN, SoundSource.PLAYERS, 1f, 1f);
                return InteractionResultHolder.success(player.getItemInHand(hand));
            } else {
                player.displayClientMessage(Component.translatable("glyphs_casket.notenoughxp"), true);
                return InteractionResultHolder.pass(player.getItemInHand(hand));
            }
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }
}
