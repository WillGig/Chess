package game;

import java.awt.Color;

import objects.Tile;
import objects.pieces.Bishop;
import objects.pieces.King;
import objects.pieces.Knight;
import objects.pieces.Pawn;
import objects.pieces.Piece;
import objects.pieces.Queen;
import objects.pieces.Rook;
import scenes.Chess.GameState;

public class State {

	private String state = "", colors = "";
	public String whiteMoves, blackMoves;
	public GameState gState;
	public Color turn;
	public int moveNumber;
	
	public State(Tile[] board, String wMoves, String bMoves, GameState gs, Color turn, int move)
	{
		whiteMoves = wMoves;
		blackMoves = bMoves;
		gState = gs;
		this.turn = turn;
		moveNumber = move;
		for(int i = 0; i < board.length; i++)
		{
			Piece p = board[i].GetPiece();
			if(p == null)
			{
				state += "0";
				colors += "0";
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
			switch(state.charAt(i))
			{
			case '0':
				board[i].SetPiece(null);
				break;
			case 'p':
				new Pawn(board[i], c);
				break;
			case 'N':
				new Knight(board[i], c);
				break;
			case 'B':
				new Bishop(board[i], c);
				break;
			case 'R':
				new Rook(board[i], c);
				break;
			case 'Q':
				new Queen(board[i], c);
				break;
			case 'K':
				new King(board[i], c);
				break;
			default:
				break;
			}
		}
	}
	
}
