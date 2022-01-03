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
		options.add(new Button(x, y, width, height, text));
		setColor(0xffffffff);
		
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
		option.setColor(getColor());
		option.setHighlightColor(Color.GRAY);
		options.add(option);
	}
	
	public void setColor(int color)
	{
		super.setColor(color);
		
		for(Button b : options)
			b.setColor(color);
	}
	
	public boolean isShowingOptions()
	{
		return showingOptions;
	}
}
