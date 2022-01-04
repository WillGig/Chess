package objects;

import java.awt.Point;

import utils.Texture;

public class Quad{
	
	private double x, y;
	
	private Texture image;

	public Quad(Point p1, Point p2, Point p3, Point p4, int color) 
	{
		x = (p1.x + p2.x + p3.x + p4.x)/4.0;
		y = (p1.y + p2.y + p3.y + p4.y)/4.0;
		
		int left = findSmallest(p1.x, p2.x, p3.x, p4.x);
		int right = findLargest(p1.x, p2.x, p3.x, p4.x);
		int top = findSmallest(p1.y, p2.y, p3.y, p4.y);
		int bottom = findLargest(p1.y, p2.y, p3.y, p4.y);
		
		int width = right - left;
		int height = bottom - top;
		
		int[] pixels = new int[width * height];
		
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				if(containsPoint(p1, p2, p3, p4, new Point(x + left, y + top)))
					pixels[x + y * width] = color;
				else
					pixels[x + y * width] = 0;
			}
		}
		
		image = new Texture(width, height, pixels);
	}

	public void render(int[] pixels)
	{
		image.render(x, y, pixels);
	}
	
	public Texture getTexture()
	{
		return image;
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
	
	private static int findSmallest(int a, int b, int c, int d)
	{
		int smallest = a;
		if(b < a)
			smallest = b;
		if(c < smallest)
			smallest = c;
		if(d < smallest)
			smallest = d;
		
		return smallest;
	}
	
	private static int findLargest(int a, int b, int c, int d)
	{
		int largest = a;
		if(b > a)
			largest = b;
		if(c > largest)
			largest = c;
		if(d > largest)
			largest = d;
		
		return largest;
	}
	
	//Works only for convex quads
	//Points p1...p4 are assumed to be in order around the edges
	public static boolean containsPoint(Point p1, Point p2, Point p3, Point p4, Point test)
	{
		//Calculate area of quad
		double t1Area = areaOfTriangle(p1, p2, p3);
		double t2Area = areaOfTriangle(p3, p4, p1);
		double quadArea = t1Area + t2Area;
		
		//Calculate area of triangles created with test point
		double t12Area = areaOfTriangle(p1, p2, test);
		double t23Area = areaOfTriangle(p2, p3, test);
		double t34Area = areaOfTriangle(p3, p4, test);
		double t41Area = areaOfTriangle(p4, p1, test);
				
		double totalArea = t12Area + t23Area + t34Area + t41Area;
		
		if(Math.abs(totalArea - quadArea) < 1)
			return true;
		return false;
	}
	
	public static double areaOfTriangle(Point p1, Point p2, Point p3)
	{
		return Math.abs((p1.x*(p2.y-p3.y) + p2.x*(p3.y-p1.y) + p3.x*(p1.y-p2.y))/2.0);
	}
}
