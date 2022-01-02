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

	public String state = "", colors = "", numMoves = "", score = "", result = "", comments = "";
	public GameState gState;
	public Color turn;
	public int moveNumber, epSquare, epPawn, fiftyMoves;
	
	public Position(Tile[] board, String moveText, GameState gs, Color turn, int move, int fiftyMoves)
	{
		super(turn == Color.BLACK ? 60 : 160, 0, 70, 20, moveText);
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
	
	public static boolean Repitition(ArrayList<Position> states, Tile[] board)
	{
		Position currentState = new Position(board, "", null, null, 0, 0);
		
		for(int i = 0; i < states.size(); i++)
		{
			int count = 0;
			if(states.get(i).state.equals(currentState.state))
				count++;
			for(int j = i; j < states.size(); j++)
			{
				if(states.get(i).state.equals(states.get(j).state))
				{
					count++;
					if(count == 3)
						return true;
				}
			}
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
