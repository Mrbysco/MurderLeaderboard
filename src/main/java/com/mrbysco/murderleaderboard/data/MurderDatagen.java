package com.mrbysco.murderleaderboard.data;

import com.mrbysco.murderleaderboard.data.assets.MurderBlockStates;
import com.mrbysco.murderleaderboard.data.assets.MurderItemModels;
import com.mrbysco.murderleaderboard.data.assets.MurderLanguage;
import com.mrbysco.murderleaderboard.data.data.MurderLootProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MurderDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

		generator.addProvider(event.includeServer(), new MurderLootProvider(packOutput));
		generator.addProvider(event.includeClient(), new MurderLanguage(packOutput));
		generator.addProvider(event.includeClient(), new MurderBlockStates(packOutput, existingFileHelper));
		generator.addProvider(event.includeClient(), new MurderItemModels(packOutput, existingFileHelper));
	}
}
