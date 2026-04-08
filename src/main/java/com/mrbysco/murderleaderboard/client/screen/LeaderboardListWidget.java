package com.mrbysco.murderleaderboard.client.screen;

import com.mrbysco.murderleaderboard.world.MurderData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jspecify.annotations.Nullable;

public class LeaderboardListWidget extends ObjectSelectionList<LeaderboardListWidget.ListEntry> {
	private final LeaderboardScreen parent;
	private final int listWidth;

	public LeaderboardListWidget(LeaderboardScreen parent, int listWidth, int top, int bottom) {
		super(parent.getMinecraft(), listWidth, bottom - top, top, parent.getFontRenderer().lineHeight * 2 + 8);
		this.parent = parent;
		this.listWidth = listWidth;
		this.refreshList();
	}

	@Override
	protected int scrollBarX() {
		return this.listWidth;
	}

	@Override
	public int getRowWidth() {
		return this.listWidth;
	}

	@Override
	public void setSelected(@Nullable LeaderboardListWidget.ListEntry selected) {
		this.parent.setSelected(getSelected(), selected);
		super.setSelected(selected);
	}

	public void refreshList() {
		this.clearEntries();
		parent.buildLeaderboard(this::addEntry, location -> new ListEntry(location, this.parent));
	}

	public class ListEntry extends ObjectSelectionList.Entry<ListEntry> {
		private final MurderData.KillData killData;
		private final LeaderboardScreen parent;

		ListEntry(MurderData.KillData data, LeaderboardScreen parent) {
			this.killData = data;
			this.parent = parent;
		}


		@Override
		public void extractContent(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
			Font font = this.parent.getFontRenderer();
			int top = getContentY();
			String killer = killData.name();
			String killCount = String.valueOf(killData.kills());

			guiGraphics.textWithWordWrap(font, Component.literal(killer), (this.parent.width / 2) - 80, top + 6, 160, 0xFFFFffFF);
			guiGraphics.text(font, killCount, (this.parent.width / 2) + 100 - (font.width(killCount) / 2), top + 6, 0xFFFFFffF, false);

			renderFloatingItem(guiGraphics, getSkull(), (this.parent.width / 2) - 106, top + 1);
		}

		private void renderFloatingItem(GuiGraphicsExtractor guiGraphics, ItemStack stack, int x, int y) {
			Minecraft mc = parent.getMinecraft();
			guiGraphics.pose().pushMatrix();
			guiGraphics.item(stack, x, y);
			var font = IClientItemExtensions.of(stack).getFont(stack, IClientItemExtensions.FontContext.ITEM_COUNT);
			guiGraphics.itemDecorations(font == null ? mc.font : font, stack, x, y, null);
			guiGraphics.pose().popMatrix();
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
