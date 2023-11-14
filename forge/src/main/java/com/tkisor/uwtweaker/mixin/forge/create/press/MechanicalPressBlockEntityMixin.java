package com.tkisor.uwtweaker.mixin.forge.create.press;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.tkisor.uwtweaker.config.CreateTweaker;
import com.tkisor.uwtweaker.config.UwTweakerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = MechanicalPressBlockEntity.class, remap = false)
public abstract class MechanicalPressBlockEntityMixin extends BasinOperatingBlockEntity {
    private static int CYCLE = UwTweakerConfig.getConfig().getCreateTweaker().getPressCYCLE();
    @Shadow public PressingBehaviour pressingBehaviour;

    public MechanicalPressBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void startProcessingBasin() {
        if (pressingBehaviour.running && pressingBehaviour.runningTicks <= CYCLE / 2)
            return;
        super.startProcessingBasin();
        pressingBehaviour.start(PressingBehaviour.Mode.BASIN);
    }

    public boolean isSpeedRequirementFulfilled() {
        if (!(getBlockState().getBlock() instanceof IRotate))
            return true;
        return Math.abs(getSpeed()) >= 4;
    }
}
