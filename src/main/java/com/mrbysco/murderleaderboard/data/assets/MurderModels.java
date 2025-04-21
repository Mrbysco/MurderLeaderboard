package com.mrbysco.murderleaderboard.data.assets;

import com.mrbysco.murderleaderboard.MurderLeaderboard;
import com.mrbysco.murderleaderboard.client.renderer.TopPlayerSpecialRenderer;
import com.mrbysco.murderleaderboard.registry.MurderRegistry;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;

public class MurderModels extends ModelProvider {

	public MurderModels(PackOutput packOutput) {
		super(packOutput, MurderLeaderboard.MOD_ID);
	}

	private static final ModelTemplate TOP_PLAYER = ModelTemplates.create(MurderLeaderboard.MOD_ID + ":top_player", TextureSlot.PARTICLE);

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		blockModels.createParticleOnlyBlock(MurderRegistry.TOP_PLAYER.get(), Blocks.SOUL_SAND);
		Item item = MurderRegistry.TOP_PLAYER.asItem();
		ResourceLocation resourcelocation = TOP_PLAYER.create(item, TextureMapping.particle(MurderRegistry.TOP_PLAYER.get()), blockModels.modelOutput);
		ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.specialModel(resourcelocation, new TopPlayerSpecialRenderer.Unbaked());
		itemModels.itemModelOutput.accept(item, itemmodel$unbaked);
	}
}