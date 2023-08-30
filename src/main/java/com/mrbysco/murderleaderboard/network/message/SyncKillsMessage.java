package com.mrbysco.murderleaderboard.network.message;

import com.mrbysco.murderleaderboard.MurderLeaderboard;
import com.mrbysco.murderleaderboard.client.ClientHandler;
import com.mrbysco.murderleaderboard.toast.RankChangeToast;
import com.mrbysco.murderleaderboard.world.MurderData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.network.NetworkEvent.Context;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class SyncKillsMessage {

	private final String user, killer;
	private final CompoundTag killMapTag;

	public SyncKillsMessage(String user, String killer, CompoundTag tag) {
		this.user = user;
		this.killer = killer == null ? "" : killer;
		this.killMapTag = tag;
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(user);
		buffer.writeUtf(killer);
		buffer.writeNbt(killMapTag);
	}

	public static SyncKillsMessage decode(final FriendlyByteBuf buffer) {
		return new SyncKillsMessage(buffer.readUtf(), buffer.readUtf(), buffer.readNbt());
	}

	public void handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isClient()) {
				UpdateList.update(this.user, this.killer, this.killMapTag).run();
			}
		});
		ctx.setPacketHandled(true);
	}

	private static class UpdateList {
		private static SafeRunnable update(String user, String killer, CompoundTag killTag) {
			return new SafeRunnable() {
				@Serial
				private static final long serialVersionUID = 1L;

				@Override
				public void run() {
					Player player = Minecraft.getInstance().player;

					if (player != null) {
						String playerName = player.getGameProfile().getName().toLowerCase(Locale.ROOT);
						if (playerName.equals(user)) {
							Map<String, Integer> killMap = new HashMap<>();

							ListTag killListTag = killTag.getList("Kills", ListTag.TAG_COMPOUND);
							for (int j = 0; j < killListTag.size(); ++j) {
								CompoundTag killTag = killListTag.getCompound(j);

								String name = killTag.getString("Name");
								int kills = killTag.getInt("Kills");

								killMap.put(name, kills);
							}

							List<MurderData.KillData> killCache = new ArrayList<>(ClientHandler.killList);
							ClientHandler.killList.clear();
							if (!killMap.isEmpty()) {
								List<MurderData.KillData> killList = killMap.entrySet().stream().map(entry -> new MurderData.KillData(entry.getKey(), entry.getValue())).toList();
								ClientHandler.killList.addAll(killList);
								ClientHandler.killList.sort(Comparator.comparingInt(MurderData.KillData::kills).reversed());


								if (killer != null && !killer.isEmpty()) {
									//Check old rank
									int oldRank = -1;
									for (MurderData.KillData data : killCache) {
										if (data.name().equals(killer)) {
											oldRank = killCache.indexOf(data);
											break;
										}
									}
									int newRank = -1;
									//Get new rank
									for (MurderData.KillData data : ClientHandler.killList) {
										if (data.name().equals(killer)) {
											newRank = ClientHandler.killList.indexOf(data);
											break;
										}
									}
//									MurderLeaderboard.LOGGER.info("Old Rank: " + oldRank + " New Rank: " + newRank);
									//Check if rank changed and if so send a message
									if (oldRank != -1 && newRank != -1 && oldRank != newRank && newRank < 10) {
										ItemStack skullStack = Items.PLAYER_HEAD.getDefaultInstance();
										skullStack.getOrCreateTag().putString("SkullOwner", killer);
										RankChangeToast toast = new RankChangeToast(newRank, killCache.get(newRank).name(), killer, skullStack);
										Minecraft.getInstance().getToasts().addToast(toast);
									}
								}
							}
						}
					}
				}
			};
		}
	}
}
