package com.mrbysco.murderleaderboard.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TopPlayerBER implements BlockEntityRenderer<TopPlayerBlockEntity> {
	protected final EntityRenderDispatcher entityRenderDispatcher;
	private final TopPlayerTileModel model;
	private final TopPlayerTileModel slimModel;

	public static final ResourceLocation defaultTexture = DefaultPlayerSkin.getDefaultSkin();

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

		render(direction, profile, blockEntity.isSlim(), poseStack, bufferSource, combinedLightIn, partialTicks);

		//Only render when the block is being looked at
		final Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.hitResult != null && minecraft.hitResult.getType() == HitResult.Type.BLOCK) {
			BlockHitResult blockhitresult = (BlockHitResult) minecraft.hitResult;
			BlockPos blockpos = blockhitresult.getBlockPos();
			if(blockEntity.getBlockPos().equals(blockpos)) {
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
				font.drawInBatch(name, halfWidth, (float) 0, 553648127, false, pose, bufferSource, flag, j, combinedLightIn);
				font.drawInBatch(name, halfWidth, (float) 0, -1, false, pose, bufferSource, false, 0, combinedLightIn);

				poseStack.popPose();
			}
		}
	}

	public void render(@Nullable Direction direction, @Nullable GameProfile profile, boolean isSlim, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, float partialTicks) {
		poseStack.translate(0.5D, 0.25D, 0.5D);
		poseStack.pushPose();
		if (direction != null) {
			switch (direction) {
				case NORTH:
					break;
				case SOUTH:
					poseStack.mulPose(Vector3f.YP.rotationDegrees(180));
					break;
				case WEST:
					poseStack.mulPose(Vector3f.YP.rotationDegrees(90));
					break;
				default:
					poseStack.mulPose(Vector3f.YP.rotationDegrees(270));
			}
		}
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		poseStack.translate(0.0D, -1.25D, 0.0D);

		if (profile != null) {
			final String s = ChatFormatting.stripFormatting(profile.getName());
			if ("Dinnerbone".equalsIgnoreCase(s) || "Grumm".equalsIgnoreCase(s)) {
				poseStack.translate(0.0D, (double) (1.85F), 0.0D);
				poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
			}
		}

		VertexConsumer vertexConsumer = bufferSource.getBuffer(getRenderType(profile));
		TopPlayerTileModel playerModel = isSlim ? slimModel : model;

		playerModel.renderToBuffer(poseStack, vertexConsumer, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

		poseStack.popPose();
	}

	public static RenderType getRenderType(@Nullable GameProfile gameProfileIn) {
		if (gameProfileIn == null || !gameProfileIn.isComplete()) {
			return RenderType.entityCutoutNoCull(defaultTexture);
		} else {
			final Minecraft minecraft = Minecraft.getInstance();
			final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().getInsecureSkinInformation(gameProfileIn);
			if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
				return RenderType.entityTranslucent(minecraft.getSkinManager().registerTexture((MinecraftProfileTexture) map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN));
			} else {
				return RenderType.entityCutoutNoCull(DefaultPlayerSkin.getDefaultSkin(UUIDUtil.getOrCreatePlayerUUID(gameProfileIn)));
			}
		}
	}
}
