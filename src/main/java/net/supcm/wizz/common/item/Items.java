package net.supcm.wizz.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.supcm.wizz.WizzMod;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class Items {

    public static Rarity FORBIDDEN = Rarity.create("Forbidden", ChatFormatting.DARK_RED);
    public static Rarity CONCEPTION = Rarity.create("Conception", ChatFormatting.GOLD);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            WizzMod.MODID);
    public static final RegistryObject<Item> WIZZ = createItem("wizz",
            () -> getItem(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> STAR = createItem("star",
            () -> getItem(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> CORE = createItem("core",
            () -> getItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> STABILIZER = createItem("stabilizer", () ->
            new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> ENHANCED_STABILIZER = createItem("enhanced_stabilizer", () ->
            new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> OVERLOADING_MECH = createItem("overloading_mech", () ->
            new Item(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> ENCHANTED_CORE = createItem("enchanted_core",
            () -> getItemWithFoil(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> ENCHANTED_HEART = createItem("enchanted_heart",
            () -> getItemWithFoil(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> ENCHANTED_STAR = createItem("enchanted_star",
            () -> getItemWithFoil(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> CLAY_PLATE = createItem("clay_plate", () ->
            new Item(new Item.Properties()));
    public static final RegistryObject<Item> PLATE = createItem("plate", () ->
            new Item(new Item.Properties()));
    public static final RegistryObject<Item> LAVA_CRYSTAL = createItem("lava_crystal", () ->
            new Item(new Item.Properties()));
    public static final RegistryObject<Item> PESTLE = createItem("pestle",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CINNABAR = createItem("cinnabar",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CINNABAR_GEM = createItem("cinnabar_gem",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> GLISTERING_POWDER = createItem("glistering_powder",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> WIZZ_POWDER = createItem("wizz_powder",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> CINNABAR_DUST = createItem("cinnabar_dust",
            () -> new Item(new Item.Properties()) {
                @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int tick, boolean flag) {
                    if(!level.isClientSide && entity instanceof LivingEntity living) {
                        if(!living.hasEffect(MobEffects.POISON))
                            living.addEffect(new MobEffectInstance(MobEffects.POISON, 25, 0));
                    }
                    super.inventoryTick(stack, level, entity, tick, flag);
                }
            });
    public static final RegistryObject<Item> SOUL_POWDER = createItem("soul_powder",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> PEARL = createItem("pearl",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> GLASS_SHARDS = createItem("glass_shards",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MERCURY_DROP = createItem("mercury_drop",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)) {
                @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int tick, boolean flag) {
                    if(!level.isClientSide && entity instanceof LivingEntity living) {
                        if(!living.hasEffect(MobEffects.POISON))
                            living.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 1));
                    }
                    super.inventoryTick(stack, level, entity, tick, flag);
                }
            });
    public static final RegistryObject<Item> SAP = createItem("sap",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> METALLIC_STRING = createItem("metallic_string",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> REFLECTIVE_SURFACE = createItem("reflective_surface",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> BLOODY_SEEDS = createItem("bloody_seeds",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> FOLDING_FAN = createItem("folding_fan",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> DISGUSTING_BALL = createItem("disgusting_ball",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> CRYSTAL_EMPTY = createItem("xp_crystal_empty",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL = createItem("xp_crystal", XpCrystalItem::new);
    public static final RegistryObject<Item> CASKET = createItem("glyphs_casket", GlyphsCasketItem::new);
    public static final RegistryObject<Item> CODEX = createItem("codex", CodexItem::new);
    public static final RegistryObject<Item> ARCHIVE = createItem("archive", ArchiveItem::new);
    public static final RegistryObject<Item> SHADOW_CRYSTAL = createItem("shadow_crystal", ShadowCrystalItem::new);
    public static final RegistryObject<Item> RETURNING_PEARL = createItem("returning_pearl", ReturningPearlItem::new);
    public static final RegistryObject<Item> CATS_EYE = createItem("cats_eye", CatsEyeItem::new);
    public static final RegistryObject<Item> WISDOM_SEED = createItem("wisdom_seed", WisdomSeedItem::new);
    public static final RegistryObject<Item> GRAVITY_BOMB = createItem("gravity_bomb", GravityBombItem::new);
    public static final RegistryObject<Item> ENCHANTED_FEATHER = createItem("enchanted_feather", EnchantedFeatherItem::new);
    public static final RegistryObject<Item> HEALING_BERRIES = createItem("healing_berries", HealingBerriesItem::new);
    public static final RegistryObject<Item> WIND_FAN = createItem("wind_fan", WindFanItem::new);
    public static final RegistryObject<Item> REVELATION_MIRROR = createItem("revelation_mirror",
            RevelationMirrorItem::new);
    public static final RegistryObject<Item> ALCHEMY_WASTE = createItem("alchemy_waste",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> OKU = createSymbol("oku");
    public static final RegistryObject<Item> GEO = createSymbol("geo");
    public static final RegistryObject<Item> ARA = createSymbol("ara");
    public static final RegistryObject<Item> YUE = createSymbol("yue");
    public static final RegistryObject<Item> QOU = createSymbol("qou");
    public static final RegistryObject<Item> RIA = createSymbol("ria");
    public static final RegistryObject<Item> LUA = createSymbol("lua");
    public static final RegistryObject<Item> DOR = createSymbol("dor");
    public static final RegistryObject<Item> ZET = createSymbol("zet");
    public static final RegistryObject<Item> IRO = createUnstableSymbol("iro");
    public static final RegistryObject<Item> NOY = createUnstableSymbol("noy");
    public static final RegistryObject<Item> SAT = createUnstableSymbol("sat");
    public static final RegistryObject<Item> BAL = createUnstableSymbol("bal");
    public static final RegistryObject<Item> WOY = createUnstableSymbol("woy");
    public static final RegistryObject<Item> VER = createUnstableSymbol("ver");
    public static final RegistryObject<Item> FIR = createItem("fir", () ->
            getItem(new Item.Properties().rarity(FORBIDDEN).fireResistant().stacksTo(1),
                    List.of(Component.translatable("item.fir.info").withStyle(ChatFormatting.GRAY)),
                    true));
    public static final RegistryObject<Item> CONCEPT_BASE = createItem("concept_base", () ->
            getItem(new Item.Properties()));
    public static final RegistryObject<Item> CONCEPT_SOUL = createConception("soul", new float[]{0.4f, 0.6f, 1f});
    public static final RegistryObject<Item> CONCEPT_BEAUTY = createConception("beauty", new float[]{1f, 0.5f, 1f});
    public static final RegistryObject<Item> CONCEPT_ART = createConception("art", new float[]{1f, 1f, 0.5f});
    public static final RegistryObject<Item> CONCEPT_CREATION = createConception("creation", new float[]{0.6f, 1f, 0.6f});
    public static final RegistryObject<Item> CONCEPT_TRUTH = createConception("truth", new float[]{0.9f, 0.9f, 0.9f});
    public static final RegistryObject<Item> CONCEPT_LIES = createConception("lies", new float[]{1f, 0.3f, 0.3f});
    public static RegistryObject<Item> createItem(String name, Supplier<Item> item) {
        RegistryObject<Item> register = ITEMS.register(name,  item);
        WizzMod.CreativeTabs.ADD_TO_TAB_WIZZ.add(register);

        return register;
    }

    private static Item getItem(Item.Properties prop) {
        return getItem(prop, null, false);
    }
    private static Item getItemWithFoil(Item.Properties prop) {
        return getItem(prop, null, true);
    }
    private static Item getItem(Item.Properties prop, @Nullable List<Component> desc) {
        return getItem(prop, desc, false);
    }
    private static Item getItem(Item.Properties prop, @Nullable List<Component> desc, boolean foil) {
        return new Item(prop) {
            @Override
            public void appendHoverText(ItemStack stack, @Nullable Level level,
                                        List<Component> list, TooltipFlag flag) {
                if(desc != null)
                    list.addAll(desc);
                super.appendHoverText(stack, level, list, flag);
            }

            @Override
            public boolean isFoil(ItemStack stack) {
                return foil;
            }
        };
    }
    public static ResourceLocation getResourceLocation(Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
    }
    private static RegistryObject<Item> createSymbol(String name) {
        return createItem(name, GlyphItem::new);
    }
    private static RegistryObject<Item> createUnstableSymbol(String name) {
        return createItem(name, UnstableGlyphItem::new);
    }
    private static RegistryObject<Item> createConception(String name, float[] color) {
        return createItem("concept_" + name, () -> new ConceptItem(color));
    }
    public static class UnstableGlyphItem extends Item {
        public UnstableGlyphItem() {
            super(new Properties().stacksTo(8).rarity(Rarity.RARE));
        }
        @Override public boolean hasCraftingRemainingItem(ItemStack stack) {
            return true;
        }
        @Override public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
            return itemStack.copy();
        }
    }
    public static class ConceptItem extends Item {
        float[] color;
        public ConceptItem(float[] color) {
            super(new Properties().rarity(CONCEPTION).stacksTo(16));
            setColor(color);
        }
        @Override public boolean isFoil(ItemStack stack) { return true; }
        public float[] getColor() { return color; }
        void setColor(float[] color) { this.color = color;}
    }
    public static class GlyphItem extends Item {
        public GlyphItem() { super(new Properties().stacksTo(16)); }
        @Override public boolean isFoil(ItemStack stack) { return true; }
        @Override public boolean hasCraftingRemainingItem(ItemStack stack) {
            return true;
        }
        @Override public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
            return itemStack.copy();
        }
    }
}
