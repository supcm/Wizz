package net.supcm.wizz.client.screen;

import com.mojang.math.Axis;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;
import net.supcm.wizz.WizzMod;
import net.supcm.wizz.common.block.entity.MortarBlockEntity;
import net.supcm.wizz.common.network.PacketHandler;
import net.supcm.wizz.common.network.packets.MortarRecipePacket;
import net.supcm.wizz.common.sound.Sounds;

import java.util.ArrayList;
import java.util.List;

public class MortarScreen extends Screen {
    private final ResourceLocation TEXTURE = new ResourceLocation(WizzMod.MODID,
            "textures/gui/mortar_gui.png");
    private final Minecraft mc = Minecraft.getInstance();
    private MortarBlockEntity tile;
    private int count = 0;
    private int cooldownTicks = 0;
    private final BlockPos pos;
    Pestle pestle;
    List<Action> actions = new ArrayList<>();
    final SoundEvent sound;
    public MortarScreen(BlockPos pos) {
        super(Component.translatable("block.wizz.mortar"));
        this.pos = pos;
        tile = (MortarBlockEntity) mc.level.getBlockEntity(pos);
        sound = isInventoryEmpty() ? Sounds.MORTAR_EMPTY.get() : Sounds.MORTAR_NONEMPTY.get();

    }
    @Override public boolean isPauseScreen() { return false; }
    @Override protected void init() {
         pestle = new Pestle();
        super.init();
    }
    @Override
    public boolean keyPressed(int key, int x, int y) {
        if(super.keyPressed(key, x, y))
            return true;
        else {
            if(key == mc.options.keyUp.getKey().getValue()) {
                if(cooldownTicks == 0 && !pestle.is_moving && pestle.dir != Pestle.Direction.UP) {
                    pestle.setDirection(Pestle.Direction.UP);
                    return true;
                }
            } else if(key == mc.options.keyDown.getKey().getValue()) {
                if(cooldownTicks == 0 && !pestle.is_moving && pestle.dir != Pestle.Direction.DOWN) {
                    pestle.setDirection(Pestle.Direction.DOWN);
                    actions.add(Action.CRUSH);
                    count++;
                    return true;
                }
            } else if(key == mc.options.keyLeft.getKey().getValue()) {
                if(cooldownTicks == 0 && !pestle.is_moving && pestle.dir != Pestle.Direction.LEFT) {
                    pestle.setDirection(Pestle.Direction.LEFT);
                    actions.add(Action.GRIND);
                    count++;
                    return true;
                }
            } else if(key == mc.options.keyRight.getKey().getValue()) {
                if(cooldownTicks == 0 && !pestle.is_moving && pestle.dir != Pestle.Direction.RIGHT) {
                    pestle.setDirection(Pestle.Direction.RIGHT);
                    actions.add(Action.GRIND);
                    count++;
                    return true;
                }
            }
            return false;
        }
    }
    @Override public void onClose() {
        if(count >= 7) {
            tile = null;
            PacketHandler.CHANNEL.send(new MortarRecipePacket(pos, getMode()), PacketDistributor.SERVER.noArg());
        }
        super.onClose();
    }
    @Override public void tick() {
        if(count >= 7) {
            onClose();
        }
        if(cooldownTicks > 0)
            cooldownTicks--;
        pestle.move();
        super.tick();
    }
    int[] getPestleOffset() {
        //x = digress, y = integer (simple coords)
        int[] offset = new int[] {0, 0};
        offset[0] = (int) (-pestle.cur_x * 15);
        offset[1] = (int) (-pestle.cur_y * 20);
        return offset;
    }
    @Override public void renderBackground(GuiGraphics gui, int x, int y, float partialTick) {
        super.renderBackground(gui, x, y, partialTick);
        gui.blit(TEXTURE, (width) / 2 - 50, height / 2 - 18, 0, 0, 95, 40);
    }
    void renderItems(GuiGraphics gui) {
        if(gui == null || tile == null) return;
        List<ItemStack> items = new ArrayList<>();
        for(int i = 0; i < tile.handler.getSlots(); i++)
            items.add(new ItemStack(tile.handler.getStackInSlot(i).getItem(), 1));
        if(!items.isEmpty()) {
            int i = -(items.size() / 2);
            for (ItemStack stack : items) {
                gui.renderFakeItem(stack, (width) / 2 - 84, height / 2 + 18 * i++);
            }
        }
    }
    void renderButtons(GuiGraphics gui) {
        renderButton(gui, pestle.dir == Pestle.Direction.UP || cooldownTicks > 0 || pestle.is_moving,
                0, (width) / 2 + 128, height / 2 - 16);
        renderButton(gui, pestle.dir == Pestle.Direction.RIGHT || cooldownTicks > 0 || pestle.is_moving,
                1, (width) / 2 + 144, height / 2);
        renderButton(gui, pestle.dir == Pestle.Direction.LEFT || cooldownTicks > 0 || pestle.is_moving,
                2, (width) / 2 + 112,height / 2);
        renderButton(gui, pestle.dir == Pestle.Direction.DOWN || cooldownTicks > 0 || pestle.is_moving,
                3, (width) / 2 + 128, height / 2);
    }
    void renderButton(GuiGraphics gui, boolean disabled, int type, int x, int y) {
        int x_offset = 192;
        int y_offset = type * 16;
        x_offset += disabled ? 16 : 0;
        gui.blit(TEXTURE, x, y, x_offset, y_offset, 16, 16);
    }
    @Override public void render(GuiGraphics gui, int x, int y, float partialTick) {
        super.render(gui, x, y, partialTick);
        renderItems(gui);
        int[] offset = getPestleOffset();
        gui.pose().pushPose();
        gui.pose().rotateAround(Axis.ZP.rotationDegrees(offset[0]), (width) / 2.0f, height / 2.0f - 8 + offset[1], 0);
        gui.blit(TEXTURE, (width) / 2 - 24, height / 2 - 40 + offset[1], 137, 0, 48, 64);
        gui.pose().popPose();
        gui.blit(TEXTURE, (width) / 2 - 51, height / 2 + 1, 0, 56, 97, 79);
        renderButtons(gui);
        gui.drawCenteredString(mc.font,
                Component.translatable("gui.wizz.mortar.progress").getString() + ": " +
                        count + "/7", (width) / 2, height / 2 + 120,
                0xFFFFFF);
    }
    String getMode() {
        List<Action> grind = actions.stream().filter(act -> act == Action.GRIND).toList();
        List<Action> crush = actions.stream().filter(act -> act == Action.CRUSH).toList();
        return grind.size() > crush.size() ? "grind" : "crush";
    }

