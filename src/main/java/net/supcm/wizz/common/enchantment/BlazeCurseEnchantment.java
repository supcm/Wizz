package net.supcm.wizz.common.enchantment;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;

public class BlazeCurseEnchantment extends Enchantment {
    protected BlazeCurseEnchantment() {
        super(Rarity.RARE, Enchantments.ALL, EquipmentSlot.values());
    }
    @Override public boolean isCurse() { return true; }

    @Override
    public void doPostAttack(LivingEntity attacker, Entity target, int level) {
        attacker.setSecondsOnFire(5);
        super.doPostAttack(attacker, target, level);
    }
    @Override
    public void doPostHurt(LivingEntity wearer, Entity attacker, int level) {
        wearer.setSecondsOnFire(5);
        super.doPostAttack(wearer, attacker, level);
    }
}
