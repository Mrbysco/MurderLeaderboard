package com.mrbysco.murderleaderboard.data;

import com.mrbysco.murderleaderboard.data.assets.MurderBlockStates;
import com.mrbysco.murderleaderboard.data.assets.MurderItemModels;
import com.mrbysco.murderleaderboard.data.assets.MurderLanguage;
import com.mrbysco.murderleaderboard.data.data.MurderLootProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MurderDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

		generator.addProvider(new MurderLootProvider(generator));
		generator.addProvider(new MurderLanguage(generator));
		generator.addProvider(new MurderBlockStates(generator, existingFileHelper));
		generator.addProvider(new MurderItemModels(generator, existingFileHelper));
	}
}
