package com.tkisor.uwtweaker.mixin;

import com.tkisor.uwtweaker.config.UwTweakerConfig;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {
    @Unique private final int uwTweaker$newLastFoodLevel = UwTweakerConfig.getConfig().getMaxFoodLevel();
    @Shadow private int foodLevel = uwTweaker$newLastFoodLevel;
    @Shadow private float saturationLevel = (float) (uwTweaker$newLastFoodLevel * 0.2);
    @Shadow private float exhaustionLevel;
    @Shadow private int tickTimer;
    @Shadow private int lastFoodLevel = uwTweaker$newLastFoodLevel;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void eat(int i, float itemStack) {
        this.foodLevel = Math.min(i + this.foodLevel, uwTweaker$newLastFoodLevel);
        this.saturationLevel = Math.min(this.saturationLevel + (float)i * itemStack * 2.0F, (float)this.foodLevel);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void tick(Player player) {
        Difficulty difficulty = player.level().getDifficulty();
        this.lastFoodLevel = this.foodLevel;
        if (this.exhaustionLevel > 4.0F) {
            this.exhaustionLevel -= 4.0F;
            if (this.saturationLevel > 0.0F) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
            } else if (difficulty != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        boolean bl = player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
        // 满饥饿值
        if (bl && this.saturationLevel > 0.0F && player.isHurt() && this.foodLevel >= uwTweaker$newLastFoodLevel) {
            ++this.tickTimer;
            if (this.tickTimer >= 10) {
                float f = Math.min(this.saturationLevel, 6.0F);
                player.heal(f / 6.0F);
                this.addExhaustion(f);
                this.tickTimer = 0;
            }
        } else if (bl && this.foodLevel >= (uwTweaker$newLastFoodLevel * 0.8) && player.isHurt()) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                player.heal(1.0F);
                this.addExhaustion(6.0F);
                this.tickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    player.hurt(player.damageSources().starve(), 1.0F);
                }

                this.tickTimer = 0;
            }
        } else {
            this.tickTimer = 0;
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean needsFood() {
        return this.foodLevel < uwTweaker$newLastFoodLevel;
    }

    @Shadow
    public abstract void addExhaustion(float f);

}
