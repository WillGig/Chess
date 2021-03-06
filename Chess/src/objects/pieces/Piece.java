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
		t.setPiece(this);
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

	public abstract char getFENName();
	
	@Override
	public void update() {}
	
	//returns captured piece
	public Piece move(Tile start, Tile end, Tile[] board)
	{
		start.setPiece(null);
		x = end.getX();
		y = end.getY();
		tileX = end.getTileX();
		tileY = end.getTileY();
		Piece p = end.getPiece();
		end.setPiece(this);
		moveCounter++;
		return p;
	}
	
	public void unmove(Tile start, Tile end, Tile[] board, Piece captured)
	{
		start.setPiece(this);
		x = start.getX();
		y = start.getY();
		tileX = start.getTileX();
		tileY = start.getTileY();
		end.setPiece(captured);
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
	
	public int getTileX()
	{
		return tileX;
	}
	
	public int getTileY()
	{
		return tileY;
	}
	
	//Finds disambiguation text for a move from tile start to end
	//Checks if multiple pieces of the same type can move to the chosen square
	public static String getDisambiguationText(String moveText, Tile start, Tile end, Tile[] board)
	{
		ArrayList<Piece> confusingPieces = Piece.confusingPieces(moveText, start, end, board);
		if(confusingPieces.size() == 0)
			return "";
			
		//If specifying the file is sufficient to disambiguate
		boolean fileChange = true;
		//If specifying the rank is sufficient to disambiguate
		boolean rankChange = true;
		for(Piece p : confusingPieces)
		{
			if(p.getTileX() == start.getTileX())
				fileChange = false;
			if(p.getTileY() == start.getTileY())
				rankChange = false;
		}
		
		String tileName = start.getSquareName();
		
		if(fileChange)
			return tileName.charAt(0) + "";
		else if(rankChange)
			return tileName.charAt(1) + "";
		return tileName;
	}
	
	//Returns a list of pieces of the same type and color as the piece moving from start to end that could also move to the end tile
	public static ArrayList<Piece> confusingPieces(String notationName, Tile start, Tile end, Tile[] board)
	{
		ArrayList<Piece> cPieces = new ArrayList<Piece>();
		
		Color c = start.getPiece().getColor();
		
		for(int i = 0; i < board.length; i++)
		{
			//Moving piece is not counted
			if(i == start.getTileX() + start.getTileY()*8)
				continue;
			
			Piece p = board[i].getPiece();
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
