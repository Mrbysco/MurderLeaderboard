package com.mrbysco.murderleaderboard.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mrbysco.murderleaderboard.MurderLeaderboard;
import com.mrbysco.murderleaderboard.client.model.TopPlayerTileModel;
import com.mrbysco.murderleaderboard.client.renderer.TopPlayerBER;
import com.mrbysco.murderleaderboard.registry.MurderRegistry;
import com.mrbysco.murderleaderboard.toast.RankChangeToast;
import com.mrbysco.murderleaderboard.world.MurderData;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
	public static final ModelLayerLocation TOP_PLAYER = new ModelLayerLocation(MurderLeaderboard.modLoc("top_player"), "main");
	public static final ModelLayerLocation TOP_PLAYER_SLIM = new ModelLayerLocation(MurderLeaderboard.modLoc("top_player_slim"), "main");
	public static final List<MurderData.KillData> killList = new ArrayList<>();

	public static final KeyMapping KEY_OPEN_LEADERBOARD = new KeyMapping(
			"key." + MurderLeaderboard.MOD_ID + ".open_leaderboard",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_KP_ADD,
			"category." + MurderLeaderboard.MOD_ID + ".main");

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

	public static void setToast(int newRank, String name, String killer, ItemStack skullStack) {
		RankChangeToast toast = new RankChangeToast(newRank, name, killer, skullStack);
		Minecraft.getInstance().getToasts().addToast(toast);
	}
}
