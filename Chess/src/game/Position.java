package game;

import java.awt.Color;
import java.util.ArrayList;
import objects.Button;
import objects.Tile;
import objects.pieces.Bishop;
import objects.pieces.King;
import objects.pieces.Knight;
import objects.pieces.Pawn;
import objects.pieces.Piece;
import objects.pieces.Queen;
import objects.pieces.Rook;
import scenes.Chess.GameState;

public class Position extends Button{

	private Position parent;
	private ArrayList<Position> children;
	public boolean hidden;
	
	public String FEN = "", pieces = "", rawText = "", comments = "", result = "", score = "";
	public GameState gState;
	public Color turn;
	public int moveNumber, epSquare, epPawn, fiftyMoves;
	
	public Position(Tile[] board, String moveText, GameState gs, Color turn, int move, int fiftyMoves, Position parent)
	{
		super(turn == Color.BLACK ? 60 : 160, 0, 70, 20, moveText);
		this.parent = parent;
		rawText = moveText;
		children = new ArrayList<Position>();
		hidden = false;
		setTextColor(Game.DARKMODE ? Color.WHITE : Color.BLACK);
		if(!Game.DARKMODE)
			setHighlightColor(Color.BLACK);
		setFontSize(16);
		setTextCentered(false);
		gState = gs;
		this.turn = turn;
		moveNumber = move;
		this.fiftyMoves = fiftyMoves;
		epPawn = Pawn.epPawn;
		epSquare = Pawn.enPassantTile;
		
		if(gs == GameState.CHECKMATE)
		{
			if(turn == Color.WHITE)
				score = "0-1";
			else
				score = "1-0";
			result = "Checkmate";
		}
		else if(gs != GameState.ONGOING)
			score = "1/2-1/2";
		
		if(gs == GameState.STALEMATE)
			result = "Draw - StaleMate";
		else if(gs == GameState.REPETITION)
			result = "Draw - Repetition";
		else if(gs == GameState.FIFTYMOVEDRAW)
			result = "Draw - 50 Moves";
		
		//Piece placement
		for(int row = 0; row < 8; row++)
		{
			int empty = 0;
			for(int col = 0; col < 8; col++)
			{
				Piece p = board[col + row*8].getPiece();
				if(p == null)
					empty++;
				else
				{
					if(empty > 0)
					{
						pieces += "" + empty;
						empty = 0;
					}
					pieces += p.getFENName();
				}
			}
			if(empty > 0)
				pieces += "" + empty;
			pieces += "/";
		}
		
		FEN += pieces;
		
		//Active color
		FEN += turn == Color.WHITE ? " w " : " b ";
		
		//Castling
		Piece K = board[4 + 7*8].getPiece();
		if(K != null && K instanceof King && K.getColor() == Color.WHITE && !K.hasMoved())
		{
			Piece kr = board[7 + 7*8].getPiece();
			if(kr != null && kr instanceof Rook && kr.getColor() == Color.WHITE && !kr.hasMoved())
				FEN += "K";
			Piece qr = board[0 + 7*8].getPiece();
			if(qr != null && qr instanceof Rook && qr.getColor() == Color.WHITE && !qr.hasMoved())
				FEN += "Q";
		}
		
		Piece k = board[4].getPiece();
		if(k != null && k instanceof King && k.getColor() == Color.BLACK && !k.hasMoved())
		{
			Piece kr = board[7].getPiece();
			if(kr != null && kr instanceof Rook && kr.getColor() == Color.BLACK && !kr.hasMoved())
				FEN += "k";
			Piece qr = board[0].getPiece();
			if(qr != null && qr instanceof Rook && qr.getColor() == Color.BLACK && !qr.hasMoved())
				FEN += "q";
		}
		if(FEN.charAt(FEN.length()-1) != ' ')
			FEN += " ";
		
		//En passant square
		if(Pawn.epPawn == -1)
			FEN += "- ";
		else
			FEN += board[Pawn.enPassantTile].getSquareName() + " ";
		
		//HalfMove Clock
		FEN += fiftyMoves + " ";
		
		//FullMove Clock
		FEN += (move/2 + 1);
	}
	
	public void loadPieces(Tile[] board)
	{
		int row = 0, col = 0;
		for(int i = 0; i < pieces.length(); i++)
		{
			char c = pieces.charAt(i);
			switch(c)
			{
			case '/':
				row++;
				col = 0;
				break;
			case 'P':
				new Pawn(board[col + row*8], Color.WHITE);
				col++;
				break;
			case 'p':
				new Pawn(board[col + row*8], Color.BLACK);
				col++;
				break;
			case 'N':
				new Knight(board[col + row*8], Color.WHITE);
				col++;
				break;
			case 'n':
				new Knight(board[col + row*8], Color.BLACK);
				col++;
				break;
			case 'B':
				new Bishop(board[col + row*8], Color.WHITE);
				col++;
				break;
			case 'b':
				new Bishop(board[col + row*8], Color.BLACK);
				col++;
				break;
			case 'R':
				new Rook(board[col + row*8], Color.WHITE);
				col++;
				break;
			case 'r':
				new Rook(board[col + row*8], Color.BLACK);
				col++;
				break;
			case 'Q':
				new Queen(board[col + row*8], Color.WHITE);
				col++;
				break;
			case 'q':
				new Queen(board[col + row*8], Color.BLACK);
				col++;
				break;
			case 'K':
				new King(board[col + row*8], Color.WHITE);
				col++;
				break;
			case 'k':
				new King(board[col + row*8], Color.BLACK);
				col++;
				break;
			default:
				for(int j = 0; j < Character.getNumericValue(c); j++)
				{
					board[col + row*8].setPiece(null);
					col++;
				}
				break;
			}
		}
	}
	
