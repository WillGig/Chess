package objects.pieces;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import objects.Tile;

public class Bishop extends Piece{

	public Bishop(Tile t, Color color) {
		super(t, color);
	}
	@Override
	public void renderText(Graphics g)
	{
		g.setFont(new Font("Arial", 1, 20));
		g.drawString("B", (int)x - 10, (int)y + 10);
	}

	@Override
	public String getName()
	{
		return "Bishop";
	}
	
	@Override
	public ArrayList<Tile> getPossibleMoves(Tile[] board) {
		ArrayList<Tile> moves = new ArrayList<Tile>();
		
		//upright
		int x = tileX + 1;
		for(int y = tileY - 1; y > -1 && x < 8; y--)
		{
			Piece p = board[x + y*8].GetPiece();
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
			Piece p = board[x + y*8].GetPiece();
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
			Piece p = board[x + y*8].GetPiece();
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
			Piece p = board[x + y*8].GetPiece();
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
