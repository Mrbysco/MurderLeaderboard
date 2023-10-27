package com.mrbysco.murderleaderboard.network.message;

import com.mrbysco.murderleaderboard.blockentity.TopPlayerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ChooseRankMessage {
	private final BlockPos pos;
	private final int rank;

	private ChooseRankMessage(FriendlyByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.rank = buf.readInt();
	}

	public ChooseRankMessage(BlockPos pos, int rank) {
		this.pos = pos;
		this.rank = rank;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(rank);
	}

	public static ChooseRankMessage decode(final FriendlyByteBuf packetBuffer) {
		return new ChooseRankMessage(packetBuffer.readBlockPos(), packetBuffer.readInt());
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isServer()) {
				final ServerLevel serverLevel = ctx.getSender().serverLevel();
				if (serverLevel.getBlockEntity(this.pos) instanceof TopPlayerBlockEntity topPlayerBlockEntity) {
					topPlayerBlockEntity.setRank(rank);
					topPlayerBlockEntity.updateTierProfile();
					topPlayerBlockEntity.setChanged();
				}
			}
		});
		ctx.setPacketHandled(true);
	}
}
