package objects;

import java.awt.Point;

import utils.Texture;

public class MoveArrow{

	public static long MOVEARROWCOLOR;
	
	public Tile start, end;
	
	private double x, y;
	
	Texture image;
	Texture head;
	
	public MoveArrow(Tile start, Tile end) 
	{
		this.start = start;
		this.end = end;
		
		x = (start.getX() + end.getX())/2;
		y = (start.getY() + end.getY())/2;
		
		double angle = Math.atan2(end.getY() - start.getY(), end.getX() - start.getX()) + Math.PI/2;
		
		Point b1 = new Point((int)(start.getX() + Math.sin(angle)*20 + Math.cos(angle)*8), (int)(start.getY() - Math.cos(angle)*20 + Math.sin(angle)*8));
		Point b2 = new Point((int)(start.getX() + Math.sin(angle)*20 - Math.cos(angle)*9), (int)(start.getY() - Math.cos(angle)*20 - Math.sin(angle)*9));
		Point b3 = new Point((int)(end.getX() - Math.cos(angle)*9), (int)(end.getY() - Math.sin(angle)*9));
		Point b4 = new Point((int)(end.getX() + Math.cos(angle)*8), (int)(end.getY() + Math.sin(angle)*8));
		
		Texture head = Texture.getTexture("ArrowHead").rotate(angle);
		Quad body = new Quad(b1, b2, b3, b4, 0xff00ff00);
		
		int bodyX = (int)body.getX() - body.getTexture().width/2;
		int bodyY = (int)body.getY() - body.getTexture().height/2;
		image = body.getTexture().combine(bodyX, bodyY, head, (int)(end.getX() - head.width/2), (int)(end.getY() - head.height/2));
		image.setColor((int)MOVEARROWCOLOR);
	}

	public void render(int[] pixels)
	{
		image.render(x, y, pixels);
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(!(other instanceof MoveArrow))
			return false;
		
		MoveArrow otherArrow = (MoveArrow)other;
		
		if(otherArrow.start != start || otherArrow.end != end)
			return false;
		return true;
	}

}
