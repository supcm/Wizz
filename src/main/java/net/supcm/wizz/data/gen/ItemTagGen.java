package net.supcm.wizz.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class ItemTagGen extends ItemTagsProvider {
    public ItemTagGen(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_) {
        super(p_275343_, p_275729_, p_275322_);
    }

    @Override
    protected void addTags(HolderLookup.Provider p_256380_) {

    }
}
