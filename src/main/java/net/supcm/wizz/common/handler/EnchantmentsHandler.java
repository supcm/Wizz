package net.supcm.wizz.common.handler;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.ForgeRegistries;
import net.supcm.wizz.WizzModConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentsHandler {
    public EnchantmentsHandler() {
        initAllLists();
    }
    public static void initAllLists() {
        EnchantmentsHandler.initGlyphsList();
        EnchantmentsHandler.initFirstList();
        EnchantmentsHandler.initSecondList();
        EnchantmentsHandler.initThirdList();
        for(String rl : WizzModConfig.T2_LIST.get()) {
            Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(rl));
            if(ench != null) T2_LIST.add(ench);
            else System.out.println("Can't add enchantment with ResourceLocation " + rl);
        }
        for(int i = 0; i < ForgeRegistries.ENCHANTMENTS.getValues().toArray().length; i++) {
            boolean contains = T1_LIST.contains(ForgeRegistries.ENCHANTMENTS.getValues().toArray()[i]) ||
                    T2_LIST.contains(ForgeRegistries.ENCHANTMENTS.getValues().toArray()[i]) ||
                    T3_LIST.contains(ForgeRegistries.ENCHANTMENTS.getValues().toArray()[i]);
            if(!contains)
                T3_LIST.add((Enchantment)ForgeRegistries.ENCHANTMENTS.getValues().toArray()[i]);
        }
    }
    public static final List<String> GLYPHS_LIST = new ArrayList<>();
    public static List<Enchantment> T1_LIST = new ArrayList<>();
    public static List<Enchantment> T2_LIST = new ArrayList<>();
    public static List<Enchantment> T3_LIST = new ArrayList<>();
    public static Map<String, Enchantment> T1_MAP = new HashMap<String, Enchantment>();
    public static Map<String, Enchantment> T2_MAP = new HashMap<String, Enchantment>();
    public static Map<String, Enchantment> T3_MAP = new HashMap<String, Enchantment>();
    public static void initGlyphsList() {
        GLYPHS_LIST.add("ara");
        GLYPHS_LIST.add("geo");
        GLYPHS_LIST.add("oku");
        GLYPHS_LIST.add("yue");
        GLYPHS_LIST.add("qou");
        GLYPHS_LIST.add("ria");
        GLYPHS_LIST.add("lua");
        GLYPHS_LIST.add("dor");
        GLYPHS_LIST.add("zet");

    }
    public static void initFirstList() {
        T1_LIST.add(Enchantments.RESPIRATION);
        T1_LIST.add(Enchantments.SMITE);
        T1_LIST.add(Enchantments.BANE_OF_ARTHROPODS);
        T1_LIST.add(Enchantments.KNOCKBACK);
        T1_LIST.add(Enchantments.PUNCH_ARROWS);
        T1_LIST.add(Enchantments.CHANNELING);
        T1_LIST.add(Enchantments.PIERCING);
        T1_LIST.add(Enchantments.THORNS);
        T1_LIST.add(Enchantments.UNBREAKING);
    }

    public static void initSecondList() {
        T2_LIST.add(Enchantments.MOB_LOOTING);
        T2_LIST.add(Enchantments.BLOCK_FORTUNE);
        T2_LIST.add(Enchantments.FLAMING_ARROWS);
        T2_LIST.add(Enchantments.FIRE_ASPECT);
        T2_LIST.add(Enchantments.SWEEPING_EDGE);
        T2_LIST.add(Enchantments.IMPALING);
        T2_LIST.add(Enchantments.LOYALTY);
        T2_LIST.add(Enchantments.QUICK_CHARGE);
        T2_LIST.add(Enchantments.SOUL_SPEED);
        T2_LIST.add(Enchantments.FROST_WALKER);
        T2_LIST.add(Enchantments.AQUA_AFFINITY);
        T2_LIST.add(Enchantments.FALL_PROTECTION);
        T2_LIST.add(Enchantments.FIRE_PROTECTION);
        T2_LIST.add(Enchantments.BLAST_PROTECTION);
        T2_LIST.add(Enchantments.PROJECTILE_PROTECTION);
        T2_LIST.add(Enchantments.FISHING_LUCK);
        T2_LIST.add(Enchantments.FISHING_SPEED);
        T2_LIST.add(Enchantments.SWIFT_SNEAK);
        T2_LIST.add(net.supcm.wizz.common.enchantment.Enchantments.XP_BOOST.get());
        T2_LIST.add(net.supcm.wizz.common.enchantment.Enchantments.UNSTABILITY.get());
    }

    public static void initThirdList() {
        T3_LIST.add(Enchantments.ALL_DAMAGE_PROTECTION);
        T3_LIST.add(Enchantments.MENDING);
        T3_LIST.add(Enchantments.INFINITY_ARROWS);
        T3_LIST.add(Enchantments.POWER_ARROWS);
        T3_LIST.add(Enchantments.SHARPNESS);
        T3_LIST.add(Enchantments.MULTISHOT);
        T3_LIST.add(Enchantments.DEPTH_STRIDER);
        T3_LIST.add(Enchantments.BLOCK_EFFICIENCY);
        T3_LIST.add(Enchantments.SILK_TOUCH);
        T3_LIST.add(Enchantments.RIPTIDE);
    }

    public static String getEnchantmentId(Enchantment enchantment) {
        ResourceLocation rl = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
        return rl != null ? rl.toString() : "ERROR";
    }

    public static Enchantment getRandomCurse(RandomSource random) {
        List<Enchantment> curses = ForgeRegistries.ENCHANTMENTS.getValues().stream().filter(Enchantment::isCurse)
                .toList();
        return curses.get(random.nextInt(curses.size()));
    }
}
