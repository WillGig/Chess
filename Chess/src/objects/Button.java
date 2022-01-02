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
	protected Color textColor, highlightColor;
	private int fontSize;
	
	private boolean selected, clicked, textCentered;
	
	public Button(double x, double y, int width, int height, String text) 
	{
		super(x, y, width, height);
		this.text = text;

		int[] pixels = new int[width*height];
		
		for(int i = 0; i < width * height; i++)
			pixels[i] = color;
		
		image = new Texture(width, height, pixels);
		
		textColor = Color.BLACK;
		highlightColor = Color.WHITE;
		fontSize = 20;
		textCentered = true;
	}

	@Override
	public void update() 
	{
		if(!InputHandler.DRAGGING && containsCursor())
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
			g.setColor(highlightColor);
		else
			g.setColor(textColor);
		g.setFont(new Font("Times", 1, (int)(fontSize*Game.SCALE)));
		
		int xPos = (int)(x*Game.SCALE) + Game.XOFF;
		if(textCentered)
			xPos -= g.getFontMetrics().stringWidth(text)/2;
		else
			xPos -= width*Game.SCALE/2;
		
		g.drawString(text, xPos, (int)(((y-4)*Game.SCALE + g.getFontMetrics().getHeight()/2 + Game.YOFF)));
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
	
;	public Color getTextColor()
	{
		return textColor;
	}
	
	public void setTextColor(Color c)
	{
		textColor = c;
	}
	
	public void setHighlightColor(Color c)
	{
		highlightColor = c;
	}
	
	public void setFontSize(int size)
	{
		fontSize = size;
	}
	
	public void setTextCentered(boolean centered)
	{
		textCentered = centered;
	}
	
	public boolean IsClicked()
	{
		return clicked;
	}

}
