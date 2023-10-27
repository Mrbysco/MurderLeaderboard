package com.mrbysco.murderleaderboard.data.assets;

import com.mrbysco.murderleaderboard.MurderLeaderboard;
import com.mrbysco.murderleaderboard.registry.MurderRegistry;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class MurderBlockStates extends BlockStateProvider {

	public MurderBlockStates(PackOutput packOutput, ExistingFileHelper helper) {
		super(packOutput, MurderLeaderboard.MOD_ID, helper);
	}

	@Override
	protected void registerStatesAndModels() {
		makeFacingBlock(MurderRegistry.TOP_PLAYER);
	}

	private void makeFacingBlock(RegistryObject<Block> registryObject) {
		ModelFile model = models().getExistingFile(modLoc("block/" + registryObject.getId().getPath()));
		getVariantBuilder(registryObject.get())
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