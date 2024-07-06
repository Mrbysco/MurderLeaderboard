package com.mrbysco.murderleaderboard.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mrbysco.murderleaderboard.client.ClientHandler;
import com.mrbysco.murderleaderboard.client.model.TopPlayerTileModel;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.HashMap;
import java.util.Map;

public class TopPlayerBEWLR extends BlockEntityWithoutLevelRenderer {
	private final TopPlayerTileModel model;
	private final TopPlayerTileModel slimModel;
	public static boolean isSlim = false;

	public TopPlayerBEWLR(BlockEntityRendererProvider.Context context) {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
		this.model = new TopPlayerTileModel(context.bakeLayer(ClientHandler.TOP_PLAYER), false);
		this.slimModel = new TopPlayerTileModel(context.bakeLayer(ClientHandler.TOP_PLAYER_SLIM), false);
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
		renderPlayerItem(stack, poseStack, bufferSource, combinedLight);
	}

	private static final Map<String, ResolvableProfile> GAMEPROFILE_CACHE = new HashMap<>();

	public void renderPlayerItem(ItemStack stack, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight) {
		poseStack.pushPose();
		if (stack != null) {
			poseStack.translate(0.5D, 1.4D, 0.5D);
			poseStack.scale(-1.0F, -1.0F, 1.0F);
			renderItem(null, poseStack, bufferSource, combinedLight);
		}
		poseStack.popPose();
	}

	public void renderItem(ResolvableProfile resolvableProfile, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight) {
		SkinManager skinmanager = Minecraft.getInstance().getSkinManager();
		if (resolvableProfile != null && isSlim != skinmanager.getInsecureSkin(resolvableProfile.gameProfile()).model().id().equals("slim"))
			isSlim = !isSlim;

		VertexConsumer vertexConsumer = bufferSource.getBuffer(TopPlayerBER.getRenderType(resolvableProfile));
		if (resolvableProfile != null) {
			final String s = ChatFormatting.stripFormatting(resolvableProfile.name().orElse("unknown"));
			if ("Dinnerbone".equalsIgnoreCase(s) || "Grumm".equalsIgnoreCase(s)) {
				poseStack.translate(0.0D, (double) (1.85F), 0.0D);
				poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
			}
		}

		if (isSlim) {
			if (slimModel != null) {
				slimModel.renderToBuffer(poseStack, vertexConsumer, combinedLight, OverlayTexture.NO_OVERLAY, -1);
			}
		} else {
			if (model != null) {
				model.renderToBuffer(poseStack, vertexConsumer, combinedLight, OverlayTexture.NO_OVERLAY, -1);
			}
		}
	}
}
