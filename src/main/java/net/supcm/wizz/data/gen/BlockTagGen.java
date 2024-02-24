package net.supcm.wizz.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.supcm.wizz.WizzMod;
import net.supcm.wizz.common.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BlockTagGen extends BlockTagsProvider {
    public BlockTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, WizzMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        tag(BlockTags.MINEABLE_WITH_AXE).add(
                Blocks.THOUGHT_LOOM.get(),
                Blocks.REASSESSMENT_PILLAR.get(),
                Blocks.REASSESSMENT_TABLE.get()
        );
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                Blocks.THOUGHT_LOOM.get(),
                Blocks.REASSESSMENT_PILLAR.get(),
                Blocks.REASSESSMENT_TABLE.get(),
                Blocks.ENCHANTED_TABLE.get(),
                Blocks.WORD_MACHINE.get(),
                Blocks.WORD_FORGE.get(),
                Blocks.WORD_ERASER.get(),
                Blocks.MATRIX.get(),
                Blocks.ENCHANTING_STATION.get(),
                Blocks.DEEPSLATE_LAVA_CRYSTAL_ORE.get(),
                Blocks.LAVA_CRYSTAL_ORE.get(),
                Blocks.DEEPSLATE_CINNABAR_ORE.get(),
                Blocks.MORTAR.get(),
                Blocks.ALCHEMY_CAULDRON.get()
        );
        //tag(BlockTags.MINEABLE_WITH_SHOVEL).add();

        tag(BlockTags.NEEDS_STONE_TOOL).add(
                Blocks.THOUGHT_LOOM.get(),
                Blocks.REASSESSMENT_PILLAR.get(),
                Blocks.REASSESSMENT_TABLE.get(),
                Blocks.DEEPSLATE_LAVA_CRYSTAL_ORE.get(),
                Blocks.LAVA_CRYSTAL_ORE.get(),
                Blocks.MORTAR.get(),
                Blocks.ALCHEMY_CAULDRON.get()
        );
        tag(BlockTags.NEEDS_IRON_TOOL).add(
                Blocks.ENCHANTED_TABLE.get(),
                Blocks.WORD_MACHINE.get(),
                Blocks.WORD_FORGE.get(),
                Blocks.WORD_ERASER.get(),
                Blocks.MATRIX.get(),
                Blocks.ENCHANTING_STATION.get(),
                Blocks.DEEPSLATE_CINNABAR_ORE.get()
        );
        //tag(BlockTags.NEEDS_DIAMOND_TOOL).add();
    }
}
