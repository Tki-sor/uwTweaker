package com.tkisor.uwtweaker.upd8r;


public enum LatestVersionObj implements IVersion {
    INSTANCE;
    final LatestVersion latestVersion;

    LatestVersionObj() {
        latestVersion = LatestVersion.deserializer(Upd8rUtil.getLatestVersion());
    }

    public String getVersionName() {
        return latestVersion.versionName;
    }

    public int getVersionCode() {
        return latestVersion.versionCode;
    }

    public String getVersionFormat() {
        return latestVersion.versionFormat
                .replace("%versionName%", latestVersion.versionName)
                .replace("%versionCode%", String.valueOf(latestVersion.versionCode));
    }
}
