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
	public boolean nextField;
	private String text, label;
	
	private int numLines, maxChars, maxCharsPerLine, blinkTimer;
	
	public TextField(double x, double y, int width, int height, String label) 
	{
		super(x, y, width, height);
		
		text = "";
		this.label = label;
		fontSize = 16;
		numLines = 1;
		maxCharsPerLine = (int)(width / ((float)fontSize * .5));
		maxChars = (height/fontSize) * maxCharsPerLine;
		blinkTimer = 0;
		
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
		numLines = 1;
		maxCharsPerLine = width / (fontSize/2);
		maxChars = (height/fontSize) * maxCharsPerLine;
		blinkTimer = 0;
		
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
		maxCharsPerLine = (int)(width / ((float)fontSize * .5));
		maxChars = (height/fontSize) * maxCharsPerLine;
		
		if(InputHandler.MouseClicked(1))
			setSelected(false);
		
		if(containsCursor() && InputHandler.MouseClicked(1))
			setSelected(true);
		
		if(selected)
		{
			blinkTimer++;
			
			if(InputHandler.KEYPRESSED != null)
			{
				if(InputHandler.KEYPRESSED == KeyEvent.VK_BACK_SPACE)
				{
					if(text.length() > 0)
					{
						if(text.charAt(text.length()-1) == '\n')
							numLines--;
						text = text.substring(0, text.length()-1);
					}
				}
				else if(InputHandler.KEYPRESSED == KeyEvent.VK_ENTER)
				{
					if(height > (numLines + 1)*fontSize)
					{
						text += "\n";
						numLines++;
					}
					else
					{
						setSelected(false);
						nextField = true;
					}
				}
				else if(InputHandler.KeyPressedAndSetFalse(KeyEvent.VK_TAB))
				{
					setSelected(false);
					nextField = true;
				}
				else if(text.length() < maxChars)
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
		
		String line = text;
		while(line.length() > maxCharsPerLine)
		{
			String splitLine = line.substring(0,maxCharsPerLine);
			
			int endIndex;
			
			if(splitLine.contains("\n"))
				endIndex = splitLine.indexOf('\n');
			else if(splitLine.contains(" "))
				endIndex = splitLine.lastIndexOf(' ');
			else
				endIndex = maxCharsPerLine;
				
			splitLine = splitLine.substring(0, endIndex);
            g.drawString(splitLine, xPos, yPos);
            line = line.substring(endIndex).trim();
            yPos += fontSize*Game.SCALE;
		}
		g.drawString(line, xPos, yPos);
		if(selected && blinkTimer % 60 < 30)
			g.drawString("|", xPos + g.getFontMetrics().stringWidth(line), yPos);
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
	
	public void setSelected(boolean s)
	{
		selected = s;
		
		if(s)
		{
			setBorderColor(0xffaaaaaa);
			InputHandler.KEYPRESSED = null;
		}
		else
			setBorderColor(0xff555555);
	}
}
