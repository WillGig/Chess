package objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import game.Game;
import utils.Texture;

public class CheckBox extends Button{
	
	private String label;
	
	private boolean checked;

	private Fill fill;
	
	public CheckBox(double x, double y, String label) {
		super(x, y, 20, 20, "");
		this.label = label;
		checked = false;
		fill = new Fill(x, y);
		
		int[] pixels = new int[width*height];
		for(int i = 0; i < width * height; i++)
			pixels[i] = 0xffffffff;
		
		image = new Texture(width, height, pixels);
	}
	
	@Override
	public void render(int[] pixels) {
		super.render(pixels);
		
		if(checked)
			fill.render(pixels);
	}
	
	@Override
	public void renderText(Graphics g)
	{
		g.setColor(Color.WHITE);
		Font font = new Font("Times", 0, (int)(16*Game.SCALE));
		g.setFont(font);
		g.drawString(label, (int) ((x - 340)*Game.SCALE), (int) ((y + 5)*Game.SCALE));
	}
	
	@Override
	public void update()
	{
		super.update();
		if(IsClicked())
			checked = !checked;
			
	}
	
	public boolean isChecked()
	{
		return checked;
	}
	
	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}
	
	private class Fill extends GameObject {
		
		public Fill(double x, double y) {
			super(x, y, 15, 15);
			int[] pixels = new int[width*height];
			for(int i = 0; i < width * height; i++)
				pixels[i] = 0xffaaaaaa;
			image = new Texture(width, height, pixels);
		}
		
		@Override
		public void update() {}
		
	}

}
