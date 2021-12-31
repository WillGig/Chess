package utils;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import game.Game;
import game.State;
import objects.Tile;
import objects.pieces.Bishop;
import objects.pieces.Knight;
import objects.pieces.Pawn;
import objects.pieces.Piece;
import objects.pieces.Queen;
import objects.pieces.Rook;
import scenes.Chess;
import scenes.Chess.GameState;

public class SaveLoadManager {
	
	private static Properties settings = new Properties();
	
	public static void saveGame(ArrayList<State> states, String path)
	{
		try
		{
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(path);
			
			for(int i = 1; i < states.size(); i++)
				writer.write(states.get(i).getText() + " ");
			
			writer.close();
		}
		catch(Exception ex) {}
	}
	
	public static ArrayList<State> loadGame(String path)
	{
		ArrayList<State> states = new ArrayList<State>();
		
		try
		{
			Scanner reader = new Scanner(new File(path));
			
			Tile[] board = Tile.getDefaultBoard();
			Pawn.enPassantTile = -1;
			Pawn.epPawn = -1;
			
			states.add(new State(board, "", GameState.ONGOING, Color.WHITE, 0, 0));
			
			int counter = -1;
			while(reader.hasNextLine())
			{
				String data = reader.nextLine();
				for(String s : data.split(" "))
				{
					counter++;
					//Move number
					if(counter%3 == 0)
						continue;
					//Move data
					else
						states.add(generateStateFromPGN(states,  board, s));
				}
			}
			
			reader.close();
		}
		catch(Exception ex) { ex.printStackTrace(); }
		return states;
	}
	
	public static State generateStateFromPGN(ArrayList<State> states, Tile[] board, String move)
	{
		State previousState = states.get(states.size()-1);
		
		makeMove(board, move, previousState.turn);
		
		Color c = Color.WHITE;
		if(previousState.turn == Color.WHITE)
			c = Color.BLACK;
		
		int fiftyMoves = previousState.fiftyMoves+1;
		
		if(move.contains("x") || !"NBRQK0".contains(move.charAt(0)+""))
			fiftyMoves = 0;
		
		GameState gs;
		if(move.contains("#"))
			gs = GameState.CHECKMATE;
		else if(fiftyMoves == 100)
			gs = GameState.FIFTYMOVEDRAW;
		else if(State.Repitition(states, board))
			gs = GameState.REPETITION;
		else
			gs = State.EvaluateState(board, c);
			
		if(c == Color.BLACK)
			move = ((previousState.moveNumber+1)/2+1) + ". " + move;
		
		return new State(board, move, gs, c, previousState.moveNumber+1, fiftyMoves);
	}
	
