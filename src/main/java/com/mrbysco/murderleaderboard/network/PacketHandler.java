package com.mrbysco.murderleaderboard.network;

import com.mrbysco.murderleaderboard.MurderLeaderboard;
import com.mrbysco.murderleaderboard.network.handler.ClientPayloadHandler;
import com.mrbysco.murderleaderboard.network.handler.ServerPayloadHandler;
import com.mrbysco.murderleaderboard.network.message.ChooseRankPayload;
import com.mrbysco.murderleaderboard.network.message.SyncKillsMessage;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler {
	public static void setupPackets(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(MurderLeaderboard.MOD_ID);

		registrar.playToClient(SyncKillsMessage.ID, SyncKillsMessage.CODEC, ClientPayloadHandler.getInstance()::handleSyncData);
		registrar.playToServer(ChooseRankPayload.ID, ChooseRankPayload.CODEC, ServerPayloadHandler.getInstance()::handleRankData);
	}
}
