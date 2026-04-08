package com.mrbysco.murderleaderboard.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrbysco.murderleaderboard.block.TopPlayerBlock;
import com.mrbysco.murderleaderboard.blockentity.TopPlayerBlockEntity;
import com.mrbysco.murderleaderboard.client.state.TopPlayerRenderState;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public class TopPlayerBER implements BlockEntityRenderer<TopPlayerBlockEntity, TopPlayerRenderState> {
	private final PlayerSkinRenderCache playerSkinRenderCache;
	private final PlayerModel model;
	private final PlayerModel slimModel;
	public static boolean isSlim = false;

	public static final Identifier defaultTexture = DefaultPlayerSkin.getDefaultTexture();

	public TopPlayerBER(BlockEntityRendererProvider.Context context) {
		this.playerSkinRenderCache = context.playerSkinRenderCache();
		this.model = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false);
		this.slimModel = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
	}

	@Override
	public TopPlayerRenderState createRenderState() {
		return new TopPlayerRenderState();
	}

	@Override
	public void extractRenderState(TopPlayerBlockEntity blockEntity, TopPlayerRenderState renderState,
	                               float partialTick, Vec3 cameraPosition,
	                               ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

		BlockState state = blockEntity.getBlockState();
		renderState.direction = state.getBlock() instanceof TopPlayerBlock ? state.getValue(TopPlayerBlock.FACING) : Direction.UP;

		renderState.profile = blockEntity.getKiller();

		if (renderState.profile != null) {
			SkinManager skinmanager = Minecraft.getInstance().getSkinManager();
			Supplier<PlayerSkin> skinSupplier = skinmanager.createLookup(renderState.profile.partialProfile(), false);
			renderState.skin = playerSkinRenderCache.getOrDefault(renderState.profile).playerSkin();
			renderState.isSlim = skinSupplier.get().model().getSerializedName().equals("slim");
			renderState.renderType = this.getRenderType(renderState.profile);
		}
		renderState.rank = blockEntity.getRank();
	}

	@Override
	public void submit(TopPlayerRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
		Direction direction = renderState.direction;
		ResolvableProfile resolvableProfile = renderState.profile;
		final RenderType renderType = renderState.renderType;
		final PlayerModel playerModel = renderState.isSlim ? slimModel : model;

		poseStack.pushPose();
		poseStack.scale(0.5625F, 0.5625F, 0.5625F);
		poseStack.translate(0.375F, 0.0F, 0.375F);
		submitPlayerStatue(nodeCollector, direction, resolvableProfile.partialProfile(), playerModel, poseStack,
				renderType, renderState.lightCoords, renderState.breakProgress);
		poseStack.popPose();

		//Only render when the block is being looked at
		final Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.hitResult != null && minecraft.hitResult.getType() == HitResult.Type.BLOCK) {
			BlockHitResult blockhitresult = (BlockHitResult) minecraft.hitResult;
			BlockPos blockpos = blockhitresult.getBlockPos();
			if (renderState.blockPos.equals(blockpos)) {
				String rank = String.format("#%s ", renderState.rank);
				Component name = Component.literal(rank + (resolvableProfile != null ? resolvableProfile.name().get() : "unknown"));
				float yOffset = 1.25F;

				float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
				int j = (int) (backgroundOpacity * 255.0F) << 24;

				nodeCollector.submitText(poseStack, 0, 0 + yOffset, name.getVisualOrderText(), false,
						Font.DisplayMode.NORMAL, renderState.lightCoords, 0, j, -1);
			}
		}
	}

	public static void submitPlayerStatue(SubmitNodeCollector nodeCollector, @Nullable Direction direction,
	                                      @Nullable GameProfile profile, PlayerModel model,
	                                      PoseStack poseStack, RenderType renderType, int combinedLight,
	                                      ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		poseStack.translate(0.5D, 0.25D, 0.5D);
		poseStack.translate(0.5D, 0.25D, 0.5D);
		poseStack.pushPose();
		if (direction != null) {
			switch (direction) {
				case NORTH:
					break;
				case SOUTH:
					poseStack.mulPose(Axis.YP.rotationDegrees(180));
					break;
				case WEST:
					poseStack.mulPose(Axis.YP.rotationDegrees(90));
					break;
				default:
					poseStack.mulPose(Axis.YP.rotationDegrees(270));
			}
		}
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		poseStack.translate(0.0D, -1.25D, 0.0D);

		if (profile != null) {
			final String s = ChatFormatting.stripFormatting(profile.name());
			if ("Dinnerbone".equalsIgnoreCase(s) || "Grumm".equalsIgnoreCase(s)) {
				poseStack.translate(0.0D, (double) (1.85F), 0.0D);
				poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
			}
		}

		AvatarRenderState renderState = new AvatarRenderState();
		model.setupAnim(renderState);
		nodeCollector.submitModel(model, renderState, poseStack, renderType,
				combinedLight, OverlayTexture.NO_OVERLAY, 0, crumblingOverlay);
		poseStack.popPose();
	}

	public RenderType getRenderType(@Nullable ResolvableProfile resolvableProfile) {
		if (resolvableProfile == null)
			return RenderTypes.entityTranslucent(defaultTexture);

		return playerSkinRenderCache.getOrDefault(resolvableProfile).renderType();
	}
}
