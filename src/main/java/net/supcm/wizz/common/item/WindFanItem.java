package net.supcm.wizz.common.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

public class WindFanItem extends Item {
    public WindFanItem() {
        super(new Properties().stacksTo(1).durability(32)
                .setNoRepair().rarity(Rarity.RARE));
    }
    @Override public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list,
                                          TooltipFlag flag) {
        list.add(Component.translatable("item.wind_fan.info"));
    }
    @Override public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if(hand == InteractionHand.MAIN_HAND) {
            if (!world.isClientSide) {
                List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, new AABB(
                        player.blockPosition().getX() - 5,
                        player.blockPosition().getY() - 1,
                        player.blockPosition().getZ() - 5,
                        player.blockPosition().getX() + 5,
                        player.blockPosition().getY() + 1,
                        player.blockPosition().getZ() + 5
                ));
                if (!entities.isEmpty()) {
                    for (LivingEntity entity : entities) {
                        if (entity != player) {
                            entity.setDeltaMovement(entity.getDeltaMovement().x,
                                    0.985d,
                                    entity.getDeltaMovement().z);
                            entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 70, 0));
                        }
                    }
                    if (!player.isCreative()) {
                        ItemStack stack = player.getMainHandItem();
                        stack.setDamageValue(stack.getDamageValue() + 1);
                    }
                }
            } else {
                world.addParticle(ParticleTypes.EXPLOSION_EMITTER,
                        player.blockPosition().getX(), player.blockPosition().getY()+0.15,
                        player.blockPosition().getZ(),
                        0.0D, 0.025D, 0.0D);
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
