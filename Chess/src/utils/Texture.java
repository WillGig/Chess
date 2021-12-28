package utils;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Texture {
	
	private static HashMap<String, Texture> textures;
	
	public int width, height;
	public int[] pixels;
	
	public Texture(int w, int h, int[] p)
	{
		width = w;
		height = h;
		pixels = p;
	}
	
	public Texture(String fileName)
	{
		try 
		{
			BufferedImage image = ImageIO.read(Texture.class.getResource(fileName));

			width = image.getWidth();
			height = image.getHeight();
			pixels = new int[width * height];
			image.getRGB(0, 0, width, height, pixels, 0, width);
		} 
		catch (Exception e) 
		{
			System.err.println("Error loading file:" + fileName);
		}
	}
	
	public static Texture GetTexture(String name)
	{
		if(textures.containsKey(name))
			return textures.get(name);
		System.out.println("Error finding texture: " + name);
		return null;
	}
	
	public static void LoadAllTextures()
	{
		textures = new HashMap<String, Texture>();
		
		SpriteSheet pieces = new SpriteSheet("/pieces.png", 64, 64);
		
		textures.put("PawnWhite", 	pieces.load(0, 0));
		textures.put("KnightWhite", pieces.load(1, 0));
		textures.put("BishopWhite", pieces.load(2, 0));
		textures.put("RookWhite", 	pieces.load(3, 0));
		textures.put("QueenWhite", 	pieces.load(4, 0));
		textures.put("KingWhite", 	pieces.load(5, 0));
		textures.put("PawnBlack", 	pieces.load(0, 1));
		textures.put("KnightBlack", pieces.load(1, 1));
		textures.put("BishopBlack", pieces.load(2, 1));
		textures.put("RookBlack", 	pieces.load(3, 1));
		textures.put("QueenBlack", 	pieces.load(4, 1));
		textures.put("KingBlack", 	pieces.load(5, 1));
	}
}

