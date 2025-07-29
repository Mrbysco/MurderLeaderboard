package com.mrbysco.murderleaderboard.data;

import com.mrbysco.murderleaderboard.data.assets.MurderLanguage;
import com.mrbysco.murderleaderboard.data.assets.MurderModels;
import com.mrbysco.murderleaderboard.data.data.MurderLootProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class MurderDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(true, new MurderLootProvider(packOutput, lookupProvider));

		generator.addProvider(true, new MurderLanguage(packOutput));
		generator.addProvider(true, new MurderModels(packOutput));
	}
}