	public static void makeMove(Tile[] board, String move, Color turn)
	{
		Pawn.enPassantTile = 0;
		Pawn.epPawn = -1;
		
		//Castling
		if(move.equals("O-O"))
		{
			if(turn == Color.WHITE)
			{
				Tile start = board[4+7*8];
				Tile end = board[6+7*8];
				start.GetPiece().move(start, end, board);
			}
			else
			{
				Tile start = board[4+0*8];
				Tile end = board[6+0*8];
				start.GetPiece().move(start, end, board);
			}
			return;
		}
		else if(move.equals("O-O-O"))
		{
			if(turn == Color.WHITE)
			{
				Tile start = board[4+7*8];
				Tile end = board[2+7*8];
				start.GetPiece().move(start, end, board);
			}
			else
			{
				Tile start = board[4+0*8];
				Tile end = board[2+0*8];
				start.GetPiece().move(start, end, board);
			}
			return;
		}
		
		//Identify Piece
		String piece = move.charAt(0) + "";
		if(!"NBRQK".contains(piece))
			piece = "";
		
		boolean capture = move.contains("x");
		
		//Disambiguation text
		int startFile = -1;
		int startRank = -1;
		if(piece.length() > 0)
		{
			char firstChar = move.charAt(1);
			char secondChar = move.charAt(2);
			if(capture)
				secondChar = move.charAt(3);
			
			//First Character is a file and second character is a file
			if(isFile(firstChar) && isFile(secondChar))
				startFile = Character.valueOf(firstChar) - 97;
			//First Character is a rank
			else if(isRank(firstChar))
				startRank = 8 - Character.getNumericValue(firstChar);
			//file then rank then file
			else if(move.length() > 4 && isFile(firstChar) && isRank(secondChar))
			{
				char thirdChar = move.charAt(3);
				if(capture)
					thirdChar = move.charAt(4);
				if(isFile(thirdChar))
				{
					startFile = Character.valueOf(firstChar) - 97;
					startRank = 8 - Character.getNumericValue(secondChar);
				}
			}
		}
		
		//Find end square
		int squareIndex = piece.length();
		if(capture)
			squareIndex = 2;
		if(startFile != -1)
			squareIndex++;
		if(startRank != -1)
			squareIndex++;
		int squareX = Character.valueOf(move.charAt(squareIndex)) - 97;
		int squareY = 8 - Character.getNumericValue(move.charAt(squareIndex+1));
		Tile end = board[squareX + 8 * squareY];
		
		//Find start square
		Tile start = null;
		for(int i = 0; i < board.length; i++)
		{
			Piece p = board[i].GetPiece();
			if(p != null && p.getColor() == turn && p.getNotationName().equals(piece))
			{
				if(p.getLegalMoves(board).contains(end))
				{
					if(startFile != -1 && startFile != board[i].getTileX())
						continue;
					if(startRank != -1 && startRank != board[i].getTileY())
						continue;
					start = board[i];
					break;
				}
			}
		}
		if(start != null)
			start.GetPiece().move(start, end, board);
		else
			System.out.println("Failed to find piece for move " + move);
		
		//Promotion
		if(move.contains("="))
		{
			char promotionPiece = move.charAt(squareIndex+3);
			if(promotionPiece == 'N')
				new Knight(end, turn);
			else if(promotionPiece == 'B')
				new Bishop(end, turn);
			else if(promotionPiece == 'R')
				new Rook(end, turn);
			else
				new Queen(end, turn);
		}
	}
	
	//returns true if character is a through h
	public static boolean isFile(char c)
	{
		int value = Character.valueOf(c);
		return value > 96 && value < 105;
	}
	
	//returns true if character is 1 through 8
	public static boolean isRank(char c)
	{
		int value = Character.valueOf(c);
		return value > 48 && value < 57;
	}
	
	public static void saveVariable(String key, int value, String path) 
	{
		saveVariable(key, Integer.toString(value), path);
	}
	
	public static void saveVariable(String key, boolean value, String path)
	{
		if(value)
			saveVariable(key, "1", path);
		else
			saveVariable(key, "0", path);
	}

	public static void saveVariable(String key, String value, String path)
	{
		try 
		{
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream write = new FileOutputStream(path);
			settings.setProperty(key, value);
			settings.storeToXML(write, path);
			write.close();
		} 
		catch (Exception localException) {}
	}
	
	public static void saveSettings()
	{
		String path = "res/settings.xml";
		
		saveVariable("showFPS", Game.SHOWFPS, path);
		saveVariable("fpsCap", Game.CAPFPS, path);
		saveVariable("darkColor", Chess.DARKCOLOR, path);
		saveVariable("lightColor", Chess.LIGHTCOLOR, path);
		saveVariable("showCoords", Chess.SHOWCOORDS, path);
		saveVariable("flipOnMove", Chess.FLIPONMOVE, path);
		saveVariable("volume", (int)(Sound.VOLUME*100), path);
	}
	
	public static void loadSettings()
	{
		try
		{
			InputStream read = new FileInputStream("res/settings.xml");
			settings.loadFromXML(read);
			
			Game.SHOWFPS = Integer.parseInt(settings.getProperty("showFPS")) == 1;
			Game.CAPFPS = Integer.parseInt(settings.getProperty("fpsCap")) == 1;
			Chess.DARKCOLOR = Integer.parseInt(settings.getProperty("darkColor"));
			Chess.LIGHTCOLOR =  Integer.parseInt(settings.getProperty("lightColor"));
			Chess.SHOWCOORDS = Integer.parseInt(settings.getProperty("showCoords")) == 1;
			Chess.FLIPONMOVE = Integer.parseInt(settings.getProperty("flipOnMove")) == 1;
			Sound.VOLUME = Integer.parseInt(settings.getProperty("volume"))/100.0f;
			
			read.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("Failed to find settings file. Using default settings.");
		}
		catch (Exception localException){localException.printStackTrace();}
	}
	
}
