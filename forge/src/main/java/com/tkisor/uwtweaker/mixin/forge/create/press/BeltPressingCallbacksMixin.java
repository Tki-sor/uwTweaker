package com.tkisor.uwtweaker.mixin.forge.create.press;

import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.press.BeltPressingCallbacks;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.tkisor.uwtweaker.config.CreateTweaker;
import com.tkisor.uwtweaker.config.UwTweakerConfig;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.HOLD;
import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.PASS;

@Mixin(value = BeltPressingCallbacks.class, remap = false)
public class BeltPressingCallbacksMixin {
    private static int CYCLE = UwTweakerConfig.getConfig().getCreateTweaker().getPressCYCLE();

    /**
     * @author
     * @reason
     */
    @Overwrite
    static BeltProcessingBehaviour.ProcessingResult whenItemHeld(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler,
                                                                 PressingBehaviour behaviour) {

        if (behaviour.specifics.getKineticSpeed() == 0)
            return PASS;
        if (!behaviour.running)
            return PASS;
        if (behaviour.runningTicks != CYCLE / 2)
            return HOLD;

        behaviour.particleItems.clear();
        ArrayList<ItemStack> results = new ArrayList<>();
        if (!behaviour.specifics.tryProcessOnBelt(transported, results, false))
            return PASS;

        boolean bulk = behaviour.specifics.canProcessInBulk() || transported.stack.getCount() == 1;

        List<TransportedItemStack> collect = results.stream()
                .map(stack -> {
                    TransportedItemStack copy = transported.copy();
                    boolean centered = BeltHelper.isItemUpright(stack);
                    copy.stack = stack;
                    copy.locked = true;
                    copy.angle = centered ? 180 : Create.RANDOM.nextInt(360);
                    return copy;
                })
                .collect(Collectors.toList());

        if (bulk) {
            if (collect.isEmpty())
                handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.removeItem());
            else
                handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.convertTo(collect));

        } else {
            TransportedItemStack left = transported.copy();
            left.stack.shrink(1);

            if (collect.isEmpty())
                handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.convertTo(left));
            else
                handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.convertToAndLeaveHeld(collect, left));
        }

        behaviour.blockEntity.sendData();
        return HOLD;
    }
}
