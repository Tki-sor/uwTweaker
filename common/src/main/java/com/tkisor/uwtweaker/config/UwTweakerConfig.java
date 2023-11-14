package com.tkisor.uwtweaker.config;

import com.tkisor.uwtweaker.UwTweaker;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = UwTweaker.MOD_ID)
public class UwTweakerConfig implements ConfigData {
    public static UwTweakerConfig getConfig() {
        return AutoConfig.getConfigHolder(UwTweakerConfig.class).getConfig();
    }

    @ConfigEntry.Gui.Tooltip
    private int maxFoodLevel = 40;
    public int getMaxFoodLevel() {
        return maxFoodLevel;
    }

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("createTweaker")
    private CreateTweaker createTweaker = new CreateTweaker();
    public CreateTweaker getCreateTweaker() {
        return createTweaker;
    }

}
