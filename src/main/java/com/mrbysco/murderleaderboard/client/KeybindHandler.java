package com.mrbysco.murderleaderboard.client;

import com.mrbysco.murderleaderboard.client.screen.LeaderboardScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(Dist.CLIENT)
public class KeybindHandler {
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Pre event) {
		Minecraft mc = Minecraft.getInstance();
		if (ClientHandler.KEY_OPEN_LEADERBOARD.consumeClick() && !(mc.screen instanceof LeaderboardScreen)) {
			mc.setScreen(new LeaderboardScreen());
		}
	}
}
