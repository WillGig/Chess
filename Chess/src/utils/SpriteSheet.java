package utils;

public class SpriteSheet {
	
	int spriteWidth, spriteHeight;
	Texture image;

	public SpriteSheet(String fileName, int spriteWidth, int spriteHeight){
		this.spriteWidth = spriteWidth;
		this.spriteHeight = spriteHeight;
		image = new Texture(fileName);
	}
	
	public Texture load(int xOff, int yOff){
		int[] data = new int[spriteWidth*spriteHeight];
		for(int y = 0; y < spriteHeight; y++){
			for(int x = 0; x < spriteWidth; x++){
				int a = (image.pixels[x+xOff*spriteWidth+(y+yOff*spriteHeight)*image.width] & 0xff000000) >> 24;
				int r = (image.pixels[x+xOff*spriteWidth+(y+yOff*spriteHeight)*image.width] & 0xff0000) >> 16;
				int g = (image.pixels[x+xOff*spriteWidth+(y+yOff*spriteHeight)*image.width] & 0xff00) >> 8;
				int b = (image.pixels[x+xOff*spriteWidth+(y+yOff*spriteHeight)*image.width] & 0xff);
				data[x + y * spriteWidth] = a << 24 | b << 16 | g << 8 | r; 
				
//				data[x + y * spriteWidth] = image.pixels[x + xOff*spriteWidth + (y+yOff*spriteHeight) * image.width];
			}
		}
		
		return new Texture(spriteWidth, spriteHeight, data);
	}
}

