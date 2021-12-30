package objects.pieces;

import java.awt.Color;
import java.util.ArrayList;

import objects.Tile;

public class King extends Piece{

	public King(Tile t, Color c) {
		super(t, c);
	}
	
	@Override
	public String getName()
	{
		return "King";
	}
	
	@Override
	public String getNotationName()
	{
		return "K";
	}

	@Override
	public Piece move(Tile start, Tile end, Tile[] board)
	{
		//Castle Kingside
		if(end.getTileX() - start.getTileX() == 2)
			board[7 + tileY * 8].GetPiece().move(board[7 + tileY * 8], board[5 + tileY * 8], board);
		
		//Castle Queenside
		if(end.getTileX() - start.getTileX() == -2)
			board[0 + tileY * 8].GetPiece().move(board[0 + tileY * 8], board[3 + tileY * 8], board);
				
		
		return super.move(start, end, board);
	}
	
	@Override
	public void unmove(Tile start, Tile end, Tile[] board, Piece captured)
	{
		super.unmove(start, end, board, captured);
		//Castle Kingside
		if(end.getTileX() - start.getTileX() == 2)
		{
			board[5 + tileY * 8].GetPiece().move(board[5 + tileY * 8], board[7 + tileY * 8], board);
			board[7 + tileY * 8].GetPiece().changeMoved(-2);
		}
			
		//Castle Queenside
		if(end.getTileX() - start.getTileX() == -2)
		{
			board[3 + tileY * 8].GetPiece().move(board[3 + tileY * 8], board[0 + tileY * 8], board);
			board[0 + tileY * 8].GetPiece().changeMoved(-2);
		}
	}
	
	@Override
	public ArrayList<Tile> getPossibleMoves(Tile[] board) {
		ArrayList<Tile> moves = new ArrayList<Tile>();
		
		for(int y = -1; y < 2; y++)
		{
			for(int x = -1; x < 2; x++)
			{
				//Cannot move to current square
				if(x == 0 && y == 0)
					continue;
				
				int tx = tileX + x;
				int ty = tileY + y;
				
				//square must be on board
				if(tx < 0 || tx > 7 || ty < 0 || ty > 7)
					continue;
				
				Piece p = board[tx + ty*8].GetPiece();
				if(p == null || p.getColor() != getColor())
					moves.add(board[tx + ty*8]);
			}
		}
		
		//Castling
		if(!hasMoved())
		{
			//Kingside
			Piece rook = board[7 + tileY * 8].GetPiece();
			if(rook instanceof Rook && !rook.hasMoved())
			{
				Tile eFile = board[4 + tileY * 8];
				Tile fFile = board[5 + tileY * 8];
				Tile gFile = board[6 + tileY * 8];
				
				if(gFile.GetPiece() == null && fFile.GetPiece() == null)
				{
					boolean squaresAttacked = false;
					for(int i = 0; i < board.length; i++)
					{
						Piece p = board[i].GetPiece();
						if(p == null || p instanceof King || p.getColor() == getColor())
							continue;
						
						ArrayList<Tile> pMoves = p.getPossibleMoves(board);
						
						if(pMoves.contains(gFile) || pMoves.contains(fFile) || pMoves.contains(eFile))
						{
							squaresAttacked = true;
							break;
						}
					}
					
					if(!squaresAttacked)
						moves.add(gFile);
				}
			}
					
			//Queenside
			rook = board[tileY * 8].GetPiece();
			if(rook instanceof Rook && !rook.hasMoved())
			{
				Tile eFile = board[4 + tileY * 8];
				Tile dFile = board[3 + tileY * 8];
				Tile cFile = board[2 + tileY * 8];
				
				if(dFile.GetPiece() == null && cFile.GetPiece() == null)
				{
					boolean squaresAttacked = false;
					for(int i = 0; i < board.length; i++)
					{
						if(i == tileX + tileY*8)
							continue;
						
						Piece p = board[i].GetPiece();
						if(p == null || p instanceof King || p.getColor() == getColor())
							continue;
						
						ArrayList<Tile> pMoves = p.getPossibleMoves(board);
						
						if(pMoves.contains(cFile) || pMoves.contains(dFile) || pMoves.contains(eFile))
						{
							squaresAttacked = true;
							break;
						}
					}
					
					if(!squaresAttacked)
						moves.add(cFile);
				}
			}
		}
		
		return moves;
	}
	
	public boolean inCheck(Tile[] board)
	{
		Tile currentTile = board[tileX + tileY * 8];
		for(int i = 0; i < board.length; i++)
		{
			Piece p = board[i].GetPiece();
			if(p == null || p.getColor() == getColor())
				continue;
			
			if(p.getPossibleMoves(board).contains(currentTile))
				return true;
		}
		return false;
	}

	public static King findKing(Tile[] board, Color c)
	{
		for(int i = 0; i < board.length; i++)
		{
			Piece p = board[i].GetPiece();
			if(p != null && p.getColor() == c && p instanceof King)
				return (King)p;
		}
		System.out.println("Failed to find King of color " + c);
		return null;
	}
	
}
