package objects;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import game.Game;
import utils.InputHandler;

public class Button extends GameObject
{

	private String text;
	
	private int color = 0xffaaaaaa;
	private int textColor = 0;
	private Font font;
	
	private int textWidth = -1;
	
	private boolean selected, clicked;
	
	public Button(double x, double y, int width, int height, String text) 
	{
		super(x, y, width, height);
		this.text = text;

		image = new int[width*height];
		
		for(int i = 0; i < width * height; i++)
			image[i] = color;
		
		font = new Font("Times", 1, (int)(20*Game.SCALE));
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
		g.setFont(font);
		
		if(textWidth == -1)
			setFont(font, g);
		
		g.drawString(text, (int)((x - textWidth/2) * Game.SCALE), (int)((y + 8)*Game.SCALE));
	}
	
	public int getColor()
	{
		return color;
	}
	
	public void setColor(int color)
	{
		this.color = color;
		
		for(int i = 0; i < width * height; i++)
			image[i] = color;
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
	
	public void setFont(Font f, Graphics g)
	{
		font = f;
		FontMetrics metrics = g.getFontMetrics(font);
		textWidth = (int) (metrics.stringWidth(text)/Game.SCALE);
	}
	
	public boolean IsClicked()
	{
		return clicked;
	}

}
