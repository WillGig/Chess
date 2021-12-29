package scenes;

import java.awt.Graphics;

import game.Game;
import objects.Button;
import objects.CheckBox;
import objects.RGBSlider;

public class Settings extends Scene{

	private RGBSlider darkColor, lightColor;
	private CheckBox showFPS, showCoords;
	
	private Button returnToMenu;
	
	@Override
	public void update(Game game) {
		darkColor.update();
		lightColor.update();
		showFPS.update();
		Game.SHOWFPS = showFPS.isChecked();
		showCoords.update();
		
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
		showCoords.render(pixels);
		returnToMenu.render(pixels);
	}

	@Override
	public void renderText(Graphics g) {
		darkColor.renderText(g);
		lightColor.renderText(g);
		showFPS.renderText(g);
		showCoords.renderText(g);
		returnToMenu.renderText(g);
	}

	@Override
	public void start() {
		darkColor = new RGBSlider(Game.WIDTH/2, Game.HEIGHT/2 - 50, 200, "Dark Squares", 0xff663400);
		darkColor.setValue(Chess.DARKCOLOR);
		lightColor = new RGBSlider(Game.WIDTH/2, Game.HEIGHT/2 - 20, 200, "Light Squares", 0xffFFE7BC);
		lightColor.setValue(Chess.LIGHTCOLOR);
		showFPS = new CheckBox(Game.WIDTH/2 + 150, Game.HEIGHT/2 + 10, "Show FPS");
		showFPS.setChecked(Game.SHOWFPS);
		showCoords = new CheckBox(Game.WIDTH/2 + 150, Game.HEIGHT/2 + 40, "Show Coordinates");
		showCoords.setChecked(Chess.SHOWCOORDS);
		
		returnToMenu = new Button(Game.WIDTH/2, Game.HEIGHT * .8, 150, 50, "RETURN");
	}
	
	private void saveSettings()
	{
		Chess.DARKCOLOR = darkColor.getValue();
		Chess.LIGHTCOLOR = lightColor.getValue();
		Chess.SHOWCOORDS = showCoords.isChecked();
	}

}
