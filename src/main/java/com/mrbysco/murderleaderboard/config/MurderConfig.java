package com.mrbysco.murderleaderboard.config;

import com.mrbysco.murderleaderboard.MurderLeaderboard;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

public class MurderConfig {
	public static class Common {
		public final ForgeConfigSpec.ConfigValue<? extends String> nameKey;

		Common(ForgeConfigSpec.Builder builder) {
			builder.comment("General settings")
					.push("General");

			nameKey = builder
					.comment("The name for the key used to store the killer's name in the murderer's persistent data")
					.define("nameKey", "Murderer");

			builder.pop();
		}
	}

	public static final ForgeConfigSpec commonSpec;
	public static final Common COMMON;

	static {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		commonSpec = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	@SubscribeEvent
	public static void onLoad(final ModConfigEvent.Loading configEvent) {
		MurderLeaderboard.LOGGER.debug("Loaded Murder Leaderboard's config file {}", configEvent.getConfig().getFileName());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
		MurderLeaderboard.LOGGER.debug("Murder Leaderboard's config just got changed on the file system!");
	}
}
