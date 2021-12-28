package objects.pieces;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import objects.Button;
import objects.ImageButton;
import objects.Tile;

public class Pawn extends Piece{

	private int direction;
	
	public static int enPassantTile;
	public static Pawn epPawn;
	
	Button[] promotionOptions;
	
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
		
		promotionOptions = new Button[4];
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
	public void renderText(Graphics g)
	{
		g.setFont(new Font("Arial", 1, 20));
		g.drawString("P", (int)x - 10, (int)y + 10);
	}
	
	@Override
	public Piece move(Tile start, Tile end, Tile[] board)
	{
		//Set en passant tile if moved forward twice
		if(Math.abs(end.getTileY() - start.getTileY()) == 2)
		{
			epPawn = this;
			enPassantTile = tileX + (tileY + direction)*8;
		}
			
		Piece p = super.move(start, end, board);

		//Captured en passant
		if(end.getTileX() != start.getTileX() && p == null)
		{
			p = board[tileX + (tileY - direction)*8].GetPiece();
			board[tileX + (tileY - direction)*8].SetPiece(null);
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
			board[captured.tileX + cY * 8].SetPiece(captured);
			end.SetPiece(null);
		}
	}
	
	@Override
	public ArrayList<Tile> getPossibleMoves(Tile[] board) {
		ArrayList<Tile> moves = new ArrayList<Tile>();
		if(Tile.GetPieceAtTile(tileX, tileY + direction, board) == null)
		{
			moves.add(board[tileX + (tileY + direction) * 8]);
			if(!hasMoved() && Tile.GetPieceAtTile(tileX, tileY + direction * 2, board) == null)
				moves.add(board[tileX + (tileY + direction * 2) * 8]);
		}
		
		Piece p = Tile.GetPieceAtTile(tileX - 1, tileY + direction, board);
		if(p != null && p.getColor() != getColor())
			moves.add(board[tileX - 1 + (tileY + direction) * 8]);
		
		p = Tile.GetPieceAtTile(tileX + 1, tileY + direction, board);
		if(p != null && p.getColor() != getColor())
			moves.add(board[tileX + 1 + (tileY + direction) * 8]);
		
		if(epPawn != null && board[enPassantTile].getTileY() == tileY + direction)
		{
			if(Math.abs(board[enPassantTile].getTileX() - tileX) < 2)
				moves.add(board[enPassantTile]);
		}
		
		return moves;
	}
	
	public Button[] GetPromotionOptions()
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
