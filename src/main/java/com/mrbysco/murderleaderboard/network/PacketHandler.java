package com.mrbysco.murderleaderboard.network;

import com.mrbysco.murderleaderboard.MurderLeaderboard;
import com.mrbysco.murderleaderboard.network.message.ChooseRankMessage;
import com.mrbysco.murderleaderboard.network.message.SyncKillsMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(MurderLeaderboard.MOD_ID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);

	private static int id = 0;

	public static void init() {
		CHANNEL.registerMessage(id++, SyncKillsMessage.class, SyncKillsMessage::encode, SyncKillsMessage::decode, SyncKillsMessage::handle);
		CHANNEL.registerMessage(id++, ChooseRankMessage.class, ChooseRankMessage::encode, ChooseRankMessage::decode, ChooseRankMessage::handle);
	}
}
