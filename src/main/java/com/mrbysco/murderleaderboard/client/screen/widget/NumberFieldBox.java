package com.mrbysco.murderleaderboard.client.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.math.NumberUtils;

public class NumberFieldBox extends EditBox {

	public int maxValue = 360;

	public NumberFieldBox(Font font, int x, int y, int width, int height, Component defaultValue) {
		super(font, x, y, width, height, defaultValue);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void insertText(String textToWrite) {
		if (this.isNumeric(textToWrite)) super.insertText(textToWrite);

		float currentValue = getInt();
		if (currentValue > maxValue || currentValue < 1) {
			this.setValue("1");
		}
	}

	@Override
	public String getValue() {
		return (this.isNumeric(super.getValue()) ? super.getValue() : "1");
	}

	@Override
	public void setValue(String value) {
		super.setValue(String.format("%o", Integer.parseInt(value)));
	}

	public int getInt() {
		return NumberUtils.toInt(super.getValue(), 1);
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		if (!focused) {
			this.setHighlightPos(this.getValue().length());
			this.moveCursorToEnd();
		}
	}

	protected boolean isNumeric(String value) {
		return NumberUtils.isParsable(value);
	}
}
