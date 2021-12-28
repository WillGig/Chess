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
	
	private int turnNumber = 0;
	
	private enum GameState { ONGOING, CHECKMATE, STALEMATE};
	
	private GameState gameState;
	
	private String wMoveHistory = "", bMoveHistory = "";
	
	@Override
	public void update(Game game) 
	{
		if(gameState != GameState.ONGOING)
			return;
		
		//Check for selected piece to be moved
		if(selectedPieceTile != null)
		{
			for(Tile t: moveOptions)
			{
				t.update();
				if(t.isClicked())
					move(t);
			}
		}
		
		//Check for piece to be selected
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
	
	public void move(Tile t)
	{
		Pawn.enPassantTile = 0;
		Pawn.epPawn = null;
		
		//Check for castling
		int castling = 0;
		if(selectedPieceTile.GetPiece() instanceof King)
			castling = t.getTileX() - selectedPieceTile.getTileX();
		
		String moveText = selectedPieceTile.GetPiece().getNotationName();
		Piece captured = selectedPieceTile.GetPiece().move(selectedPieceTile, t, board);
		
		if(captured != null)
		{
			//Name column if pawn captures
			if(moveText.length() == 0)
				moveText += selectedPieceTile.GetSquareName().substring(0, 1);
			moveText += "x";
		}
		moveText += t.GetSquareName();
		
		if(castling == 2)
			moveText = "0-0";
		else if(castling == -2)
			moveText = "0-0-0";
		
		updateGameState();
		
		if(gameState == GameState.CHECKMATE)
			moveText += "#";
		else if(gameState == GameState.STALEMATE)
			moveText += "\nStaleMate";
		else if(King.findKing(board, turn).inCheck(board))
			moveText += "+";
		
		if(turn == Color.BLACK)
		{
			turnNumber++;
			wMoveHistory += turnNumber + ". " + moveText + "\n";
		}
		else
			bMoveHistory += moveText + "\n";
		
		selectedPieceTile = null;
	}
	
	public void updateGameState()
	{
		if(turn == Color.WHITE)
			turn = Color.BLACK;
		else
			turn = Color.WHITE;
		
		//ensure the current side has legal moves
		boolean canMove = false;
		for(int i = 0; i < board.length; i++)
		{
			Piece p = board[i].GetPiece();
			
			if(p == null || p.getColor() != turn)
				continue;
			
			if(!p.getLegalMoves(board).isEmpty())
			{
				canMove = true;
				break;
			}
		}
		
		if(canMove)
			return;
		
		if(King.findKing(board, turn).inCheck(board))
			gameState = GameState.CHECKMATE;
		else
			gameState = GameState.STALEMATE;
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
			t.renderText(g);
		
		g.setColor(Color.CYAN);
		if(selectedPieceTile != null)
			selectedPieceTile.GetPiece().renderText(g);
		
		//Coordinates
		g.setColor(Color.WHITE);
		for(int i = 0; i < 8; i++)
		{
			g.drawString("" + (8 - i), (int)(board[0].getX() - board[0].getWidth()), (int)board[i * 8].getY() + 8);
			g.drawString(String.valueOf((char)(i + 65)), (int)board[i].getX() - 8, (int)(board[7*8].getY() + board[0].getHeight()));
		}
		
		//Move History
		int h = g.getFontMetrics().getHeight();
		int y = 100;
		for (String line : wMoveHistory.split("\n"))
            g.drawString(line, 20, y += h);
		
		y = 100;
		for (String line : bMoveHistory.split("\n"))
            g.drawString(line, 120, y += h);
	}

	@Override
	public void start() 
	{
		turn = Color.WHITE;
		
		int size = 64;
		int darkColor = 0xff663400;//0xff331C00;
		int lightColor = 0xffFFE7BC;
		
		//Create board
		board = new Tile[64];
		for(int y = 0; y < 8; y++)
		{
			for(int x = 0; x < 8; x++)
			{
				int color = lightColor;
				if((x + y % 2 + 1) % 2 == 0)
					color = darkColor;
				int xC = (x * size) + (Game.WIDTH - 7 * size) / 2 + 100;
				int yC = (y * size) + (Game.HEIGHT - 7 * size) / 2 - 20;
				board[x+y*8] = new Tile(xC, yC, size, size, x, y, color);
			}
		}
			
		//Setup pieces
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
		
		gameState = GameState.ONGOING;
	}

}
