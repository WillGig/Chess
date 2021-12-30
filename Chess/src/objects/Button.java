package objects;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import game.Game;
import utils.InputHandler;
import utils.Texture;

public class Button extends GameObject
{

	private String text;
	
	private int color = 0xffaaaaaa;
	private int textColor = 0;
	private int fontSize;
	
	private boolean selected, clicked;
	
	public Button(double x, double y, int width, int height, String text) 
	{
		super(x, y, width, height);
		this.text = text;

		int[] pixels = new int[width*height];
		
		for(int i = 0; i < width * height; i++)
			pixels[i] = color;
		
		image = new Texture(width, height, pixels);
		
		fontSize = 20;
	}

	@Override
	public void update() 
	{
		if(!InputHandler.DRAGGING && ContainsCursor())
		{
			selected = true;
			if(InputHandler.MouseClickedAndSetFalse(1))
				clicked = true;
			else
				clicked = false;
		}
		else 
		{
			selected = false;
			clicked = false;
		}
	}
	
	@Override
	public void renderText(Graphics g)
	{
		if(text.length() == 0)
			return;
		
		if(selected)
			g.setColor(new Color(0xffffff));
		else
			g.setColor(new Color(textColor));
		g.setFont(new Font("Times", 1, (int)(fontSize*Game.SCALE)));
		
		g.drawString(text, (int)((x*Game.SCALE + Game.XOFF - g.getFontMetrics().stringWidth(text)/2)), (int)(((y-4)*Game.SCALE + g.getFontMetrics().getHeight()/2 + Game.YOFF)));
	}
	
	public int getColor()
	{
		return color;
	}
	
	public void setColor(int color)
	{
		this.color = color;
		
		for(int i = 0; i < width * height; i++)
			image.data[i] = color;
	}
	
	public String getText()
	{
		return text;
	}
	
;	public int getTextColor()
	{
		return textColor;
	}
	
	public void setTextColor(int color)
	{
		textColor = color;
	}
	
	public void setFontSize(int size)
	{
		fontSize = size;
	}
	
	public boolean IsClicked()
	{
		return clicked;
	}

}
