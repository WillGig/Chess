package objects.pieces;

import java.awt.Color;
import java.util.ArrayList;

import objects.GameObject;
import objects.Tile;
import utils.InputHandler;

public abstract class Piece extends GameObject{
	
	private boolean clicked;
	
	private int moveCounter;
	
	protected int tileX, tileY;
	
	private Color color;
	
	public Piece(Tile t, Color color) {
		super(t.getX(), t.getY(), 64, 64);
		t.SetPiece(this);
		this.color = color;
		tileX = t.getTileX();
		tileY = t.getTileY();
		moveCounter = 0;
	}
	
	public abstract String getName();
	
	public abstract String getNotationName();

	@Override
	public void update() {
		if(ContainsCursor() && InputHandler.MouseClickedAndSetFalse(1))
			clicked = true;
		else
			clicked = false;
	}
	
	//returns captured piece
	public Piece move(Tile start, Tile end, Tile[] board)
	{
		start.SetPiece(null);
		x = end.getX();
		y = end.getY();
		tileX = end.getTileX();
		tileY = end.getTileY();
		Piece p = end.GetPiece();
		end.SetPiece(this);
		moveCounter++;
		return p;
	}
	
	public void unmove(Tile start, Tile end, Tile[] board, Piece captured)
	{
		start.SetPiece(this);
		x = start.getX();
		y = start.getY();
		tileX = start.getTileX();
		tileY = start.getTileY();
		end.SetPiece(captured);
		moveCounter--;
	}
	
	//Gets squares piece can move to
	public abstract ArrayList<Tile> getPossibleMoves(Tile[] board);

	//checks available squares and ensures that moving to them would not put the king in check
	public ArrayList<Tile> getLegalMoves(Tile[] board)
	{
		ArrayList<Tile> moves = getPossibleMoves(board);
		ArrayList<Tile> invalidMoves = new ArrayList<Tile>();
		
		King king = King.findKing(board, getColor());
		Tile currentTile = board[tileX + tileY * 8];
		for(Tile t : moves)
		{
			//Move 
			Piece captured = move(currentTile, t, board);
			//Check if king is in check
			if(king.inCheck(board))
				invalidMoves.add(t);
			//undo move
			unmove(currentTile, t, board, captured);
		}
		
		moves.removeAll(invalidMoves);
		
		return moves;
	}
	
	public boolean isClicked()
	{
		return clicked;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public boolean hasMoved()
	{
		return moveCounter > 0;
	}
	
	public int GetTileX()
	{
		return tileX;
	}
	
	public int GetTileY()
	{
		return tileY;
	}
}
