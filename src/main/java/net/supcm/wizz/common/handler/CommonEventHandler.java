package net.supcm.wizz.common.handler;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.network.PacketDistributor;
import net.supcm.wizz.WizzMod;
import net.supcm.wizz.common.enchantment.DarknessCurseEnchantment;
import net.supcm.wizz.common.enchantment.Enchantments;
import net.supcm.wizz.common.item.Items;
import net.supcm.wizz.common.network.PacketHandler;
import net.supcm.wizz.common.network.packets.T2ListPacket;
import net.supcm.wizz.common.network.packets.T3ListPacket;

import java.util.*;

public class CommonEventHandler {

    @Mod.EventBusSubscriber(modid = WizzMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventHandler {
        @SubscribeEvent public static void initLists(FMLLoadCompleteEvent e) {
            e.enqueueWork(EnchantmentsHandler::initAllLists);
        }
    }

    @Mod.EventBusSubscriber(modid = WizzMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEventHandler {
        @SubscribeEvent public static void onLootTableLoad(LootTableLoadEvent e) {
            if(e.getTable().getLootTableId().getPath().startsWith("chest") &&
                    !e.getTable().getLootTableId().getPath().contains("village"))
                e.getTable().addPool(LootPool.lootPool()
                        .name("casket_pool")
                        .add(LootTableReference.lootTableReference(new ResourceLocation(WizzMod.MODID,
                                "casket_pool")))
                        .build());
            else if(e.getTable().getLootTableId().getPath().startsWith("entities"))
                e.getTable().addPool(LootPool.lootPool()
                        .name("unstable_glyphs")
                        .add(LootTableReference.lootTableReference(new ResourceLocation(WizzMod.MODID,
                                "unstable_glyphs")))
                        .build());
        }
        @SubscribeEvent public static void onWorldLoaded(LevelEvent.Load e) {
            if(e.getLevel() instanceof ServerLevel) {
                long seed = ((ServerLevel)e.getLevel()).getSeed();
                randomizeFirstList(new Random(seed));
                createSecondList(new Random(seed));
                createThirdList(new Random(seed));
            }
        }
        @SubscribeEvent public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent e) {
            if(!e.getEntity().level().isClientSide){
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) e.getEntity()),
                        new T2ListPacket(createSecondList(new Random(((ServerLevel)e.getEntity().level()).getSeed()))));
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) e.getEntity()),
                        new T3ListPacket(createThirdList(new Random(((ServerLevel)e.getEntity().level()).getSeed()))));
            }
        }
        @SubscribeEvent public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent e) {
            if(!e.getEntity().level().isClientSide){
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) e.getEntity()),
                        new T2ListPacket(new ArrayList<>()));
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) e.getEntity()),
                        new T3ListPacket(new ArrayList<>()));
            }
        }
        public static void randomizeFirstList(Random rand) {
            Collections.shuffle(EnchantmentsHandler.GLYPHS_LIST, rand);
            for(int i = 0; i < EnchantmentsHandler.T1_LIST.size(); i++)
                EnchantmentsHandler.T1_MAP.put(EnchantmentsHandler.GLYPHS_LIST.get(i),
                        EnchantmentsHandler.T1_LIST.get(i));
        }
        public static List<String> createSecondList(Random rand) {
            List<String> words = new ArrayList<>();
            for (String symbol : EnchantmentsHandler.GLYPHS_LIST)
                for (String symbol1 : EnchantmentsHandler.GLYPHS_LIST)
                    words.add(symbol + "_" + symbol1);
            Collections.shuffle(words, rand);
            for(int i = 0; i < EnchantmentsHandler.T2_LIST.size(); i++)
                if(!EnchantmentsHandler.T2_MAP.containsValue(EnchantmentsHandler.T2_LIST.get(i)))
                    EnchantmentsHandler.T2_MAP.put(words.get(i), EnchantmentsHandler.T2_LIST.get(i));
            return new ArrayList<>(EnchantmentsHandler.T2_MAP.keySet());
        }
        public static List<String> createThirdList(Random rand) {
            List<String> words = new ArrayList<>();
            for (String symbol : EnchantmentsHandler.GLYPHS_LIST)
                for (String symbol1 : EnchantmentsHandler.GLYPHS_LIST)
                    for (String symbol2 : EnchantmentsHandler.GLYPHS_LIST)
                        words.add(symbol + "_" + symbol1
                                + "_" + symbol2);
            Collections.shuffle(words, rand);
            for(int i = 0; i < EnchantmentsHandler.T3_LIST.size(); i++)
                if(!EnchantmentsHandler.T3_MAP.containsValue(EnchantmentsHandler.T3_LIST.get(i)))
                    EnchantmentsHandler.T3_MAP.put(words.get(i), EnchantmentsHandler.T3_LIST.get(i));
            return new ArrayList<>(EnchantmentsHandler.T3_MAP.keySet());
        }
        @SubscribeEvent public static void xpDrop(BlockEvent.BreakEvent e) {
            if(e.getPlayer() != null && e.getPlayer().getMainHandItem().isEnchanted()) {
                Map<Enchantment, Integer> data =
                        EnchantmentHelper.getEnchantments(e.getPlayer().getMainHandItem());
                if(data.containsKey(Enchantments.XP_BOOST.get()))
                    e.setExpToDrop(e.getExpToDrop() *
                            data.get(Enchantments.XP_BOOST.get()));
            }
        }
        @SubscribeEvent public static void xpDrop(LivingExperienceDropEvent e) {
            if(e.getAttackingPlayer() != null && e.getAttackingPlayer().getMainHandItem().isEnchanted()) {
                Map<Enchantment, Integer> data =
                        EnchantmentHelper.getEnchantments(e.getAttackingPlayer().getMainHandItem());
                if(data.containsKey(Enchantments.XP_BOOST.get()))
                    e.setDroppedExperience(e.getDroppedExperience() *
                            data.get(Enchantments.XP_BOOST.get()));
            }
        }
        @SubscribeEvent public static void itemDrop(LivingDropsEvent e) {
            if(e.isRecentlyHit() && e.getSource().getEntity() != null) {
                DamageSource source = e.getSource();
                if(source.getMsgId().equals("player")) {
                    Player player = (Player)source.getEntity();
                    if(player != null && player.getMainHandItem().isEnchanted()) {
                        Map<Enchantment, Integer> data =
                                EnchantmentHelper.getEnchantments(player.getMainHandItem());
                        if(data.containsKey(Enchantments.UNSTABILITY.get()) &&
                                e.getEntity().level().getRandom().nextInt(3) == 0) {
                            e.getDrops().clear();
                            e.getDrops().add(createDropsList(e.getEntity())
                                    .get(e.getEntity().level().getRandom().nextInt(6)));
                        }
                        if(data.containsKey(Enchantments.XP_BOOST.get()))
                            e.setCanceled(true);
                    }
                }
            }
        }
        private static List<ItemEntity> createDropsList(Entity entity) {
            List<ItemEntity> entities = new ArrayList<>();
            ItemStack[] stack = new ItemStack[] {
                    new ItemStack(Items.IRO.get()),
                    new ItemStack(Items.NOY.get()),
                    new ItemStack(Items.SAT.get()),
                    new ItemStack(Items.BAL.get()),
                    new ItemStack(Items.WOY.get()),
                    new ItemStack(Items.VER.get())
            };
            for(int i = 0; i < stack.length; i++) {
                ItemEntity item = new ItemEntity(entity.level(),
                        entity.blockPosition().getX(),
                        entity.blockPosition().getY(),
                        entity.blockPosition().getZ(),
                        stack[i]);
                entities.add(item);
            }
            return entities;
        }
        @SubscribeEvent public static void onRightClicked(PlayerInteractEvent.RightClickItem e) {
            if(!e.getLevel().isClientSide){
                if (e.getItemStack().isEnchanted()) {
                    Map<Enchantment, Integer> data =
                            EnchantmentHelper.getEnchantments(e.getItemStack());
                    if (data.containsKey(Enchantments.GRAVITY_CORE.get())) {
                        List<ItemEntity> entities = e.getLevel().getEntitiesOfClass(ItemEntity.class,
                                new AABB(
                                        e.getEntity().blockPosition().getX() - (4 * data.get(Enchantments.GRAVITY_CORE.get())),
                                        e.getEntity().blockPosition().getY() - (2 * data.get(Enchantments.GRAVITY_CORE.get())),
                                        e.getEntity().blockPosition().getZ() - (4 * data.get(Enchantments.GRAVITY_CORE.get())),
                                        e.getEntity().blockPosition().getX() + (4 * data.get(Enchantments.GRAVITY_CORE.get())),
                                        e.getEntity().blockPosition().getY() + (2 * data.get(Enchantments.GRAVITY_CORE.get())),
                                        e.getEntity().blockPosition().getZ() + (4 * data.get(Enchantments.GRAVITY_CORE.get()))));
                        if(entities.size() > 0 && !e.getEntity().isCreative()) {
                            if(e.getItemStack().isDamageableItem())
                                e.getItemStack().setDamageValue(e.getItemStack().getDamageValue() + 1);
                            else
                                e.getItemStack().shrink(1);
                        }
                        for (ItemEntity entity : entities) {
                            if(e.getEntity().isCreative() && e.getEntity().getInventory().getFreeSlot() == -1) break;
                            entity.setPickUpDelay(0);
                            Vec3 move_to = new Vec3((e.getPos().getX() + 0.5) - entity.getX(),
                                    e.getPos().getY() - entity.getY(),
                                    (e.getPos().getZ() + 0.5) - entity.getZ());
                            entity.move(MoverType.PLAYER, move_to);
                        }
                    }
                }
            }
        }
        @SubscribeEvent public static void onLivingTick(LivingEvent.LivingTickEvent e) {
            if(!e.getEntity().level().isClientSide) {
                if(e.getEntity().getItemInHand(InteractionHand.MAIN_HAND).getAllEnchantments().keySet().stream()
                        .anyMatch(ench -> ench instanceof DarknessCurseEnchantment) ||
                        e.getEntity().getItemInHand(InteractionHand.OFF_HAND).getAllEnchantments().keySet().stream()
                                .anyMatch(ench -> ench instanceof DarknessCurseEnchantment)) {
                    e.getEntity().addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100));
                    return;
                }
                for(ItemStack item : e.getEntity().getArmorSlots()) {
                    if(item.getAllEnchantments().keySet().stream()
                            .anyMatch(ench -> ench instanceof DarknessCurseEnchantment)) {
                        e.getEntity().addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100));
                        return;
                    }
                }
            }
        }
    }
}
