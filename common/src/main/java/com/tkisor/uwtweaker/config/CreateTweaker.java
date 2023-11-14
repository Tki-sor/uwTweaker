package com.tkisor.uwtweaker.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class CreateTweaker {
    @ConfigEntry.Gui.Tooltip
    private int pressCYCLE = 600;

    // 辊压机速度
    public int getPressCYCLE() {
        return pressCYCLE;
    }
}
