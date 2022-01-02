package scenes;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import game.Game;
import game.GameData;
import game.Position;
import objects.Button;
import objects.ImageButton;
import objects.TextField;
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
	
	public enum GameState { ONGOING, CHECKMATE, STALEMATE, DRAW, REPETITION, FIFTYMOVEDRAW};
	
	private GameState gameState;
	
	private Button save, load, menu, reset, forward, back, swap;
	
	private TextField event, site, date, round, white, black, result, comments;
	
	private ArrayList<Position> positions;
	
	public Chess()
	{
		save = new Button(55, 475, 80, 30, "SAVE");
		save.setFontSize(16);
		load = new Button(55, 515, 80, 30, "LOAD");
		load.setFontSize(16);
		menu = new Button(145, 475, 80, 30, "MENU");
		menu.setFontSize(16);
		reset = new Button(145, 515, 80, 30, "RESET");
		reset.setFontSize(16);
		forward = new Button(180, 560, 50, 30, "->");
		forward.setFontSize(16);
		back = new Button(40, 560, 50, 30, "<-");
		back.setFontSize(16);
		swap = new Button(110, 560, 70, 30, "FLIP");
		swap.setFontSize(16);
		
		event = new TextField(950, 50, 220, 30, "Event");
		site = new TextField(950, 90, 220, 30, "Site");
		date = new TextField(950, 130, 220, 30, "Date");
		round = new TextField(950, 170, 220, 30, "Round");
		white = new TextField(950, 210, 220, 30, "White");
		black = new TextField(950, 250, 220, 30, "Black");
		result = new TextField(950, 290, 220, 30, "Result");
		comments = new TextField(920, 450, 280, 240, "");
	}
	
	@Override
	public void update(Game game) 
	{
		//Saving Button
		save.update();
		if(save.IsClicked())
		{
			FileDialog dialog = new FileDialog((Frame)null, "Save Game", FileDialog.SAVE);
			dialog.setDirectory("res/games");
		    dialog.setVisible(true);
		    String path = dialog.getDirectory() + dialog.getFile();
		    if(dialog.getDirectory() != null && dialog.getFile() != null)
		    	SaveLoadManager.saveGame(new GameData(positions, event.getText(), site.getText(), date.getText(), round.getText(), white.getText(), black.getText(), result.getText()), path);
		    return;
		}
		
		//Load Button
		load.update();
		if(load.IsClicked())
		{
			FileDialog dialog = new FileDialog((Frame)null, "Select Game to Open", FileDialog.LOAD);
		    dialog.setDirectory("res/games");
		    dialog.setVisible(true);
		    String file = dialog.getDirectory() + dialog.getFile();
		    if(dialog.getDirectory() != null && dialog.getFile() != null)
		    {
		    	GameData loadedFile = SaveLoadManager.loadGame(file);
				if(loadedFile != null && loadedFile.positions.size() > 0)
				{
					positions = loadedFile.positions;
					loadPosition(positions.get(0));
					event.setText(loadedFile.event);
					site.setText(loadedFile.site);
					date.setText(loadedFile.date);
					round.setText(loadedFile.round);
					white.setText(loadedFile.white);
					black.setText(loadedFile.black);
					result.setText(loadedFile.result);
				}
		    }
		    return;
		}
		
		//Menu Button
		menu.update();
		if(menu.IsClicked())
			game.SetScene(0);
		
		//Reset Button
		reset.update();
		if(reset.IsClicked())
			reset();
		
		//Swap Button
		swap.update();
		if(swap.IsClicked())
			Tile.flip(board);
		
		//Text Fields
		event.update();
		if(event.nextField)
		{
			event.nextField = false;
			site.setSelected(true);
		}
		site.update();
		if(site.nextField)
		{
			site.nextField = false;
			date.setSelected(true);
		}
		date.update();
		if(date.nextField)
		{
			date.nextField = false;
			round.setSelected(true);
		}
		round.update();
		if(round.nextField)
		{
			round.nextField = false;
			white.setSelected(true);
		}
		white.update();
		if(white.nextField)
		{
			white.nextField = false;
			black.setSelected(true);
		}
		black.update();
		if(black.nextField)
		{
			black.nextField = false;
			result.setSelected(true);
		}
		result.update();
		if(result.nextField)
		{
			result.nextField = false;
			comments.setSelected(true);
		}
		comments.update();
		if(comments.nextField)
			comments.nextField = false;
		
		positions.get(turnNumber).comments = comments.getText();
		
		//Scroll through moves
		if(InputHandler.MOUSEX < 200)
		{
			int scrollAmount = InputHandler.getMouseScroll() * 40;
			if(positions.size() > 30)
			{
				historyScroll -= scrollAmount;
				
				int cap = 100 - (positions.size()-30)*12;
				if(positions.get(positions.size()-1).gState != GameState.ONGOING)
					cap -= 72;
				
				if(historyScroll > 40)
					historyScroll = 40;
				else if(historyScroll < cap)
					historyScroll = cap;
			}
			else
				historyScroll = 40;
		}
		
		//Check if a move in the move history is clicked
		int selectedMove = getSelectedMove();
		if(selectedMove != -1 && selectedMove < positions.size())
			loadPosition(positions.get(selectedMove));
		
		//Update move history positions
		for(int i = 1; i < positions.size(); i++)
			positions.get(i).setY((historyScroll + ((i-1)/2) * 24.0f));
			
		//Check if arrow keys or forward and back buttons are pressed
		forward.update();
		back.update();
		if(InputHandler.KeyPressedAndSetFalse(KeyEvent.VK_RIGHT) || forward.IsClicked())
		{
			if(turnNumber < positions.size() - 1)
			{
				int numP = getNumPieces();
				loadPosition(positions.get(turnNumber + 1));
				if(numP == getNumPieces())
					SoundEffect.MOVE.play();
				else
					SoundEffect.CAPTURE.play();
			}
		}
		else if(InputHandler.KeyPressedAndSetFalse(KeyEvent.VK_LEFT) || back.IsClicked())
			if(turnNumber - 1 > -1)
				loadPosition(positions.get(turnNumber-1));
		
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
		Pawn.enPassantTile = -1;
		Pawn.epPawn = -1;
		
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
			moveText = "O-O";
		else if(castling == -2)
			moveText = "O-O-O";
		
		//50 Move Rule
		if(t.GetPiece() instanceof Pawn || captured != null)
			fiftyMoves = 0;
		else
			fiftyMoves++;
		
		if(promotionPiece != ' ')
		{
			if(promotionPiece == 'N')
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
		
		if(turn == Color.BLACK)
			moveText = (turnNumber/2 + 1) + ". " + moveText;
		turnNumber++;
		
		//Override later moves
		if(turnNumber-1 != positions.size() - 1)
			while(turnNumber-1 != positions.size() - 1)
				positions.remove(positions.size()-1);
		
		//Automatically scroll to new move
		if(turnNumber > 30 && historyScroll > 100 - (turnNumber-30)*12)
			historyScroll = 100 - (turnNumber-30)*12;
		
		if(gameState != GameState.ONGOING && (turnNumber > 30 && historyScroll > 100 - (turnNumber-24)*12))
			historyScroll = 100 - (turnNumber-24)*12;
		
		for(Position state : positions)
			state.setTextColor(0xffaaaaaa);
		positions.add(new Position(board, moveText, gameState, turn, turnNumber, fiftyMoves));
		comments.setText("");
		
		if(FLIPONMOVE)
			Tile.flip(board);
		
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
		if(Position.Repitition(positions, board))
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
		gameState = Position.EvaluateState(board, turn);
	}

	public void loadPosition(Position p)
	{
		p.LoadState(board);
		gameState = p.gState;
		if(turn != p.turn && FLIPONMOVE)
			Tile.flip(board);
		turn = p.turn;
		turnNumber = p.moveNumber;
		fiftyMoves = p.fiftyMoves;
		Pawn.enPassantTile = p.epSquare;
		Pawn.epPawn = p.epPawn;
		promoting = null;
		moveOptions = null;
		selectedPieceTile = null;
		draggingPiece = null;
		comments.setText(p.comments);
		
		for(Position state : positions)
			state.setTextColor(0xffaaaaaa);
		p.setTextColor(0xffffffff);
	}
	
	@Override
	public void render(int[] pixels) 
	{
		save.render(pixels);
		load.render(pixels);
		menu.render(pixels);
		reset.render(pixels);
		forward.render(pixels);
		back.render(pixels);
		swap.render(pixels);
		
		event.render(pixels);
		site.render(pixels);
		date.render(pixels);
		round.render(pixels);
		white.render(pixels);
		black.render(pixels);
		result.render(pixels);
		comments.render(pixels);
		
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
				g.drawString("" + (8 - i), (int)((210)*Game.SCALE) + Game.XOFF, (int)((board[i * 8].getY() + 8) * Game.SCALE) + Game.YOFF);
				g.drawString(String.valueOf((char)(i + 65)), (int)((board[i].getX() - 8)*Game.SCALE) + Game.XOFF, (int)((Game.HEIGHT/2 + 270) * Game.SCALE) + Game.YOFF);
			}
		}
		
		
		//Move History
		for(int i = 1; i < positions.size(); i++)
		{
			double y = positions.get(i).getY();
			if(y > 30 && y < 450)
				positions.get(i).renderText(g);
		}
		
		Position lastMove = positions.get(positions.size()-1);
		if(lastMove.gState != GameState.ONGOING)
		{
			g.setColor(Color.WHITE);
			
			int y = (int) ((historyScroll + (positions.size()/2 + 0.5f) * 24.0f)*Game.SCALE) + Game.YOFF;
			if(y > 90*Game.SCALE && y < 450*Game.SCALE)
				g.drawString(lastMove.score, (int) (20*Game.SCALE + Game.XOFF), y);
			y += 24.0f*Game.SCALE;
			if(y > 90*Game.SCALE && y < 450*Game.SCALE)
				g.drawString(lastMove.result, (int) (20*Game.SCALE + Game.XOFF), y);
		}
			
		save.renderText(g);
		load.renderText(g);
		menu.renderText(g);
		reset.renderText(g);
		forward.renderText(g);
		back.renderText(g);
		swap.renderText(g);
		
		event.renderText(g);
		site.renderText(g);
		date.renderText(g);
		round.renderText(g);
		white.renderText(g);
		black.renderText(g);
		result.renderText(g);
		comments.renderText(g);
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
		Pawn.epPawn = -1;
		
		gameState = GameState.ONGOING;
		positions = new ArrayList<Position>();
		positions.add(new Position(board, "", gameState, turn, 0, 0));
		
		event.setText("");
		site.setText("");
		date.setText("");
		round.setText("");
		white.setText("");
		black.setText("");
		result.setText("");
		comments.setText("");
		
		turnNumber = 0;
		fiftyMoves = 0;
		historyScroll = 40;
	}
	
	private int getSelectedMove()
	{
		for(int i = 1; i < positions.size(); i++)
		{
			double y = positions.get(i).getY();
			if(y > 30 && y < 450)
			{
				positions.get(i).update();
				if(positions.get(i).IsClicked())
					return i;
			}
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
	
}
