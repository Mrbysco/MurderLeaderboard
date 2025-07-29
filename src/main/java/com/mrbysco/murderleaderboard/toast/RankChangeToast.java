package com.mrbysco.murderleaderboard.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fStack;

public class RankChangeToast implements Toast {
	private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/advancement");
	private final int newRank;
	private final String oldUser, newUser;
	private final ItemStack skull;
	private final Component title;
	private final Component subtitle;
	private long lastChanged;
	private boolean changed;
	private Toast.Visibility wantedVisibility = Toast.Visibility.HIDE;

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

	@Override
	public Visibility getWantedVisibility() {
		return this.wantedVisibility;
	}

	@Override
	public void update(ToastManager toastManager, long time) {
		if (this.changed) {
			this.lastChanged = time;
			this.changed = false;
		}

		this.wantedVisibility = time - this.lastChanged < 5000L ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
	}

	@Override
	public void render(GuiGraphics guiGraphics, Font font, long visibilityTime) {
		guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
		guiGraphics.drawString(font, title, 30, 7, -11534256, false);
		guiGraphics.drawString(font, subtitle, 30, 18, -16777216, false);
		Matrix4fStack viewStack = RenderSystem.getModelViewStack();
		viewStack.pushMatrix();
		viewStack.translate(2.5F, 5F, 0F);
		viewStack.scale(1.0F, 1.0F, 1.0F);
		guiGraphics.renderFakeItem(skull, 3, 3);
		viewStack.popMatrix();
	}
}
