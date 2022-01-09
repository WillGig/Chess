package objects;

import java.awt.Graphics;
import java.awt.Rectangle;

import game.Position;

public class PositionOptions {

	public enum positionAction {DELETE, PROMOTE, TOGGLESHOW, NULL}
	
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
		
		if(p.getChildren().size() > 0)
		{
			if(p.getChildren().get(0).hidden)
				collapse = new Button(90, p.getY() + 40, 140, 20, "Show Line");
			else
				collapse = new Button(90, p.getY() + 40, 140, 20, "Hide Line");
			collapse.setFontSize(14);
			collapse.setBorderWidth(2);
			
			collider = new Rectangle(90 - 70, (int)p.getY() - p.height/2, 140, 60);
		}
		else
			collider = new Rectangle(90 - 70, (int)p.getY() - p.height/2, 140, 40);
		
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
		if(collapse != null)
		{
			collapse.update();
			if(collapse.IsClicked())
				status = positionAction.TOGGLESHOW;
		}
	}
	
	public void render(int[] pixels)
	{
		delete.render(pixels);
		promote.render(pixels);
		if(collapse != null)
			collapse.render(pixels);
	}
	
	public void renderText(Graphics g)
	{
		delete.renderText(g);
		promote.renderText(g);
		if(collapse != null)
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
