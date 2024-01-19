package com.mrbysco.murderleaderboard.network.message;

import com.mrbysco.murderleaderboard.MurderLeaderboard;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncKillsMessage(String user, String killer, CompoundTag killMapTag) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(MurderLeaderboard.MOD_ID, "sync_kills");

	public SyncKillsMessage(final FriendlyByteBuf buffer) {
		this(buffer.readUtf(), buffer.readUtf(), buffer.readNbt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(user);
		buffer.writeUtf(killer);
		buffer.writeNbt(killMapTag);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}
}
