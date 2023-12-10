package com.mrbysco.murderleaderboard;

import com.mojang.logging.LogUtils;
import com.mrbysco.murderleaderboard.blockentity.TopPlayerBlockEntity;
import com.mrbysco.murderleaderboard.client.ClientHandler;
import com.mrbysco.murderleaderboard.client.KeybindHandler;
import com.mrbysco.murderleaderboard.command.LeaderboardCommands;
import com.mrbysco.murderleaderboard.config.MurderConfig;
import com.mrbysco.murderleaderboard.handler.KillHandler;
import com.mrbysco.murderleaderboard.handler.SyncHandler;
import com.mrbysco.murderleaderboard.network.PacketHandler;
import com.mrbysco.murderleaderboard.registry.MurderRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import org.slf4j.Logger;

@Mod(MurderLeaderboard.MOD_ID)
public class MurderLeaderboard {
	public static final String MOD_ID = "murderleaderboard";
	public static final Logger LOGGER = LogUtils.getLogger();

	public MurderLeaderboard() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MurderConfig.commonSpec);
		eventBus.register(MurderConfig.class);

		MurderRegistry.BLOCKS.register(eventBus);
		MurderRegistry.ITEMS.register(eventBus);
		MurderRegistry.BLOCK_ENTITIES.register(eventBus);

		eventBus.addListener(this::commonSetup);
		eventBus.addListener(this::addTabContents);
		NeoForge.EVENT_BUS.addListener(this::serverAboutToStart);

		NeoForge.EVENT_BUS.register(new KillHandler());
		NeoForge.EVENT_BUS.register(new SyncHandler());
		NeoForge.EVENT_BUS.addListener(this::onCommandRegister);

		if (FMLEnvironment.dist.isClient()) {
			eventBus.addListener(ClientHandler::doClientStuff);
			eventBus.addListener(ClientHandler::registerKeymapping);
			eventBus.addListener(ClientHandler::registerEntityRenders);
			eventBus.addListener(ClientHandler::registerLayerDefinitions);
			NeoForge.EVENT_BUS.addListener(KeybindHandler::onClientTick);
			NeoForge.EVENT_BUS.addListener(ClientHandler::onLogin);
			NeoForge.EVENT_BUS.addListener(ClientHandler::onRespawn);
		}
	}

	public void commonSetup(FMLCommonSetupEvent event) {
		PacketHandler.init();
	}

	private void addTabContents(final BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
			event.accept(MurderRegistry.TOP_PLAYER.get());
		}
	}

	public void onCommandRegister(RegisterCommandsEvent event) {
		LeaderboardCommands.initializeCommands(event.getDispatcher());
	}

	public void serverAboutToStart(final ServerAboutToStartEvent event) {
		MinecraftServer server = event.getServer();
		TopPlayerBlockEntity.setup(server.getProfileCache(), server.getSessionService(), server);
		GameProfileCache.setUsesAuthentication(server.usesAuthentication());
	}
}
