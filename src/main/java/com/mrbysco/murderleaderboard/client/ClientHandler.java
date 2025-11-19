package com.mrbysco.murderleaderboard.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mrbysco.murderleaderboard.MurderLeaderboard;
import com.mrbysco.murderleaderboard.client.renderer.TopPlayerBER;
import com.mrbysco.murderleaderboard.client.renderer.TopPlayerSpecialRenderer;
import com.mrbysco.murderleaderboard.registry.MurderRegistry;
import com.mrbysco.murderleaderboard.toast.RankChangeToast;
import com.mrbysco.murderleaderboard.world.MurderData;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
	public static final List<MurderData.KillData> killList = new ArrayList<>();

	public static KeyMapping.Category CATEGORY = new KeyMapping.Category(MurderLeaderboard.modLoc("category"));
	public static final KeyMapping KEY_OPEN_LEADERBOARD = new KeyMapping(
			"key." + MurderLeaderboard.MOD_ID + ".open_leaderboard",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_KP_ADD,
			CATEGORY);

	public static void registerKeymapping(final RegisterKeyMappingsEvent event) {
		event.registerCategory(CATEGORY);
		event.register(KEY_OPEN_LEADERBOARD);
	}

	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(MurderRegistry.TOP_PLAYER_ENTITY.get(), TopPlayerBER::new);
	}

	public static void setToast(int newRank, String name, String killer, ItemStack skullStack) {
		RankChangeToast toast = new RankChangeToast(newRank, name, killer, skullStack);
		Minecraft.getInstance().getToastManager().addToast(toast);
	}

	public static void registerSpecialModelRenderers(RegisterSpecialModelRendererEvent event) {
		event.register(MurderLeaderboard.modLoc("top_player"), TopPlayerSpecialRenderer.Unbaked.MAP_CODEC);
	}
}
