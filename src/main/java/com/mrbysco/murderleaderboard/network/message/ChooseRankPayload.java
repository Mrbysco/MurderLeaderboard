package com.mrbysco.murderleaderboard.network.message;

import com.mrbysco.murderleaderboard.MurderLeaderboard;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ChooseRankPayload(BlockPos pos, int rank) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(MurderLeaderboard.MOD_ID, "choose_rank");

	public ChooseRankPayload(final FriendlyByteBuf packetBuffer) {
		this(packetBuffer.readBlockPos(), packetBuffer.readInt());
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(rank);
	}
	@Override
	public ResourceLocation id() {
		return ID;
	}
}
