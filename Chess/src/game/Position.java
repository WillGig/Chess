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
	
	public String state = "", colors = "", numMoves = "", score = "", result = "", comments = "";
	public GameState gState;
	public Color turn;
	public int moveNumber, epSquare, epPawn, fiftyMoves;
	
	public Position(Tile[] board, String moveText, GameState gs, Color turn, int move, int fiftyMoves, Position parent)
	{
		super(turn == Color.BLACK ? 60 : 160, 0, 70, 20, moveText);
		this.parent = parent;
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
		
		for(int i = 0; i < board.length; i++)
		{
			Piece p = board[i].GetPiece();
			if(p == null)
			{
				state += "0";
				colors += "0";
				numMoves += "0";
			}
			else
			{
				if(p.getColor() == Color.WHITE)
					colors += "w";
				else
					colors += "b";
				
				if(p instanceof Pawn)
					state += "p";
				else
					state += p.getNotationName();
				
				if(p.hasMoved())
					numMoves += "1";
				else
					numMoves += "0";
			}
		}
	}
	
	public void LoadState(Tile[] board)
	{
		for(int i = 0; i < board.length; i++)
		{
			char color = colors.charAt(i);
			Color c;
			if(color == 'w')
				c = Color.WHITE;
			else
				c = Color.BLACK;
			
			int nMoves = Character.getNumericValue(numMoves.charAt(i));
			
			switch(state.charAt(i))
			{
			case '0':
				board[i].SetPiece(null);
				break;
			case 'p':
				new Pawn(board[i], c).setNumberOfMoves(nMoves);
				break;
			case 'N':
				new Knight(board[i], c).setNumberOfMoves(nMoves);
				break;
			case 'B':
				new Bishop(board[i], c).setNumberOfMoves(nMoves);
				break;
			case 'R':
				new Rook(board[i], c).setNumberOfMoves(nMoves);
				break;
			case 'Q':
				new Queen(board[i], c).setNumberOfMoves(nMoves);
				break;
			case 'K':
				new King(board[i], c).setNumberOfMoves(nMoves);
				break;
			default:
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
		
		int yStart = yPos;
		setY(yPos);
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
		
		//Return to Main Line
		if(children.size() > 0)
		{
			double temp = children.get(0).getY();
			yPos += children.get(0).setPositionOfTree(yPos);
			children.get(0).setY(temp);
		}
		
		if(yPos - yStart < 24)
			return 24;
		
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
			setText("(" + getText());
		
		//If end of line, close all brackets
		if(children.size() == 0)
		{
			for(int i = 0; i < numberOfOpenBrackets; i++)
				setText(getText() + ")");
		}
		else
		{
			//continue main line with same number of brackets
			children.get(0).setLineBrackets(numberOfOpenBrackets, false);
			//alternate lines start a new set of brackets
			for(int i = 1; i < children.size(); i++)
				children.get(i).setLineBrackets(numberOfOpenBrackets+1, true);
		}
	}
	
	public Position hasChild(Position child)
	{
		for(Position p : children)
			if(p.state.equals(child.state))
				return p;
			
		return null;
	}
	
	public boolean repitition()
	{
		int count = 1;
		Position current = parent;
		while(current != null)
		{
			if(current.state.equals(state))
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
			return GameState.ONGOING;
		
		if(King.findKing(board, turn).inCheck(board))
			return GameState.CHECKMATE;
		else
			return GameState.STALEMATE;
	}
}
