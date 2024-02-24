package net.supcm.wizz.data.gen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.supcm.wizz.WizzMod;

public class BlockStateGen extends BlockStateProvider {
    public BlockStateGen(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, WizzMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

    }
}
