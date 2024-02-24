package net.supcm.wizz.common.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.supcm.wizz.WizzMod;
import net.supcm.wizz.common.block.Blocks;

public class BlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES,
            WizzMod.MODID);

    public static final RegistryObject<BlockEntityType<EnchantedTableBlockEntity>> ENCHANTED_TABLE =
            BLOCK_ENTITIES.register("enchanted_table", () -> BlockEntityType.Builder.of(EnchantedTableBlockEntity::new,
                    Blocks.ENCHANTED_TABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<WordMachineBlockEntity>> WORD_MACHINE =
            BLOCK_ENTITIES.register("word_machine", () -> BlockEntityType.Builder.of(WordMachineBlockEntity::new,
                    Blocks.WORD_MACHINE.get()).build(null));
    public static final RegistryObject<BlockEntityType<WordForgeBlockEntity>> WORD_FORGE =
            BLOCK_ENTITIES.register("word_forge", () -> BlockEntityType.Builder.of(WordForgeBlockEntity::new,
                    Blocks.WORD_FORGE.get()).build(null));
    public static final RegistryObject<BlockEntityType<MatrixBlockEntity>> MATRIX =
            BLOCK_ENTITIES.register("matrix", () -> BlockEntityType.Builder.of(MatrixBlockEntity::new,
                    Blocks.MATRIX.get()).build(null));
    public static final RegistryObject<BlockEntityType<EnchantingStationBlockEntity>> ENCHANTING_STATION =
            BLOCK_ENTITIES.register("enchanting_station", () -> BlockEntityType.Builder.of(EnchantingStationBlockEntity::new,
                    Blocks.ENCHANTING_STATION.get()).build(null));
    public static final RegistryObject<BlockEntityType<ThoughtLoomBlockEntity>> THOUGHT_LOOM =
            BLOCK_ENTITIES.register("thought_loom", () -> BlockEntityType.Builder.of(ThoughtLoomBlockEntity::new,
                    Blocks.THOUGHT_LOOM.get()).build(null));
    public static final RegistryObject<BlockEntityType<ReassessmentTableBlockEntity>> REASSESSMENT_TABLE =
            BLOCK_ENTITIES.register("reassessment_table", () -> BlockEntityType.Builder.of(ReassessmentTableBlockEntity::new,
                    Blocks.REASSESSMENT_TABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<ReassessmentPillarBlockEntity>> REASSESSMENT_PILLAR =
            BLOCK_ENTITIES.register("reassessment_pillar", () -> BlockEntityType.Builder.of(ReassessmentPillarBlockEntity::new,
                    Blocks.REASSESSMENT_PILLAR.get()).build(null));
    public static final RegistryObject<BlockEntityType<WordEraserBlockEntity>> WORD_ERASER =
            BLOCK_ENTITIES.register("word_eraser", () -> BlockEntityType.Builder.of(WordEraserBlockEntity::new,
                    Blocks.WORD_ERASER.get()).build(null));
    public static final RegistryObject<BlockEntityType<AlchemyCauldronBlockEntity>> ALCHEMY_CAULDRON =
            BLOCK_ENTITIES.register("alchemy_cauldron", () -> BlockEntityType.Builder.of(AlchemyCauldronBlockEntity::new,
                    Blocks.ALCHEMY_CAULDRON.get()).build(null));
    public static final RegistryObject<BlockEntityType<MortarBlockEntity>> MORTAR =
            BLOCK_ENTITIES.register("mortar", () -> BlockEntityType.Builder.of(MortarBlockEntity::new,
                    Blocks.MORTAR.get()).build(null));
}
