package objects;
import java.awt.Graphics;

import game.Game;
import utils.InputHandler;

public abstract class GameObject 
{

	protected double x, y;
	protected int width, height;
	
	protected int[] image;
	
	public GameObject(double x, double y, int width, int height) 
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public abstract void update();
	
	public void render(int[] pixels) 
	{
		renderAtPosition(x, y, pixels);
	}
	
	public void renderAtPosition(double xPos, double yPos, int[] pixels)
	{
		if(image == null)
			return;
		
		for(int i = (int) (yPos - height/2); i < (int)(yPos+height/2); i++) 
		{
			for(int j = (int) (xPos - width/2); j < (int)(xPos+width/2); j++) 
			{
				if(j > 0 && j < Game.WIDTH && i > 0 && i < Game.HEIGHT) 
				{
					
					int color = image[(j-(int)(xPos - width/2))+(i-(int)(yPos - height/2))*width];
					
					if(color != 0)
						pixels[j+i*Game.WIDTH] = color;
				}
			}
		}
	}
	
	public void renderText(Graphics g) 
	{
		
	}
	
	public void setX(double x)
	{
		this.x = x;
	}
	
	public void setY(double y)
	{
		this.y = y;
	}
	
	public double getX() 
	{
		return x;
	}
	
	public double getY() 
	{
		return y;
	}
	
	public int getWidth() 
	{
		return width;
	}
	
	public int getHeight() 
	{
		return height;
	}
	
	public int[] getImage() 
	{
		return image;
	}
	
	public void setImage(int[] image) 
	{
		this.image = image;
	}
	
	public double getDistance(double xPos, double yPos) 
	{
		return Math.sqrt(Math.pow(x-xPos, 2)+Math.pow(y-yPos, 2));
	}
	
	public boolean collides(GameObject other) 
	{
		double oX = other.getX();
		double oY = other.getY();
		double oW = other.getWidth();
		double oH = other.getHeight();
		return !(x + width/2 < oX - oW/2 || x - width/2 > oX+oW/2 || y + height/2 < oY - oH/2 || y - height/2 > oY + oH/2);
	}
	
	public boolean ContainsCursor()
	{
		int cX = InputHandler.MOUSEX;
		int cY = InputHandler.MOUSEY;
		return cX > x - width/2 && cX < x + width/2 && cY > y - height/2 && cY < y + height/2;
	}
}
