package scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import game.Game;
import objects.Button;

public class MainMenu extends Scene
{

	private Button continueGame, newGame, settings, exit;
	
	public MainMenu()
	{
		start();
	}
	
	@Override
	public void update(Game game) 
	{
		if(Chess.CANCONTINUE)
			continueGame.update();
		if(continueGame.IsClicked())
			game.SetScene(1);
		
		newGame.update();
		if(newGame.IsClicked())
		{
			game.resetScene(1);
			game.SetScene(1);
		}
		
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
		if(Chess.CANCONTINUE)
			continueGame.render(pixels);
		newGame.render(pixels);
		settings.render(pixels);
		exit.render(pixels);
	}

	@Override
	public void renderText(Graphics g) 
	{
		if(Chess.CANCONTINUE)
			continueGame.renderText(g);
		newGame.renderText(g);
		settings.renderText(g);
		exit.renderText(g);
		
		Font font = new Font("Bell", 1, 75);
		g.setFont(font);
		g.setColor(Color.WHITE);
		int textWidth = (int)(g.getFontMetrics(font).stringWidth("CHESS")/Game.SCALE);
		g.drawString("CHESS", (int) ((Game.WIDTH/2 - textWidth/2)*Game.SCALE), (int) ((Game.HEIGHT/2 - 100)*Game.SCALE));
	}
	
	@Override
	public void start()
	{
		continueGame = new Button(Game.WIDTH/2, Game.HEIGHT/2 - 50, 150, 40, "CONTINUE");
		newGame = new Button(Game.WIDTH/2, Game.HEIGHT/2 + 0, 150, 40, "NEW GAME");
		settings = new Button(Game.WIDTH/2, Game.HEIGHT/2 + 50, 150, 40, "SETTINGS");
		exit = new Button(Game.WIDTH/2, Game.HEIGHT/2 + 100, 150, 40, "EXIT");
	}

}
