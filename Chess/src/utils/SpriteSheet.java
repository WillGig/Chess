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
		for(int y = 0; y < spriteHeight; y++)
			for(int x = 0; x < spriteWidth; x++)
				data[x + y * spriteWidth] = image.image[x + xOff*spriteWidth + (y+yOff*spriteHeight) * image.width];
		
		return new Texture(spriteWidth, spriteHeight, data);
	}
}

