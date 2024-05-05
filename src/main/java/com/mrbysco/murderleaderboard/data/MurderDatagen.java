package com.mrbysco.murderleaderboard.data;

import com.mrbysco.murderleaderboard.data.assets.MurderBlockStates;
import com.mrbysco.murderleaderboard.data.assets.MurderItemModels;
import com.mrbysco.murderleaderboard.data.assets.MurderLanguage;
import com.mrbysco.murderleaderboard.data.data.MurderLootProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class MurderDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(event.includeServer(), new MurderLootProvider(packOutput, lookupProvider));
		generator.addProvider(event.includeClient(), new MurderLanguage(packOutput));
		generator.addProvider(event.includeClient(), new MurderBlockStates(packOutput, existingFileHelper));
		generator.addProvider(event.includeClient(), new MurderItemModels(packOutput, existingFileHelper));
	}
}
