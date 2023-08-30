package com.mrbysco.murderleaderboard.item;

import com.mrbysco.murderleaderboard.client.renderer.TopPlayerBEWLR;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;

import java.util.function.Consumer;

public class TopPlayerBlockItem extends BlockItem {

	public TopPlayerBlockItem(Block blockIn, Item.Properties builder) {
		super(blockIn, builder.tab(CreativeModeTab.TAB_MISC));
	}

	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		consumer.accept(new IItemRenderProperties() {
			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
				return new TopPlayerBEWLR(new BlockEntityRendererProvider.Context(
						Minecraft.getInstance().getBlockEntityRenderDispatcher(),
						Minecraft.getInstance().getBlockRenderer(),
						Minecraft.getInstance().getEntityModels(),
						Minecraft.getInstance().font
				));
			}
		});
	}
}
