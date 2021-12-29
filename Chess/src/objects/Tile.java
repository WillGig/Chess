package objects;

import objects.pieces.Piece;
import utils.InputHandler;
import utils.Texture;

public class Tile extends GameObject{
	
	private int tileX, tileY;
	
	private boolean clicked;
	
	private Piece containedPiece;
	
	private Texture highlightedImage;

	public Tile(double x, double y, int width, int height, int tX, int tY, int color) {
		super(x, y, width, height);
		tileX = tX;
		tileY = tY;
		int[] pixels = new int[width*height];
		int[] highlightedPixels = new int[width*height];
		for(int i = 0; i < width*height; i++)
		{
			pixels[i] = color;
			highlightedPixels[i] = 0x77ffffff;
		}
		image = new Texture(width, height, pixels);
		highlightedImage = new Texture(width, height, highlightedPixels);
	}

	public void RenderHighLighted(int[] pixels)
	{
		Texture temp = image;
		image = highlightedImage;
		render(pixels);
		image = temp;
	}
	
	@Override
	public void update() 
	{
		if(ContainsCursor() && InputHandler.MouseClicked(1))
			clicked = true;
		else
			clicked = false;
	}
	
	@Override
	public void render(int[] pixels)
	{
		super.render(pixels);
		if(containedPiece != null)
			containedPiece.render(pixels);
	}
	
	public int getTileX()
	{
		return tileX;
	}
	
	public int getTileY()
	{
		return tileY;
	}
	
	public Piece GetPiece()
	{
		return containedPiece;
	}
	
	public void SetPiece(Piece p)
	{
		containedPiece = p;
	}
	
	public static Piece GetPieceAtTile(int x, int y, Tile[] board)
	{
		if(x < 0 || x > 7 || y < 0 || y > 7)
			return null;
		return board[x + y * 8].GetPiece();
	}
	
	public boolean isClicked()
	{
		return clicked;
	}

	public String getSquareName()
	{
		return String.valueOf((char)(tileX + 97)) + (8 - tileY);
	}
	
	public void setColor(int color)
	{
		for(int i = 0; i < image.data.length; i++)
			image.data[i] = color;
	}
	
	public static Tile getCursorTile(Tile[] board)
	{
		for(int i = 0; i < board.length; i++)
			if(board[i].ContainsCursor())
				return board[i];
		return null;
	}
	
}
