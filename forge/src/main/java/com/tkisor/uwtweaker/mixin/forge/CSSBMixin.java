package com.tkisor.uwtweaker.mixin.forge;

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
        if (player.getFoodData().getSaturationLevel() > 0) {
            //第一部分
            double food = Math.ceil(player.getFoodData().getFoodLevel() * 10) / 10; // 饥饿度
            int xx = x + 10;
            String text = String.valueOf(food);
            text = text.replace(".0", "");
            //第二部分
            xx = xx + font.width(text);
            food = Math.ceil(player.getFoodData().getSaturationLevel() * 10) / 10; // 饱食度
            text = "+" + food;
            text = text.replace(".0", "");
            //第三部分
            xx = xx + font.width(text);
            int foodMax = player.getFoodData().getLastFoodLevel(); // 饥饿最大值
            text = String.valueOf(foodMax);
            text = text.replace(".0", "");
            text = "/" + text;
            guiGraphics.drawString(font, text, xx, y - 9, 0xF4A460, false);
        }
    }
}
