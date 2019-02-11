package org.powerhigh.objects;

import org.powerhigh.graphics.Drawer;
import org.powerhigh.graphics.Interface;
import org.powerhigh.utils.Color;

public class Text extends GameObject {

	Object font;
	Color color;
	String text;

	public Text(int x, int y, String text, Color color) {
		this(x, y, text, color, null);
	}

	public Text(String text) {
		this(0, 0, text);
	}

	public Text(int x, int y, String text) {
		this(x, y, text, Color.BLACK);
	}

	public Text() {
		this("");
	}

	public Text(int x, int y, String text, Color color, Object font) {
		this.x = x;
		this.y = y;
		this.text = text;
		this.color = color;
		this.font = font;
	}

	@Override
	public void paint(Drawer g, Interface source) {
		g.setColor(color);
		if (!text.contains("\n")) {
			width = g.getEstimatedWidth(text);
			g.drawText(x, y + g.getEstimatedHeight(), text);
			height = g.getEstimatedHeight();
		} else {
			width = 0;
			int txtY = y + g.getEstimatedHeight();
			height = g.getEstimatedHeight() * text.split("\n").length;
			for (String str : text.split("\n")) {
				g.drawText(x, txtY, str);
				txtY += g.getEstimatedHeight();
				width = Math.max(width, g.getEstimatedWidth(str));
			}
		}
	}

	public void setText(String txt) {
		text = txt;
	}

	public Object getFont() {
		return font;
	}

	public void setFont(Object font) {
		this.font = font;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	public String getText() {
		return text;
	}

}
