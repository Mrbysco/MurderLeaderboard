package com.mrbysco.murderleaderboard.handler;

import com.mrbysco.murderleaderboard.world.MurderData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class SyncHandler {
	@SubscribeEvent
	public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		Player player = event.getEntity();
		if (player instanceof ServerPlayer serverPlayer) {
			MurderData murderData = MurderData.get(serverPlayer.level().getServer().overworld());
			murderData.setDirty();

			MurderData.syncMap();
		}
	}
}
