package scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import game.Game;
import objects.Button;
import objects.CheckBox;
import objects.RGBSlider;
import objects.Slider;
import utils.SaveLoadManager;
import utils.Sound;

public class Settings extends Scene{

	private RGBSlider darkColor, lightColor;
	private CheckBox showFPS, capFPS, showCoords, swapOnMove, darkMode;
	private Slider sound;
	
	private Button returnToMenu;
	
	@Override
	public void update(Game game) {
		darkColor.update();
		lightColor.update();
		showFPS.update();
		Game.SHOWFPS = showFPS.isChecked();
		capFPS.update();
		Game.CAPFPS = capFPS.isChecked();
		showCoords.update();
		swapOnMove.update();
		sound.update();
		
		darkMode.update();
		if(darkMode.IsClicked())
		{
			game.setDarkMode(darkMode.isChecked());
			updateColor();
		}
		
		returnToMenu.update();
		if(returnToMenu.IsClicked())
		{
			saveSettings();
			game.SetScene(0);
		}
	}

	@Override
	public void render(int[] pixels) {
		darkColor.render(pixels);
		lightColor.render(pixels);
		showFPS.render(pixels);
		capFPS.render(pixels);
		showCoords.render(pixels);
		swapOnMove.render(pixels);
		darkMode.render(pixels);
		sound.render(pixels);
		returnToMenu.render(pixels);
	}

	@Override
	public void renderText(Graphics g) {
		darkColor.renderText(g);
		lightColor.renderText(g);
		showFPS.renderText(g);
		capFPS.renderText(g);
		showCoords.renderText(g);
		swapOnMove.renderText(g);
		darkMode.renderText(g);
		sound.renderText(g);
		returnToMenu.renderText(g);
		
		Font font = new Font("Bell", 1, (int)(60*Game.SCALE));
		g.setFont(font);
		g.setColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
		int textWidth = g.getFontMetrics(font).stringWidth("SETTINGS");
		g.drawString("SETTINGS", (int) (Game.WIDTH*Game.SCALE/2) - textWidth/2 + Game.XOFF, (int) ((Game.HEIGHT/2 - 150)*Game.SCALE) + Game.YOFF);
	}

	@Override
	public void start() {
		darkColor = new RGBSlider(Game.WIDTH/2, Game.HEIGHT/2 - 80, 200, "Dark Squares", 0xff663400);
		darkColor.setValue(Chess.DARKCOLOR);
		lightColor = new RGBSlider(Game.WIDTH/2, Game.HEIGHT/2 - 50, 200, "Light Squares", 0xffFFE7BC);
		lightColor.setValue(Chess.LIGHTCOLOR);
		showFPS = new CheckBox(Game.WIDTH/2 + 150, Game.HEIGHT/2 - 20, "Show FPS");
		showFPS.setChecked(Game.SHOWFPS);
		capFPS = new CheckBox(Game.WIDTH/2 + 150, Game.HEIGHT/2 + 10, "Cap FPS");
		capFPS.setChecked(Game.CAPFPS);
		showCoords = new CheckBox(Game.WIDTH/2 + 150, Game.HEIGHT/2 + 40, "Show Coordinates");
		showCoords.setChecked(Chess.SHOWCOORDS);
		swapOnMove = new CheckBox(Game.WIDTH/2 + 150, Game.HEIGHT/2 + 70, "Flip Board After Moving");
		swapOnMove.setChecked(Chess.FLIPONMOVE);
		darkMode = new CheckBox(Game.WIDTH/2 + 150, Game.HEIGHT/2 + 100, "Dark Mode");
		darkMode.setChecked(Game.DARKMODE);
		sound = new Slider(Game.WIDTH/2, Game.HEIGHT/2 + 130, 200, "Sound", 0.0f, 1.0f);
		sound.setValue(Sound.VOLUME);
		
		returnToMenu = new Button(Game.WIDTH/2, Game.HEIGHT * .8, 150, 50, "RETURN");
		
		updateColor();
	}
	
	private void saveSettings()
	{
		Chess.DARKCOLOR = darkColor.getValue();
		Chess.LIGHTCOLOR = lightColor.getValue();
		Chess.SHOWCOORDS = showCoords.isChecked();
		Chess.FLIPONMOVE = swapOnMove.isChecked();
		Sound.VOLUME = sound.getValue();
		SaveLoadManager.saveSettings();
	}
	
	private void updateColor()
	{
		darkColor.setTextColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
		lightColor.setTextColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
		showFPS.setTextColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
		capFPS.setTextColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
		showCoords.setTextColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
		swapOnMove.setTextColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
		darkMode.setTextColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
		sound.setTextColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
	}

}
