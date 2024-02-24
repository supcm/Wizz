package net.supcm.wizz.common.enchantment;


import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.ForgeRegistries;

public class UnstabilityEnchantment extends Enchantment {
    public UnstabilityEnchantment() { this(EquipmentSlot.MAINHAND); }
    protected UnstabilityEnchantment(EquipmentSlot... slots) { super(Rarity.RARE, EnchantmentCategory.WEAPON, slots); }

    @Override public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof SwordItem ||
                stack.getItem() instanceof BowItem ||
                stack.getItem() instanceof TridentItem;
    }

    @Override public void doPostAttack(LivingEntity attacker, Entity target, int damage) {
        super.doPostAttack(attacker, target, damage);
        if(!attacker.level().isClientSide){
            switch (attacker.level().getRandom().nextInt(6)) {
                case 0:
                    attacker.moveTo(attacker.blockPosition().getX(),
                            attacker.blockPosition().getY() + attacker.level().getRandom().nextInt(5) + 1,
                            attacker.blockPosition().getZ());
                    break;
                case 1:
                    if(target.level().canSeeSkyFromBelowWater(target.blockPosition())) {
                        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(target.level());
                        bolt.setVisualOnly(true);
                        bolt.moveTo(target.blockPosition(), 0, 0);
                        target.level().addFreshEntity(bolt);
                        target.hurt(bolt.damageSources().lightningBolt(), 10f);
                    }
                    break;
                case 2, 3:
                    target.setSecondsOnFire(attacker.level().getRandom().nextInt(5));
                    break;
                case 4, 5:
                    MobEffect effect = (MobEffect) ForgeRegistries.MOB_EFFECTS.getValues().toArray()
                            [attacker.level().getRandom().nextInt(ForgeRegistries.MOB_EFFECTS.getValues().size())];
                    if(attacker.level().getRandom().nextInt(60) < 20) {
                        attacker.addEffect(new MobEffectInstance(effect, attacker.level().getRandom().nextInt(140),
                            attacker.level().getRandom().nextInt(2)));
                    } else if(target instanceof LivingEntity le) {
                        le.addEffect(new MobEffectInstance(effect, le.level().getRandom().nextInt(140),
                            le.level().getRandom().nextInt(2)));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override public int getMaxLevel() { return 1; }
}
