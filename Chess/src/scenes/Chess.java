package scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import game.Game;
import game.State;
import objects.Button;
import objects.ImageButton;
import objects.Tile;
import objects.pieces.Bishop;
import objects.pieces.King;
import objects.pieces.Knight;
import objects.pieces.Pawn;
import objects.pieces.Piece;
import objects.pieces.Queen;
import objects.pieces.Rook;
import utils.InputHandler;
import utils.Texture;

public class Chess extends Scene{

	private Color turn;
	
	private Tile[] board;
	
	private Tile selectedPieceTile, promotionSquare;
	
	private Piece draggingPiece;
	
	private ArrayList<Tile> moveOptions;
	
	private int turnNumber = 0, historyScroll;
	
	private Pawn promoting = null;
	
	private String promotionPiece;
	
	public enum GameState { ONGOING, CHECKMATE, STALEMATE};
	
	private GameState gameState;
	
	private String wMoveHistory = "", bMoveHistory = "";
	
	private Button undo;
	
	private ArrayList<State> previousPositions;
	
	public Chess()
	{
		undo = new Button(100, 500, 100, 50, "UNDO");
	}
	
	@Override
	public void update(Game game) 
	{
		if(InputHandler.MOUSEX < 200)
		{
			historyScroll -= InputHandler.getMouseScroll() * 40;
			if(historyScroll > 100)
				historyScroll = 100;
		}
			
		undo.update();
		if(undo.IsClicked())
		{
			if(previousPositions.size() > 0)
			{
				loadState(previousPositions.get(previousPositions.size()-1));
				previousPositions.remove(previousPositions.size()-1);
			}
		}
		
		if(gameState != GameState.ONGOING)
			return;
		
		//Check for pawn promotion options
		if(promoting != null)
		{
			ImageButton[] promotionOptions = promoting.GetPromotionOptions();
			for(ImageButton b : promotionOptions)
			{
				b.update();
				if(b.IsClicked())
				{
					promotionPiece = Texture.GetTextureName(b.GetButtonImage()).substring(0, 1);
					move(promotionSquare);
				}
			}
			return;
		}
		
		//Check for dragging piece to be dropped
		if(draggingPiece != null) 
		{
			if(!InputHandler.MouseClicked(1))
			{
				InputHandler.DRAGGING = false;
				Tile dropSquare = Tile.getCursorTile(board);
				if(dropSquare != null && moveOptions.contains(dropSquare))
				{
					if(draggingPiece instanceof Pawn && dropSquare.getTileY() % 7 == 0)
					{
						promoting = (Pawn)draggingPiece;
						promotionSquare = dropSquare;
						promoting.UpdatePromotionPosition(promotionSquare);
					}
					else
						move(dropSquare);
				}
				else 
				{
					draggingPiece.setX(selectedPieceTile.getX());
					draggingPiece.setY(selectedPieceTile.getY());
					draggingPiece = null;
				}
			}
			else
			{
				draggingPiece.setX(InputHandler.MOUSEX);
				draggingPiece.setY(InputHandler.MOUSEY);
			}
			return;
		}
		
		//Check for selected piece to be moved
		if(selectedPieceTile != null)
		{
			for(Tile t: moveOptions)
			{
				t.update();
				if(t.isClicked())
				{
					//pawn promotion
					if(selectedPieceTile.GetPiece() instanceof Pawn && t.getTileY() % 7 == 0)
					{
						promoting = (Pawn)selectedPieceTile.GetPiece();
						promotionSquare = t;
						promoting.UpdatePromotionPosition(promotionSquare);
					}
					else
						move(t);
				}
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
					//Unselect if the tile is the same
					if(t == selectedPieceTile && draggingPiece == null)
					{
						selectedPieceTile = null;
						moveOptions = null;
						InputHandler.MouseClickedAndSetFalse(1);
						return;
					}
					draggingPiece = p;
					InputHandler.DRAGGING = true;
					selectedPieceTile = t;
					moveOptions = t.GetPiece().getLegalMoves(board);
				}
				return;
			}
		}
	}
	
	public void move(Tile t)
	{
		previousPositions.add(new State(board, wMoveHistory, bMoveHistory, gameState, turn, turnNumber));
		
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
				moveText += selectedPieceTile.getSquareName().substring(0, 1);
			moveText += "x";
		}
		moveText += t.getSquareName();
		
		if(castling == 2)
			moveText = "0-0";
		else if(castling == -2)
			moveText = "0-0-0";
		
		if(promotionPiece != null)
		{
			if(promotionPiece.equals("K"))
				new Knight(t, promoting.getColor());
			else if(promotionPiece.equals("B"))
				new Bishop(t, promoting.getColor());
			else if(promotionPiece.equals("R"))
				new Rook(t, promoting.getColor());
			else
				new Queen(t, promoting.getColor());
			
			moveText += "=" + t.GetPiece().getNotationName();
			promotionPiece = null;
			promoting = null;
		}
		
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
			if(turnNumber > 15 && historyScroll > 100 - (turnNumber-15)*24)
				historyScroll = 100 - (turnNumber-15)*24;
				
			wMoveHistory += turnNumber + ". " + moveText + "\n";
		}
		else
			bMoveHistory += moveText + "\n";
		
		selectedPieceTile = null;
		draggingPiece = null;
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

	public void loadState(State s)
	{
		s.LoadState(board);
		wMoveHistory = s.whiteMoves;
		bMoveHistory = s.blackMoves;
		gameState = s.gState;
		turn = s.turn;
		turnNumber = s.moveNumber;
	}
	
	@Override
	public void render(int[] pixels) 
	{
		undo.render(pixels);
		
		for(int i = 0; i < board.length; i++)
			board[i].render(pixels);
		
		if(selectedPieceTile != null)
			for(Tile t: moveOptions)
				t.RenderHighLighted(pixels);	
		
		if(draggingPiece != null)
			draggingPiece.render(pixels);
		
		if(promoting != null)
			for(int i = 0; i < promoting.GetPromotionOptions().length; i++)
				promoting.GetPromotionOptions()[i].render(pixels);
	}

	@Override
	public void renderText(Graphics g) 
	{
		//Coordinates
		g.setFont(new Font("Arial", 1, 20));
		g.setColor(Color.WHITE);
		for(int i = 0; i < 8; i++)
		{
			g.drawString("" + (8 - i), (int)(board[0].getX() - board[0].getWidth()), (int)board[i * 8].getY() + 8);
			g.drawString(String.valueOf((char)(i + 65)), (int)board[i].getX() - 8, (int)(board[7*8].getY() + board[0].getHeight()));
		}
		
		//Move History
		int h = g.getFontMetrics().getHeight();
		int y = historyScroll;
		for (String line : wMoveHistory.split("\n"))
		{
			if(y > 90 && y < 450)
				g.drawString(line, 20, y);
			y += h;
		}
			
		
		y = historyScroll;
		for (String line : bMoveHistory.split("\n"))
		{
			if(y > 90 && y < 450)
				g.drawString(line, 120, y);
			y += h;
		}
		
		undo.renderText(g);
	}

	@Override
	public void start() 
	{
		turn = Color.WHITE;
		
		int size = 64;
		int darkColor = 0xff663400;
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
		promoting = null;
		promotionPiece = null;
		
		gameState = GameState.ONGOING;
		previousPositions = new ArrayList<State>();
		
		turnNumber = 0;
		historyScroll = 100;
	}

}
