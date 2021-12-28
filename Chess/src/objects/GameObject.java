package objects;
import java.awt.Graphics;

import utils.InputHandler;
import utils.Texture;

public abstract class GameObject 
{

	protected double x, y;
	protected int width, height;
	
	protected Texture image;
	
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
		image.render(xPos, yPos, pixels);
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
	
	public Texture getImage() 
	{
		return image;
	}
	
	public void setImage(Texture image) 
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
