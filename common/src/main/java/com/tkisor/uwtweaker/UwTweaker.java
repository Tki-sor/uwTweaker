package com.tkisor.uwtweaker;

import com.mojang.logging.LogUtils;
import com.tkisor.uwtweaker.config.UwTweakerConfig;
import dev.architectury.platform.Platform;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import java.lang.reflect.Field;

public class UwTweaker {
	public static final String MOD_ID = "uwtweaker";

	public static void init() {
		try {
			Field maximumValueField = RangedAttribute.class.getDeclaredField("f_22308_");
			maximumValueField.setAccessible(true);
			maximumValueField.set(Attributes.MAX_HEALTH, Double.MAX_VALUE);
		} catch (Exception e) {
			LogUtils.getLogger().error("UwTweaker failed to set max health to Double.MAX_VALUE: " + e.getMessage());
		}

		AutoConfig.register(UwTweakerConfig.class, GsonConfigSerializer::new);

		Platform.getMod(MOD_ID).registerConfigurationScreen(config -> AutoConfig.getConfigScreen(UwTweakerConfig.class, config).get());
    }
}
