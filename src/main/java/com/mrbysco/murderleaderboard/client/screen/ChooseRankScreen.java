package com.mrbysco.murderleaderboard.client.screen;

import com.mrbysco.murderleaderboard.client.screen.widget.NumberFieldBox;
import com.mrbysco.murderleaderboard.network.PacketHandler;
import com.mrbysco.murderleaderboard.network.message.ChooseRankMessage;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class ChooseRankScreen extends Screen {
	private final BlockPos position;
	private final int originalRank;

	private NumberFieldBox rankField;

	public ChooseRankScreen(BlockPos pos, int rank) {
		super(GameNarrator.NO_TITLE);

		this.position = pos;
		this.originalRank = rank;
	}

	public static void openScreen(BlockPos pos, int rank) {
		Minecraft.getInstance().setScreen(new ChooseRankScreen(pos, rank));
	}

	@Override
	protected void init() {
		super.init();

		int centerWidth = (this.width) / 2;
		int centerHeight = (this.height) / 2;

		this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, b -> this.minecraft.setScreen((Screen) null))
				.bounds(centerWidth - 65, centerHeight, 60, 20).build());

		this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> {
			updateBlock();
			this.minecraft.setScreen((Screen) null);
		}).bounds(centerWidth + 5, centerHeight, 60, 20).build());

		this.rankField = new NumberFieldBox(this.font, centerWidth - 15, centerHeight - 35, 30, 20, Component.translatable("murderleaderboard.screen.rank_text"));
		this.rankField.setValue(String.valueOf(originalRank));
		this.rankField.setMaxLength(4);
		this.addWidget(this.rankField);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);

		this.rankField.render(guiGraphics, mouseX, mouseY, partialTicks);

		int centerWidth = (this.width) / 2;
		int centerHeight = (this.height) / 2;
		String title = "Choose Rank";

		guiGraphics.drawString(font, title, centerWidth - (this.font.width(title) / 2), centerHeight - 70, 16777215, false);

		super.render(guiGraphics, mouseX, mouseY, partialTicks);
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		return super.charTyped(codePoint, modifiers);
	}

	private void updateBlock() {
		int rank = rankField.getInt();
		PacketHandler.CHANNEL.send(PacketDistributor.SERVER.noArg(), new ChooseRankMessage(position, rank));
	}
}
