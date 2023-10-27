package com.mrbysco.murderleaderboard.client.screen;

import com.mrbysco.murderleaderboard.client.ClientHandler;
import com.mrbysco.murderleaderboard.world.MurderData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.loading.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LeaderboardScreen extends Screen {
	private static final int PADDING = 6;

	private LeaderboardListWidget leaderboardWidget;
	private LeaderboardListWidget.ListEntry selected = null;
	private int listWidth;
	private List<MurderData.KillData> kills;
	private final List<MurderData.KillData> unsortedKills;

	private String lastFilterText = "";

	private EditBox search;

	private static final Component murdererText = Component.translatable("murderleaderboard.leaderboard.murderer").withStyle(ChatFormatting.UNDERLINE);
	private static final Component killCountText = Component.translatable("murderleaderboard.leaderboard.killCount").withStyle(ChatFormatting.UNDERLINE);

	public LeaderboardScreen() {
		super(Component.translatable("murderleaderboard.leaderboard.title"));

		List<MurderData.KillData> killList = new ArrayList<>();
		for (MurderData.KillData killData : ClientHandler.killList) {
			if (killData != null) {
				killList.add(killData);
			}
		}

		this.kills = Collections.unmodifiableList(killList);
		this.unsortedKills = Collections.unmodifiableList(ClientHandler.killList);
	}

	@Override
	public boolean isPauseScreen() {
		return true;
	}

	@Override
	protected void init() {
		int centerWidth = this.width / 2;
		for (MurderData.KillData data : kills) {
			listWidth = Math.max(listWidth, getFontRenderer().width(data.toString()) + 10);
		}
		listWidth = Math.max(Math.min(listWidth, width / 3), 200);
		int structureWidth = this.width - this.listWidth - (PADDING * 3);
		int closeButtonWidth = Math.min(structureWidth, 200);
		int y = this.height - 20 - PADDING;
		this.addRenderableWidget(Button.builder(Component.translatable("gui.back"), b -> onClose())
				.bounds(centerWidth - (closeButtonWidth / 2) + PADDING, y, closeButtonWidth, 20).build());

		y -= 14 + PADDING;
		search = new EditBox(getFontRenderer(), centerWidth - listWidth / 2 + PADDING + 1, y, listWidth - 2, 14,
				Component.translatable("murderleaderboard.leaderboard.search"));

		int fullButtonHeight = PADDING + 20 + PADDING;
		this.leaderboardWidget = new LeaderboardListWidget(this, width, fullButtonHeight, search.getY() - getFontRenderer().lineHeight - PADDING);
		this.leaderboardWidget.setLeftPos(0);

		addWidget(search);
		addWidget(leaderboardWidget);
		search.setFocused(false);
		search.setCanLoseFocus(true);
	}

	@Override
	public void tick() {
		search.tick();
		leaderboardWidget.setSelected(selected);

		if (!search.getValue().equals(lastFilterText)) {
			reloadKills();
		}
	}

	private void reloadKills() {
		this.kills = this.unsortedKills.stream().
				filter(data -> StringUtils.toLowerCase(data.name()).contains(StringUtils.toLowerCase(search.getValue())))
				.collect(Collectors.toList());
		leaderboardWidget.refreshList();
		lastFilterText = search.getValue();
	}

	public <T extends ObjectSelectionList.Entry<T>> void buildLeaderboard(Consumer<T> ListViewConsumer, Function<MurderData.KillData, T> newEntry) {
		kills.forEach(mod -> ListViewConsumer.accept(newEntry.apply(mod)));
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.leaderboardWidget.render(guiGraphics, mouseX, mouseY, partialTicks);

		guiGraphics.drawCenteredString(font, murdererText, this.width / 2 - 56,
				16, 0xFFFFFF);
		guiGraphics.drawCenteredString(font, killCountText, this.width / 2 + 100,
				16, 0xFFFFFF);

		guiGraphics.drawCenteredString(font, Component.translatable("murderleaderboard.leaderboard.search"), this.width / 2 + PADDING,
				search.getY() - getFontRenderer().lineHeight - 2, 0xFFFFFF);

		this.search.render(guiGraphics, mouseX, mouseY, partialTicks);

		super.render(guiGraphics, mouseX, mouseY, partialTicks);
	}

	public Font getFontRenderer() {
		return font;
	}

	public void setSelected(LeaderboardListWidget.ListEntry entry) {
		this.selected = entry == this.selected ? null : entry;
	}

	/**
	 * Clear the search field when right-clicked on it
	 */
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean flag = super.mouseClicked(mouseX, mouseY, button);
		if (button == 1 && search.isMouseOver(mouseX, mouseY)) {
			search.setValue("");
		}
		return flag;
	}

	@Override
	public void resize(Minecraft mc, int width, int height) {
		String s = this.search.getValue();
		LeaderboardListWidget.ListEntry selected = this.selected;
		this.init(mc, width, height);
		this.search.setValue(s);
		this.selected = selected;
		if (!this.search.getValue().isEmpty())
			reloadKills();
	}

	@Override
	public void onClose() {
		this.minecraft.setScreen(null);
	}
}
