package com.mrbysco.murderleaderboard;

import com.mojang.logging.LogUtils;
import com.mrbysco.murderleaderboard.command.LeaderboardCommands;
import com.mrbysco.murderleaderboard.config.MurderConfig;
import com.mrbysco.murderleaderboard.handler.KillHandler;
import com.mrbysco.murderleaderboard.handler.SyncHandler;
import com.mrbysco.murderleaderboard.network.PacketHandler;
import com.mrbysco.murderleaderboard.registry.MurderRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

@Mod(MurderLeaderboard.MOD_ID)
public class MurderLeaderboard {
	public static final String MOD_ID = "murderleaderboard";
	public static final Logger LOGGER = LogUtils.getLogger();

	public MurderLeaderboard(IEventBus eventBus, Dist dist, ModContainer container) {
		container.registerConfig(ModConfig.Type.COMMON, MurderConfig.commonSpec);
		eventBus.register(MurderConfig.class);

		MurderRegistry.BLOCKS.register(eventBus);
		MurderRegistry.ITEMS.register(eventBus);
		MurderRegistry.BLOCK_ENTITIES.register(eventBus);

		eventBus.addListener(PacketHandler::setupPackets);
		eventBus.addListener(this::addTabContents);

		NeoForge.EVENT_BUS.register(new KillHandler());
		NeoForge.EVENT_BUS.register(new SyncHandler());
		NeoForge.EVENT_BUS.addListener(this::onCommandRegister);

		if (dist.isClient()) {
			container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
		}
	}

	private void addTabContents(final BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
			event.accept(MurderRegistry.TOP_PLAYER.get());
		}
	}

	public void onCommandRegister(RegisterCommandsEvent event) {
		LeaderboardCommands.initializeCommands(event.getDispatcher());
	}

	public static Identifier modLoc(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
