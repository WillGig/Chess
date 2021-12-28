package scenes;

import java.awt.Graphics;

import game.Game;
import objects.Button;

public class Settings extends Scene{

	private Button returnToMenu;
	
	@Override
	public void update(Game game) {
		
		returnToMenu.update();
		if(returnToMenu.IsClicked())
		{
			saveSettings();
			game.SetScene(0);
		}
	}

	@Override
	public void render(int[] pixels) {
		returnToMenu.render(pixels);
	}

	@Override
	public void renderText(Graphics g) {
		returnToMenu.renderText(g);
	}

	@Override
	public void start() {
		returnToMenu = new Button(Game.WIDTH/2, Game.HEIGHT * .8, 150, 50, "RETURN");
	}
	
	private void saveSettings()
	{
		
	}

}
