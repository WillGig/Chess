package objects;

import java.awt.Color;

import game.Game;
import objects.pieces.Bishop;
import objects.pieces.King;
import objects.pieces.Knight;
import objects.pieces.Pawn;
import objects.pieces.Piece;
import objects.pieces.Queen;
import objects.pieces.Rook;
import scenes.Chess;
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
			highlightedPixels[i] = 0xaaffffff;
		}
		image = new Texture(width, height, pixels);
		highlightedImage = new Texture(width, height, highlightedPixels);
	}

	public void RenderHighLighted(int[] pixels)
	{
		highlightedImage.render(x, y, pixels);
	}
	
	@Override
	public void update() 
	{
		if(containsCursor() && InputHandler.MouseClicked(1) && !InputHandler.DRAGGING)
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

	public String getSquareName()
	{
		return String.valueOf((char)(tileX + 97)) + (8 - tileY);
	}
	
	public void setColor(int color)
	{
		for(int i = 0; i < image.data.length; i++)
			image.data[i] = color;
	}
	
	//Returns the tile the cursor is currently over
	//Returns null if no tiles contain the cursor
	public static Tile getCursorTile(Tile[] board)
	{
		for(int i = 0; i < board.length; i++)
			if(board[i].containsCursor())
				return board[i];
		return null;
	}
	

	public static Tile[] getDefaultBoard()
	{
		//Create board
		Tile[] board = new Tile[64];
		for(int y = 0; y < 8; y++)
		{
			for(int x = 0; x < 8; x++)
			{
				int color = Chess.LIGHTCOLOR;
				if((x + y % 2 + 1) % 2 == 0)
					color = Chess.DARKCOLOR;
				int xC = (x * 64) + (800 - 7 * 64) / 2 + 100;
				int yC = (y * 64) + (Game.HEIGHT - 7 * 64) / 2 - 20;
				board[x+y*8] = new Tile(xC, yC, 64, 64, x, y, color);
			}
		}
					
		//Setup pieces
		for(int i = 0; i < 8; i++)
		{
			new Pawn(board[i + 6*8], Color.WHITE);
			new Pawn(board[i + 8], Color.BLACK);
		}
			
		new Rook(board[0 + 7*8], Color.WHITE);
		new Rook(board[7 + 7*8], Color.WHITE);
		new Knight(board[1 + 7*8], Color.WHITE);
		new Knight(board[6 + 7*8], Color.WHITE);
		new Bishop(board[2 + 7*8], Color.WHITE);
		new Bishop(board[5 + 7*8], Color.WHITE);
		new Queen(board[3 + 7*8], Color.WHITE);
		new King(board[4 + 7*8], Color.WHITE);
		
		new Rook(board[0 + 0*8], Color.BLACK);
		new Rook(board[7 + 0*8], Color.BLACK);
		new Knight(board[1 + 0*8], Color.BLACK);
		new Knight(board[6 + 0*8], Color.BLACK);
		new Bishop(board[2 + 0*8], Color.BLACK);
		new Bishop(board[5 + 0*8], Color.BLACK);
		new Queen(board[3 + 0*8], Color.BLACK);
		new King(board[4 + 0*8], Color.BLACK);
		
		return board;
	}
	
	public static void flip(Tile[] board)
	{
		for(int i = 0; i < board.length/2; i++)
		{
			Tile t1 = board[i];
			Tile t2 = board[board.length-i-1];
			
			double x1 = t1.getX();
			double y1 = t1.getY();
			double x2 = t2.getX();
			double y2 = t2.getY();
			
			t1.setX(x2);
			t1.setY(y2);
			if(t1.GetPiece() != null)
			{
				t1.GetPiece().setX(x2);
				t1.GetPiece().setY(y2);
			}
			
			t2.setX(x1);
			t2.setY(y1);
			if(t2.GetPiece() != null)
			{
				t2.GetPiece().setX(x1);
				t2.GetPiece().setY(y1);
			}
		}
	}
	
	public static int getNumPieces(Tile[] board)
	{
		int num = 0;
		for(int i = 0; i < board.length; i++)
			if(board[i].GetPiece() != null)
				num++;
		return num;
	}
	
}
