package objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import game.Game;

public class ColorSlider extends Slider{

	int colorValue, oldColor;
	
	public ColorSlider(double x, double y, int width, String label) {
		super(x, y, width, label, 0, 0);
	}
	
	@Override
	public void update() {
		ball.update();
		
		//slider position from 0 to 1
		float position = (float) ((ball.getX() + width/2 - x) / width);
		
		float r = 0, g = 0, b = 0;
		
		if(position < 1.0f/6.0f)
		{
			r = 1.0f;
			g = position * 6;
			b = 0.0f;
		}
		else if(position < 2.0f/6.0f)
		{
			r = 2.0f - position*6;
			g = 1.0f;
			b = 0.0f;
		}
		else if(position < 3.0f/6.0f)
		{
			r = 0.0f;
			g = 1.0f;
			b = position * 6 - 2;
		}
		else if(position < 4.0f/6.0f)
		{
			r = 0.0f;
			g = 4.0f - position * 6;
			b = 1.0f;
		}
		else if(position < 5.0f/6.0f)
		{
			r = position * 6 - 4;
			g = 0.0f;
			b = 1.0f;
		}
		else
		{
			r = 1.0f;
			g = 0.0f;
			b = 6.0f - position * 6;
		}
		
		colorValue = (int) (r*0xff) * 0x10000 + (int)(g*0xff) * 0x100 + (int)(b*0xff);
		
		if(colorValue != oldColor)
		{
			oldColor = colorValue;
			
			for(int i = 0; i < width * height; i++)
				image.data[i] = 0xff000000 + colorValue;
		}
	}
	
	public int getColorValue()
	{
		return colorValue + 0xff000000;
	}
	
	public void setColorValue(int value)
	{
		colorValue = value - 0xff000000;
		
		float r = (float)(colorValue & 0xff0000)/0xff0000;
		float g = (float)(colorValue & 0x00ff00)/0x00ff00;
		float b = (float)(colorValue & 0x0000ff)/0x0000ff;
		
		float position = 0.0f;
		if(b == 1.0f)
		{
			if(r == 0.0f)
				position = (4.0f - g)/6.0f;
			else
				position = (r + 4.0f)/6.0f;
		}
		else if(g == 1.0f)
		{
			if(r == 0.0f)
				position = (b + 2.0f)/6.0f;
			else
				position = (2.0f - r)/6.0f;
		}
		else
		{
			if(b == 0.0f)
				position = g/6.0f;
			else
				position = 1.0f - b/6.0f;
		}
		
		ball.x = (position*width) + x - width/2;
		
		for(int i = 0; i < width * height; i++)
			image.data[i] = 0xff000000 + colorValue;
	}
	
	@Override
	public void renderText(Graphics g)
	{
		g.setColor(Color.WHITE);
		Font font = new Font("Times", 0, (int)(16*Game.SCALE));
		g.setFont(font);
		g.drawString(label, (int) ((x - 250)*Game.SCALE), (int) ((y + 5)*Game.SCALE));
		g.drawString(Integer.toHexString(colorValue), (int) ((x + width/2 + 20)*Game.SCALE), (int) ((y + 5)*Game.SCALE));
	}

}
