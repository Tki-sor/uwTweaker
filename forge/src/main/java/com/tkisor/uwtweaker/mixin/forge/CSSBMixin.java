package com.tkisor.uwtweaker.mixin.forge;

import cn.mcxkly.classicandsimplestatusbars.other.helper;
import cn.mcxkly.classicandsimplestatusbars.overlays.FoodLevel;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodLevel.class)
public class CSSBMixin {
    @Inject(method = "renderFoodValue", at = @At("RETURN"), remap = false)
    private void renderFoodValue(Font font, GuiGraphics guiGraphics, int x, int y, Player player, CallbackInfo ci) {
        String text2 = null;
        int x1 = x + 10 + font.width(helper.KeepOneDecimal(player.getFoodData().getFoodLevel()));
        int foodMax = player.getFoodData().getLastFoodLevel(); // 饥饿最大值
        if (player.getFoodData().getSaturationLevel() > 0) {
            // 添加的部分 ---
            x1 += font.width("+" + helper.KeepOneDecimal(player.getFoodData().getSaturationLevel()));
            text2 = "/" + helper.KeepOneDecimal(foodMax);
            guiGraphics.drawString(font, text2, x1, y - 9, 0xF4A460, false);
        } else {
            text2 = "/" + helper.KeepOneDecimal(foodMax);
            guiGraphics.drawString(font, text2, x1, y - 9, 0xF4A460, false);
        }
    }
}