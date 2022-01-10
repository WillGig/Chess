package objects.pieces;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import objects.Tile;

public class Knight extends Piece{

	public Knight(Tile t, Color c) {
		super(t, c);
	}
	
	@Override
	public String getName()
	{
		return "Knight";
	}
	
	@Override
	public String getNotationName()
	{
		return "N";
	}
	
	@Override
	public char getFENName()
	{
		return getColor() == Color.WHITE ? 'N' : 'n';
	}
	
	static Point[] moveCoords = new Point[] {new Point(2, 1), new Point(2, -1), new Point(-2, 1), new Point(-2, -1),
											new Point(1, 2), new Point(1, -2), new Point(-1, 2), new Point(-1, -2)};
	
	@Override
	public ArrayList<Tile> getPossibleMoves(Tile[] board) {
		ArrayList<Tile> moves = new ArrayList<Tile>();
		
		for(Point p: moveCoords)
		{
			int tx = tileX + p.x;
			int ty = tileY + p.y;
			
			if(tx < 0 || tx > 7 || ty < 0 || ty > 7)
				continue;
			
			Piece piece = board[tx + ty * 8].getPiece();
			if(piece == null || piece.getColor() != getColor())
				moves.add(board[tx + ty * 8]);
		}
		
		return moves;
	}

}
