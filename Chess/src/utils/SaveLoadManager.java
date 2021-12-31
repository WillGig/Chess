package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import game.Game;
import scenes.Chess;

public class SaveLoadManager {
	
	private static Properties settings = new Properties();
	
	public static void saveVariable(String key, int value, String path) 
	{
		saveVariable(key, Integer.toString(value), path);
	}
	
	public static void saveVariable(String key, boolean value, String path)
	{
		if(value)
			saveVariable(key, "1", path);
		else
			saveVariable(key, "0", path);
	}

	public static void saveVariable(String key, String value, String path)
	{
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream write = new FileOutputStream(path);
			settings.setProperty(key, value);
			settings.storeToXML(write, path);
			write.close();
		} catch (Exception localException) {
		}
	}
	
	public static void saveSettings()
	{
		String path = "res/settings.xml";
		
		saveVariable("showFPS", Game.SHOWFPS, path);
		saveVariable("fpsCap", Game.CAPFPS, path);
		saveVariable("darkColor", Chess.DARKCOLOR, path);
		saveVariable("lightColor", Chess.LIGHTCOLOR, path);
		saveVariable("showCoords", Chess.SHOWCOORDS, path);
		saveVariable("flipOnMove", Chess.FLIPONMOVE, path);
		saveVariable("volume", (int)(Sound.VOLUME*100), path);
	}
	
	public static void loadSettings()
	{
		try
		{
			InputStream read = new FileInputStream("res/settings.xml");
			settings.loadFromXML(read);
			
			Game.SHOWFPS = Integer.parseInt(settings.getProperty("showFPS")) == 1;
			Game.CAPFPS = Integer.parseInt(settings.getProperty("fpsCap")) == 1;
			Chess.DARKCOLOR = Integer.parseInt(settings.getProperty("darkColor"));
			Chess.LIGHTCOLOR =  Integer.parseInt(settings.getProperty("lightColor"));
			Chess.SHOWCOORDS = Integer.parseInt(settings.getProperty("showCoords")) == 1;
			Chess.FLIPONMOVE = Integer.parseInt(settings.getProperty("flipOnMove")) == 1;
			Sound.VOLUME = Integer.parseInt(settings.getProperty("volume"))/100.0f;
			
			read.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("Failed to find settings file. Using default settings.");
		}
		catch (Exception localException){localException.printStackTrace();}
	}
	
}
