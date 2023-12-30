package com.tkisor.uwtweaker.mixin.forge.eio;

import com.enderio.base.common.AlphaWarning;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = AlphaWarning.class, remap = false)
public class AlphaWarningMixin {
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
    }
}
