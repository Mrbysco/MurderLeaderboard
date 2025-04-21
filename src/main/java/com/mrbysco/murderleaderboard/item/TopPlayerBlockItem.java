package com.mrbysco.murderleaderboard.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

public class TopPlayerBlockItem extends BlockItem {

	public TopPlayerBlockItem(Block blockIn, Item.Properties builder) {
		super(blockIn, builder);
	}

	@Override
	public void verifyComponentsAfterLoad(ItemStack stack) {
		ResolvableProfile resolvableprofile = stack.get(DataComponents.PROFILE);
		if (resolvableprofile != null && !resolvableprofile.isResolved()) {
			resolvableprofile.resolve()
					.thenAcceptAsync(profile -> stack.set(DataComponents.PROFILE, profile), SkullBlockEntity.CHECKED_MAIN_THREAD_EXECUTOR);
		}
	}
}
