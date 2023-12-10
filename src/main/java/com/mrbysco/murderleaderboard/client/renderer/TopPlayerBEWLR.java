package com.mrbysco.murderleaderboard.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mrbysco.murderleaderboard.blockentity.TopPlayerBlockEntity;
import com.mrbysco.murderleaderboard.client.ClientHandler;
import com.mrbysco.murderleaderboard.client.model.TopPlayerTileModel;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Locale;
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
							String playerProfile = compoundtag.getString("PlayerProfile");
							compoundtag.remove("PlayerProfile");

							TopPlayerBlockEntity.fetchGameProfile(playerProfile).thenAccept((profile) -> {
								if (profile.isPresent()) {
									GameProfile profile1 = profile.orElse(new GameProfile(Util.NIL_UUID, playerProfile));
									GAMEPROFILE_CACHE.put(profile1.getName().toLowerCase(), profile1);
								}
							});
							gameprofile = GAMEPROFILE_CACHE.get(stackName);
						}
					}

					if (gameprofile == null) {
						TopPlayerBlockEntity.fetchGameProfile(stackName).thenAccept((profile) -> {
							if (profile.isPresent()) {
								GameProfile profile1 = profile.orElse(new GameProfile(Util.NIL_UUID, stackName));
								GAMEPROFILE_CACHE.put(profile1.getName().toLowerCase(), profile1);
							}
						});
					}
				} else {
					if (GAMEPROFILE_CACHE.containsKey("steve"))
						gameprofile = GAMEPROFILE_CACHE.get("steve");

					if (gameprofile == null) {
						TopPlayerBlockEntity.fetchGameProfile("steve").thenAccept((profile) -> {
							if (profile.isPresent()) {
								GameProfile profile1 = profile.orElse(new GameProfile(Util.NIL_UUID, "steve"));
								GAMEPROFILE_CACHE.put(profile1.getName().toLowerCase(), profile1);
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
		SkinManager skinmanager = Minecraft.getInstance().getSkinManager();
		if (gameprofile != null && isSlim != skinmanager.getInsecureSkin(gameprofile).model().id().equals("slim"))
			isSlim = !isSlim;

		VertexConsumer vertexConsumer = bufferSource.getBuffer(TopPlayerBER.getRenderType(gameprofile));
		if (gameprofile != null) {
			final String s = ChatFormatting.stripFormatting(gameprofile.getName());
			if ("Dinnerbone".equalsIgnoreCase(s) || "Grumm".equalsIgnoreCase(s)) {
				poseStack.translate(0.0D, (double) (1.85F), 0.0D);
				poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
			}
		}

		if (isSlim) {
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
