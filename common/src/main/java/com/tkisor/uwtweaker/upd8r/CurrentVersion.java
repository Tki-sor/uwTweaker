package com.tkisor.uwtweaker.upd8r;

public class CurrentVersion {
    String versionName;
    int versionCode;
    String versionFormat;

    protected CurrentVersion(String versionName, int versionCode, String versionFormat) {
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.versionFormat = versionFormat;
    }
}
