package com.mrbysco.murderleaderboard.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class RankChangeToast implements Toast {
	private final int newRank;
	private final String oldUser, newUser;
	private final ItemStack skull;
	private final Component title;
	private final Component subtitle;
	private long lastChanged;
	private boolean changed;

	public RankChangeToast(int newRank, String oldUser, String newUser, ItemStack skull) {
		this.newRank = newRank + 1;
		this.oldUser = oldUser;
		this.newUser = newUser;
		this.skull = skull;
		this.title = Component.translatable("murderleaderboard.toast.title",
						Component.literal(getShortUser(this.newUser, 12)).withStyle(ChatFormatting.GOLD),
						Component.literal("#" + this.newRank).withStyle(ChatFormatting.GOLD))
				.withStyle(ChatFormatting.WHITE);
		this.subtitle = Component.translatable("murderleaderboard.toast.subtitle", getShortUser(this.oldUser, 12)).withStyle(ChatFormatting.GRAY);
	}

	private String getShortUser(String user, int max) {
		String s = user;
		if (s.length() > max) {
			s = s.substring(0, max) + "...";
		}
		return s;
	}

	public Toast.Visibility render(PoseStack poseStack, ToastComponent component, long time) {
		if (this.changed) {
			this.lastChanged = time;
			this.changed = false;
		}

		RenderSystem.setShaderTexture(0, TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		component.blit(poseStack, 0, 0, 0, 0, this.width(), this.height());
		Font font = component.getMinecraft().font;
		font.draw(poseStack, title, 30.0F, 7.0F, -11534256);
		font.draw(poseStack, subtitle, 30.0F, 18.0F, -16777216);
		PoseStack posestack = RenderSystem.getModelViewStack();
		posestack.pushPose();
		posestack.translate(2.5D, 5D, 0);
		posestack.scale(1.0F, 1.0F, 1.0F);
		RenderSystem.applyModelViewMatrix();
		component.getMinecraft().getItemRenderer().renderAndDecorateFakeItem(skull, 3, 3);
		posestack.popPose();

		return time - this.lastChanged < 5000L ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
	}
}
