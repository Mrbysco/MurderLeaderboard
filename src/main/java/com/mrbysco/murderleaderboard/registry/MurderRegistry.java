package com.mrbysco.murderleaderboard.registry;

import com.mrbysco.murderleaderboard.MurderLeaderboard;
import com.mrbysco.murderleaderboard.block.TopPlayerBlock;
import com.mrbysco.murderleaderboard.blockentity.TopPlayerBlockEntity;
import com.mrbysco.murderleaderboard.item.TopPlayerBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MurderRegistry {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MurderLeaderboard.MOD_ID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MurderLeaderboard.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MurderLeaderboard.MOD_ID);

	public static final DeferredBlock<TopPlayerBlock> TOP_PLAYER = registerTopPlayer("top_player", () -> new TopPlayerBlock(
			BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(0.6F)), new Item.Properties());

	public static <B extends Block> DeferredBlock<B> registerTopPlayer(String name, Supplier<? extends B> supplier, Item.Properties properties) {
		DeferredBlock<B> block = MurderRegistry.BLOCKS.register(name, supplier);
		ITEMS.register(name, () -> new TopPlayerBlockItem(block.get(), properties));
		return block;
	}

	public static final Supplier<BlockEntityType<TopPlayerBlockEntity>> TOP_PLAYER_ENTITY = BLOCK_ENTITIES.register("top_player", () -> BlockEntityType.Builder.of(TopPlayerBlockEntity::new,
			MurderRegistry.TOP_PLAYER.get()).build(null));
}
