package objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;

import game.Game;
import utils.InputHandler;
import utils.Texture;

public class TextField extends GameObject{

	private int fontSize, borderColor, fillColor, borderWidth, 
	numLines, maxChars, maxCharsPerLine, blinkTimer, cursorIndex;
	
	private boolean selected;
	public boolean nextField;
	private String text, label;
	
	private Color labelColor;
	
	public TextField(double x, double y, int width, int height, String label) 
	{
		super(x, y, width, height);
		
		text = "";
		this.label = label;
		labelColor = Color.WHITE;
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
		labelColor = Color.WHITE;
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
		
		if(containsCursor() && InputHandler.MouseClicked(1) && !InputHandler.DRAGGING)
		{
			if(selected)
				setCursorPosition(getPositionAtClick());
			else
			{
				setSelected(true);
				cursorIndex = text.length();
			}
		}
		else if(InputHandler.MouseClicked(1))
			setSelected(false);
			
		if(selected)
		{
			blinkTimer++;
			
			if(InputHandler.KEYPRESSED != null)
			{
				if(InputHandler.KEYPRESSED == KeyEvent.VK_BACK_SPACE)
				{
					if(cursorIndex > 0)
					{
						if(text.charAt(cursorIndex-1) == '\n')
							numLines--;
							
						text = text.substring(0, cursorIndex-1) + text.substring(cursorIndex);
						cursorIndex--;
					}
				}
				else if(InputHandler.KEYPRESSED == KeyEvent.VK_ENTER)
				{
					if(height > (numLines + 1)*fontSize)
					{
						text = text.substring(0, cursorIndex) + "\n" + text.substring(cursorIndex);
						numLines++;
						cursorIndex++;
					}
					else
					{
						setSelected(false);
						nextField = true;
					}
				}
				else if(text.length() < maxChars)
				{
					text = text.substring(0, cursorIndex) + InputHandler.KEYPRESSED + text.substring(cursorIndex);
					cursorIndex++;
				}
					
				InputHandler.KEYPRESSED = null;
			}
			
			if(InputHandler.KeyPressedAndSetFalse(KeyEvent.VK_RIGHT))
			{
				if(cursorIndex < text.length())
					cursorIndex++;
			}
			else if(InputHandler.KeyPressedAndSetFalse(KeyEvent.VK_LEFT))
			{
				if(cursorIndex > 0)
					cursorIndex--;
			}
			else if(InputHandler.KeyPressedAndSetFalse(KeyEvent.VK_UP))
			{
				CursorPosition pos = getCursorPosition();
				if(pos.lineNumber > 1)
					setCursorPosition(new CursorPosition(pos.x, pos.lineNumber-1));
			}
			else if(InputHandler.KeyPressedAndSetFalse(KeyEvent.VK_DOWN))
			{
				CursorPosition pos = getCursorPosition();
				if(pos.lineNumber < numLines)
					setCursorPosition(new CursorPosition(pos.x, pos.lineNumber+1));
			}
		}
	}
	
