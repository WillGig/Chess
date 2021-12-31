package objects.pieces;

import java.awt.Color;
import java.util.ArrayList;

import objects.GameObject;
import objects.Tile;
import utils.Texture;

public abstract class Piece extends GameObject{
	
	private boolean clicked;
	
	private int moveCounter;
	
	protected int tileX, tileY;
	
	private Color color;
	
	public Piece(Tile t, Color color) {
		super(t.getX(), t.getY(), 64, 64);
		t.SetPiece(this);
		tileX = t.getTileX();
		tileY = t.getTileY();
		this.color = color;
		moveCounter = 0;
		String texName = getName();
		if(color == Color.WHITE)
			texName += "White";
		else
			texName += "Black";
		image = Texture.getTexture(texName);
	}
	
	public abstract String getName();
	
	public abstract String getNotationName();

	@Override
	public void update() {
		
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
		
		//Store en passant data
		int ept = Pawn.enPassantTile;
		int ep = Pawn.epPawn;
		
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
		
		//Restore en passant data
		Pawn.enPassantTile = ept;
		Pawn.epPawn = ep;
		
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
	
	public void changeMoved(int change)
	{
		moveCounter += change;
	}
	
	public boolean hasMoved()
	{
		return moveCounter > 0;
	}
	
	public void setNumberOfMoves(int num)
	{
		moveCounter = num;
	}
	
	public int GetTileX()
	{
		return tileX;
	}
	
	public int GetTileY()
	{
		return tileY;
	}
	
	//Returns a list of pieces of the same type and color as the piece moving from start to end that could also move to the end tile
	public static ArrayList<Piece> confusingPieces(String notationName, Tile start, Tile end, Tile[] board)
	{
		ArrayList<Piece> cPieces = new ArrayList<Piece>();
		
		Color c = start.GetPiece().getColor();
		
		for(int i = 0; i < board.length; i++)
		{
			//Moving piece is not counted
			if(i == start.getTileX() + start.getTileY()*8)
				continue;
			
			Piece p = board[i].GetPiece();
			if(p == null)
				continue;
			
			//Piece must be of the same type and color
			if(p.getColor() == c && p.getNotationName().equals(notationName))
				if(p.getLegalMoves(board).contains(end))
					cPieces.add(p);
		}
		
		return cPieces;
	}
}
