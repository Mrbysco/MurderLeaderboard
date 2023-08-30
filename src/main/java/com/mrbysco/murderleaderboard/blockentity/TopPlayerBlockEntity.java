package com.mrbysco.murderleaderboard.blockentity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mrbysco.murderleaderboard.MurderLeaderboard;
import com.mrbysco.murderleaderboard.registry.MurderRegistry;
import com.mrbysco.murderleaderboard.world.MurderData;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.Services;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.StringUtil;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class TopPlayerBlockEntity extends BlockEntity implements Nameable {
	@Nullable
	private static GameProfileCache profileCache;
	@Nullable
	private static MinecraftSessionService sessionService;
	@Nullable
	private static Executor mainThreadExecutor;

	private String owner;
	private GameProfile playerProfile;
	private boolean isSlim = false;
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

	public boolean isSlim() {
		return this.isSlim;
	}

	public void setOwnerProfile(@NotNull String owner) {
		this.owner = owner;
	}

	public void setPlayerProfile(@Nullable GameProfile profile) {
		synchronized (this) {
			this.playerProfile = profile;
			if (this.level != null && this.level.isClientSide && this.playerProfile != null && this.playerProfile.isComplete()) {
				Minecraft.getInstance().getSkinManager().registerSkins(this.playerProfile, (textureType, textureLocation, profileTexture) -> {
					if (textureType.equals(MinecraftProfileTexture.Type.SKIN)) {
						String metadata = profileTexture.getMetadata("model");
						this.isSlim = metadata != null && metadata.equals("slim");
					}
				}, true);
			}
		}

		this.updateOwnerProfile();
	}

	private void updateOwnerProfile() {
		updateGameprofile(this.playerProfile, (profile) -> {
			this.playerProfile = profile;
			refreshClient();
		});
	}

	private void refreshClient() {
		this.setChanged();
		BlockState state = level.getBlockState(worldPosition);
		level.sendBlockUpdated(worldPosition, state, state, 2);
	}

	@Nullable
	public static void updateGameprofile(@Nullable GameProfile profile, Consumer<GameProfile> profileConsumer) {
		if (profile != null && !StringUtil.isNullOrEmpty(profile.getName()) && (!profile.isComplete() || !profile.getProperties().containsKey("textures")) && profileCache != null && sessionService != null) {
			profileCache.getAsync(profile.getName(), (gameProfile) -> {
				Util.backgroundExecutor().execute(() -> {
					Util.ifElse(gameProfile, (gameProfile1) -> {
						Property property = Iterables.getFirst(gameProfile1.getProperties().get("textures"), (Property) null);
						if (property == null) {
							gameProfile1 = sessionService.fillProfileProperties(gameProfile1, true);
						}

						GameProfile gameprofile = gameProfile1;
						mainThreadExecutor.execute(() -> {
							profileCache.add(gameprofile);
							profileConsumer.accept(gameprofile);
						});
					}, () -> {
						mainThreadExecutor.execute(() -> {
							profileConsumer.accept(profile);
						});
					});
				});
			});
		} else {
			profileConsumer.accept(profile);
		}
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
}
