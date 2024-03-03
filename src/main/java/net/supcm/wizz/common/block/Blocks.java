package net.supcm.wizz.common.block;

import ca.weblite.objc.Proxy;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.supcm.wizz.WizzMod;
import net.supcm.wizz.common.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class Blocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            WizzMod.MODID);

    public static final RegistryObject<Block> LAVA_CRYSTAL_ORE = createBlock("lava_crystal_ore",
            () -> new LavaCrystalOreBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.STONE)
                    .strength(2.0f, 2.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(light -> 5)),
            null, null, false);
    public static final RegistryObject<Block> DEEPSLATE_LAVA_CRYSTAL_ORE = createBlock("deepslate_lava_crystal_ore",
            () -> new LavaCrystalOreBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.DEEPSLATE)
                    .strength(3.0f, 3.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(light -> 5)), null, null, false);
    public static final RegistryObject<Block> DEEPSLATE_CINNABAR_ORE = createBlock("deepslate_cinnabar_ore",
            () -> new Block(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.DEEPSLATE)
                    .strength(3.0f, 3.0f)
                    .requiresCorrectToolForDrops()), null, null, false);
    /*public static final RegistryObject<Block> WIZZ_TRANS = createBlock("wizz_trans",
            WizzTransBlock::new, null, null, true);*/
    public static final RegistryObject<Block> ENCHANTED_TABLE = createBlock("enchanted_table",
            EnchantedTableBlock::new, null, null, false);
    public static final RegistryObject<Block> WORD_MACHINE = createBlock("word_machine",
            WordMachineBlock::new, null, null, false);
    public static final RegistryObject<Block> WORD_FORGE = createBlock("word_forge",
            WordForgeBlock::new, null, null, false);
    public static final RegistryObject<Block> WORD_ERASER = createBlock("word_eraser",
            WordEraserBlock::new, null, null, false);
    public static final RegistryObject<Block> MATRIX = createBlock("matrix",
            MatrixBlock::new, new Item.Properties().rarity(Rarity.RARE),
            List.of(Component.translatable("block.matrix.info"),
                    Component.translatable("block.matrix.info1")), false);
    public static final RegistryObject<Block> ENCHANTING_STATION= createBlock("enchanting_station",
            EnchantingStationBlock::new, new Item.Properties().rarity(Rarity.RARE),
            List.of(Component.translatable("block.enchanting_station.info")), false);
    public static final RegistryObject<Block> THOUGHT_LOOM = createBlock("thought_loom",
            ThoughtLoomBlock::new, null, null, true);
    public static final RegistryObject<Block> REASSESSMENT_TABLE = createBlock("reassessment_table",
            ReassessmentTableBlock::new, new Item.Properties().rarity(Rarity.UNCOMMON),
            List.of(Component.translatable("block.reassessment_table.info"),
                    Component.translatable("block.reassessment.info")), true);
    public static final RegistryObject<Block> REASSESSMENT_PILLAR = createBlock("reassessment_pillar",
            ReassessmentPillarBlock::new, new Item.Properties().rarity(Rarity.UNCOMMON),
            List.of(Component.translatable("block.reassessment_pillar.info"),
                    Component.translatable("block.reassessment.info")), true);
    public static final RegistryObject<Block> ALCHEMY_CAULDRON = createBlock("alchemy_cauldron",
            AlchemyCauldronBlock::new, null, null, false);
    public static final RegistryObject<Block> MORTAR = createBlock("mortar",
            MortarBlock::new, null, null, false);

    public static RegistryObject<Block> createBlock(String name, Supplier<Block> block, @Nullable Item.Properties props,
                                                    @Nullable List<Component> desc, boolean foil) {
        RegistryObject<Block> register = BLOCKS.register(name, block);
        final Item.Properties properties = props != null ? props : new Item.Properties();
        Items.createItem(name, () -> new BlockItem(register.get(), properties) {
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
        });

        return register;
    }
}
