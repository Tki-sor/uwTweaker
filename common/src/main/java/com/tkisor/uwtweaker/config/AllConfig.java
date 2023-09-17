package com.tkisor.uwtweaker.config;

import me.shedaniel.autoconfig.AutoConfig;

public class AllConfig {
    public static UwTweakerConfig getUwTweakerConfig() {
        return AutoConfig.getConfigHolder(UwTweakerConfig.class).getConfig();
    }

}
