package net.supcm.wizz.common.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.supcm.wizz.common.block.Blocks;

import java.util.List;

public class XpCrystalItem extends Item {
    public XpCrystalItem() { super(new Properties().rarity(Rarity.UNCOMMON)); }
    @Override public boolean isFoil(ItemStack stack) { return true; }

    @Override public void appendHoverText(ItemStack stack, Level world,
                                          List<Component> text, TooltipFlag flag) {
        text.add(Component.translatable("item.crystal.info"));
        text.add(Component.translatable("item.crystal.use"));
        super.appendHoverText(stack, world, text, flag);
    }


    @Override public InteractionResult useOn(UseOnContext ctx) {
        InteractionHand hand = ctx.getHand();
        Level world = ctx.getLevel();
        if(!world.isClientSide && hand == InteractionHand.MAIN_HAND) {
            if(world.getBlockState(ctx.getClickedPos()).getBlock() == Blocks.REASSESSMENT_TABLE.get())
                return InteractionResult.CONSUME;
            Player player = ctx.getPlayer();
            player.onEnchantmentPerformed(player.getItemInHand(InteractionHand.MAIN_HAND), -6);
            if(!player.isCreative())
                player.getItemInHand(hand).shrink(1);
            return InteractionResult.CONSUME;
        } else if(world.isClientSide && hand == InteractionHand.MAIN_HAND) {
            Player player = ctx.getPlayer();
            world.addParticle(ParticleTypes.SOUL,
                    player.getPosition(0.5f).x, player.getPosition(0.5f).y + 0.15,
                    player.getPosition(0.5f).z,
                    0.0D, 0.025D, 0.0D);
            player.playSound(SoundEvents.GLASS_BREAK, 1.0F, 1.0F);
        }
        return super.useOn(ctx);
    }
}
