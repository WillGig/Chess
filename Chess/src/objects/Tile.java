package objects;

import objects.pieces.Piece;
import utils.InputHandler;

public class Tile extends GameObject{
	
	private int tileX, tileY;
	
	private boolean clicked;
	
	private Piece containedPiece;
	
	int[] highlightedImage;

	public Tile(double x, double y, int width, int height, int tX, int tY, int color) {
		super(x, y, width, height);
		tileX = tX;
		tileY = tY;
		image = new int[width*height];
		highlightedImage = new int[width*height];
		for(int i = 0; i < width*height; i++)
		{
			image[i] = color;
			highlightedImage[i] = 0x77ffffff;
		}
	}

	public void RenderHighLighted(int[] pixels)
	{
		int[] temp = image;
		image = highlightedImage;
		render(pixels);
		image = temp;
	}
	
	@Override
	public void update() 
	{
		if(ContainsCursor() && InputHandler.MouseClickedAndSetFalse(1))
			clicked = true;
		else
			clicked = false;
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

}