    boolean isInventoryEmpty() {
        for(int i = 0; i < tile.handler.getSlots(); i++)
            if(!tile.handler.getStackInSlot(i).isEmpty())
                return false;
        return true;
    }
    private enum Action {
        CRUSH, GRIND
    }
    public class Pestle {
        private enum Direction {
            UP(0, 0), DOWN(0, -1), LEFT(-1, -1), RIGHT(1, -1);
            final int x;
            final int y;
            Direction(int x, int y) {
                this.x = x;
                this.y = y;
            }
        }

        float cur_x;
        float cur_y;
        Direction dir = Direction.DOWN;
        boolean is_moving = false;
        private Pestle() {
            cur_x = 0;
            cur_y = -1;
        }

        void move() {
            if(!isFloatEquals(cur_x, dir.x) || !isFloatEquals(cur_y, dir.y)) {
                is_moving = true;
                switch (dir) {
                    case UP -> {
                        if (isFloatEquals(cur_x, 0))
                            cur_y += 0.1f;
                        else if (cur_x < 0)
                            cur_x += 0.1f;
                        else
                            cur_x -= 0.1f;
                    }
                    case DOWN -> {
                        if (isFloatEquals(cur_x, 0))
                            cur_y -= 0.1f;
                        else if (cur_x < 0)
                            cur_x += 0.1f;
                        else
                            cur_x -= 0.1f;
                    }
                    case RIGHT -> {
                        if (isFloatEquals(cur_y, -1)) {
                            cur_x += 0.1f;
                        } else
                            cur_y -= 0.1f;
                    }
                    case LEFT -> {
                        if (isFloatEquals(cur_y, -1)) {
                            cur_x -= 0.1f;
                        } else
                            cur_y -= 0.1f;
                    }
                }
            } else {
                if(is_moving && dir != Direction.UP) {
                    Minecraft.getInstance().player.playSound(sound, 1, 1);
                    cooldownTicks += 15;
                }
                is_moving = false;
            }
        }

        boolean isFloatEquals(float a, float b) {
            return Math.abs(b - a) < 0.0001f;
        }

        void setDirection(Direction dir) {
            this.dir = dir;
        }
    }
}
