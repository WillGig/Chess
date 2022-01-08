package objects;

import java.awt.Graphics;
import java.awt.Rectangle;

import game.Position;

public class PositionOptions {

	public enum positionAction {DELETE, PROMOTE, COLLAPSE, NULL}
	
	public positionAction status;
	
	public Position position;
	
	private Button delete, promote, collapse;
	
	private Rectangle collider;
	
	public PositionOptions(Position p)
	{
		position = p;
		delete = new Button(90, p.getY(), 140, 20, "Delete Line");
		delete.setFontSize(14);
		delete.setBorderWidth(2);
		promote = new Button(90, p.getY() + 20, 140, 20, "Promote Line");
		promote.setFontSize(14);
		promote.setBorderWidth(2);
		collapse = new Button(90, p.getY() + 40, 140, 20, "Collapse Line");
		collapse.setFontSize(14);
		collapse.setBorderWidth(2);
		
		collider = new Rectangle(90 - 70, (int)p.getY() - p.height/2, 140, 60);
		
		status = positionAction.NULL;
	}
	
	public void update()
	{
		delete.update();
		if(delete.IsClicked())
			status = positionAction.DELETE;
		promote.update();
		if(promote.IsClicked())
			status = positionAction.PROMOTE;
		collapse.update();
		if(collapse.IsClicked())
			status = positionAction.COLLAPSE;
	}
	
	public void render(int[] pixels)
	{
		delete.render(pixels);
		promote.render(pixels);
		collapse.render(pixels);
	}
	
	public void renderText(Graphics g)
	{
		delete.renderText(g);
		promote.renderText(g);
		collapse.renderText(g);
	}
	
	public boolean containsPoint(int x, int y)
	{
		return collider.contains(x, y);
	}
	
	public boolean overlapsRect(int x, int y, int width, int height)
	{
		Rectangle collides = new Rectangle(x - width/2, y - height/2, width, height);
		return collider.intersects(collides);
	}
}
