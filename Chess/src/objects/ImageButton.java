package objects;

import utils.Texture;

public class ImageButton extends Button{
	
	public ImageButton(double x, double y, int width, int height, String image) {
		super(x, y, width, height, "");
		
		this.image = Texture.GetTexture(image);
	}
	
	

}
