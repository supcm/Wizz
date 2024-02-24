package net.supcm.wizz.data.gen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.supcm.wizz.WizzMod;

public class ItemModelGen extends ItemModelProvider {
    public ItemModelGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, WizzMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

    }
}
