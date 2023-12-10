package com.mrbysco.murderleaderboard.data.assets;

import com.mrbysco.murderleaderboard.MurderLeaderboard;
import com.mrbysco.murderleaderboard.block.TopPlayerBlock;
import com.mrbysco.murderleaderboard.registry.MurderRegistry;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class MurderBlockStates extends BlockStateProvider {

	public MurderBlockStates(PackOutput packOutput, ExistingFileHelper helper) {
		super(packOutput, MurderLeaderboard.MOD_ID, helper);
	}

	@Override
	protected void registerStatesAndModels() {
		makeFacingBlock(MurderRegistry.TOP_PLAYER);
	}

	private void makeFacingBlock(DeferredBlock<TopPlayerBlock> deferredBlock) {
		ModelFile model = models().getExistingFile(modLoc("block/" + deferredBlock.getId().getPath()));
		getVariantBuilder(deferredBlock.get())
				.partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
				.modelForState().modelFile(model).addModel()
				.partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
				.modelForState().modelFile(model).rotationY(90).addModel()
				.partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
				.modelForState().modelFile(model).rotationY(180).addModel()
				.partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)
				.modelForState().modelFile(model).rotationY(270).addModel();
	}
}