package com.mrbysco.murderleaderboard.registry;

import com.mrbysco.murderleaderboard.MurderLeaderboard;
import com.mrbysco.murderleaderboard.block.TopPlayerBlock;
import com.mrbysco.murderleaderboard.blockentity.TopPlayerBlockEntity;
import com.mrbysco.murderleaderboard.item.TopPlayerBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class MurderRegistry {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MurderLeaderboard.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MurderLeaderboard.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MurderLeaderboard.MOD_ID);

	public static final RegistryObject<Block> TOP_PLAYER = registerTopPlayer("top_player", () -> new TopPlayerBlock(
			BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(0.6F)), new Item.Properties());

	public static <B extends Block> RegistryObject<B> registerTopPlayer(String name, Supplier<? extends B> supplier, Item.Properties properties) {
		RegistryObject<B> block = MurderRegistry.BLOCKS.register(name, supplier);
		ITEMS.register(name, () -> new TopPlayerBlockItem(block.get(), properties));
		return block;
	}

	public static final RegistryObject<BlockEntityType<TopPlayerBlockEntity>> TOP_PLAYER_ENTITY = BLOCK_ENTITIES.register("top_player", () -> BlockEntityType.Builder.of(TopPlayerBlockEntity::new,
			MurderRegistry.TOP_PLAYER.get()).build(null));
}
