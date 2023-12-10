package com.mrbysco.murderleaderboard.client;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.platform.InputConstants;
import com.mrbysco.murderleaderboard.MurderLeaderboard;
import com.mrbysco.murderleaderboard.blockentity.TopPlayerBlockEntity;
import com.mrbysco.murderleaderboard.client.model.TopPlayerTileModel;
import com.mrbysco.murderleaderboard.client.renderer.TopPlayerBER;
import com.mrbysco.murderleaderboard.registry.MurderRegistry;
import com.mrbysco.murderleaderboard.world.MurderData;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Services;
import net.minecraft.server.players.GameProfileCache;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
	public static final ModelLayerLocation TOP_PLAYER = new ModelLayerLocation(new ResourceLocation(MurderLeaderboard.MOD_ID, "top_player"), "main");
	public static final ModelLayerLocation TOP_PLAYER_SLIM = new ModelLayerLocation(new ResourceLocation(MurderLeaderboard.MOD_ID, "top_player_slim"), "main");
	public static List<MurderData.KillData> killList = new ArrayList<>();

	public static KeyMapping KEY_OPEN_LEADERBOARD = new KeyMapping(
			"key." + MurderLeaderboard.MOD_ID + ".open_leaderboard",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_KP_ADD,
			"category." + MurderLeaderboard.MOD_ID + ".main");

	public static void doClientStuff(final FMLClientSetupEvent event) {
		setPlayerCache(Minecraft.getInstance());
	}

	public static void registerKeymapping(final RegisterKeyMappingsEvent event) {
		event.register(KEY_OPEN_LEADERBOARD);
	}

	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(MurderRegistry.TOP_PLAYER_ENTITY.get(), TopPlayerBER::new);
	}

	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(TOP_PLAYER, () -> LayerDefinition.create(TopPlayerTileModel.createPlayerMesh(CubeDeformation.NONE, false), 64, 64));
		event.registerLayerDefinition(TOP_PLAYER_SLIM, () -> LayerDefinition.create(TopPlayerTileModel.createPlayerMesh(CubeDeformation.NONE, true), 64, 64));
	}


	public static void onLogin(ClientPlayerNetworkEvent.LoggingIn event) {
		Minecraft mc = Minecraft.getInstance();
		if (!mc.isLocalServer()) {
			setPlayerCache(mc);
		}
	}

	public static void onRespawn(ClientPlayerNetworkEvent.Clone event) {
		Minecraft mc = Minecraft.getInstance();
		if (!mc.isLocalServer()) {
			setPlayerCache(mc);
		}
	}

	private static void setPlayerCache(Minecraft mc) {
		YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(mc.getProxy());
		Services services = Services.create(authenticationService, mc.gameDirectory);
		services.profileCache().setExecutor(mc);
		TopPlayerBlockEntity.setup(services, mc);
		GameProfileCache.setUsesAuthentication(false);
	}
}
