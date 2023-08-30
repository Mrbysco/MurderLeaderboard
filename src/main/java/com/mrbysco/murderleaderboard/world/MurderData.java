package com.mrbysco.murderleaderboard.world;

import com.mrbysco.murderleaderboard.MurderLeaderboard;
import com.mrbysco.murderleaderboard.network.PacketHandler;
import com.mrbysco.murderleaderboard.network.message.SyncKillsMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MurderData extends SavedData {
	public static Map<String, Map<String, Integer>> userKillMap = new HashMap<>();

	private static final String DATA_NAME = MurderLeaderboard.MOD_ID + "_data";

	public MurderData(Map<String, Map<String, Integer>> map) {
		MurderData.userKillMap.clear();
		MurderData.userKillMap.putAll(map);
	}

	public MurderData() {
		this(new HashMap<>());
	}

	public void addKill(String user, String killer) {
		Map<String, Integer> killMap = MurderData.userKillMap.getOrDefault(user.toLowerCase(Locale.ROOT), new HashMap<>());

		int killCount = killMap.getOrDefault(killer, 0);
		killCount += 1;
		killMap.put(killer, killCount);
		MurderData.userKillMap.put(user.toLowerCase(Locale.ROOT), killMap);

		MurderData.syncMap(killer);

		setDirty();
	}

	public void setKill(String user, String killer, int count) {
		Map<String, Integer> killMap = MurderData.userKillMap.getOrDefault(user.toLowerCase(Locale.ROOT), new HashMap<>());

		killMap.put(killer, count);
		MurderData.userKillMap.put(user.toLowerCase(Locale.ROOT), killMap);

		MurderData.syncMap(killer);

		setDirty();
	}

	public void removeKiller(String user, String killer) {
		Map<String, Integer> killMap = MurderData.userKillMap.getOrDefault(user.toLowerCase(Locale.ROOT), new HashMap<>());

		killMap.remove(killer);
		MurderData.userKillMap.put(user.toLowerCase(Locale.ROOT), killMap);

		MurderData.syncMap();

		setDirty();
	}

	public void clearKillers(String user) {
		MurderData.userKillMap.put(user.toLowerCase(Locale.ROOT), new HashMap<>());

		MurderData.syncMap();

		setDirty();
	}

	public List<KillData> getKillers(String user) {
		List<MurderData.KillData> killList = new ArrayList<>();
		Map<String, Integer> killMap = MurderData.userKillMap.getOrDefault(user.toLowerCase(Locale.ROOT), new HashMap<>());
		if (!killMap.isEmpty()) {
			killList.addAll(killMap.entrySet().stream().map(entry -> new MurderData.KillData(entry.getKey(), entry.getValue())).toList());
			killList.sort(Comparator.comparingInt(MurderData.KillData::kills).reversed());
		}
		return killList;
	}

	public static void syncMap() {
		MurderData.syncMap("");
	}

	public static void syncMap(String killer) {
		Set<String> users = userKillMap.keySet();
		for (String user : users) {
			CompoundTag saveTag = saveMap(new CompoundTag(), MurderData.userKillMap.getOrDefault(user, new HashMap<>()));
			PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SyncKillsMessage(user, killer, saveTag));
		}
	}

	public static MurderData load(CompoundTag tag) {
		syncMap();

		ListTag userKillMapTag = tag.getList("KillMap", CompoundTag.TAG_COMPOUND);
		Map<String, Map<String, Integer>> userKillMap = new HashMap<>();

		for (int i = 0; i < userKillMapTag.size(); ++i) {
			CompoundTag userKillTag = userKillMapTag.getCompound(i);
			String user = userKillTag.getString("Username");

			Map<String, Integer> killMap = new HashMap<>();
			ListTag killListTag = userKillTag.getList("Kills", ListTag.TAG_COMPOUND);
			for (int j = 0; j < killListTag.size(); ++j) {
				CompoundTag killTag = killListTag.getCompound(j);

				String name = killTag.getString("Name");
				int kills = killTag.getInt("Kills");

				killMap.put(name, kills);
			}

			userKillMap.put(user, killMap);
		}

		return new MurderData(userKillMap);
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		ListTag userKillMapTag = new ListTag();
		for (Map.Entry<String, Map<String, Integer>> entry : userKillMap.entrySet()) {
			CompoundTag userKillTag = new CompoundTag();
			userKillTag.putString("Username", entry.getKey());

			saveMap(userKillTag, entry.getValue());

			userKillMapTag.add(userKillTag);
		}
		tag.put("KillMap", userKillMapTag);

		return tag;
	}

	private static CompoundTag saveMap(CompoundTag tag, Map<String, Integer> map) {
		ListTag killListTag = new ListTag();
		for (Map.Entry<String, Integer> killEntry : map.entrySet()) {
			CompoundTag killTag = new CompoundTag();
			killTag.putString("Name", killEntry.getKey());
			killTag.putInt("Kills", killEntry.getValue());

			killListTag.add(killTag);
		}
		tag.put("Kills", killListTag);

		return tag;
	}

	public static MurderData get(Level level) {
		if (!(level instanceof ServerLevel)) {
			throw new RuntimeException("Attempted to get the data from a client level. This is wrong.");
		}
		ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);

		DimensionDataStorage storage = overworld.getDataStorage();
		return storage.computeIfAbsent(MurderData::load, MurderData::new, DATA_NAME);
	}

	public static final class KillData {
		private final String name;
		private final int kills;

		private ItemStack skull = ItemStack.EMPTY;

		public KillData(String name, int kills) {
			this.name = name;
			this.kills = kills;
		}

		public ItemStack getSkull() {
			if (skull == ItemStack.EMPTY) {
				ItemStack skullStack = Items.PLAYER_HEAD.getDefaultInstance();
				skullStack.getOrCreateTag().putString("SkullOwner", name);
				this.skull = skullStack;
			}
			return skull;
		}

		public String name() {
			return name;
		}

		public int kills() {
			return kills;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) return true;
			if (obj == null || obj.getClass() != this.getClass()) return false;
			var that = (KillData) obj;
			return Objects.equals(this.name, that.name) &&
					this.kills == that.kills;
		}

		@Override
		public int hashCode() {
			return Objects.hash(name, kills);
		}

		@Override
		public String toString() {
			return "KillData[" +
					"name=" + name + ", " +
					"kills=" + kills + ']';
		}

	}
}
