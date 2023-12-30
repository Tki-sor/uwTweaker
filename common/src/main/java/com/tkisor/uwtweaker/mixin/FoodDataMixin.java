package com.tkisor.uwtweaker.mixin;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {
    private final int maxFoodLevel = 100;
    @Shadow private int foodLevel = maxFoodLevel;
    @Shadow private float saturationLevel = (float) (maxFoodLevel * 0.2);
    @Shadow private float exhaustionLevel;
    @Shadow private int tickTimer;
    @Shadow private int lastFoodLevel = maxFoodLevel;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void eat(int i, float itemStack) {
        this.foodLevel = Math.min(i + this.foodLevel, maxFoodLevel);
        this.saturationLevel = Math.min(this.saturationLevel + (float) i * itemStack * 2.0F, (float) this.foodLevel);
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
        if (bl && player.isHurt()) {
            if (this.saturationLevel > 0.0F && this.foodLevel >= maxFoodLevel) {
                ++this.tickTimer;
                if (this.tickTimer >= 10) {  // 默认10
                    float f = Math.min(this.saturationLevel, 6.0F);
                    player.heal(f / 6.0F);
                    this.addExhaustion(f);
                    this.tickTimer = 0;
                }
            }
            else if (this.foodLevel >= (maxFoodLevel * 0.75)) {
                ++this.tickTimer;
                if (this.tickTimer >= 80) {
                    player.heal(1.0F);
                    this.addExhaustion(6.0F);
                    this.tickTimer = 0;
                }
            }
            else if (this.foodLevel >= (maxFoodLevel * 0.4) && this.saturationLevel > 0.0F) {
                ++this.tickTimer;
                if (this.tickTimer >= 120) {
                    player.heal(1.0F);
                    this.addExhaustion(6.0F);
                    this.tickTimer = 0;
                }
            }
            else if (this.foodLevel >= (maxFoodLevel * 0.4)) {
                ++this.tickTimer;
                if (this.tickTimer >= 240) {
                    player.heal(1.0F);
                    this.addExhaustion(6.0F);
                    this.tickTimer = 0;
                }
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
        return this.foodLevel < maxFoodLevel;
    }

    @Shadow
    public abstract void addExhaustion(float f);

}
