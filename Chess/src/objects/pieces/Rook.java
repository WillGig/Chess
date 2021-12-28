package objects.pieces;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import objects.Tile;

public class Rook extends Piece{

	public Rook(Tile t, Color color) {
		super(t, color);
	}
	
	@Override
	public String getName()
	{
		return "Rook";
	}

	@Override
	public void renderText(Graphics g)
	{
		g.setFont(new Font("Arial", 1, 20));
		g.drawString("R", (int)x - 10, (int)y + 10);
	}
	
	@Override
	public ArrayList<Tile> getPossibleMoves(Tile[] board) {
		ArrayList<Tile> moves = new ArrayList<Tile>();
		//up
		for(int y = tileY - 1; y > -1; y--)
		{
			Piece p = board[tileX + y*8].GetPiece();
			if(p != null && p.getColor() == getColor())
				break;
			moves.add(board[tileX + y*8]);
			if(p != null)
				break;
		}
		
		//down
		for(int y = tileY + 1; y < 8; y++)
		{
			Piece p = board[tileX + y*8].GetPiece();
			if(p != null && p.getColor() == getColor())
				break;
			moves.add(board[tileX + y*8]);
			if(p != null)
				break;
		}
		
		//left
		for(int x = tileX - 1; x > -1; x--)
		{
			Piece p = board[x + tileY*8].GetPiece();
			if(p != null && p.getColor() == getColor())
				break;
			moves.add(board[x + tileY*8]);
			if(p != null)
				break;
		}
		
		//right
		for(int x = tileX + 1; x < 8; x++)
		{
			Piece p = board[x + tileY*8].GetPiece();
			if(p != null && p.getColor() == getColor())
				break;
			moves.add(board[x + tileY*8]);
			if(p != null)
				break;
		}
		
		return moves;
	}

}
