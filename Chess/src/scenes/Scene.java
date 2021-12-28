package scenes;

import java.awt.Graphics;

import game.Game;

public abstract class Scene 
{
	
	public abstract void update(Game game);
	
	public abstract void render(int[] pixels);
	
	public abstract void renderText(Graphics g);
	
	public abstract void start();
	
	public void stop() {};

}
