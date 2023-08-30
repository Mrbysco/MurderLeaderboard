package com.mrbysco.murderleaderboard.client;

import com.mrbysco.murderleaderboard.client.screen.LeaderboardScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;

public class KeybindHandler {
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if (ClientHandler.KEY_OPEN_LEADERBOARD.consumeClick() && !(mc.screen instanceof LeaderboardScreen)) {
			mc.setScreen(new LeaderboardScreen());
		}
	}
}
