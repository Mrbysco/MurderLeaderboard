package com.mrbysco.murderleaderboard.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mrbysco.murderleaderboard.block.TopPlayerBlock;
import com.mrbysco.murderleaderboard.blockentity.TopPlayerBlockEntity;
import com.mrbysco.murderleaderboard.client.ClientHandler;
import com.mrbysco.murderleaderboard.client.model.TopPlayerTileModel;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class TopPlayerBER implements BlockEntityRenderer<TopPlayerBlockEntity> {
	protected final EntityRenderDispatcher entityRenderDispatcher;
	private final TopPlayerTileModel model;
	private final TopPlayerTileModel slimModel;
	public static boolean isSlim = false;

	public static final ResourceLocation defaultTexture = DefaultPlayerSkin.getDefaultTexture();

	public TopPlayerBER(BlockEntityRendererProvider.Context context) {
		this.entityRenderDispatcher = context.getEntityRenderer();
		this.model = new TopPlayerTileModel(context.bakeLayer(ClientHandler.TOP_PLAYER), false);
		this.slimModel = new TopPlayerTileModel(context.bakeLayer(ClientHandler.TOP_PLAYER_SLIM), true);
	}

	@Override
	public void render(TopPlayerBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLightIn, int combinedOverlayIn) {
		BlockState blockstate = blockEntity.getBlockState();
		boolean flag = blockstate.getBlock() instanceof TopPlayerBlock;
		Direction direction = flag ? blockstate.getValue(TopPlayerBlock.FACING) : Direction.UP;
		GameProfile profile = blockEntity.getPlayerProfile();

		if (profile != null) {
			SkinManager skinmanager = Minecraft.getInstance().getSkinManager();
			if (isSlim != skinmanager.getInsecureSkin(profile).model().id().equals("slim"))
				isSlim = !isSlim;
		}

		render(direction, profile, poseStack, bufferSource, combinedLightIn, partialTicks);

		//Only render when the block is being looked at
		final Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.hitResult != null && minecraft.hitResult.getType() == HitResult.Type.BLOCK) {
			BlockHitResult blockhitresult = (BlockHitResult) minecraft.hitResult;
			BlockPos blockpos = blockhitresult.getBlockPos();
			if (blockEntity.getBlockPos().equals(blockpos)) {
				String rank = String.format("#%s ", blockEntity.getRank());
				Component name = profile != null ? Component.literal(rank + profile.getName()) : Component.literal(rank + "Unknown");
				float yOffset = 1.25F;
				poseStack.pushPose();
				poseStack.translate(0.0D, (double) yOffset, 0.0D);
				poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
				poseStack.scale(-0.025F, -0.025F, 0.025F);
				Matrix4f pose = poseStack.last().pose();
				float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
				int j = (int) (backgroundOpacity * 255.0F) << 24;
				Font font = minecraft.font;
				float halfWidth = (float) (-font.width(name) / 2);
				font.drawInBatch(name, halfWidth, (float) 0, 553648127, false, pose, bufferSource, flag ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, j, combinedLightIn);
				font.drawInBatch(name, halfWidth, (float) 0, -1, false, pose, bufferSource, Font.DisplayMode.NORMAL, 0, combinedLightIn);

				poseStack.popPose();
			}
		}
	}

	public void render(@Nullable Direction direction, @Nullable GameProfile profile, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, float partialTicks) {
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
			final String s = ChatFormatting.stripFormatting(profile.getName());
			if ("Dinnerbone".equalsIgnoreCase(s) || "Grumm".equalsIgnoreCase(s)) {
				poseStack.translate(0.0D, (double) (1.85F), 0.0D);
				poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
			}
		}

		VertexConsumer vertexConsumer = bufferSource.getBuffer(getRenderType(profile));
		TopPlayerTileModel playerModel = isSlim ? slimModel : model;

		playerModel.renderToBuffer(poseStack, vertexConsumer, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

		poseStack.popPose();
	}

	public static RenderType getRenderType(@Nullable GameProfile gameProfile) {
		if (gameProfile == null)
			return RenderType.entityTranslucent(defaultTexture);
		SkinManager skinmanager = Minecraft.getInstance().getSkinManager();
		return RenderType.entityTranslucent(skinmanager.getInsecureSkin(gameProfile).texture());
	}
}
