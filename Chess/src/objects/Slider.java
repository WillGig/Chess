package objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import game.Game;
import utils.InputHandler;
import utils.Texture;

public class Slider extends GameObject
{
	protected SliderBall ball;
	
	//value based on position of slider from minValue to maxValue
	protected float value;

	private float minValue;

	private float maxValue;
	
	protected String label;
	
	public Slider(double x, double y, int width, String label, float minValue, float maxValue)
	{
		super(x, y, width, 10);
		value = minValue;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.label = label;
		ball = new SliderBall(x - width/2, y, width);
		
		int[] pixels = new int[width*height];
		
		for(int i = 0; i < width * height; i++)
			pixels[i] = 0xffffffff;
		
		image = new Texture(width, height, pixels);
	}

	@Override
	public void update() {
		ball.update();
		value = (float) ((ball.getX() + width/2 - x) / width) * (maxValue - minValue) + minValue;
	}
	
	@Override
	public void render(int[] pixels)
	{
		super.render(pixels);
		ball.render(pixels);
	}
	
	@Override
	public void renderText(Graphics g)
	{
		g.setColor(Color.WHITE);
		Font font = new Font("Times", 0, (int)(16*Game.SCALE));
		g.setFont(font);
		g.drawString(label, (int) ((x - 250)*Game.SCALE) + Game.XOFF, (int) ((y + 5)*Game.SCALE) + Game.YOFF);
		g.drawString(String.format("%.3g%n", value), (int) ((x + width/2 + 35)*Game.SCALE + Game.XOFF), (int) ((y + 5)*Game.SCALE + Game.YOFF));
	}
	
	public float getValue()
	{
		return value;
	}
	
	public void setValue(float value)
	{
		this.value = value;
		ball.x = (((value-minValue)/(maxValue-minValue))*width) + x - width/2;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public void setColor(int color)
	{
		for(int i = 0; i < image.data.length; i++)
			image.data[i] = color;
	}
	
	class SliderBall extends GameObject
	{
		private double minX, maxX;
		private boolean dragging;
		
		public SliderBall(double x, double y, int length) {
			super(x, y, 20, 20);
			
			minX = x;
			maxX = x + length;
			
			int[] pixels = new int[width*height];
			
			for(int i = 0; i < width * height; i++)
				pixels[i] = 0xffaaaaaa;
			
			image = new Texture(width, height, pixels);
			
			dragging = false;
		}

		@Override
		public void update() 
		{
			if(dragging)
			{
				if(!InputHandler.MouseClicked(1))
				{
					dragging = false;
					InputHandler.DRAGGING = false;
				}
				else
				{
					x = InputHandler.MOUSEX;
					if(x < minX)
						x = minX;
					else if(x > maxX)
						x = maxX;
				}
			}
			else if(containsCursor())
			{
				if(InputHandler.MouseClicked(1) && ! InputHandler.DRAGGING)
				{
					dragging = true;
					InputHandler.DRAGGING = true;
				}
					
			}
		}
	}

}
