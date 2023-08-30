package com.mrbysco.murderleaderboard.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.murderleaderboard.world.MurderData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

public class LeaderboardListWidget extends ObjectSelectionList<LeaderboardListWidget.ListEntry> {
	private final LeaderboardScreen parent;
	private final int listWidth;

	public LeaderboardListWidget(LeaderboardScreen parent, int listWidth, int top, int bottom) {
		super(parent.getMinecraft(), listWidth, parent.height, top, bottom, parent.getFontRenderer().lineHeight * 2 + 8);
		this.parent = parent;
		this.listWidth = listWidth;
		this.refreshList();
	}

	@Override
	protected int getScrollbarPosition() {
		return this.listWidth;
	}

	@Override
	public int getRowWidth() {
		return this.listWidth;
	}

	public void refreshList() {
		this.clearEntries();
		parent.buildLeaderboard(this::addEntry, location -> new ListEntry(location, this.parent));
	}

	@Override
	protected void renderBackground(PoseStack mStack) {
		this.parent.renderBackground(mStack);
	}

	public class ListEntry extends ObjectSelectionList.Entry<ListEntry> {
		private final MurderData.KillData killData;
		private final LeaderboardScreen parent;

		ListEntry(MurderData.KillData data, LeaderboardScreen parent) {
			this.killData = data;
			this.parent = parent;
		}

		@Override
		public void render(PoseStack poseStack, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
			String killer = killData.name();
			String killCount = String.valueOf(killData.kills());

			Font font = this.parent.getFontRenderer();
			font.draw(poseStack, killer, (this.parent.width / 2) - 80, top + 6, 0xFFFFFF);
			font.draw(poseStack, killCount, (this.parent.width / 2) + 100 - (font.width(killCount) / 2), top + 6, 0xFFFFFF);

			renderFloatingItem(getSkull(), (this.parent.width / 2) - 106, top + 1);
		}

		private void renderFloatingItem(ItemStack stack, int x, int y) {
			Minecraft mc = parent.getMinecraft();
			ItemRenderer itemRenderer = mc.getItemRenderer();

			PoseStack posestack = RenderSystem.getModelViewStack();
			posestack.translate(0.0D, 0.0D, 32.0D);
			RenderSystem.applyModelViewMatrix();
			parent.setBlitOffset(200);
			itemRenderer.blitOffset = 200.0F;
			net.minecraft.client.gui.Font font = net.minecraftforge.client.RenderProperties.get(stack).getFont(stack);
			if (font == null) font = parent.getFontRenderer();
			itemRenderer.renderAndDecorateItem(stack, x, y);
			itemRenderer.renderGuiItemDecorations(font, stack, x, y, null);
			parent.setBlitOffset(0);
			itemRenderer.blitOffset = 0.0F;
		}

		@Override
		public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
			parent.setSelected(this);
			LeaderboardListWidget.this.setSelected(this);
			return false;
		}

		public String getKiller() {
			return killData.name();
		}

		public int getKills() {
			return killData.kills();
		}

		public ItemStack getSkull() {
			return killData.getSkull();
		}

		@Override
		public Component getNarration() {
			return new TranslatableComponent("murderleaderboard.leaderboard.narration", getKiller(), getKills());
		}
	}
}
