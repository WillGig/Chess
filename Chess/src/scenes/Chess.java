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
import utils.SaveLoadManager;
import utils.Sound.SoundEffect;
import utils.Texture;

public class Chess extends Scene{

	public static int DARKCOLOR = 0xff663400, LIGHTCOLOR = 0xffFFE7BC;
	
	public static boolean SHOWCOORDS = true, CANCONTINUE = false, FLIPONMOVE = false;
	
	private Color turn;
	
	private Tile[] board;
	
	private Tile selectedPieceTile, promotionSquare;
	
	private Piece draggingPiece;
	
	private ArrayList<Tile> moveOptions;
	
	private int turnNumber, historyScroll, fiftyMoves;
	
	private Pawn promoting = null;
	
	private char promotionPiece;
	
	public enum GameState { ONGOING, CHECKMATE, STALEMATE, REPETITION, FIFTYMOVEDRAW};
	
	private GameState gameState;
	
	private String score, result;
	
	private Button undo, menu, forward, back, swap;
	
	private ArrayList<State> previousPositions;
	
	public Chess()
	{
		undo = new Button(60, 500, 75, 50, "UNDO");
		menu = new Button(150, 500, 75, 50, "MENU");
		forward = new Button(180, 560, 50, 30, "->");
		back = new Button(40, 560, 50, 30, "<-");
		swap = new Button(110, 560, 70, 30, "^v");
	}
	
