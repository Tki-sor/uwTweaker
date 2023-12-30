package com.tkisor.uwtweaker.config;

import com.tkisor.uwtweaker.UwTweaker;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.List;

public class Upd8rConfig {
    @ConfigEntry.Gui.Tooltip()
    private String CurrentVersionName = "1.0.0";

    @ConfigEntry.Gui.Tooltip
    private int CurrentVersionCode = 0;

    @ConfigEntry.Gui.Tooltip
    private String CurrentVersionFormat = "%versionName%";

    @ConfigEntry.Gui.Tooltip
    private List<String> LatestVersionUrls = List.of();

    public String getCurrentVersionName() {
        return CurrentVersionName;
    }

    public int getCurrentVersionCode() {
        return CurrentVersionCode;
    }

    public String getCurrentVersionFormat() {
        return CurrentVersionFormat;
    }

    public List<String> getLatestVersionUrls() {
        return LatestVersionUrls;
    }
}
