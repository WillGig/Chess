package scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import game.Game;
import objects.Button;

public class MainMenu extends Scene
{

	private Button board, settings, exit;
	
	public MainMenu()
	{
		start();
	}
	
	@Override
	public void update(Game game) 
	{
		board.update();
		if(board.IsClicked())
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
		board.render(pixels);
		settings.render(pixels);
		exit.render(pixels);
	}

	@Override
	public void renderText(Graphics g) 
	{
		board.renderText(g);
		settings.renderText(g);
		exit.renderText(g);
		
		g.setFont(new Font("Bell", 1, (int)(75*Game.SCALE)));
		g.setColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
		int textWidth = g.getFontMetrics().stringWidth("CHESS");
		g.drawString("CHESS", (int) (Game.WIDTH*Game.SCALE/2) - textWidth/2 + Game.XOFF, (int) ((Game.HEIGHT/2 - 100)*Game.SCALE) + Game.YOFF);
	}
	
	@Override
	public void start()
	{
		board = new Button(Game.WIDTH/2, Game.HEIGHT/2 + 0, 150, 40, "BOARD");
		settings = new Button(Game.WIDTH/2, Game.HEIGHT/2 + 50, 150, 40, "SETTINGS");
		exit = new Button(Game.WIDTH/2, Game.HEIGHT/2 + 100, 150, 40, "EXIT");
	}

}
