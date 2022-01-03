package objects;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import utils.InputHandler;

public class DropDown extends Button{

	private ArrayList<Button> options;
	
	boolean showingOptions;
	
	public DropDown(double x, double y, int width, int height, String text) 
	{
		super(x, y, width, height, text);
		
		options = new ArrayList<Button>();
		addOption(new Button(x, y, width, height, text));
		
		setFillColor(0xffffffff);
		setBorderColor(0xff555555);
		setBorderWidth(3);
		setFontSize(16);
		setFontStyle(0);
		
		showingOptions = false;
	}

	@Override
	public void update() 
	{
		if(showingOptions)
		{
			for(Button b : options)
			{
				b.update();
				if(b.IsClicked())
				{
					image = b.getImage();
					setText(b.getText());
					showingOptions = false;
				}
			}
			
			if(InputHandler.MouseClicked(1))
				showingOptions = false;
		}
		else if(containsCursor() && InputHandler.MouseClickedAndSetFalse(1))
			showingOptions = true;
	}
	
	@Override
	public void render(int[] pixels)
	{
		super.render(pixels);
		
		if(showingOptions)
			for(Button b : options)
				b.render(pixels);
	}
	
	@Override
	public void renderText(Graphics g)
	{
		if(!showingOptions)
			super.renderText(g);
		else
			for(Button b : options)
				b.renderText(g);
	}
	
	public void addOption(Button option)
	{
		option.setX(x);
		option.setY(y + height * options.size());
		option.setFillColor(getColor());
		option.setHighlightColor(Color.GRAY);
		option.setFillColor(0xffffffff);
		option.setBorderColor(0xff555555);
		option.setBorderWidth(3);
		option.setFontSize(getFontSize());
		option.setFontStyle(getFontStyle());
		options.add(option);
	}
	
	public void setFillColor(int color)
	{
		super.setFillColor(color);
		
		for(Button b : options)
			b.setFillColor(color);
	}
	
	public boolean isShowingOptions()
	{
		return showingOptions;
	}
	
	@Override
	public void setFontSize(int size)
	{
		super.setFontSize(size);
		
		for(Button b : options)
			b.setFontSize(size);
	}
	
	@Override
	public void setFontStyle(int style)
	{
		super.setFontStyle(style);
		
		for(Button b : options)
			b.setFontStyle(style);
	}
}
