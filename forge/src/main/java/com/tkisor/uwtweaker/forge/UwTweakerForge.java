package com.tkisor.uwtweaker.forge;

import com.tkisor.uwtweaker.UwTweaker;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(UwTweaker.MOD_ID)
public class UwTweakerForge {
    public UwTweakerForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(UwTweaker.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        UwTweaker.init();


    }
}