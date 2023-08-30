package com.mrbysco.murderleaderboard.data;

import com.mrbysco.murderleaderboard.data.assets.MurderBlockStates;
import com.mrbysco.murderleaderboard.data.assets.MurderItemModels;
import com.mrbysco.murderleaderboard.data.assets.MurderLanguage;
import com.mrbysco.murderleaderboard.data.data.MurderLootProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MurderDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

		generator.addProvider(event.includeServer(), new MurderLootProvider(generator));
		generator.addProvider(event.includeClient(), new MurderLanguage(generator));
		generator.addProvider(event.includeClient(), new MurderBlockStates(generator, existingFileHelper));
		generator.addProvider(event.includeClient(), new MurderItemModels(generator, existingFileHelper));
	}
}