	@Override
	public void update(Game game) 
	{
		if(InputHandler.KeyPressedAndSetFalse(KeyEvent.VK_1))
			SaveLoadManager.saveGame(previousPositions, "res/save.pgn");
		else if(InputHandler.KeyPressedAndSetFalse(KeyEvent.VK_2))
		{
			ArrayList<State> loadedFile = SaveLoadManager.loadGame("res/save.pgn");
			if(loadedFile != null && loadedFile.size() > 0)
			{
				previousPositions = loadedFile;
				loadState(previousPositions.get(0));
			}
			return;
		}
		
		//Scroll through moves
		if(InputHandler.MOUSEX < 200)
		{
			int scrollAmount = InputHandler.getMouseScroll() * 40;
			if(previousPositions.size() > 30)
			{
				historyScroll -= scrollAmount;
				
				int cap = 100 - (previousPositions.size()-30)*12;
				if(gameState != GameState.ONGOING)
					cap -= 72;
				
				if(historyScroll > 100)
					historyScroll = 100;
				else if(historyScroll < cap)
					historyScroll = cap;
			}
			else
				historyScroll = 100;
		}
		
		//Check if a move in the move history is clicked
		int selectedMove = getSelectedMove();
		if(selectedMove != -1 && selectedMove < previousPositions.size())
			loadState(previousPositions.get(selectedMove));
		
		//Update move history positions
		for(int i = 1; i < previousPositions.size(); i++)
			previousPositions.get(i).setY((historyScroll + ((i-1)/2) * 24.0f) + Game.YOFF);
			
		//Check if arrow keys or forward and back buttons are pressed
		forward.update();
		back.update();
		if(InputHandler.KeyPressedAndSetFalse(KeyEvent.VK_RIGHT) || forward.IsClicked())
		{
			if(turnNumber < previousPositions.size() - 1)
			{
				int numP = getNumPieces();
				loadState(previousPositions.get(turnNumber + 1));
				if(numP == getNumPieces())
					SoundEffect.MOVE.play();
				else
					SoundEffect.CAPTURE.play();
			}
		}
		else if(InputHandler.KeyPressedAndSetFalse(KeyEvent.VK_LEFT) || back.IsClicked())
		{
			if(turnNumber - 1 > -1)
				loadState(previousPositions.get(turnNumber-1));
				
		}
		
		//Swap Button
		swap.update();
		if(swap.IsClicked())
			flip();
		
		//Undo Button
		undo.update();
		if(undo.IsClicked())
		{
			if(previousPositions.size() > 1)
			{
				State s = previousPositions.get(previousPositions.size()-2);
				loadState(s);
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
					promotionPiece = Texture.getTextureName(b.GetButtonImage()).charAt(0);
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
		
		//Piece name
		String moveText = selectedPieceTile.GetPiece().getNotationName();
		
		
		//Disambiguation text
		ArrayList<Piece> confusingPieces = Piece.confusingPieces(moveText, selectedPieceTile, t, board);
		if(confusingPieces.size() > 0)
		{
			//If specifying the file is sufficient to disambiguate
			boolean fileChange = true;
			//If specifying the rank is sufficient to disambiguate
			boolean rankChange = true;
			for(Piece p : confusingPieces)
			{
				if(p.GetTileX() == selectedPieceTile.getTileX())
					fileChange = false;
				if(p.GetTileY() == selectedPieceTile.getTileY())
					rankChange = false;
			}
			
			String tileName = selectedPieceTile.getSquareName();
			
			if(fileChange)
				moveText += tileName.charAt(0);
			else if(rankChange)
				moveText += tileName.charAt(1);
			else
				moveText += tileName;
		}
		
		//Captured text
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
		
		if(promotionPiece != ' ')
		{
			if(promotionPiece == 'K')
				new Knight(t, promoting.getColor());
			else if(promotionPiece == 'B')
				new Bishop(t, promoting.getColor());
			else if(promotionPiece == 'R')
				new Rook(t, promoting.getColor());
			else
				new Queen(t, promoting.getColor());
			
			moveText += "=" + t.GetPiece().getNotationName();
			promotionPiece = ' ';
			promoting = null;
		}
		
		updateGameState();
		
		if(gameState == GameState.CHECKMATE)
			moveText += "#";
		else if(King.findKing(board, turn).inCheck(board))
			moveText += "+";
		
		//Override later moves
		if(turnNumber != previousPositions.size() - 1)
			while(turnNumber != previousPositions.size() - 1)
				previousPositions.remove(previousPositions.size()-1);
		
		if(turn == Color.BLACK)
			moveText = (turnNumber/2 + 1) + ". " + moveText;
		turnNumber++;
		
		//Automatically scroll to new move
		if(turnNumber > 30 && historyScroll > 100 - (turnNumber-30)*12)
			historyScroll = 100 - (turnNumber-30)*12;
		
		if(gameState == GameState.CHECKMATE)
		{
			if(turn == Color.WHITE)
				score = "0-1";
			else
				score = "1-0";
			result = "Checkmate";
		}
		else if(gameState != GameState.ONGOING)
			score = "1/2-1/2";
		
		if(gameState == GameState.STALEMATE)
			result = "Draw - StaleMate";
		else if(gameState == GameState.REPETITION)
			result = "Draw - Repetition";
		else if(gameState == GameState.FIFTYMOVEDRAW)
			result = "Draw - 50 Moves";
		
		if(gameState != GameState.ONGOING && (turnNumber > 30 && historyScroll > 100 - (turnNumber-24)*12))
			historyScroll = 100 - (turnNumber-24)*12;
		
		for(State state : previousPositions)
			state.setTextColor(0xffaaaaaa);
		previousPositions.add(new State(board, moveText, gameState, turn, turnNumber, fiftyMoves));
		
		if(FLIPONMOVE)
			flip();
		
		selectedPieceTile = null;
		draggingPiece = null;
		
		if(captured == null)
			SoundEffect.MOVE.play();
		else
			SoundEffect.CAPTURE.play();
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
		
		//Checkmate, stalemate
		gameState = State.EvaluateState(board, turn);
	}

	public void loadState(State s)
	{
		s.LoadState(board);
		gameState = s.gState;
		if(turn != s.turn && FLIPONMOVE)
			flip();
		turn = s.turn;
		turnNumber = s.moveNumber;
		fiftyMoves = s.fiftyMoves;
		score = s.score;
		result = s.result;
		Pawn.enPassantTile = s.epSquare;
		if(s.epPawn != null)
			Pawn.epPawn = (Pawn)board[s.epPawn.GetTileX() + s.epPawn.GetTileY() * 8].GetPiece();
		else
			Pawn.epPawn = null;
		promoting = null;
		moveOptions = null;
		selectedPieceTile = null;
		draggingPiece = null;
		
		for(State state : previousPositions)
			state.setTextColor(0xffaaaaaa);
		s.setTextColor(0xffffffff);
	}
	
	@Override
	public void render(int[] pixels) 
	{
		undo.render(pixels);
		menu.render(pixels);
		forward.render(pixels);
		back.render(pixels);
		swap.render(pixels);
		
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
				g.drawString("" + (8 - i), (int)((Game.WIDTH/2 - 190)*Game.SCALE) + Game.XOFF, (int)((board[i * 8].getY() + 8) * Game.SCALE) + Game.YOFF);
				g.drawString(String.valueOf((char)(i + 65)), (int)((board[i].getX() - 8)*Game.SCALE) + Game.XOFF, (int)((Game.HEIGHT/2 + 270) * Game.SCALE) + Game.YOFF);
			}
		}
		
		
		//Move History
		for(int i = 1; i < previousPositions.size(); i++)
		{
			double y = previousPositions.get(i).getY();
			if(y > 90 && y < 450)
				previousPositions.get(i).renderText(g);
		}
		
		if(gameState != GameState.ONGOING)
		{
			int y = (int) ((historyScroll + (previousPositions.size()/2 + 0.5f) * 24.0f)*Game.SCALE) + Game.YOFF;
			if(y > 90*Game.SCALE && y < 450*Game.SCALE)
				g.drawString(score, (int) (20*Game.SCALE + Game.XOFF), y);
			y += 24.0f*Game.SCALE;
			if(y > 90*Game.SCALE && y < 450*Game.SCALE)
				g.drawString(result, (int) (20*Game.SCALE + Game.XOFF), y);
		}
			
		undo.renderText(g);
		menu.renderText(g);
		forward.renderText(g);
		back.renderText(g);
		swap.renderText(g);
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
		
		board = Tile.getDefaultBoard();
		
		selectedPieceTile = null;
		promoting = null;
		promotionPiece = ' ';
		Pawn.enPassantTile = -1;
		Pawn.epPawn = null;
		
		gameState = GameState.ONGOING;
		previousPositions = new ArrayList<State>();
		previousPositions.add(new State(board, "", gameState, turn, 0, 0));
		
		turnNumber = 0;
		fiftyMoves = 0;
		historyScroll = 100;
	}
	
	private int getSelectedMove()
	{
		for(int i = 1; i < previousPositions.size(); i++)
		{
			previousPositions.get(i).update();
			if(previousPositions.get(i).IsClicked())
				return i;
		}
		return -1;
	}
	
	private int getNumPieces()
	{
		int num = 0;
		for(int i = 0; i < board.length; i++)
			if(board[i].GetPiece() != null)
				num++;
		return num;
	}
	
	private void flip()
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
}
