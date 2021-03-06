package objects.pieces;

import java.awt.Color;
import java.util.ArrayList;

import objects.ImageButton;
import objects.Tile;

public class Pawn extends Piece{

	private int direction;
	
	public static int enPassantTile, epPawn;
	
	private ImageButton[] promotionOptions;
	
	public Pawn(Tile t, Color c) {
		super(t, c);

		String colorText;
		if(c == Color.WHITE) 
		{
			direction = -1;
			colorText = "White";
		}
		else
		{
			direction = 1;
			colorText = "Black";
		}
		
		promotionOptions = new ImageButton[4];
		promotionOptions[0] = new ImageButton(0, 0, width, height, "Knight" + colorText);
		promotionOptions[1] = new ImageButton(0, 0, width, height, "Bishop" + colorText);
		promotionOptions[2] = new ImageButton(0, 0, width, height, "Rook" + colorText);
		promotionOptions[3] = new ImageButton(0, 0, width, height, "Queen" + colorText);
	}

	@Override
	public String getName()
	{
		return "Pawn";
	}
	
	@Override
	public String getNotationName()
	{
		return "";
	}
	
	@Override
	public char getFENName()
	{
		return getColor() == Color.WHITE ? 'P' : 'p';
	}
	
	@Override
	public Piece move(Tile start, Tile end, Tile[] board)
	{
		//Set en passant tile if moved forward twice
		if(Math.abs(end.getTileY() - start.getTileY()) == 2)
		{
			epPawn = end.getTileX() + end.getTileY() * 8;
			enPassantTile = tileX + (tileY + direction)*8;
		}
			
		Piece p = super.move(start, end, board);

		//Captured en passant
		if(end.getTileX() != start.getTileX() && p == null)
		{
			p = board[tileX + (tileY - direction)*8].getPiece();
			board[tileX + (tileY - direction)*8].setPiece(null);
		}
		
		return p;
	}
	
	@Override
	public void unmove(Tile start, Tile end, Tile[] board, Piece captured)
	{
		//Check if captured en passant
		int cY = -1;
		if(captured != null && end.getTileY() != captured.tileY)
			cY = captured.tileY;
		
		super.unmove(start, end, board, captured);
		
		//Restore piece captured by en passant to correct square
		if(cY != -1)
		{
			board[captured.tileX + cY * 8].setPiece(captured);
			end.setPiece(null);
		}
	}
	
	@Override
	public ArrayList<Tile> getPossibleMoves(Tile[] board) {
		ArrayList<Tile> moves = new ArrayList<Tile>();
		if(Tile.getPieceAtTile(tileX, tileY + direction, board) == null)
		{
			moves.add(board[tileX + (tileY + direction) * 8]);
			if(!hasMoved() && Tile.getPieceAtTile(tileX, tileY + direction * 2, board) == null)
				moves.add(board[tileX + (tileY + direction * 2) * 8]);
		}
		
		Piece p = Tile.getPieceAtTile(tileX - 1, tileY + direction, board);
		if(p != null && p.getColor() != getColor())
			moves.add(board[tileX - 1 + (tileY + direction) * 8]);
		
		p = Tile.getPieceAtTile(tileX + 1, tileY + direction, board);
		if(p != null && p.getColor() != getColor())
			moves.add(board[tileX + 1 + (tileY + direction) * 8]);
		
		if(epPawn != -1 && board[enPassantTile].getTileY() == tileY + direction)
		{
			if(Math.abs(board[enPassantTile].getTileX() - tileX) < 2)
				moves.add(board[enPassantTile]);
		}
		
		return moves;
	}
	
	public ImageButton[] GetPromotionOptions()
	{
		return promotionOptions;
	}
	
	public void UpdatePromotionPosition(Tile t)
	{
		for(int i = 0; i < promotionOptions.length; i++)
		{
			if(t.getTileX() > 3)
				promotionOptions[i].setX(t.getX() - t.getWidth() * (3-i));
			else
				promotionOptions[i].setX(t.getX() + t.getWidth() * i);
			promotionOptions[i].setY(t.getY());
		}
	}

}
