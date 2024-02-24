package net.supcm.wizz.common.block;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.DropExperienceBlock;

public class LavaCrystalOreBlock extends DropExperienceBlock {
    public LavaCrystalOreBlock(Properties props) {
        super(props, UniformInt.of(0, 5));
    }
}
