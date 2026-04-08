package com.mrbysco.murderleaderboard.client.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;

public class TopPlayerRenderState extends BlockEntityRenderState {
	public Direction direction = Direction.UP;
	public ResolvableProfile profile;
	public PlayerSkin skin;
	public boolean isSlim = false;
	public RenderType renderType;
	public int rank;
}
