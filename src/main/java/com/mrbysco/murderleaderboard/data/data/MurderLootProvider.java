package com.mrbysco.murderleaderboard.data.data;

import com.mrbysco.murderleaderboard.registry.MurderRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MurderLootProvider extends LootTableProvider {
	public MurderLootProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, Set.of(), List.of(
				new SubProviderEntry(MurderBlocks::new, LootContextParamSets.BLOCK)
		), lookupProvider);
	}

	private static class MurderBlocks extends BlockLootSubProvider {

		protected MurderBlocks(HolderLookup.Provider provider) {
			super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
		}

		@Override
		protected void generate() {
			this.add(MurderRegistry.TOP_PLAYER.get(), createNameableBlockEntityTable(MurderRegistry.TOP_PLAYER.get()));
		}

		@Override
		protected Iterable<Block> getKnownBlocks() {
			return (Iterable<Block>) MurderRegistry.BLOCKS.getEntries().stream().map(holder -> (Block) holder.get())::iterator;
		}
	}

}
