package net.supcm.wizz.common.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import net.supcm.wizz.WizzMod;
import net.supcm.wizz.common.enchantment.Enchantments;
import net.supcm.wizz.common.handler.EnchantmentsHandler;
import net.supcm.wizz.common.network.PacketHandler;
import net.supcm.wizz.common.network.packets.CodexScreenPacket;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class CodexItem extends Item {
    private final int type;
    public CodexItem() {
        this(0);
    }
    protected CodexItem(int type) {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
        this.type = type;
    }
    @Override public boolean isFoil(ItemStack stack) {return true;}
    @Override public void appendHoverText(ItemStack stack, @Nullable Level world,
                                          List<Component> list, TooltipFlag flag) {
        list.add(1, Component.translatable("item.codex.info1", 0));
        list.add(2, Component.translatable("item.codex.info2", 0));
    }
    @Override public void inventoryTick(ItemStack stack, Level world, Entity entity,
                                        int slot, boolean flag) {
        if(!world.isClientSide) {
            CompoundTag tag = stack.getOrCreateTag();
            if(!tag.contains("Revealed")) {
                ListTag list = new ListTag();
                EnchantmentsHandler.T1_LIST.forEach(enchantment ->
                        list.add(StringTag.valueOf(getGlyphsFor(enchantment) +
                                "'" + EnchantmentsHandler.getEnchantmentId(enchantment))));
                list.add(StringTag.valueOf(getGlyphsFor(
                        Enchantments.UNSTABILITY.get()) +
                        "'" + EnchantmentsHandler.getEnchantmentId(Enchantments.UNSTABILITY.get())));
                tag.put("Revealed", list);
            }
        }
    }

    @Override public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if(!world.isClientSide) {
            CompoundTag tag = player.getItemInHand(hand).getOrCreateTag();
            PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new CodexScreenPacket(tag));
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }
    public static String getGlyphsFor(Enchantment ench) {
        for (Map.Entry<String, Enchantment> entry : EnchantmentsHandler.T1_MAP.entrySet())
            if(entry.getValue() == ench)
                return entry.getKey();
        for (Map.Entry<String, Enchantment> entry : EnchantmentsHandler.T2_MAP.entrySet())
            if(entry.getValue() == ench)
                return entry.getKey();
        for (Map.Entry<String, Enchantment> entry : EnchantmentsHandler.T3_MAP.entrySet())
            if(entry.getValue() == ench)
                return entry.getKey();
        return "exception";
    }
}
