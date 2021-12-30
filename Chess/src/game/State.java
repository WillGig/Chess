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

public class State extends Button{

	public String state = "", colors = "", numMoves = "";
	public GameState gState;
	public Color turn;
	public int moveNumber, epSquare, fiftyMoves;
	public Pawn epPawn;
	
	public State(Tile[] board, String moveText, GameState gs, Color turn, int move, int fiftyMoves)
	{
		super(turn == Color.BLACK ? 50 : 150, 0, 70, 20, moveText);
		setTextColor(0xffffffff);
		setTextCentered(false);
		gState = gs;
		this.turn = turn;
		moveNumber = move;
		this.fiftyMoves = fiftyMoves;
		epPawn = Pawn.epPawn;
		epSquare = Pawn.enPassantTile;
		
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
	
	public static boolean Repitition(ArrayList<State> states, Tile[] board)
	{
		State currentState = new State(board, "", null, null, 0, 0);
		
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
	
}
