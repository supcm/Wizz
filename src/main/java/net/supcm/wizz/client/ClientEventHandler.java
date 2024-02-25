package net.supcm.wizz.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.supcm.wizz.WizzMod;
import net.supcm.wizz.client.renderer.blockentity.*;
import net.supcm.wizz.common.block.Blocks;
import net.supcm.wizz.common.block.entity.BlockEntities;
import net.supcm.wizz.common.item.Items;

public class ClientEventHandler {

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = WizzMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventHandler {
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers e) {
            e.registerBlockEntityRenderer(BlockEntities.ENCHANTED_TABLE.get(),
                    EnchantedTableBlockEntityRenderer::new);
            e.registerBlockEntityRenderer(BlockEntities.WORD_MACHINE.get(),
                    WordMachineBlockEntityRenderer::new);
            e.registerBlockEntityRenderer(BlockEntities.WORD_FORGE.get(),
                    WordForgeBlockEntityRenderer::new);
            e.registerBlockEntityRenderer(BlockEntities.MATRIX.get(),
                    MatrixBlockEntityRenderer::new);
            e.registerBlockEntityRenderer(BlockEntities.ENCHANTING_STATION.get(),
                    EnchantingStationBlockEntityRenderer::new);
            e.registerBlockEntityRenderer(BlockEntities.WORD_ERASER.get(),
                    WordEraserBlockEntityRenderer::new);
            e.registerBlockEntityRenderer(BlockEntities.THOUGHT_LOOM.get(),
                    ThoughtLoomBlockEntityRenderer::new);
            e.registerBlockEntityRenderer(BlockEntities.REASSESSMENT_PILLAR.get(),
                    ReassessmentPillarBlockEntityRenderer::new);
            e.registerBlockEntityRenderer(BlockEntities.REASSESSMENT_TABLE.get(),
                    ReassessmentTableBlockEntityRenderer::new);
            e.registerBlockEntityRenderer(BlockEntities.ALCHEMY_CAULDRON.get(),
                    AlchemyCauldronBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
            event.getBlockColors().register((state, getter, pos, tint) -> BiomeColors.getAverageWaterColor(getter, pos),
                    Blocks.ALCHEMY_CAULDRON.get());
        }
        @SubscribeEvent
        public static void init(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ItemProperties.register(Items.SHADOW_CRYSTAL.get(),
                        new ResourceLocation(WizzMod.MODID, "shadow"),
                        (stack, world, entity, id) -> {
                            if(stack.getTag() != null && !stack.getTag().getBoolean("InShadow"))
                                return 1.0f;
                            return 0.0f;
                        });
                ItemProperties.register(Items.CATS_EYE.get(),
                        new ResourceLocation(WizzMod.MODID, "opened"),
                        (stack, world, entity, id) -> {
                            if(stack.getTag() != null && stack.getTag().getBoolean("Activated"))
                                return 1.0f;
                            return 0.0f;
                        });
                ItemProperties.register(Items.WISDOM_SEED.get(),
                        new ResourceLocation(WizzMod.MODID, "fullness"),
                        (stack, world, entity, id) -> {
                            if(stack.getTag() != null) {
                                int stored = stack.getTag().getInt("Stored");
                                if(stored >= 128)
                                    return 3.0f;
                                else if(stored >= 64)
                                    return 2.0f;
                                else if(stored >= 16)
                                    return 1.0f;
                            }
                            return 0.0f;
                        });
            });
        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = WizzMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEventHandler {
        @SubscribeEvent
        public static void onOpenGui(ScreenEvent.Opening e) {
            if(e.getNewScreen() instanceof EnchantmentScreen) {
                Minecraft.getInstance().player.closeContainer();
                Minecraft.getInstance().player.displayClientMessage(
                        Component.translatable("enchanting_table.message", 0),
                        true);
                e.setCanceled(true);
            }
        }
    }
}
