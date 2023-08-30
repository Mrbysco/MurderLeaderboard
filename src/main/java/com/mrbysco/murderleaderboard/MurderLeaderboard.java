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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
		MinecraftForge.EVENT_BUS.addListener(this::serverAboutToStart);

		MinecraftForge.EVENT_BUS.register(new KillHandler());
		MinecraftForge.EVENT_BUS.register(new SyncHandler());
		MinecraftForge.EVENT_BUS.addListener(this::onCommandRegister);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			eventBus.addListener(ClientHandler::doClientStuff);
			eventBus.addListener(ClientHandler::registerKeymapping);
			eventBus.addListener(ClientHandler::registerEntityRenders);
			eventBus.addListener(ClientHandler::registerLayerDefinitions);
			MinecraftForge.EVENT_BUS.addListener(KeybindHandler::onClientTick);
			MinecraftForge.EVENT_BUS.addListener(ClientHandler::onLogin);
			MinecraftForge.EVENT_BUS.addListener(ClientHandler::onRespawn);
		});
	}

	public void commonSetup(FMLCommonSetupEvent event) {
		PacketHandler.init();
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