	public Position getParent()
	{
		return parent;
	}
	
	public Position getHead()
	{
		if(parent != null)
			return parent.getHead();
		return this;
	}
	
	public void addChild(Position child)
	{
		children.add(child);
	}
	
	public void removeChild(Position child)
	{
		children.remove(child);
	}
	
	public void delete()
	{
		parent.removeChild(this);
	}
	
	public void promote()
	{
		parent.promoteChild(this);
	}
	
	public void promoteChild(Position child)
	{
		for(int i = 1; i < children.size(); i++)
		{
			if(children.get(i).equals(child))
			{
				Position temp = children.get(i-1);
				children.set(i-1, child);
				children.set(i, temp);
				return;
			}
		}
	}
	
	public ArrayList<Position> getChildren()
	{
		return children;
	}
	
	public Position getNextPosition()
	{
		if(children.size() > 0)
			return children.get(0);
		return null;
	}
	
	public ArrayList<Position> getAllDescendants()
	{
		ArrayList<Position> descendants = new ArrayList<Position>();
		if(children.size() != 0)
			for(Position p : children)
				descendants.addAll(p.getAllDescendants());
		descendants.add(this);
		return descendants;
	}
	
	public Position getEndOfLine()
	{
		if(children.size() != 0)
			return children.get(0).getEndOfLine();
		return this;
	}
	
	public Position getLowestPosition()
	{
		Position lowest = null;
		for(Position p : getAllDescendants())
		{
			if(p.hidden)
				continue;
			
			if(lowest == null || lowest.getY() < p.getY())
				lowest = p;
		}
		return lowest;
	}
	
	public void showLine(boolean show)
	{
		for(Position p : children)
		{
			p.hidden = !show;
			p.showLine(show);
		}
	}
	
	public int setPositionOfTree(int yPos)
	{
		if(hidden)
			return 0;
		
		setY(yPos);
		
		if(children.size() == 0)
			return 24;
		
		int yStart = yPos;
		
		//Add row if black made a move
		if(turn == Color.WHITE)
			yPos += 24;
		
		//Main Move
		if(children.size() > 0)
		{
			children.get(0).setY(yPos);
			if(children.size() > 1)
				yPos += 24;
		}
		
		//Alternate Lines
		for(int i = 1; i < children.size(); i++)
			yPos += children.get(i).setPositionOfTree(yPos);
		if(children.size() > 1)
			yPos -= 24;
			
		//Return to Main Line
		if(children.size() > 0)
		{
			double temp = children.get(0).getY();
			yPos += children.get(0).setPositionOfTree(yPos);
			children.get(0).setY(temp);
		}
		
		return yPos - yStart;
	}
	
	//Goes through tree originating from position and sets text to include correct number of ( and )
	//for each line
	public void setLineBrackets(int numberOfOpenBrackets, boolean startOfLine)
	{
		//Trim any brackets
		setText(getText().replace("(", ""));
		setText(getText().replace(")", ""));
		
		if(startOfLine)
		{
			if(turn == Color.BLACK)
				setText("(" + getText());
			else
			{
				if(children.size() > 0 && children.get(0).hidden)
					setText("(" + moveNumber/2 + "... " + rawText + "...");
				else
					setText("(" + moveNumber/2 + "... " + rawText);
			}
				
		}
		
		//If end of line, close all brackets
		if(children.size() == 0)
		{
			for(int i = 0; i < numberOfOpenBrackets; i++)
				setText(getText() + ")");
		}
		else
		{
			// line continues with same number of brackets
			if(children.size() == 1) 
			{
				children.get(0).setLineBrackets(numberOfOpenBrackets, false);
			}
			// line continuation will hold the remaining open brackets, new lines get 1 bracket
			else if(children.get(0).getChildren().size() > 0)
			{
				children.get(0).setLineBrackets(numberOfOpenBrackets, false);
				for(int i = 1; i < children.size(); i++)
					children.get(i).setLineBrackets(1, true);
			}
			//main line is on last move, alternate lines will end with remaining open brackets
			else
			{
				children.get(0).setLineBrackets(0, false);
				for(int i = 1; i < children.size(); i++)
					if(i == children.size() - 1)
						children.get(i).setLineBrackets(numberOfOpenBrackets+1, true);
					else
						children.get(i).setLineBrackets(1, true);
			}
		}
	}
	
	public Position hasChild(Position child)
	{
		for(Position p : children)
			if(p.FEN.equals(child.FEN))
				return p;
			
		return null;
	}
	
	public boolean repitition()
	{
		int count = 1;
		Position current = parent;
		while(current != null)
		{
			if(current.pieces.equals(pieces))
				count++;
			if(count > 2)
				return true;
			current = current.parent;
		}
		return false;
	}
	
	//Determines if the current positions is checkmate, Stalemate, or neither
	public static GameState EvaluateState (Tile[] board, Color turn)
	{
		boolean canMove = false;
		for(int i = 0; i < board.length; i++)
		{
			Piece p = board[i].getPiece();
			
			if(p == null || p.getColor() != turn)
				continue;
			
			if(!p.getLegalMoves(board).isEmpty())
			{
				canMove = true;
				break;
			}
		}
		
		if(canMove)
			return GameState.ONGOING;
		
		if(King.findKing(board, turn).inCheck(board))
			return GameState.CHECKMATE;
		else
			return GameState.STALEMATE;
	}
}
