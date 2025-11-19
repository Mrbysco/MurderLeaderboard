package com.mrbysco.murderleaderboard.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.Block;

public class TopPlayerBlockItem extends BlockItem {

	public TopPlayerBlockItem(Block blockIn, Item.Properties builder) {
		super(blockIn, builder);
	}

	public Component getName(ItemStack stack) {
		ResolvableProfile resolvableprofile = (ResolvableProfile) stack.get(DataComponents.PROFILE);
		return (Component) (resolvableprofile != null && resolvableprofile.name().isPresent() ? Component.translatable(this.descriptionId + ".named", new Object[]{resolvableprofile.name().get()}) : super.getName(stack));
	}
}
