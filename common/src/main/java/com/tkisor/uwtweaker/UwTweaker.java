package com.tkisor.uwtweaker;

import com.tkisor.uwtweaker.config.UwTweakerConfig;
import com.tkisor.uwtweaker.upd8r.compat.fancymenu.Upd8rPlace;
import de.keksuccino.fancymenu.menu.placeholder.v2.PlaceholderRegistry;
import dev.architectury.platform.Platform;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public class UwTweaker {
	public static final String MOD_ID = "uwtweaker";

	public static void init() {

		AutoConfig.register(UwTweakerConfig.class, GsonConfigSerializer::new);

		Platform.getMod(MOD_ID).registerConfigurationScreen(config -> AutoConfig.getConfigScreen(UwTweakerConfig.class, config).get());

		if (Platform.isModLoaded("fancymenu")) {
			PlaceholderRegistry.registerPlaceholder(new Upd8rPlace.CurrentVersion());
			PlaceholderRegistry.registerPlaceholder(new Upd8rPlace.LatestVersion());
		}
	}
}
