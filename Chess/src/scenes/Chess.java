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
import objects.DropDown;
import objects.ImageButton;
import objects.MoveArrow;
import objects.PositionOptions;
import objects.PositionOptions.positionAction;
import objects.ScrollBar;
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
	
	private TextField event, site, date, round, white, black, comments;
	
	private DropDown result;
	
	private Position startPosition, currentPosition;
	
	private ScrollBar moveScroller;
	
	private MoveArrow currentArrow;
	private Tile moveArrowStart;
	private ArrayList<MoveArrow> moveArrows;
	
	private PositionOptions positionOptions;
	
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
		comments = new TextField(920, 450, 280, 240, "");
		
		result = new DropDown(950, 290, 220, 30, "");
		result.addOption(new Button(0, 0, 220, 30, "1-0"));
		result.addOption(new Button(0, 0, 220, 30, "0-1"));
		result.addOption(new Button(0, 0, 220, 30, "1/2-1/2"));
		
		moveScroller = new ScrollBar(190, 240, 15, 430, 0);
	}
	
	@Override
	public void update(Game game) 
	{
		//Check for move arrow being drawn
		if(InputHandler.MouseClicked(2))
		{
			Tile t = Tile.getCursorTile(board);
			if(t != null)
			{
				if(moveArrowStart != null && t != moveArrowStart)
					currentArrow = new MoveArrow(moveArrowStart, t);
				else
					moveArrowStart = t;
			}
		}
		else
		{
			if(currentArrow != null)
			{
				if(moveArrows.contains(currentArrow))
					moveArrows.remove(currentArrow);
				else
					moveArrows.add(currentArrow);
				currentArrow = null;
				moveArrowStart = null;
			}
			else
				moveArrowStart = null;
		}
		
		//Saving Button
		save.update();
		if(save.IsClicked())
		{
			FileDialog dialog = new FileDialog((Frame)null, "Save Game", FileDialog.SAVE);
			dialog.setDirectory("res/games");
		    dialog.setVisible(true);
		    String path = dialog.getDirectory() + dialog.getFile();
		    if(dialog.getDirectory() != null && dialog.getFile() != null)
		    	SaveLoadManager.saveGame(new GameData(startPosition, event.getText(), site.getText(), date.getText(), round.getText(), white.getText(), black.getText(), result.getText()), path);
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
				if(loadedFile != null)
				{
					startPosition = loadedFile.startPosition;
					loadPosition(startPosition);
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
		
		//Results Dropdown
		result.update();
		String finalScore = startPosition.getEndOfLine().score;
		if(!finalScore.equals(""))
			result.setText(finalScore);
		
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
			comments.setSelected(true);
		}
		comments.update();
		if(comments.nextField)
			comments.nextField = false;
		
		currentPosition.comments = comments.getText();
		
		//position options
		if(positionOptions != null)
		{
			if(InputHandler.MouseClicked(1))
			{
				positionOptions.update();
				if(positionOptions.status == positionAction.DELETE)
				{
					if(positionOptions.position.getAllDescendants().contains(currentPosition))
					{
						loadPosition(positionOptions.position.getParent());
						scrollToMove();
					}
					positionOptions.position.delete();
					startPosition.setLineBrackets(0, false);
				}
				else if(positionOptions.status == positionAction.PROMOTE)
				{
					positionOptions.position.promote();
					startPosition.setLineBrackets(0, false);
				}
				else if(positionOptions.status == positionAction.TOGGLESHOW)
				{
					Position p = positionOptions.position;
					if(p.getChildren().get(0).hidden)
					{
						p.showLine(true);
						p.setText(p.getText().replace("...", ""));
					}
					else
					{
						p.showLine(false);
						p.setText(p.getText() + "...");
					}
					startPosition.setPositionOfTree(historyScroll - 24);
				}
				else
					InputHandler.MouseClickedAndSetFalse(1);
				positionOptions = null;
			}
			else
				positionOptions.update();
		}
		
		//Update move history positions
		startPosition.setPositionOfTree(historyScroll - 24);
		
		//Calculate scroll cap
		int scrollCap = (int) (436-startPosition.getLowestPosition().getY() + historyScroll);
		if(startPosition.getEndOfLine().gState != GameState.ONGOING)
			scrollCap -= 72;
		if(scrollCap > 40)
			scrollCap = 40;
		
		//Scroll through moves
		if(scrollCap < 40)
		{
			if(InputHandler.MOUSEX < 200 && positionOptions == null)
			{
				int scrollAmount = InputHandler.getMouseScroll() * 40;
				historyScroll -= scrollAmount;
				
				if(historyScroll > 40)
					historyScroll = 40;
				else if(historyScroll < scrollCap)
					historyScroll = scrollCap;
				
				moveScroller.setPosition((historyScroll - 40.0f)/(scrollCap - 40.0f));
			}
			
			moveScroller.update();
			moveScroller.setHeightOfScrollMaterial(moveScroller.getHeight() - (scrollCap - 40));
			if(moveScroller.getPosition() != -1)
				historyScroll = (int) (moveScroller.getPosition() * (scrollCap - 40)) + 40;
			moveScroller.setPosition((historyScroll - 40.0f)/(scrollCap - 40.0f));
		}
		else
		{
			historyScroll = 40;
			moveScroller.setHeightOfScrollMaterial(moveScroller.getHeight());
		}
		
		//Check if a move in the move history is clicked
		Position selected = getSelectedMove();
		if(selected != null)
			loadPosition(selected);
		
		//Check if arrow keys or forward and back buttons are pressed
		if(positionOptions == null)
		{
			forward.update();
			back.update();
			if(InputHandler.KeyPressedAndSetFalse(KeyEvent.VK_RIGHT) || forward.IsClicked())
			{
				if(currentPosition.getChildren().size() > 0)
				{
					int numP = Tile.getNumPieces(board);
					loadPosition(currentPosition.getChildren().get(0));
					scrollToMove();
					if(numP == Tile.getNumPieces(board))
						SoundEffect.MOVE.play();
					else
						SoundEffect.CAPTURE.play();
				}
			}
			else if(InputHandler.KeyPressedAndSetFalse(KeyEvent.VK_LEFT) || back.IsClicked())
			{
				if(turnNumber - 1 > -1)
				{
					loadPosition(currentPosition.getParent());
					scrollToMove();
				}
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
		//Reset en passant data. Will be updated if a pawn moves
		Pawn.enPassantTile = -1;
		Pawn.epPawn = -1;
		
		//Check for castling
		int castling = 0;
		if(selectedPieceTile.GetPiece() instanceof King)
			castling = t.getTileX() - selectedPieceTile.getTileX();
		
		//Piece name
		String moveText = selectedPieceTile.GetPiece().getNotationName();
		
		//Disambiguation text
		moveText += Piece.getDisambiguationText(moveText, selectedPieceTile, t, board);
		
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
		
		//Automatically scroll to new move
		scrollToMove();
		
		for(Position position : startPosition.getAllDescendants())
			position.setTextColor(Game.DARKMODE ? new Color(0xffaaaaaa) : new Color(0xff777777));
		
		Position newPos = new Position(board, moveText, gameState, turn, turnNumber, fiftyMoves, currentPosition);
		Position alreadyExists = currentPosition.hasChild(newPos);
		if(alreadyExists != null)
		{
			currentPosition = alreadyExists;
			currentPosition.setTextColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
		}
		else
		{
			currentPosition.addChild(newPos);
			currentPosition = newPos;
			comments.setText("");
		}
		
		startPosition.setLineBrackets(0, false);
		
		if(FLIPONMOVE)
			Tile.flip(board);
		
		selectedPieceTile = null;
		draggingPiece = null;
		
		if(captured == null)
			SoundEffect.MOVE.play();
		else
			SoundEffect.CAPTURE.play();
		
		moveArrows.clear();
	}
	
	public void updateGameState()
	{
		if(turn == Color.WHITE)
			turn = Color.BLACK;
		else
			turn = Color.WHITE;
		
		//Check for 3 fold repetition
		if(currentPosition.repitition())
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

	//Sets board to position from move history
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
		moveArrows.clear();
		
		for(Position state : startPosition.getAllDescendants())
			state.setTextColor(Game.DARKMODE ? new Color(0xffaaaaaa) : new Color(0xff777777));
		p.setTextColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
		
		currentPosition = p;
	}
	
	@Override
	public void render(int[] pixels) 
	{
		//Buttons
		save.render(pixels);
		load.render(pixels);
		menu.render(pixels);
		reset.render(pixels);
		forward.render(pixels);
		back.render(pixels);
		swap.render(pixels);
		
		//Scroll bar
		moveScroller.render(pixels);
		
		//Text Fields
		event.render(pixels);
		site.render(pixels);
		date.render(pixels);
		round.render(pixels);
		white.render(pixels);
		black.render(pixels);
		comments.render(pixels);
		
		//Drop Downs
		result.render(pixels);
		
		//Board
		for(int i = 0; i < board.length; i++)
		{
			board[i].render(pixels);
			Piece p = board[i].GetPiece();
			if(p != null && ! (p == draggingPiece))
				p.render(pixels);
		}
			
		//Highlighted Squares
		if(selectedPieceTile != null)
			for(Tile t: moveOptions)
				t.RenderHighLighted(pixels);
		
		//Move Arrows
		for(MoveArrow a: moveArrows)
			a.render(pixels);
		if(currentArrow != null)
			currentArrow.render(pixels);
		
		//Piece dragging over board
		if(draggingPiece != null)
			draggingPiece.render(pixels);
		
		//Promotion Options
		if(promoting != null)
			for(int i = 0; i < promoting.GetPromotionOptions().length; i++)
				promoting.GetPromotionOptions()[i].render(pixels);
		
		//Position Options
		if(positionOptions != null)
			positionOptions.render(pixels);
	}

	@Override
	public void renderText(Graphics g) 
	{
		//Coordinates
		if(SHOWCOORDS)
		{
			g.setFont(new Font("Arial", 1, (int)(20*Game.SCALE)));
			g.setColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
			for(int i = 0; i < 8; i++)
			{
				g.drawString("" + (8 - i), (int)((210)*Game.SCALE) + Game.XOFF, (int)((board[i * 8].getY() + 8) * Game.SCALE) + Game.YOFF);
				g.drawString(String.valueOf((char)(i + 65)), (int)((board[i].getX() - 8)*Game.SCALE) + Game.XOFF, (int)((Game.HEIGHT/2 + 270) * Game.SCALE) + Game.YOFF);
			}
		}
		
		//Move History
		for(Position p : startPosition.getAllDescendants())
			if(!p.hidden && p.getY() > 30 && p.getY() < 450 && (positionOptions == null || !positionOptions.overlapsRect((int)p.getX(), (int)p.getY(), p.getWidth(), p.getHeight())))
				p.renderText(g);
		
		//Game result
		Position lastMove = startPosition.getEndOfLine();
		if(lastMove.gState != GameState.ONGOING)
		{
			g.setColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
			
			int y = (int) ((startPosition.getLowestPosition().getY()+36.0f)*Game.SCALE) + Game.YOFF;
			if(y > 90*Game.SCALE + Game.YOFF && y < 450*Game.SCALE + Game.YOFF)
				g.drawString(lastMove.score, (int) (20*Game.SCALE + Game.XOFF), y);
			y += 24.0f*Game.SCALE;
			if(y > 90*Game.SCALE + Game.YOFF && y < 450*Game.SCALE + Game.YOFF)
				g.drawString(lastMove.result, (int) (20*Game.SCALE + Game.XOFF), y);
		}
			
		//Buttons
		save.renderText(g);
		load.renderText(g);
		menu.renderText(g);
		reset.renderText(g);
		forward.renderText(g);
		back.renderText(g);
		swap.renderText(g);
		
		//Drop Downs
		g.setColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
		g.drawString("Result", (int)(770*Game.SCALE + Game.XOFF), (int)((result.getY()+5)*Game.SCALE + Game.YOFF));
		result.renderText(g);
		
		//Text Fields
		event.renderText(g);
		site.renderText(g);
		date.renderText(g);
		round.renderText(g);
		white.renderText(g);
		black.renderText(g);
		if(!result.isShowingOptions())
			comments.renderText(g);
		
		//Position Options
		if(positionOptions != null)
			positionOptions.renderText(g);
	}

	@Override
	public void start() 
	{
		CANCONTINUE = true;
		
		event.setLabelColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
		site.setLabelColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
		date.setLabelColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
		round.setLabelColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
		white.setLabelColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
		black.setLabelColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
		
		result.setFillColor(Game.DARKMODE ? 0xffffffff : 0xffdddddd);
		event.setFillColor(Game.DARKMODE ? 0xffffffff : 0xffdddddd);
		site.setFillColor(Game.DARKMODE ? 0xffffffff : 0xffdddddd);
		date.setFillColor(Game.DARKMODE ? 0xffffffff : 0xffdddddd);
		round.setFillColor(Game.DARKMODE ? 0xffffffff : 0xffdddddd);
		white.setFillColor(Game.DARKMODE ? 0xffffffff : 0xffdddddd);
		black.setFillColor(Game.DARKMODE ? 0xffffffff : 0xffdddddd);
		result.setFillColor(Game.DARKMODE ? 0xffffffff : 0xffdddddd);
		comments.setFillColor(Game.DARKMODE ? 0xffffffff : 0xffdddddd);
		
		if(board == null)
		{
			reset();
			return;
		}
		
		for(Position p : startPosition.getAllDescendants())
		{
			p.setTextColor(Game.DARKMODE ? new Color(0xffaaaaaa) : new Color(0xff777777));
			p.setHighlightColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
		}
		
		currentPosition.setTextColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);	
		
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
		startPosition = new Position(board, "", gameState, turn, 0, 0, null);
		currentPosition = startPosition;
				
		moveArrows = new ArrayList<MoveArrow>();
		
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
	
	private Position getSelectedMove()
	{
		for(Position p : startPosition.getAllDescendants())
		{
			double y = p.getY();
			if(!p.hidden && y > 30 && y < 450)
			{
				p.update();
				if(p.IsClicked())
					return p;
				if(p.containsCursor() && InputHandler.MouseClickedAndSetFalse(2))
					positionOptions = new PositionOptions(p);
			}
		}
		return null;
	}
	
	private void scrollToMove()
	{
		//Find minimum scroll value
		int scrollCap = (int) (436-startPosition.getLowestPosition().getY() + historyScroll);
		if(startPosition.getEndOfLine().gState != GameState.ONGOING)
			scrollCap -= 72;
		if(scrollCap > 40)
			scrollCap = 40;
		
		//Check if scroll is too high
		if(historyScroll < 40 + historyScroll - currentPosition.getY())
		{
			historyScroll = (int)(40 + historyScroll - currentPosition.getY());
			if(historyScroll > 40)
				historyScroll = 40;
			moveScroller.setPosition((historyScroll - 40.0f)/(scrollCap - 40.0f));
			return;
		}
			
		//Check if scroll is too low
		//Check if a space is needed for result text
		if(startPosition.getEndOfLine().gState != GameState.ONGOING && (historyScroll > (int) (364-currentPosition.getY() + historyScroll)))
			historyScroll = (int) (364-currentPosition.getY() + historyScroll);
		else if(historyScroll > (int) (436-currentPosition.getY() + historyScroll))
			historyScroll = (int) (436-currentPosition.getY() + historyScroll);
		
		if(historyScroll > 40)
			historyScroll = 40;
		else if(historyScroll < scrollCap)
			historyScroll = scrollCap;
		
		//Update position of scroll bar
		moveScroller.setPosition((historyScroll - 40.0f)/(scrollCap - 40.0f));
	}
}
