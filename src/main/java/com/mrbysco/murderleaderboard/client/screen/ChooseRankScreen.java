package com.mrbysco.murderleaderboard.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.murderleaderboard.client.screen.widget.NumberFieldBox;
import com.mrbysco.murderleaderboard.network.PacketHandler;
import com.mrbysco.murderleaderboard.network.message.ChooseRankMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.network.PacketDistributor;

public class ChooseRankScreen extends Screen {
	private final BlockPos position;
	private final int originalRank;

	private NumberFieldBox rankField;

	public ChooseRankScreen(BlockPos pos, int rank) {
		super(NarratorChatListener.NO_TITLE);

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

		this.addRenderableWidget(new Button(centerWidth - 65, centerHeight, 60, 20, CommonComponents.GUI_CANCEL, (p_238847_1_) -> {
			this.minecraft.setScreen((Screen) null);
		}));

		this.addRenderableWidget(new Button(centerWidth + 5, centerHeight, 60, 20, CommonComponents.GUI_DONE, (p_238847_1_) -> {
			updateBlock();
			this.minecraft.setScreen((Screen) null);
		}));

		this.rankField = new NumberFieldBox(this.font, centerWidth - 15, centerHeight - 35, 30, 20, new TranslatableComponent("murderleaderboard.screen.rank_text"));
		this.rankField.setValue(String.valueOf(originalRank));
		this.rankField.setMaxLength(4);
		this.addWidget(this.rankField);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(poseStack);

		this.rankField.render(poseStack, mouseX, mouseY, partialTicks);

		int centerWidth = (this.width) / 2;
		int centerHeight = (this.height) / 2;
		String title = "Choose Rank";

		this.font.draw(poseStack, title, centerWidth - (this.font.width(title) / 2), centerHeight - 70, 16777215);

		super.render(poseStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public void tick() {
		super.tick();
		this.rankField.tick();
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
