package com.mrbysco.murderleaderboard.network;

import com.mrbysco.murderleaderboard.MurderLeaderboard;
import com.mrbysco.murderleaderboard.network.handler.ClientPayloadHandler;
import com.mrbysco.murderleaderboard.network.handler.ServerPayloadHandler;
import com.mrbysco.murderleaderboard.network.message.ChooseRankPayload;
import com.mrbysco.murderleaderboard.network.message.SyncKillsMessage;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class PacketHandler {
	public static void setupPackets(final RegisterPayloadHandlerEvent event) {
		final IPayloadRegistrar registrar = event.registrar(MurderLeaderboard.MOD_ID);

		registrar.play(SyncKillsMessage.ID, SyncKillsMessage::new, handler -> handler
				.client(ClientPayloadHandler.getInstance()::handleSyncData));
		registrar.play(ChooseRankPayload.ID, ChooseRankPayload::new, handler -> handler
				.server(ServerPayloadHandler.getInstance()::handleRankData));
	}
}
