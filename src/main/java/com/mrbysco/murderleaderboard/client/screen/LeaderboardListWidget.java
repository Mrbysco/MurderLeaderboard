package com.mrbysco.murderleaderboard.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.murderleaderboard.world.MurderData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
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
	protected void renderBackground(GuiGraphics guiGraphics) {
		this.parent.renderBackground(guiGraphics);
	}

	public class ListEntry extends ObjectSelectionList.Entry<ListEntry> {
		private final MurderData.KillData killData;
		private final LeaderboardScreen parent;

		ListEntry(MurderData.KillData data, LeaderboardScreen parent) {
			this.killData = data;
			this.parent = parent;
		}

		@Override
		public void render(GuiGraphics guiGraphics, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
			String killer = killData.name();
			String killCount = String.valueOf(killData.kills());

			Font font = this.parent.getFontRenderer();
			guiGraphics.drawWordWrap(font, Component.literal(killer), (this.parent.width / 2) - 80, top + 6, 160, 0xFFFFFF);
			guiGraphics.drawString(font, killCount, (this.parent.width / 2) + 100 - (font.width(killCount) / 2), top + 6, 0xFFFFFF, false);

			renderFloatingItem(guiGraphics, getSkull(), (this.parent.width / 2) - 106, top + 1);
		}

		private void renderFloatingItem(GuiGraphics guiGraphics, ItemStack stack, int x, int y) {
			Minecraft mc = parent.getMinecraft();
			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(0.0F, 0.0F, 232.0F);
			guiGraphics.renderItem(stack, x, y);
			var font = net.minecraftforge.client.extensions.common.IClientItemExtensions.of(stack).getFont(stack, net.minecraftforge.client.extensions.common.IClientItemExtensions.FontContext.ITEM_COUNT);
			guiGraphics.renderItemDecorations(font == null ? mc.font : font, stack, x, y, null);
			guiGraphics.pose().popPose();
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
			return Component.translatable("murderleaderboard.leaderboard.narration", getKiller(), getKills());
		}
	}
}
