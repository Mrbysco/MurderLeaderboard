package com.mrbysco.murderleaderboard.item;

import com.mrbysco.murderleaderboard.client.renderer.TopPlayerBEWLR;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class TopPlayerBlockItem extends BlockItem {

	public TopPlayerBlockItem(Block blockIn, Item.Properties builder) {
		super(blockIn, builder);
	}

	@Override
	public Component getName(ItemStack pStack) {
		ResolvableProfile resolvableprofile = pStack.get(DataComponents.PROFILE);
		return (Component) (resolvableprofile != null && resolvableprofile.name().isPresent()
				? Component.translatable(this.getDescriptionId() + ".named", resolvableprofile.name().get())
				: super.getName(pStack));
	}

	@Override
	public void verifyComponentsAfterLoad(ItemStack pStack) {
		ResolvableProfile resolvableprofile = pStack.get(DataComponents.PROFILE);
		if (resolvableprofile != null && !resolvableprofile.isResolved()) {
			resolvableprofile.resolve()
					.thenAcceptAsync(p_332155_ -> pStack.set(DataComponents.PROFILE, p_332155_), SkullBlockEntity.CHECKED_MAIN_THREAD_EXECUTOR);
		}
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return new TopPlayerBEWLR(new BlockEntityRendererProvider.Context(
						Minecraft.getInstance().getBlockEntityRenderDispatcher(),
						Minecraft.getInstance().getBlockRenderer(),
						Minecraft.getInstance().getItemRenderer(),
						Minecraft.getInstance().getEntityRenderDispatcher(),
						Minecraft.getInstance().getEntityModels(),
						Minecraft.getInstance().font
				));
			}
		});
	}
}
