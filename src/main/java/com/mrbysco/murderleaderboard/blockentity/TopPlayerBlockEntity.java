package com.mrbysco.murderleaderboard.blockentity;

import com.mojang.authlib.properties.PropertyMap;
import com.mrbysco.murderleaderboard.registry.MurderRegistry;
import com.mrbysco.murderleaderboard.world.MurderData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class TopPlayerBlockEntity extends BlockEntity implements Nameable {

	@Nullable
	private ResolvableProfile killer;
	@Nullable
	private Component customName;
	private String owner;
	private int rank = 1;

	public TopPlayerBlockEntity(BlockPos pos, BlockState state) {
		super(MurderRegistry.TOP_PLAYER_ENTITY.get(), pos, state);
	}

	@Override
	public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.loadAdditional(compound, provider);

		if (compound.contains("Owner")) {
			this.owner = compound.getStringOr("Owner", "");
		}

		this.setKiller(compound.read("profile", ResolvableProfile.CODEC).orElse(null));

		this.customName = parseCustomNameSafe(compound.get("custom_name"), provider);

		this.setRank(compound.getIntOr("Rank", 0));
	}

	@Override
	public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.saveAdditional(compound, provider);

		if (this.owner != null) {
			compound.putString("Owner", this.owner);
		}

		compound.storeNullable("profile", ResolvableProfile.CODEC, this.killer);
		compound.storeNullable("custom_name", ComponentSerialization.CODEC, provider.createSerializationContext(NbtOps.INSTANCE), this.customName);

		compound.putInt("Rank", this.rank);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider provider) {
		loadAdditional(packet.getTag(), provider);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
		return this.saveCustomOnly(provider);
	}

	@Override
	public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
		super.handleUpdateTag(tag, provider);
	}

	@Override
	public CompoundTag getPersistentData() {
		CompoundTag nbt = new CompoundTag();
		this.saveAdditional(nbt, level != null ? level.registryAccess() : VanillaRegistries.createLookup());
		return nbt;
	}

	@Override
	public boolean hasCustomName() {
		return this.killer != null && !this.killer.name().orElse("").isEmpty();
	}

	@Nullable
	public ResolvableProfile getKiller() {
		return this.killer;
	}


	public void setKiller(@Nullable ResolvableProfile killer) {
		synchronized (this) {
			this.killer = killer;
		}

		this.updateOwnerProfile();
	}

	private void updateOwnerProfile() {
		if (this.killer != null && !this.killer.isResolved()) {
			this.killer.resolve().thenAcceptAsync(profile -> {
				this.killer = profile;
				this.setChanged();
			}, SkullBlockEntity.CHECKED_MAIN_THREAD_EXECUTOR);
		} else {
			this.setChanged();
		}
	}

	public void refreshClient() {
		this.setChanged();
		BlockState state = level.getBlockState(worldPosition);
		level.sendBlockUpdated(worldPosition, state, state, 2);
	}

	@Override
	public Component getName() {
		return this.hasCustomName() ? Component.literal(this.killer != null ? killer.name().orElse("unknown") : "") : Component.translatable("murderleaderboard.blockentity.top_player");
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

	public void setOwnerName(String name) {
		this.owner = name;
	}

	public String getOwnerName() {
		if (owner == null) {
			return "";
		}
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
			ResolvableProfile currentProfile = getKiller();
			MurderData data = MurderData.get(level);
			ResolvableProfile defaultProfile = new ResolvableProfile(Optional.of("steve"), Optional.empty(), new PropertyMap());
			List<MurderData.KillData> killList = data.getKillers(getOwnerName());
			if (killList.isEmpty()) {
				if (currentProfile == null || !currentProfile.name().get().equalsIgnoreCase(defaultProfile.name().get()))
					this.setKiller(defaultProfile);
			} else {
				if (killList.size() < getRank()) {
					if (currentProfile == null || !currentProfile.name().get().equalsIgnoreCase(defaultProfile.name().get()))
						this.setKiller(defaultProfile);
				} else {
					String killer = killList.get((getRank() - 1)).name().toLowerCase(Locale.ROOT);
					if (currentProfile == null || !currentProfile.name().get().equalsIgnoreCase(killer))
						this.setKiller(new ResolvableProfile(Optional.of(killer), Optional.empty(), new PropertyMap()));
				}
			}
		}
	}

	@Override
	protected void applyImplicitComponents(DataComponentGetter componentGetter) {
		super.applyImplicitComponents(componentGetter);
		this.setKiller(componentGetter.get(DataComponents.PROFILE));
		this.customName = componentGetter.get(DataComponents.CUSTOM_NAME);
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder pComponents) {
		super.collectImplicitComponents(pComponents);
		pComponents.set(DataComponents.PROFILE, this.killer);
		pComponents.set(DataComponents.CUSTOM_NAME, this.customName);
	}
}
