package com.mrbysco.murderleaderboard.network.handler;

import com.mrbysco.murderleaderboard.client.ClientHandler;
import com.mrbysco.murderleaderboard.network.message.SyncKillsMessage;
import com.mrbysco.murderleaderboard.toast.RankChangeToast;
import com.mrbysco.murderleaderboard.world.MurderData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ClientPayloadHandler {
	private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

	public static ClientPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleSyncData(final SyncKillsMessage syncData, final PlayPayloadContext context) {
		context.workHandler().submitAsync(() -> {
					context.player().ifPresent(player -> {
						String playerName = player.getGameProfile().getName().toLowerCase(Locale.ROOT);
						String user = syncData.user();
						String killer = syncData.killer();
						CompoundTag killMapTag = syncData.killMapTag();
						if (playerName.equals(user)) {
							Map<String, Integer> killMap = new HashMap<>();

							ListTag killListTag = killMapTag.getList("Kills", ListTag.TAG_COMPOUND);
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
					});
				})
				.exceptionally(e -> {
					// Handle exception
					context.packetHandler().disconnect(Component.translatable("murderleaderboard.networking.sync_kills.failed", e.getMessage()));
					return null;
				});
	}
}
