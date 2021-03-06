package objects.pieces;

import java.awt.Color;
import java.util.ArrayList;

import objects.Tile;

public class Bishop extends Piece{

	public Bishop(Tile t, Color c) {
		super(t, c);
	}
	
	@Override
	public String getName()
	{
		return "Bishop";
	}
	
	@Override
	public String getNotationName()
	{
		return "B";
	}
	
	@Override
	public char getFENName()
	{
		return getColor() == Color.WHITE ? 'B' : 'b';
	}
	
	@Override
	public ArrayList<Tile> getPossibleMoves(Tile[] board) {
		ArrayList<Tile> moves = new ArrayList<Tile>();
		
		//upright
		int x = tileX + 1;
		for(int y = tileY - 1; y > -1 && x < 8; y--)
		{
			Piece p = board[x + y*8].getPiece();
			if(p != null && p.getColor() == getColor())
				break;
			moves.add(board[x + y*8]);
			if(p != null)
				break;
			x++;
		}
		
		//downright
		x = tileX + 1;
		for(int y = tileY + 1; y < 8 && x < 8; y++)
		{
			Piece p = board[x + y*8].getPiece();
			if(p != null && p.getColor() == getColor())
				break;
			moves.add(board[x + y*8]);
			if(p != null)
				break;
			x++;
		}
		
		//downleft
		x = tileX - 1;
		for(int y = tileY + 1; y < 8 && x > -1; y++)
		{
			Piece p = board[x + y*8].getPiece();
			if(p != null && p.getColor() == getColor())
				break;
			moves.add(board[x + y*8]);
			if(p != null)
				break;
			x--;
		}
		
		//upleft
		x = tileX - 1;
		for(int y = tileY - 1; y > -1 && x > -1; y--)
		{
			Piece p = board[x + y*8].getPiece();
			if(p != null && p.getColor() == getColor())
				break;
			moves.add(board[x + y*8]);
			if(p != null)
				break;
			x--;
		}
		
		return moves;
	}

}
