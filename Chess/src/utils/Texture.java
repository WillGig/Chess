package utils;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;

import javax.imageio.ImageIO;

import game.Game;

public class Texture {
	
	private static HashMap<String, Texture> textures;
	
	public int width, height;
	public int[] image;
	
	public Texture(int w, int h, int[] p)
	{
		width = w;
		height = h;
		image = p;
	}
	
	public Texture(String fileName)
	{
		try 
		{
			BufferedImage bi = ImageIO.read(Texture.class.getResource(fileName));

			width = bi.getWidth();
			height = bi.getHeight();
			image = new int[width * height];
			bi.getRGB(0, 0, width, height, image, 0, width);
		} 
		catch (Exception e) 
		{
			System.err.println("Error loading file:" + fileName);
		}
	}
	
	public void render(double xPos, double yPos, int[] pixels)
	{
		if(image == null)
			return;
		
		for(int i = (int) (yPos - height/2); i < (int)(yPos+height/2); i++) 
		{
			for(int j = (int) (xPos - width/2); j < (int)(xPos+width/2); j++) 
			{
				if(j > 0 && j < Game.WIDTH && i > 0 && i < Game.HEIGHT) 
				{
					
					int color = image[(j-(int)(xPos - width/2))+(i-(int)(yPos - height/2))*width];
					
					if(color != 0)
						pixels[j+i*Game.WIDTH] = color;
				}
			}
		}
	}
	
	public static Texture GetTexture(String name)
	{
		if(textures.containsKey(name))
			return textures.get(name);
		System.out.println("Error finding texture: " + name);
		return null;
	}
	
	public static String GetTextureName(Texture tex)
	{
		if(textures.containsValue(tex))
		{
			for (Entry<String, Texture> entry : textures.entrySet()) {
		        if (Objects.equals(tex, entry.getValue())) {
		            return entry.getKey();
		        }
		    }
		}
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

