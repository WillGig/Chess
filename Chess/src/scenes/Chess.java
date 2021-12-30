package scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
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

	public static int DARKCOLOR = 0xff663400, LIGHTCOLOR = 0xffFFE7BC;
	
	public static boolean SHOWCOORDS = true, CANCONTINUE = false;
	
	private Color turn;
	
	private Tile[] board;
	
	private Tile selectedPieceTile, promotionSquare;
	
	private Piece draggingPiece;
	
	private ArrayList<Tile> moveOptions;
	
	private int turnNumber, historyScroll, fiftyMoves;
	
	private Pawn promoting = null;
	
	private String promotionPiece;
	
	public enum GameState { ONGOING, CHECKMATE, STALEMATE, REPETITION, FIFTYMOVEDRAW};
	
	private GameState gameState;
	
	private String wMoveHistory, bMoveHistory;
	
	private Button undo, menu;
	
	private ArrayList<State> previousPositions;
	
	public Chess()
	{
		undo = new Button(60, 500, 75, 50, "UNDO");
		menu = new Button(150, 500, 75, 50, "MENU");
	}
	
	@Override
	public void update(Game game) 
	{
		if(InputHandler.MOUSEX < 200)
		{
			//Scroll through moves
			int scrollAmount = InputHandler.getMouseScroll() * 40;
			if(turnNumber > 30)
			{
				historyScroll -= scrollAmount;
				
				int cap = 100 - (turnNumber-30)*12;
				if(gameState != GameState.ONGOING)
					cap -= 72;
				
				if(historyScroll > 100)
					historyScroll = 100;
				else if(historyScroll < cap)
					historyScroll = cap;
			}
			else
				historyScroll = 100;
			
			//Check if a move in the move history is clicked
			int selectedMove = getSelectedMove();
			if(selectedMove != -1 && selectedMove < previousPositions.size())
				loadState(previousPositions.get(selectedMove));
		}
			
		//Check if arrow keys are pressed
		if(InputHandler.KeyPressedAndSetFalse(KeyEvent.VK_RIGHT))
		{
			if(turnNumber < previousPositions.size() - 1)
				loadState(previousPositions.get(turnNumber + 1));
		}
		else if(InputHandler.KeyPressedAndSetFalse(KeyEvent.VK_LEFT))
		{
			if(turnNumber - 1 > -1)
				loadState(previousPositions.get(turnNumber-1));
		}
		
		//Undo Button
		undo.update();
		if(undo.IsClicked())
		{
			if(previousPositions.size() > 1)
			{
				State s = previousPositions.get(previousPositions.size()-2);
				loadState(s);
				wMoveHistory = s.whiteMoves;
				bMoveHistory = s.blackMoves;
				previousPositions.remove(previousPositions.size()-1);
			}
		}
		
		//Menu Button
		menu.update();
		if(menu.IsClicked())
			game.SetScene(0);
		
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
					promotionPiece = Texture.getTextureName(b.GetButtonImage()).substring(0, 1);
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
		
		//50 Move Rule
		if(t.GetPiece() instanceof Pawn || captured != null)
			fiftyMoves = 0;
		else
			fiftyMoves++;
		
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
		else if(King.findKing(board, turn).inCheck(board))
			moveText += "+";
		
		//Override later moves
		if(turnNumber != previousPositions.size() - 1)
		{
			State s = previousPositions.get(turnNumber);
			wMoveHistory = s.whiteMoves;
			bMoveHistory = s.blackMoves;
			while(turnNumber != previousPositions.size() - 1)
				previousPositions.remove(previousPositions.size()-1);
		}
		
		if(turn == Color.BLACK)
			wMoveHistory += (turnNumber/2 + 1) + ". " + moveText + "\n";
		else
			bMoveHistory += moveText + "\n";
		turnNumber++;
		
		//Automatically scroll to new move
		if(turnNumber > 30 && historyScroll > 100 - (turnNumber-30)*12)
			historyScroll = 100 - (turnNumber-30)*12;
		
		if(gameState == GameState.CHECKMATE)
		{
			if(turn == Color.WHITE)
				wMoveHistory += "0-1\n";
			else
				wMoveHistory += "1-0\n";
			wMoveHistory += "Checkmate";
		}
		else if(gameState != GameState.ONGOING)
			wMoveHistory += "1/2-1/2\n";
		
		if(gameState == GameState.STALEMATE)
			wMoveHistory += "Draw by StaleMate";
		else if(gameState == GameState.REPETITION)
			wMoveHistory += "Draw by Repetition";
		else if(gameState == GameState.FIFTYMOVEDRAW)
			wMoveHistory += "Draw by 50\nMove Rule";
		
		if(gameState != GameState.ONGOING && (turnNumber > 30 && historyScroll > 100 - (turnNumber-24)*12))
			historyScroll = 100 - (turnNumber-24)*12;
		
		previousPositions.add(new State(board, wMoveHistory, bMoveHistory, gameState, turn, turnNumber, fiftyMoves));
		
		selectedPieceTile = null;
		draggingPiece = null;
	}
	
	public void updateGameState()
	{
		if(turn == Color.WHITE)
			turn = Color.BLACK;
		else
			turn = Color.WHITE;
		
		//Check for 3 fold repetition
		if(State.Repitition(previousPositions, board))
		{
			gameState = GameState.REPETITION;
			return;
		}
		
		//Check 50 Move rule
		if(fiftyMoves == 100)
		{
			gameState = GameState.FIFTYMOVEDRAW;
			return;
		}
		
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
		gameState = s.gState;
		turn = s.turn;
		turnNumber = s.moveNumber;
		fiftyMoves = s.fiftyMoves;
		Pawn.enPassantTile = s.epSquare;
		if(s.epPawn != null)
			Pawn.epPawn = (Pawn)board[s.epPawn.GetTileX() + s.epPawn.GetTileY() * 8].GetPiece();
		else
			Pawn.epPawn = null;
	}
	
	@Override
	public void render(int[] pixels) 
	{
		undo.render(pixels);
		menu.render(pixels);
		
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
		g.setFont(new Font("Arial", 1, (int)(20*Game.SCALE)));
		g.setColor(Color.WHITE);
		
		//Coordinates
		if(SHOWCOORDS)
		{
			for(int i = 0; i < 8; i++)
			{
				g.drawString("" + (8 - i), (int)((board[0].getX() - board[0].getWidth())*Game.SCALE) + Game.XOFF, (int)((board[i * 8].getY() + 8) * Game.SCALE) + Game.YOFF);
				g.drawString(String.valueOf((char)(i + 65)), (int)((board[i].getX() - 8)*Game.SCALE) + Game.XOFF, (int)((board[7*8].getY() + board[0].getHeight()) * Game.SCALE) + Game.YOFF);
			}
		}
		
		
		//Move History
		int h = g.getFontMetrics().getHeight();
		int y = historyScroll;
		for (String line : wMoveHistory.split("\n"))
		{
			if(y > 90 && y < 450)
				g.drawString(line, (int)(20*Game.SCALE) + Game.XOFF, (int)(y*Game.SCALE) + Game.YOFF);
			y += h;
		}
			
		
		y = historyScroll;
		for (String line : bMoveHistory.split("\n"))
		{
			if(y > 90 && y < 450)
				g.drawString(line, (int)(120*Game.SCALE) + Game.XOFF, (int)(y*Game.SCALE) + Game.YOFF);
			y += h;
		}
		
		undo.renderText(g);
		menu.renderText(g);
	}

	@Override
	public void start() 
	{
		CANCONTINUE = true;
		if(board == null)
		{
			reset();
			return;
		}
		
		//Update board color
		for(int y = 0; y < 8; y++)
		{
			for(int x = 0; x < 8; x++)
			{
				int color = LIGHTCOLOR;
				if((x + y % 2 + 1) % 2 == 0)
					color = DARKCOLOR;
				board[x+y*8].setColor(color);
			}
		}
	}
	
	public void reset()
	{
		turn = Color.WHITE;
		
		int size = 64;
		
		//Create board
		board = new Tile[64];
		for(int y = 0; y < 8; y++)
		{
			for(int x = 0; x < 8; x++)
			{
				int color = LIGHTCOLOR;
				if((x + y % 2 + 1) % 2 == 0)
					color = DARKCOLOR;
				int xC = (x * size) + (Game.WIDTH - 7 * size) / 2 + 100;
				int yC = (y * size) + (Game.HEIGHT - 7 * size) / 2 - 20;
				board[x+y*8] = new Tile(xC, yC, size, size, x, y, color);
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
		
		selectedPieceTile = null;
		promoting = null;
		promotionPiece = null;
		Pawn.enPassantTile = -1;
		Pawn.epPawn = null;
		
		gameState = GameState.ONGOING;
		previousPositions = new ArrayList<State>();
		previousPositions.add(new State(board, "", "", gameState, turn, 0, 0));
		
		turnNumber = 0;
		fiftyMoves = 0;
		historyScroll = 100;
		wMoveHistory = "";
		bMoveHistory = "";
	}
	
	private int getSelectedMove()
	{
		if(!InputHandler.MouseClicked(1))
			return -1;
		
		for(int i = 0; i < previousPositions.size(); i++)
		{
			int pX = (int)(50*Game.SCALE) + (i%2) * 100;
			int pY = historyScroll - 10 + (int)(24*Game.SCALE)*(i/2);
			int w = 70;
			int h = 20;
			
			int cX = InputHandler.MOUSEX;
			int cY = InputHandler.MOUSEY;
			if(cX > pX - w/2 && cX < pX + w/2 && cY > pY - h/2 && cY < pY + h/2)
				if(InputHandler.MouseClickedAndSetFalse(1))
					return i+1;
		}
		
		return -1;
	}
}
