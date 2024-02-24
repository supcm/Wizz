package net.supcm.wizz.common.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.supcm.wizz.WizzMod;

public class Sounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,
            WizzMod.MODID);

    public static final RegistryObject<SoundEvent> MORTAR_EMPTY = registerSound("mortar_empty");
    public static final RegistryObject<SoundEvent> MORTAR_NONEMPTY = registerSound("mortar_nonempty");

    private static RegistryObject<SoundEvent> registerSound(String name) {
        return SOUNDS.register(name, () ->
                SoundEvent.createVariableRangeEvent(new ResourceLocation(WizzMod.MODID, name)));
    }
}
