package com.tkisor.uwtweaker.upd8r;

public class Api {
    public static IVersion getLatestVersion() {
        return LatestVersionObj.INSTANCE;
    }

    public static IVersion getCurrentVersion() {
        return CurrentVersionObj.INSTANCE;
    }
}
