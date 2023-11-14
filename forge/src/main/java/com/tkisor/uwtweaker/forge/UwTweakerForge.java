package com.tkisor.uwtweaker.forge;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.tkisor.uwtweaker.UwTweaker;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.objectweb.asm.*;
import sun.misc.Unsafe;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(UwTweaker.MOD_ID)
public class UwTweakerForge {
    public UwTweakerForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(UwTweaker.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        UwTweaker.init();

    }
}