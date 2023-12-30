package com.tkisor.uwtweaker.upd8r;

import com.tkisor.uwtweaker.config.UwTweakerConfig;

public enum CurrentVersionObj implements IVersion {
    INSTANCE;
    final CurrentVersion currentVersion;

    CurrentVersionObj() {
        currentVersion = new CurrentVersion(
                UwTweakerConfig.getConfig().getUpd8rConfig().getCurrentVersionName(),
                UwTweakerConfig.getConfig().getUpd8rConfig().getCurrentVersionCode(),
                UwTweakerConfig.getConfig().getUpd8rConfig().getCurrentVersionFormat()
        );
    }

    @Override
    public String getVersionName() {
        return currentVersion.versionName;
    }

    @Override
    public int getVersionCode() {
        return currentVersion.versionCode;
    }

    @Override
    public String getVersionFormat() {
        return currentVersion.versionFormat
                .replace("%versionName%", currentVersion.versionName)
                .replace("%versionCode%", String.valueOf(currentVersion.versionCode));
    }
}
