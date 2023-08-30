package com.mrbysco.murderleaderboard.data.assets;

import com.mrbysco.murderleaderboard.MurderLeaderboard;
import com.mrbysco.murderleaderboard.registry.MurderRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class MurderItemModels extends ItemModelProvider {
	public MurderItemModels(DataGenerator gen, ExistingFileHelper helper) {
		super(gen, MurderLeaderboard.MOD_ID, helper);
	}

	@Override
	protected void registerModels() {
		withBlockParent(MurderRegistry.TOP_PLAYER.getId());
	}

	private void withBlockParent(ResourceLocation location) {
		withExistingParent(location.getPath(), modLoc("block/" + location.getPath()));
	}
}
