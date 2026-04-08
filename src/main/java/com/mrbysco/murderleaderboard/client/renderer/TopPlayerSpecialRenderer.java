package com.mrbysco.murderleaderboard.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class TopPlayerSpecialRenderer implements SpecialModelRenderer<PlayerSkinRenderCache.RenderInfo> {
	private final PlayerSkinRenderCache playerSkinRenderCache;
	private final PlayerModel model;
	private final PlayerModel slimModel;
	public boolean isSlim = false;

	public TopPlayerSpecialRenderer(PlayerSkinRenderCache playerSkinRenderCache,
	                                PlayerModel model, PlayerModel slimModel) {
		this.playerSkinRenderCache = playerSkinRenderCache;
		this.model = model;
		this.slimModel = slimModel;
	}

	@Override
	public void submit(@Nullable PlayerSkinRenderCache.RenderInfo argument, ItemDisplayContext displayContext,
	                   PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay,
	                   boolean hasFoil, int outlineColor) {
		poseStack.pushPose();
		transform(poseStack);
		PlayerModel playerModel = isSlim ? slimModel : model;
		RenderType rendertype = argument != null ? argument.renderType() : PlayerSkinRenderCache.DEFAULT_PLAYER_SKIN_RENDER_TYPE;
		GameProfile gameProfile = argument != null ? argument.gameProfile() : new GameProfile(Util.NIL_UUID, "Steve");
		TopPlayerBER.submitPlayerStatue(nodeCollector, null, gameProfile, playerModel,
				poseStack, rendertype, packedLight, null);
		poseStack.popPose();
	}

	@Override
	public void getExtents(Consumer<Vector3fc> output) {
		PoseStack posestack = new PoseStack();
		transform(posestack);
		this.model.root().getExtentsForGui(posestack, output);
		this.slimModel.root().getExtentsForGui(posestack, output);
	}

	private static void transform(PoseStack poseStack) {
		poseStack.scale(0.375F, 0.375F, 0.375F);
		poseStack.translate(1D, 0D, 0.75D);
	}

	@Nullable
	public PlayerSkinRenderCache.RenderInfo extractArgument(ItemStack stack) {
		ResolvableProfile resolvableprofile = stack.get(DataComponents.PROFILE);
		return resolvableprofile == null ? null : this.playerSkinRenderCache.getOrDefault(resolvableprofile);
	}

	public record Unbaked() implements SpecialModelRenderer.Unbaked {
		public static final Unbaked INSTANCE = new Unbaked();
		public static final MapCodec<TopPlayerSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(INSTANCE);

		@NotNull
		@Override
		public MapCodec<TopPlayerSpecialRenderer.Unbaked> type() {
			return MAP_CODEC;
		}

		@NotNull
		@Override
		public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext context) {
			final EntityModelSet entityModelSet = context.entityModelSet();
			PlayerModel model = new PlayerModel(entityModelSet.bakeLayer(ModelLayers.PLAYER), false);
			PlayerModel slimModel = new PlayerModel(entityModelSet.bakeLayer(ModelLayers.PLAYER_SLIM), true);
			return new TopPlayerSpecialRenderer(context.playerSkinRenderCache(), model, slimModel);
		}
	}
}