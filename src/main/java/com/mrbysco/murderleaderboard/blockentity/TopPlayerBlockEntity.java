package com.mrbysco.murderleaderboard.blockentity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mrbysco.murderleaderboard.registry.MurderRegistry;
import com.mrbysco.murderleaderboard.world.MurderData;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.Services;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TopPlayerBlockEntity extends BlockEntity implements Nameable {
	@Nullable
	private static GameProfileCache profileCache;
	@Nullable
	private static MinecraftSessionService sessionService;
	@Nullable
	private static Executor mainThreadExecutor;
	private static final Executor CHECKED_MAIN_THREAD_EXECUTOR = runnable -> {
		Executor executor = mainThreadExecutor;
		if (executor != null) {
			executor.execute(runnable);
		}
	};

	private String owner;
	private GameProfile playerProfile;
	private int rank = 1;

	public TopPlayerBlockEntity(BlockPos pos, BlockState state) {
		super(MurderRegistry.TOP_PLAYER_ENTITY.get(), pos, state);
	}

	public static void setup(GameProfileCache gameProfileCache, MinecraftSessionService service, Executor executor) {
		profileCache = gameProfileCache;
		sessionService = service;
		mainThreadExecutor = executor;
	}

	public static void setup(Services services, Executor executor) {
		setup(services.profileCache(), services.sessionService(), executor);
	}

	public static void clear() {
		profileCache = null;
		sessionService = null;
		mainThreadExecutor = null;
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);

		this.setOwnerProfile(compound.getString("Owner"));
		if (compound.contains("PlayerProfile", 10)) {
			this.playerProfile = NbtUtils.readGameProfile(compound.getCompound("PlayerProfile"));
		}
		this.setRank(compound.getInt("Rank"));
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.putString("Owner", this.owner == null ? "" : this.owner);
		if (this.playerProfile != null) {
			CompoundTag tag = new CompoundTag();
			NbtUtils.writeGameProfile(tag, this.playerProfile);
			compound.put("PlayerProfile", tag);
		}
		compound.putInt("Rank", this.rank);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
		load(packet.getTag());
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		saveAdditional(tag);
		return tag;
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		super.handleUpdateTag(tag);
	}

	@Override
	public CompoundTag getPersistentData() {
		CompoundTag nbt = new CompoundTag();
		this.saveAdditional(nbt);
		return nbt;
	}

	@Override
	public boolean hasCustomName() {
		return this.playerProfile != null && !this.playerProfile.getName().isEmpty();
	}

	@Nullable
	public GameProfile getPlayerProfile() {
		return this.playerProfile;
	}

	public void setOwnerProfile(@NotNull String owner) {
		this.owner = owner;
	}

	public void setPlayerProfile(@Nullable GameProfile profile) {
		this.playerProfile = profile;
		this.updateOwnerProfile();
	}

	private void updateOwnerProfile() {
		if (this.playerProfile != null && !Util.isBlank(this.playerProfile.getName()) && !hasTextures(this.playerProfile)) {
			fetchGameProfile(this.playerProfile.getName()).thenAcceptAsync(profile -> {
				this.playerProfile = profile.orElse(this.playerProfile);
				this.setChanged();
			}, CHECKED_MAIN_THREAD_EXECUTOR);
		} else {
			this.setChanged();
		}
	}

	private void refreshClient() {
		this.setChanged();
		BlockState state = level.getBlockState(worldPosition);
		level.sendBlockUpdated(worldPosition, state, state, 2);
	}

	@Override
	public Component getName() {
		return this.hasCustomName() ? Component.literal(this.playerProfile != null ? playerProfile.getName() : "") : Component.translatable("murderleaderboard.blockentity.top_player");
	}

	@Nullable
	@Override
	public Component getCustomName() {
		return null;
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, TopPlayerBlockEntity blockEntity) {
		if (level != null && level.getGameTime() % 100 == 0) {
			blockEntity.updateTierProfile();
		}
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwner() {
		return owner;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getRank() {
		return rank;
	}

	public void updateTierProfile() {
		if (this.level != null && !this.level.isClientSide && this.owner != null) {
			MurderData data = MurderData.get(level);
			List<MurderData.KillData> killList = data.getKillers(owner);
			GameProfile defaultProfile = new GameProfile((UUID) null, "steve");
			if (killList.isEmpty()) {
				this.setPlayerProfile(defaultProfile);
			} else {
				if (killList.size() < getRank()) {
					this.setPlayerProfile(defaultProfile);
				} else {
					String killer = killList.get((getRank() - 1)).name().toLowerCase(Locale.ROOT);
					this.setPlayerProfile(new GameProfile((UUID) null, killer));
				}
			}
		}
	}

	@Nullable
	public static GameProfile getOrResolveGameProfile(CompoundTag tag) {
		if (tag.contains("SkullOwner", 10)) {
			return NbtUtils.readGameProfile(tag.getCompound("SkullOwner"));
		} else {
			if (tag.contains("SkullOwner", 8)) {
				String s = tag.getString("SkullOwner");
				if (!Util.isBlank(s)) {
					tag.remove("SkullOwner");
					resolveGameProfile(tag, s);
				}
			}

			return null;
		}
	}

	public static void resolveGameProfile(CompoundTag tag) {
		String s = tag.getString("SkullOwner");
		if (!Util.isBlank(s)) {
			resolveGameProfile(tag, s);
		}
	}

	public static void resolveGameProfile(CompoundTag compoundTag, String username) {
		fetchGameProfile(username)
				.thenAccept(
						profile -> compoundTag.put("SkullOwner",
								NbtUtils.writeGameProfile(new CompoundTag(), profile.orElse(new GameProfile(Util.NIL_UUID, username))))
				);
	}

	public static CompletableFuture<Optional<GameProfile>> fetchGameProfile(String username) {
		GameProfileCache gameprofilecache = profileCache;
		return gameprofilecache == null
				? CompletableFuture.completedFuture(Optional.empty())
				: gameprofilecache.getAsync(username)
				.thenCompose(profile -> profile.isPresent() ? fillProfileTextures(profile.get()) : CompletableFuture.completedFuture(Optional.empty()))
				.thenApplyAsync((profile -> {
					GameProfileCache cache = profileCache;
					if (cache != null) {
						profile.ifPresent(cache::add);
						return profile;
					} else {
						return Optional.empty();
					}
				}), CHECKED_MAIN_THREAD_EXECUTOR);
	}

	private static CompletableFuture<Optional<GameProfile>> fillProfileTextures(GameProfile profile) {
		return hasTextures(profile) ? CompletableFuture.completedFuture(Optional.of(profile)) : CompletableFuture.supplyAsync(() -> {
			MinecraftSessionService minecraftsessionservice = sessionService;
			if (minecraftsessionservice != null) {
				ProfileResult profileresult = minecraftsessionservice.fetchProfile(profile.getId(), true);
				return profileresult == null ? Optional.of(profile) : Optional.of(profileresult.profile());
			} else {
				return Optional.empty();
			}
		}, Util.backgroundExecutor());
	}

	private static boolean hasTextures(GameProfile profile) {
		return profile.getProperties().containsKey("textures");
	}
}
