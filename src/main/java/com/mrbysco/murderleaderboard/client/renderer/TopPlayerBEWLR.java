package com.mrbysco.murderleaderboard.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mrbysco.murderleaderboard.blockentity.TopPlayerBlockEntity;
import com.mrbysco.murderleaderboard.client.ClientHandler;
import com.mrbysco.murderleaderboard.client.model.TopPlayerTileModel;
import com.mrbysco.murderleaderboard.util.SkinUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class TopPlayerBEWLR extends BlockEntityWithoutLevelRenderer {
	private final TopPlayerTileModel model;
	private final TopPlayerTileModel slimModel;

	public TopPlayerBEWLR(BlockEntityRendererProvider.Context context) {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
		this.model = new TopPlayerTileModel(context.bakeLayer(ClientHandler.TOP_PLAYER), false);
		this.slimModel = new TopPlayerTileModel(context.bakeLayer(ClientHandler.TOP_PLAYER_SLIM), false);
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
		renderPlayerItem(stack, poseStack, bufferSource, combinedLight);
	}

	private static final Map<String, GameProfile> GAMEPROFILE_CACHE = new HashMap<>();

	public void renderPlayerItem(ItemStack stack, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight) {
		poseStack.pushPose();
		if (stack != null) {
			GameProfile gameprofile = null;

			if (stack.hasCustomHoverName()) {
				String stackName = stack.getHoverName().getString().toLowerCase(Locale.ROOT);
				boolean validFlag = !stackName.isEmpty() && !stackName.contains(" ");

				if (validFlag) {
					if (GAMEPROFILE_CACHE.containsKey(stackName))
						gameprofile = GAMEPROFILE_CACHE.get(stackName);

					if (stack.hasTag() && gameprofile == null) {
						CompoundTag compoundtag = stack.getTag();
						if (compoundtag.contains("PlayerProfile", 10)) {
							GameProfile foundProfile = NbtUtils.readGameProfile(compoundtag.getCompound("PlayerProfile"));
							if (foundProfile != null) {
								GAMEPROFILE_CACHE.put(foundProfile.getName().toLowerCase(), foundProfile);
							}
							if (foundProfile.getName().equalsIgnoreCase(stackName)) {
								gameprofile = foundProfile;
							}
						} else if (compoundtag.contains("PlayerProfile", 8) && !StringUtils.isBlank(compoundtag.getString("PlayerProfile"))) {
							GameProfile gameprofile1 = new GameProfile((UUID) null, compoundtag.getString("PlayerProfile"));
							compoundtag.remove("PlayerProfile");
							TopPlayerBlockEntity.updateGameprofile(gameprofile1, (profile) -> {
								compoundtag.put("PlayerProfile", NbtUtils.writeGameProfile(new CompoundTag(), profile));
								if (profile != null) {
									GAMEPROFILE_CACHE.put(profile.getName().toLowerCase(), profile);
								}
							});
							gameprofile = GAMEPROFILE_CACHE.get(stackName);
						}
					}

					if (gameprofile == null) {
						GameProfile gameprofile1 = new GameProfile((UUID) null, stackName);
						TopPlayerBlockEntity.updateGameprofile(gameprofile1, (profile) -> {
							if (profile != null) {
								GAMEPROFILE_CACHE.put(profile.getName().toLowerCase(), profile);
							}
						});
					}
				} else {
					if (GAMEPROFILE_CACHE.containsKey("steve"))
						gameprofile = GAMEPROFILE_CACHE.get("steve");

					if (gameprofile == null) {
						GameProfile gameprofile1 = new GameProfile((UUID) null, "steve");
						TopPlayerBlockEntity.updateGameprofile(gameprofile1, (profile) -> {
							if (profile != null) {
								GAMEPROFILE_CACHE.put(profile.getName(), profile);
							}
						});
					}
				}
			}

			poseStack.translate(0.5D, 1.4D, 0.5D);
			poseStack.scale(-1.0F, -1.0F, 1.0F);
			renderItem(gameprofile, poseStack, bufferSource, combinedLight);
		}
		poseStack.popPose();
	}

	public void renderItem(GameProfile gameprofile, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight) {
		boolean flag = gameprofile != null && gameprofile.isComplete() && SkinUtil.isSlimSkin(gameprofile.getId());
		VertexConsumer vertexConsumer = bufferSource.getBuffer(TopPlayerBER.getRenderType(gameprofile));
		if (gameprofile != null) {
			final String s = ChatFormatting.stripFormatting(gameprofile.getName());
			if ("Dinnerbone".equalsIgnoreCase(s) || "Grumm".equalsIgnoreCase(s)) {
				poseStack.translate(0.0D, (double) (1.85F), 0.0D);
				poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
			}
		}

		if (flag) {
			if (slimModel != null) {
				slimModel.renderToBuffer(poseStack, vertexConsumer, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
			}
		} else {
			if (model != null) {
				model.renderToBuffer(poseStack, vertexConsumer, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
			}
		}
	}
}
