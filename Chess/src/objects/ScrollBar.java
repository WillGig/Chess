package objects;

import utils.InputHandler;
import utils.Texture;

public class ScrollBar extends GameObject{

	//The height of everything that can be scrolled through
	int heightOfScrollMaterial;
	
	private Bar bar;
	
	public ScrollBar(double x, double y, int width, int height, int scrollHeight) 
	{
		super(x, y, width, height);
		setHeightOfScrollMaterial(scrollHeight);
	}

	@Override
	public void update() 
	{
		if(heightOfScrollMaterial > height + 1)
			bar.update();
	}
	
	@Override
	public void render(int[] pixels)
	{
		if(heightOfScrollMaterial > height + 1)
			bar.render(pixels);
	}
	
	//Returns position of scrollbar from 0.0f to 1.0f
	//Returns -1 if the bar is unneeded
	public float getPosition()
	{
		if(bar == null)
			return -1;
		return bar.getValue();
	}
	
	public void setPosition(float position)
	{
		bar.setValue(position);
	}
	
	public void setHeightOfScrollMaterial(int h)
	{
		if(h == heightOfScrollMaterial)
			return;
		heightOfScrollMaterial = h;
		if(heightOfScrollMaterial > height + 1)
		{
			int barHeight = (int) (((float)(height*height))/h);
			if(barHeight < 20)
				barHeight = 20;
			bar = new Bar(x, y - height/2 + barHeight/2, width, barHeight, height);
		}
	}

	private class Bar extends GameObject
	{
		int minY, maxY, color;
		
		boolean dragging;
		
		public Bar(double x, double y, int width, int height, int length)
		{
			super(x, y, width, height);
			dragging = false;
			
			color = 0xffaaaaaa;
			
			int[] pixels = new int[20 * height];
			for(int i = 0; i < pixels.length; i++)
				pixels[i] = color;
			image = new Texture(width, height, pixels);
			
			minY = (int)y;
			maxY = (int)y + length - height;
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
					y = InputHandler.MOUSEY;
					if(y < minY)
						y = minY;
					else if(y > maxY)
						y = maxY;
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
		
		public void setValue(float value)
		{
			y = value * (maxY - minY) + minY;
		}
		
		public float getValue()
		{
			return (float) ((y - minY) / (maxY-minY));
		}
	}
}