	public void renderText(Graphics g)
	{
		int yPos = (int)((y - height/2 + 20) * Game.SCALE) + Game.YOFF;
		int xPos = (int)((x - width/2 + 10) * Game.SCALE) + Game.XOFF;
		
		g.setFont(new Font("Arial", 1, (int)(fontSize*Game.SCALE)));
		g.setColor(labelColor);
		g.drawString(label, xPos - (int)(80*Game.SCALE), yPos);
		
		g.setFont(new Font("Arial", 0, (int)(fontSize*Game.SCALE)));
		g.setColor(Color.BLACK);
		
		String line = text;
		int cursorXPos = -1;
		int cursorYPos = yPos;
		int cursorXIndex = cursorIndex;
		while(line.length() > maxCharsPerLine || line.contains("\n"))
		{
			String splitLine = line;
			if(line.length() > maxCharsPerLine)
				splitLine = line.substring(0,maxCharsPerLine);
			
			int endIndex;
			
			if(splitLine.contains("\n"))
				endIndex = splitLine.indexOf('\n');
			else if(splitLine.contains(" "))
				endIndex = splitLine.lastIndexOf(' ');
			else
				endIndex = maxCharsPerLine;
				
			splitLine = splitLine.substring(0, endIndex);
            g.drawString(splitLine, xPos, yPos);
            
            if(cursorXPos == -1)
            {
            	if(splitLine.length() < cursorXIndex)
            	{
            		cursorXIndex -= splitLine.length()+1;
            		cursorYPos += fontSize*Game.SCALE;
            	}
                else
                	cursorXPos = g.getFontMetrics().stringWidth(splitLine.substring(0, cursorXIndex));
            }
            
            line = line.substring(endIndex+1);
            yPos += fontSize*Game.SCALE;
		}
		g.drawString(line, xPos, yPos);
		if(cursorXPos == -1)
			cursorXPos = g.getFontMetrics().stringWidth(line.substring(0, cursorXIndex));
		
		if(selected && blinkTimer % 60 < 30)
			g.drawString("|", xPos + cursorXPos, cursorYPos);
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
	
	public void setLabelColor(Color c)
	{
		labelColor = c;
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
	
	public class CursorPosition
	{
		public int x, lineNumber;
		
		public CursorPosition(int x, int lineNumber)
		{
			this.x = x;
			this.lineNumber = lineNumber;
		}
	}
	
	private CursorPosition getCursorPosition()
	{
		String line = text;
		int cursorPosition = cursorIndex;
		int lineNumber = 1;
		while(line.length() > maxCharsPerLine || line.contains("\n"))
		{
			String splitLine = line;
			if(line.length() > maxCharsPerLine)
				splitLine = line.substring(0,maxCharsPerLine);
			
			int endIndex;
			
			if(splitLine.contains("\n"))
				endIndex = splitLine.indexOf('\n');
			else if(splitLine.contains(" "))
				endIndex = splitLine.lastIndexOf(' ');
			else
				endIndex = maxCharsPerLine;
				
			splitLine = splitLine.substring(0, endIndex);
            
        	if(splitLine.length() < cursorPosition)
        		cursorPosition -= splitLine.length()+1;
            else
            	return new CursorPosition(cursorPosition, lineNumber);
            
        	lineNumber++;
            line = line.substring(endIndex+1);
		}
		return new CursorPosition(cursorPosition, lineNumber);
	}
	
	private CursorPosition getPositionAtClick()
	{
		int mX = InputHandler.MOUSEX;
		int mY = InputHandler.MOUSEY;
		
		int lineNumber = (int) ((mY - (y-height/2+5))/fontSize)+1;
		
		if(lineNumber < 1)
			lineNumber = 1;
	
		String text = getLine(lineNumber);
		
		Font f = new Font("Arial", 1, (int)(fontSize*Game.SCALE));
		FontRenderContext frc = new FontRenderContext(f.getTransform(), false, true);
		
		for(int i = 0; i < text.length(); i++)
		{
			int xPos = (int) (f.getStringBounds(text.substring(0, i), frc).getWidth()*0.9/Game.SCALE + (x - width/2 + 15));
			if(mX < xPos)
				return new CursorPosition(i, lineNumber);
		}
		
		return new CursorPosition(text.length(), lineNumber);
	}
	
	private String getLine(int ln)
	{
		String line = text;
		int lineNumber = 1;
		while(line.length() > maxCharsPerLine || line.contains("\n"))
		{
			String splitLine = line;
			if(line.length() > maxCharsPerLine)
				splitLine = line.substring(0,maxCharsPerLine);
			
			int endIndex;
			
			if(splitLine.contains("\n"))
				endIndex = splitLine.indexOf('\n');
			else if(splitLine.contains(" "))
				endIndex = splitLine.lastIndexOf(' ');
			else
				endIndex = maxCharsPerLine;
				
			splitLine = splitLine.substring(0, endIndex);
            
        	if(lineNumber == ln)
        		return splitLine;
            
        	lineNumber++;
            line = line.substring(endIndex+1);
		}
		if(lineNumber == ln)
			return line;
		return "";
	}
	
	private void setCursorPosition(CursorPosition cp)
	{
		String line = text;
		int index = 0;
		int lineNumber = 1;
		while(line.length() > maxCharsPerLine || line.contains("\n"))
		{
			String splitLine = line;
			if(line.length() > maxCharsPerLine)
				splitLine = line.substring(0,maxCharsPerLine);
			
			int endIndex;
			
			if(splitLine.contains("\n"))
				endIndex = splitLine.indexOf('\n');
			else if(splitLine.contains(" "))
				endIndex = splitLine.lastIndexOf(' ');
			else
				endIndex = maxCharsPerLine;
				
			splitLine = splitLine.substring(0, endIndex);
            
			if(lineNumber == cp.lineNumber)
			{
				if(cp.x > splitLine.length())
					cursorIndex = splitLine.length() + index;
				else
					cursorIndex = cp.x + index;
				return;
			}
			else
				index += splitLine.length()+1;
			
        	lineNumber++;
            line = line.substring(endIndex+1);
		}
		if(cp.x > line.length())
			cursorIndex = line.length() + index;
		else
			cursorIndex = cp.x + index;
	}
}
