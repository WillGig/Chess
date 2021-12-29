package scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import game.Game;
import objects.Button;

public class MainMenu extends Scene
{

	private Button newGame, continueGame, settings, exit;
	
	public MainMenu()
	{
		start();
	}
	
	@Override
	public void update(Game game) 
	{
		newGame.update();
		if(newGame.IsClicked())
		{
			game.resetScene(1);
			game.SetScene(1);
		}
		
		continueGame.update();
		if(continueGame.IsClicked())
			game.SetScene(1);
		
		settings.update();
		if(settings.IsClicked())
			game.SetScene(2);
		
		exit.update();
		if(exit.IsClicked())
			game.stop();
	}

	@Override
	public void render(int[] pixels) 
	{	
		newGame.render(pixels);
		continueGame.render(pixels);
		settings.render(pixels);
		exit.render(pixels);
	}

	@Override
	public void renderText(Graphics g) 
	{
		newGame.renderText(g);
		continueGame.renderText(g);
		settings.renderText(g);
		exit.renderText(g);
		
		Font font = new Font("Bell", 1, 75);
		g.setFont(font);
		g.setColor(Color.WHITE);
		int textWidth = (int)(g.getFontMetrics(font).stringWidth("GAME")/Game.SCALE);
		g.drawString("GAME", (int) ((Game.WIDTH/2 - textWidth/2)*Game.SCALE), (int) ((Game.HEIGHT/2 - 75)*Game.SCALE));
	}
	
	@Override
	public void start()
	{
		newGame = new Button(Game.WIDTH/2, Game.HEIGHT/2 - 0, 150, 40, "NEW GAME");
		continueGame = new Button(Game.WIDTH/2, Game.HEIGHT/2 + 50, 150, 40, "CONTINUE");
		settings = new Button(Game.WIDTH/2, Game.HEIGHT/2 + 100, 150, 40, "SETTINGS");
		exit = new Button(Game.WIDTH/2, Game.HEIGHT/2 + 150, 150, 40, "EXIT");
	}

}
