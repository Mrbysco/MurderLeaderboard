package com.mrbysco.murderleaderboard.client;

import com.mrbysco.murderleaderboard.client.screen.LeaderboardScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class KeybindHandler {
	public static void onClientTick(ClientTickEvent.Pre event) {
		Minecraft mc = Minecraft.getInstance();
		if (ClientHandler.KEY_OPEN_LEADERBOARD.consumeClick() && !(mc.screen instanceof LeaderboardScreen)) {
			mc.setScreen(new LeaderboardScreen());
		}
	}
}
