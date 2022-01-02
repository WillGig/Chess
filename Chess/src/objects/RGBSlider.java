package objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import game.Game;
import utils.Texture;

public class RGBSlider extends GameObject{

	private String label;
	
	private Slider r, g, b;
	
	private Texture colorDemo;
	
	private int defaultValue;
	
	private Color textColor;
	
	private Button reset;
	
	private TextField hexField;
	
	public RGBSlider(double x, double y, int width, String label, int defaultValue)
	{
		super(x, y, width, 10);
		this.label = label;
		this.defaultValue = defaultValue;
		r = new Slider(x - width/3, y, width/4, "", 0.0f, 1.0f);
		g = new Slider(x, y, width/4, "", 0.0f, 1.0f);
		b = new Slider(x + width/3, y, width/4, "", 0.0f, 1.0f);
		
		colorDemo = new Texture(25, 25, new int[25 * 25]);
		
		reset = new Button(Game.WIDTH/2 + 337, y, 80, 20, "Reset");
		reset.setFontSize(12);
		
		hexField = new TextField(x+230, y, 80, 26, "");
		
		setValue(defaultValue);
		
		textColor = Color.WHITE;
	}

	@Override
	public void update() {
		hexField.update();
		try 
		{
			int value = Integer.parseInt(hexField.getText(), 16);
			if(value != getValue()-0xff000000)
				setValue(value + 0xff000000);
		}
		catch(NumberFormatException ex) {}
		
		r.update();
		g.update();
		b.update();
		
		reset.update();
		if(reset.IsClicked())
			setValue(defaultValue);
		
		int color = getValue();
		if(colorDemo.data[0] != color)
		{
			for(int i = 0; i < colorDemo.data.length; i++)
				colorDemo.data[i] = color;
			
			r.setColor(0xff000000 + (color & 0xff0000));
			g.setColor(0xff000000 + (color & 0xff00));
			b.setColor(0xff000000 + (color & 0xff));
			hexField.setText(Integer.toHexString(getValue()-0xff000000));
		}
	}
	
	@Override
	public void render(int[] pixels)
	{
		r.render(pixels);
		g.render(pixels);
		b.render(pixels);
		colorDemo.render(x + 150, y, pixels);
		hexField.render(pixels);
		reset.render(pixels);
	}
	
	@Override
	public void renderText(Graphics g)
	{
		g.setColor(textColor);
		g.setFont(new Font("Times", 0, (int)(16*Game.SCALE)));
		g.drawString(label, (int) ((x - 250)*Game.SCALE  + Game.XOFF), (int) ((y + 5)*Game.SCALE + Game.YOFF));
		
		hexField.renderText(g);
		reset.renderText(g);
	}
	
	public int getValue()
	{
		return new Color(r.getValue(), g.getValue(), b.getValue()).getRGB();
	}
	
	public void setValue(int value)
	{
		r.setValue(((value & 0xff0000) >> 16)/255.0f);
		g.setValue(((value & 0xff00) >> 8)/255.0f);
		b.setValue((value & 0xff)/255.0f);
		hexField.setText(Integer.toHexString(getValue()-0xff000000));
	}
	
	public void setTextColor(Color c)
	{
		textColor = c;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public TextField getHexField()
	{
		return hexField;
	}

}
