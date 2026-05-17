package com.mrbysco.murderleaderboard.network.message;

import com.mrbysco.murderleaderboard.MurderLeaderboard;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncKillsMessage(String user, String killer, CompoundTag killMapTag) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, SyncKillsMessage> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			SyncKillsMessage::user,
			ByteBufCodecs.STRING_UTF8,
			SyncKillsMessage::killer,
			ByteBufCodecs.COMPOUND_TAG,
			SyncKillsMessage::killMapTag,
			SyncKillsMessage::new);
	public static final Type<SyncKillsMessage> ID = new Type<>(MurderLeaderboard.modLoc("sync_kills"));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
