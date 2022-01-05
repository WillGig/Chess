package objects;

import java.awt.Color;

import utils.Texture;

public class RGBASlider extends RGBSlider{
	
	private float alpha;

	private Texture demoBackground;
	
	public RGBASlider(double x, double y, int width, String label, int defaultValue, float alpha) {
		super(x, y, width, label, (int)defaultValue);
		
		demoBackground = new Texture(25, 25, new int[25 * 25]);
		for(int i = 0; i < demoBackground.data.length; i++)
			demoBackground.data[i] = 0xffffffff;

		this.alpha = alpha;
	}

	@Override
	public void render(int[] pixels)
	{
		demoBackground.render(x + 150, y, pixels);
		super.render(pixels);
	}
	
	@Override
	public void update() {
		hexField.update();
		try 
		{
			long value = Long.parseLong(hexField.getText(), 16);
			if(value != getValue())
				setValue(value);
		}
		catch(NumberFormatException ex) {}
		
		r.update();
		g.update();
		b.update();
		
		reset.update();
		if(reset.IsClicked())
			setValue((long)defaultValue);
		
		int color = getValue();
		if(colorDemo.data[0] != color)
		{
			for(int i = 0; i < colorDemo.data.length; i++)
				colorDemo.data[i] = color;
			
			r.setColor(0xff000000 + (color & 0xff0000));
			g.setColor(0xff000000 + (color & 0xff00));
			b.setColor(0xff000000 + (color & 0xff));
			hexField.setText(Integer.toHexString(getValue()));
		}
	}
	
	public void setValue(long value)
	{
		//Restore integer that overflowed
		if(value < 0)
			value = (value - (long)Integer.MIN_VALUE) + (long)Integer.MAX_VALUE+1;
		
		alpha = ((value & 0xff000000) >> 24)/255.0f;
		if(alpha > 1)
			alpha = 1.0f;
		r.setValue(((value & 0xff0000) >> 16)/255.0f);
		g.setValue(((value & 0xff00) >> 8)/255.0f);
		b.setValue((value & 0xff)/255.0f);
		hexField.setText(Integer.toHexString(getValue()));
	}
	
	@Override
	public int getValue()
	{
		return new Color(r.getValue(), g.getValue(), b.getValue(), alpha).getRGB();
	}
	
	public void setAlpha(float alpha)
	{
		this.alpha = alpha;
	}
	
}
