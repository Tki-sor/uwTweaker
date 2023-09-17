package com.tkisor.uwtweaker.config;

import com.tkisor.uwtweaker.UwTweaker;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

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
    @ConfigEntry.Category("upd8r")
    private Upd8r upd8r = new Upd8r();

    static class Upd8r {
        @ConfigEntry.Gui.Tooltip
        private boolean enableUpd8r = true;
        public boolean isEnableUpd8r() {
            return enableUpd8r;
        }
    }


    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("memorysweep")
    private MemorySweep memorySweep = new MemorySweep();

    static class MemorySweep {
        @ConfigEntry.Gui.Tooltip
        private boolean enableMemorySweep = true;
        public boolean isEnableMemorySweep() {
            return enableMemorySweep;
        }
    }

}
