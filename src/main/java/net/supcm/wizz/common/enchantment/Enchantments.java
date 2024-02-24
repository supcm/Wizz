package net.supcm.wizz.common.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.supcm.wizz.WizzMod;

public class Enchantments {
    public static final EnchantmentCategory ALL = EnchantmentCategory.create("all", item -> true);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS,
            WizzMod.MODID);

    public static RegistryObject<Enchantment> XP_BOOST = ENCHANTMENTS.register("xp_boost",
            XpBoostEnchantment::new);
    public static RegistryObject<Enchantment> GRAVITY_CORE = ENCHANTMENTS.register("gravity_core",
            GravityCoreEnchantment::new);
    public static RegistryObject<Enchantment> UNSTABILITY = ENCHANTMENTS.register("unstability",
            UnstabilityEnchantment::new);
    public static RegistryObject<Enchantment> BLAZE_CURSE = ENCHANTMENTS.register("blaze_curse",
            BlazeCurseEnchantment::new);
    public static RegistryObject<Enchantment> DARKNESS_CURSE = ENCHANTMENTS.register("darkness_curse",
            DarknessCurseEnchantment::new);
}
