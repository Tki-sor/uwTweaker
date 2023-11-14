package com.tkisor.uwtweaker.mixin.forge.create.press;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.tkisor.uwtweaker.config.CreateTweaker;
import com.tkisor.uwtweaker.config.UwTweakerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.*;

import java.util.List;

@Mixin(value = PressingBehaviour.class, remap = false)
public abstract class PressingBehaviourMixin extends BeltProcessingBehaviour {
    private static int CYCLE = UwTweakerConfig.getConfig().getCreateTweaker().getPressCYCLE();
    @Shadow @Final public static int ENTITY_SCAN;
    @Shadow public List<ItemStack> particleItems;

    @Shadow public PressingBehaviour.PressingBehaviourSpecifics specifics;
    @Shadow public int prevRunningTicks;
    @Shadow public int runningTicks;
    @Shadow public boolean running;
    @Shadow public boolean finished;
    @Shadow public PressingBehaviour.Mode mode;

    @Shadow int entityScanCooldown;

    public PressingBehaviourMixin(SmartBlockEntity be) {
        super(be);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public float getRenderedHeadOffset(float partialTicks) {
        if (!running)
            return 0;
        int runningTicks = Math.abs(this.runningTicks);
        float ticks = Mth.lerp(partialTicks, prevRunningTicks, runningTicks);
        if (runningTicks < (CYCLE * 2) / 3)
            return (float) Mth.clamp(Math.pow(ticks / CYCLE * 2, 3), 0, 1);
        return Mth.clamp((CYCLE - ticks) / CYCLE * 3, 0, 1);
    }

    @Shadow public abstract void start(PressingBehaviour.Mode mode);
    @Shadow public abstract boolean inWorld();
    @Shadow public abstract boolean onBasin();

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void tick() {
        super.tick();

        Level level = getWorld();
        BlockPos worldPosition = getPos();

        if (!running || level == null) {
            if (level != null && !level.isClientSide) {

                if (specifics.getKineticSpeed() == 0)
                    return;
                if (entityScanCooldown > 0)
                    entityScanCooldown--;
                if (entityScanCooldown <= 0) {
                    entityScanCooldown = ENTITY_SCAN;

                    if (BlockEntityBehaviour.get(level, worldPosition.below(2),
                            TransportedItemStackHandlerBehaviour.TYPE) != null)
                        return;
                    if (AllBlocks.BASIN.has(level.getBlockState(worldPosition.below(2))))
                        return;

                    for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class,
                            new AABB(worldPosition.below()).deflate(.125f))) {
                        if (!itemEntity.isAlive() || !itemEntity.onGround())
                            continue;
                        if (!specifics.tryProcessInWorld(itemEntity, true))
                            continue;
                        start(PressingBehaviour.Mode.WORLD);
                        return;
                    }
                }

            }
            return;
        }

        if (level.isClientSide && runningTicks == -CYCLE / 2) {
            prevRunningTicks = CYCLE / 2;
            return;
        }

        if (runningTicks == CYCLE / 2 && specifics.getKineticSpeed() != 0) {
            if (inWorld())
                applyInWorld();
            if (onBasin())
                applyOnBasin();

            if (level.getBlockState(worldPosition.below(2))
                    .getSoundType() == SoundType.WOOL)
                AllSoundEvents.MECHANICAL_PRESS_ACTIVATION_ON_BELT.playOnServer(level, worldPosition);
            else
                AllSoundEvents.MECHANICAL_PRESS_ACTIVATION.playOnServer(level, worldPosition, .5f,
                        .75f + (Math.abs(specifics.getKineticSpeed()) / 1024f));

            if (!level.isClientSide)
                blockEntity.sendData();
        }

        if (!level.isClientSide && runningTicks > CYCLE) {
            finished = true;
            running = false;
            particleItems.clear();
            specifics.onPressingCompleted();
            blockEntity.sendData();
            return;
        }

        prevRunningTicks = runningTicks;
        runningTicks += getRunningTickSpeed();
        if (prevRunningTicks < CYCLE / 2 && runningTicks >= CYCLE / 2) {
            runningTicks = CYCLE / 2;
            // Pause the ticks until a packet is received
            if (level.isClientSide && !blockEntity.isVirtual())
                runningTicks = -(CYCLE / 2);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int getRunningTickSpeed() {
        Level level = getWorld();
        BlockPos worldPosition = getPos();

        MechanicalPressBlockEntity blockEntity = (MechanicalPressBlockEntity) level.getBlockEntity(worldPosition);
        if (blockEntity != null && !blockEntity.isSpeedRequirementFulfilled()) {
            return 0;
        }

        float speed = specifics.getKineticSpeed();
        speed = Math.abs(speed);
        if (speed >= 8) {
            speed = (float) ((speed / (Math.log(speed) / Math.log(1.4))) * 4 + 2);
        }

        if (speed == 0)
            return 0;
        return (int) Mth.lerp(Mth.clamp(Math.abs(speed) / 512f, 0, 1), 1, 60);
    }

    @Shadow protected abstract void applyOnBasin();
    @Shadow protected abstract void applyInWorld();
}
