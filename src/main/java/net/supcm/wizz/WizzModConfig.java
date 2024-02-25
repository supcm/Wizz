package net.supcm.wizz;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class WizzModConfig {
    public static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_DESCRIPTIONS_IN_CODEX;
    public static final ForgeConfigSpec.ConfigValue<Integer> ENCHANTING_MULT;
    public static final ForgeConfigSpec.ConfigValue<Double> ENCHANTING_EASE;
    public static final ForgeConfigSpec.ConfigValue<List<String>> T2_LIST;

    static {
        BUILDER.push("Configuration file for Wizz");
        ENABLE_DESCRIPTIONS_IN_CODEX = BUILDER.comment("Enable descriptions in codex",
                        "(WARNING! Descriptions doesn't work without 'Enchantments Description mod!')")
                .define("ENABLE_DESCRIPTIONS_IN_CODEX", false);
        ENCHANTING_MULT = BUILDER.comment("Enchanting start lvl (formula for enchantments levels:" +
                                " (ENCHANTING_MULT / Enchantment max levels) * Item count in Enchanter)",
                        "Default: 30")
                .define("ENCHANTING_MULT", 30);
        ENCHANTING_EASE = BUILDER.comment("Eases overchanting by this multiper (ENCHANTING_MULT "
                                + "(ENCHANTING_MULT / Enchantment max levels) * Item count in Enchanter * EASE)",
                        "Default: 0.75")
                .defineInRange("ENCHANTING_EASE", 0.55d, 0.0d, 1.0d);
        T2_LIST = BUILDER.comment("List with Enchantments for Enchanted Machine to ADD " +
                                "(All non-vanilla enchantments adds to Enchanted Forge List,",
                        "if it don't presents here) (max. 62)",
                        "Default : []")
                .define("T2_LIST", new ArrayList<>());
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
