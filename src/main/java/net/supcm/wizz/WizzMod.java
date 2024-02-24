package net.supcm.wizz;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.supcm.wizz.common.block.Blocks;
import net.supcm.wizz.common.block.entity.BlockEntities;
import net.supcm.wizz.common.enchantment.Enchantments;
import net.supcm.wizz.common.item.Items;
import net.supcm.wizz.common.network.PacketHandler;
import net.supcm.wizz.common.sound.Sounds;
import net.supcm.wizz.data.recipes.Recipes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(WizzMod.MODID)
public class WizzMod {
    public static final String MODID = "wizz";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public WizzMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Items.ITEMS.register(bus);
        CreativeTabs.TABS.register(bus);
        Blocks.BLOCKS.register(bus);
        BlockEntities.BLOCK_ENTITIES.register(bus);
        Enchantments.ENCHANTMENTS.register(bus);
        Recipes.RECIPES.register(bus);
        Recipes.SERIALIZERS.register(bus);
        Sounds.SOUNDS.register(bus);
        PacketHandler.registerPackets();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, WizzModConfig.SPEC,
                MODID + "-common.toml");
    }

    public static class CreativeTabs {

        public static final List<RegistryObject<Item>> ADD_TO_TAB_WIZZ = new ArrayList<>();
        public static final DeferredRegister<CreativeModeTab> TABS =
                DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WizzMod.MODID);

        public static final RegistryObject<CreativeModeTab> WIZZ_TAB = TABS.register("wizz_tab", () ->
                CreativeModeTab.builder().title(Component.translatable("creative_tab.wizz.wizz_tab")).icon(() -> new ItemStack(Items.OKU.get()))
                        .displayItems((params, out) -> ADD_TO_TAB_WIZZ.forEach(item -> out.accept(item.get()))).build());
    }

}
