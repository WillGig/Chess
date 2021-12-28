package objects;

import utils.Texture;

public class ImageButton extends Button{
	
	Texture img;
	
	public ImageButton(double x, double y, int width, int height, String image) {
		super(x, y, width, height, "");
		
		img = Texture.GetTexture(image);
	}
	
	@Override
	public void render(int[] pixels)
	{
		super.render(pixels);
		img.render(x, y, pixels);
	}

}
