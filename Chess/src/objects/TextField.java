package objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import game.Game;
import utils.InputHandler;
import utils.Texture;

public class TextField extends GameObject{

	private int fontSize, borderColor, fillColor, borderWidth;
	
	private boolean selected;
	private String text, label;
	
	public TextField(double x, double y, int width, int height, String label) 
	{
		super(x, y, width, height);
		
		text = "";
		this.label = label;
		fontSize = 16;
		
		borderWidth = 3;
		borderColor = 0xff555555;
		fillColor = 0xffffffff;
		
		int[] pixels = new int[width * height];
		for(int yPix = 0; yPix < height; yPix++)
		{
			for(int xPix = 0; xPix < width; xPix++)
			{
				if(xPix < borderWidth || width-xPix < borderWidth + 1 || yPix < borderWidth || height - yPix < borderWidth + 1)
					pixels[xPix + yPix*width] = borderColor;
				else
					pixels[xPix + yPix*width] = fillColor;
			}
		}
		
		image = new Texture(width, height, pixels);
	}
	
	public TextField(double x, double y, int width, int height, String label, String text)
	{
		super(x, y, width, height);
		
		this.text = text;
		this.label = label;
		fontSize = 16;
		
		borderWidth = 3;
		borderColor = 0xff555555;
		fillColor = 0xffffffff;
		
		int[] pixels = new int[width * height];
		for(int yPix = 0; yPix < height; yPix++)
		{
			for(int xPix = 0; xPix < width; xPix++)
			{
				if(xPix < borderWidth || width-xPix < borderWidth + 1 || yPix < borderWidth || height - yPix < borderWidth + 1)
					pixels[xPix + yPix*width] = borderColor;
				else
					pixels[xPix + yPix*width] = fillColor;
			}
		}
		
		image = new Texture(width, height, pixels);
	}

	@Override
	public void update() 
	{
		if(InputHandler.MouseClicked(1))
		{
			selected = false;
			setBorderColor(0xff555555);
		}
		
		if(containsCursor() && InputHandler.MouseClicked(1))
		{
			selected = true;
			setBorderColor(0xffaaaaaa);
		}
		
		if(selected)
		{
			if(InputHandler.KEYPRESSED != null)
			{
				if(InputHandler.KEYPRESSED == KeyEvent.VK_BACK_SPACE)
				{
					if(text.length() > 0)
						text = text.substring(0, text.length()-1);
				}
				else
					text += InputHandler.KEYPRESSED;
				InputHandler.KEYPRESSED = null;
			}
		}
	}
	
	public void renderText(Graphics g)
	{
		int yPos = (int)((y - height/2 + 20) * Game.SCALE) + Game.YOFF;
		int xPos = (int)((x - width/2 + 10) * Game.SCALE) + Game.XOFF;
		
		g.setFont(new Font("Arial", 1, (int)(fontSize*Game.SCALE)));
		g.setColor(Color.WHITE);
		g.drawString(label, xPos - 80, yPos);
		
		g.setFont(new Font("Arial", 0, (int)(fontSize*Game.SCALE)));
		g.setColor(Color.BLACK);
		for(String line : text.split("\n"))
		{
			g.drawString(line, xPos, yPos);
			yPos += fontSize*Game.SCALE;
		}
	}
	
	private void setBorderColor(int color)
	{
		borderColor = color;
		for(int yPix = 0; yPix < height; yPix++)
		{
			for(int xPix = 0; xPix < width; xPix++)
			{
				if(xPix < borderWidth || width-xPix < borderWidth + 1 || yPix < borderWidth || height - yPix < borderWidth + 1)
					image.data[xPix + yPix*width] = borderColor;
				else
					image.data[xPix + yPix*width] = fillColor;
			}
		}
	}
	
	public void setFillColor(int color)
	{
		fillColor = color;
		for(int yPix = 0; yPix < height; yPix++)
		{
			for(int xPix = 0; xPix < width; xPix++)
			{
				if(xPix < borderWidth || width-xPix < borderWidth + 1 || yPix < borderWidth || height - yPix < borderWidth + 1)
					image.data[xPix + yPix*width] = borderColor;
				else
					image.data[xPix + yPix*width] = fillColor;
			}
		}
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public String getText()
	{
		return text;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}
}
