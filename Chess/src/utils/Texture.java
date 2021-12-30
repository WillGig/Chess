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
	public int[] data;
	
	public Texture(int w, int h, int[] p)
	{
		width = w;
		height = h;
		data = p;
	}
	
	public Texture(String fileName)
	{
		try 
		{
			BufferedImage bi = ImageIO.read(Texture.class.getResource(fileName));

			width = bi.getWidth();
			height = bi.getHeight();
			data = new int[width * height];
			bi.getRGB(0, 0, width, height, data, 0, width);
		} 
		catch (Exception e) 
		{
			System.err.println("Error loading file:" + fileName);
		}
	}
	
	public void render(double xPos, double yPos, int[] pixels)
	{
		if(data == null)
			return;
		
		for(int i = (int) (yPos - height/2); i < (int)(yPos+height/2); i++) 
		{
			for(int j = (int) (xPos - width/2); j < (int)(xPos+width/2); j++) 
			{
				if(j > 0 && j < Game.WIDTH && i > 0 && i < Game.HEIGHT) 
				{
					
					int color = data[(j-(int)(xPos - width/2))+(i-(int)(yPos - height/2))*width];
					
					if(color != 0)
					{
						if((color >> 24 & 0xff) == 255)
							pixels[j+i*Game.WIDTH] = color;
						else
							pixels[j+i*Game.WIDTH] = blend(color, pixels[j+i*Game.WIDTH]);
					}
				}
			}
		}
	}
	
	public static int blend(int color1, int color2)
	{
		float a1 = (color1 >> 24 & 0xff)/255.0f;
		float r1 = ((color1 & 0xff0000) >> 16)/255.0f;
		float g1 = ((color1 & 0xff00) >> 8)/255.0f;
		float b1 = (color1 & 0xff)/255.0f;

		float a2 = (color2 >> 24 & 0xff)/255.0f;
		float r2 = ((color2 & 0xff0000) >> 16)/255.0f;
		float g2 = ((color2 & 0xff00) >> 8)/255.0f;
		float b2 = (color2 & 0xff)/255.0f;

	    float a = (1 - a1)*a2 + a1;
	    float r = ((1 - a1)*a2*r2 + a1*r1) / a;
	    float g = ((1 - a1)*a2*g2 + a1*g1) / a;
	    float b = ((1 - a1)*a2*b2 + a1*b1) / a;
		
		return (int)(a*255) << 24 | (int)(r*255) << 16 | (int)(g*255) << 8 | (int)(b*255);
	}
	
	public static Texture getTexture(String name)
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

