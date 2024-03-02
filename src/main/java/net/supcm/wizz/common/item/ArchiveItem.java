package net.supcm.wizz.common.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.supcm.wizz.common.handler.EnchantmentsHandler;

import javax.annotation.Nullable;
import java.util.List;

public class ArchiveItem extends CodexItem {
    public ArchiveItem() { super(1); }
    @Override public Rarity getRarity(ItemStack stack) { return Rarity.UNCOMMON; }
    @Override public void appendHoverText(ItemStack stack, @Nullable Level world,
                                          List<Component> list, TooltipFlag flag) {
        list.add(1, Component.translatable("item.archive.info", 0));
    }
    @Override public void inventoryTick(ItemStack stack, Level world, Entity entity,
                                        int slot, boolean flag) {
        if(!world.isClientSide) {
            CompoundTag tag = stack.getOrCreateTag();
            if(!tag.contains("Revealed")) {
                ListTag list = new ListTag();
                EnchantmentsHandler.T1_LIST.forEach(str ->
                        list.add(StringTag.valueOf(getGlyphsFor(str) +
                                "'" + EnchantmentsHandler.getEnchantmentId(str))));
                EnchantmentsHandler.T2_LIST.forEach(str ->
                        list.add(StringTag.valueOf(getGlyphsFor(str) +
                                "'" + EnchantmentsHandler.getEnchantmentId(str))));
                EnchantmentsHandler.T3_LIST.forEach(str ->
                        list.add(StringTag.valueOf(getGlyphsFor(str) +
                                "'" + EnchantmentsHandler.getEnchantmentId(str))));
                tag.put("Revealed", list);
            }
        }
    }
}
