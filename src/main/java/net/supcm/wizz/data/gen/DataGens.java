package net.supcm.wizz.data.gen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.supcm.wizz.WizzMod;

@Mod.EventBusSubscriber(modid = WizzMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGens {

    @SubscribeEvent
    public static void genData(GatherDataEvent e) {
        DataGenerator gen = e.getGenerator();

        gen.addProvider(e.includeServer(), new BlockStateGen(gen.getPackOutput(), e.getExistingFileHelper()));
        gen.addProvider(e.includeServer(), new ItemModelGen(gen.getPackOutput(), e.getExistingFileHelper()));
        BlockTagGen block_gen = gen.addProvider(e.includeServer(), new BlockTagGen(gen.getPackOutput(), e.getLookupProvider(), e.getExistingFileHelper()));
        gen.addProvider(e.includeServer(), new ItemTagGen(gen.getPackOutput(), e.getLookupProvider(), block_gen.contentsGetter()));

    }
}
