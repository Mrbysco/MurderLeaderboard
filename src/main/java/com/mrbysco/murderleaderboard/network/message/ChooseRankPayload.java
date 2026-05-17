package com.mrbysco.murderleaderboard.network.message;

import com.mrbysco.murderleaderboard.MurderLeaderboard;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ChooseRankPayload(BlockPos pos, int rank) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, ChooseRankPayload> CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC,
			ChooseRankPayload::pos,
			ByteBufCodecs.INT,
			ChooseRankPayload::rank,
			ChooseRankPayload::new);
	public static final Type<ChooseRankPayload> ID = new Type<>(MurderLeaderboard.modLoc("choose_rank"));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
