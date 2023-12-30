package com.tkisor.uwtweaker.fabric;

import com.tkisor.uwtweaker.UwTweaker;
import net.fabricmc.api.ModInitializer;

public class UwTweakerFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        UwTweaker.init();

    }
}