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
	
	private int borderWidth, fillColor, borderColor, fontSize, style;
	protected Color textColor, highlightColor;
	
	private boolean selected, clicked, textCentered;
	
	public Button(double x, double y, int width, int height, String text) 
	{
		super(x, y, width, height);
		this.text = text;

		textColor = Color.BLACK;
		highlightColor = Color.WHITE;
		fillColor = 0xffaaaaaa;
		borderColor = 0xff555555;
		fontSize = 20;
		style = 1;
		textCentered = true;
		
		int[] pixels = new int[width*height];
		for(int i = 0; i < width * height; i++)
			pixels[i] = fillColor;
		
		image = new Texture(width, height, pixels);
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
		g.setFont(new Font("Times", style, (int)(fontSize*Game.SCALE)));
		
		int xPos = (int)(x*Game.SCALE) + Game.XOFF;
		if(textCentered)
			xPos -= g.getFontMetrics().stringWidth(text)/2;
		else
			xPos -= width*Game.SCALE/2;
		
		g.drawString(text, xPos, (int)(((y-4)*Game.SCALE + g.getFontMetrics().getHeight()/2 + Game.YOFF)));
	}
	
	public int getColor()
	{
		return fillColor;
	}
	
	public void setFillColor(int color)
	{
		this.fillColor = color;
		updateImage();
	}
	
	public void setBorderColor(int color)
	{
		this.borderColor = color;
		updateImage();
	}
	
	public void setBorderWidth(int width)
	{
		borderWidth = width;
		updateImage();
	}
	
	public void updateImage()
	{
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
	
	public int getFontSize()
	{
		return fontSize;
	}
	
	public void setFontSize(int size)
	{
		fontSize = size;
	}
	
	public int getFontStyle()
	{
		return style;
	}
	
	public void setFontStyle(int style)
	{
		this.style = style;
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
