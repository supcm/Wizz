package net.supcm.wizz.common.item;


import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class HealingBerriesItem extends Item {
    public static final FoodProperties HEALING_BERRIES_FOOD = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.1F)
            .alwaysEat()
            .fast()
            .effect(() -> new MobEffectInstance(MobEffects.HEAL, 1, 1), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 200, 0), 0.8f)
            .build();
    public HealingBerriesItem() {
        super(new Properties().stacksTo(16).rarity(Rarity.UNCOMMON).food(HEALING_BERRIES_FOOD));
    }
    @Override public boolean isFoil(ItemStack stack) { return true; }
}
