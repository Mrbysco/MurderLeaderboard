package com.mrbysco.murderleaderboard.network.handler;

import com.mrbysco.murderleaderboard.blockentity.TopPlayerBlockEntity;
import com.mrbysco.murderleaderboard.network.message.ChooseRankPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ServerPayloadHandler {
	public static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

	public static ServerPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleRankData(final ChooseRankPayload data, final PlayPayloadContext context) {
		// Do something with the data, on the main thread
		context.workHandler().submitAsync(() -> {
					context.player().ifPresent(player -> {
						if(player instanceof ServerPlayer serverPlayer) {
							final ServerLevel serverLevel = serverPlayer.serverLevel();
							if (serverLevel.getBlockEntity(data.pos()) instanceof TopPlayerBlockEntity topPlayerBlockEntity) {
								topPlayerBlockEntity.setRank(data.rank());
								topPlayerBlockEntity.updateTierProfile();
								topPlayerBlockEntity.setChanged();
							}
						}
					});
				})
				.exceptionally(e -> {
					// Handle exception
					context.packetHandler().disconnect(Component.translatable("murderleaderboard.networking.choose_rank.failed", e.getMessage()));
					return null;
				});
	}
}
