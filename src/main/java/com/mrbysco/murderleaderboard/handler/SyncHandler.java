package com.mrbysco.murderleaderboard.handler;

import com.mrbysco.murderleaderboard.world.MurderData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SyncHandler {
	@SubscribeEvent
	public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		Player player = event.getEntity();
		if (!player.getLevel().isClientSide) {
			MurderData murderData = MurderData.get(player.getServer().getLevel(Level.OVERWORLD));
			murderData.setDirty();

			MurderData.syncMap();
		}
	}
}
