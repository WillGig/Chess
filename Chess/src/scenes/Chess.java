package scenes;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import game.Game;
import objects.Tile;
import objects.pieces.Bishop;
import objects.pieces.King;
import objects.pieces.Knight;
import objects.pieces.Pawn;
import objects.pieces.Piece;
import objects.pieces.Queen;
import objects.pieces.Rook;

public class Chess extends Scene{

	private Color turn;
	
	private Tile[] board;
	
	private Tile selectedPieceTile;
	
	private ArrayList<Tile> moveOptions;
	
	public Chess()
	{
		start();
	}
	
	@Override
	public void update(Game game) 
	{
		if(selectedPieceTile != null)
		{
			for(Tile t: moveOptions)
			{
				t.update();
				if(t.isClicked())
				{
					Pawn.enPassantTile = 0;
					Pawn.epPawn = null;
					selectedPieceTile.GetPiece().move(selectedPieceTile, t, board);
					selectedPieceTile = null;
					if(turn == Color.WHITE)
						turn = Color.BLACK;
					else
						turn = Color.WHITE;
					return;
				}
			}
		}
		
		for(Tile t: board)
		{
			t.update();
			if(t.isClicked())
			{
				Piece p = t.GetPiece();
				if(p != null && p.getColor() == turn)
				{
					selectedPieceTile = t;
					moveOptions = t.GetPiece().getLegalMoves(board);
				}
				return;
			}
		}
	}

	@Override
	public void render(int[] pixels) 
	{
		for(int i = 0; i < board.length; i++)
			board[i].render(pixels);
		
		if(selectedPieceTile != null)
			for(Tile t: moveOptions)
				t.RenderHighLighted(pixels);				
	}

	@Override
	public void renderText(Graphics g) 
	{
		for(Tile t: board)
		{
			Piece p = t.GetPiece();
			if(p != null)
			{
				g.setColor(p.getColor());
				p.renderText(g);
			}
		}
		
		if(selectedPieceTile != null)
		{
			g.setColor(Color.CYAN);
			selectedPieceTile.GetPiece().renderText(g);
		}
		
	}

	@Override
	public void start() 
	{
		turn = Color.WHITE;
		
		int size = 64;
		int darkColor = 0xff663400;//0xff331C00;
		int lightColor = 0xffFFE7BC;
		
		board = new Tile[64];
		for(int y = 0; y < 8; y++)
		{
			for(int x = 0; x < 8; x++)
			{
				int color = lightColor;
				if((x + y % 2 + 1) % 2 == 0)
					color = darkColor;
				int xC = (x * size) + (Game.WIDTH - 7 * size) / 2;
				int yC = (y * size) + (Game.HEIGHT - 7 * size) / 2;
				board[x+y*8] = new Tile(xC, yC, size, size, x, y, color);
			}
		}
			
		for(int i = 0; i < 8; i++)
			new Pawn(board[i + 6*8], Color.WHITE);
		new Rook(board[0 + 7*8], Color.WHITE);
		new Rook(board[7 + 7*8], Color.WHITE);
		new Knight(board[1 + 7*8], Color.WHITE);
		new Knight(board[6 + 7*8], Color.WHITE);
		new Bishop(board[2 + 7*8], Color.WHITE);
		new Bishop(board[5 + 7*8], Color.WHITE);
		new Queen(board[3 + 7*8], Color.WHITE);
		new King(board[4 + 7*8], Color.WHITE);
		
		for(int i = 0; i < 8; i++)
			new Pawn(board[i + 8], Color.BLACK);
		new Rook(board[0 + 0*8], Color.BLACK);
		new Rook(board[7 + 0*8], Color.BLACK);
		new Knight(board[1 + 0*8], Color.BLACK);
		new Knight(board[6 + 0*8], Color.BLACK);
		new Bishop(board[2 + 0*8], Color.BLACK);
		new Bishop(board[5 + 0*8], Color.BLACK);
		new Queen(board[3 + 0*8], Color.BLACK);
		new King(board[4 + 0*8], Color.BLACK);
		
		selectedPieceTile = null;
	}

}
