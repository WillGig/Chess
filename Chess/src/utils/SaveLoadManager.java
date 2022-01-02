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
import game.GameData;
import game.Position;
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
	
	public static void saveGame(GameData gd, String path)
	{
		try
		{
			if(!path.contains(".pgn"))
				path += ".pgn";
			
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(path);
			
			if(gd.event.length() > 0)
				writer.write("[Event \"" + gd.event + "\"]\n");
			if(gd.site.length() > 0)
				writer.write("[Site \"" + gd.site + "\"]\n");
			if(gd.date.length() > 0)
				writer.write("[Date \"" + gd.date + "\"]\n");
			if(gd.round.length() > 0)
				writer.write("[Round \"" + gd.round + "\"]\n");
			if(gd.white.length() > 0)
				writer.write("[White \"" + gd.white + "\"]\n");
			if(gd.black.length() > 0)
				writer.write("[Black \"" + gd.black + "\"]\n");
			if(gd.result.length() > 0)
				writer.write("[Result \"" + gd.result + "\"]\n");
			
			for(int i = 1; i < gd.positions.size(); i++)
			{
				writer.write(gd.positions.get(i).getText() + " ");
				String moveComments = gd.positions.get(i).comments;
				if(moveComments.length() > 0)
					writer.write("{" + moveComments + "} ");
			}
			
			if(gd.result.length() > 0)
				writer.write(gd.result);
			
			writer.close();
		}
		catch(Exception ex) {}
	}
	
	public static GameData loadGame(String path)
	{
		String event = "", site = "", date = "", round = "", white = "", black = "", result = "";
		ArrayList<Position> states = new ArrayList<Position>();
		
		try
		{
			Scanner reader = new Scanner(new File(path));
			
			Tile[] board = Tile.getDefaultBoard();
			Pawn.enPassantTile = -1;
			Pawn.epPawn = -1;
			
			states.add(new Position(board, "", GameState.ONGOING, Color.WHITE, 0, 0));
			
			int counter = -1;
			boolean readingComment = false;
			String comment = "";
			while(reader.hasNextLine())
			{
				String data = reader.nextLine();
				if(data.length() < 1)
					continue;
				
				if(data.charAt(0) == '[')
				{
					if(data.length() > 6)
					{
						String attribute = data.substring(1, 6);
						if(attribute.equals("Event"))
						{
							event = data.substring(8);
							event = event.substring(0, event.length()-2);
						}
						attribute = data.substring(1, 5);
						if(attribute.equals("Site"))
						{
							site = data.substring(7);
							site = site.substring(0, site.length()-2);
						}
						attribute = data.substring(1, 5);
						if(attribute.equals("Date"))
						{
							date = data.substring(7);
							date = date.substring(0, date.length()-2);
						}
						attribute = data.substring(1, 6);
						if(attribute.equals("Round"))
						{
							round = data.substring(8);
							round = round.substring(0, round.length()-2);
						}
						attribute = data.substring(1, 6);
						if(attribute.equals("White"))
						{
							white = data.substring(8);
							white = white.substring(0, white.length()-2);
						}
						attribute = data.substring(1, 6);
						if(attribute.equals("Black"))
						{
							black = data.substring(8);
							black = black.substring(0, black.length()-2);
						}
						attribute = data.substring(1, 7);
						if(attribute.equals("Result"))
						{
							result = data.substring(9);
							result = result.substring(0, result.length()-2);
						}
					}
					continue;
				}
				
				for(String s : data.split(" "))
				{
					if(s.charAt(0) == '{')
						readingComment = true;
					
					if(readingComment)
					{
						comment += s + " ";
						if(s.charAt(s.length()-1) == '}')
						{
							readingComment = false;
							comment = comment.substring(1, comment.length()-2);
							states.get(states.size()-1).comments = comment;
							comment = "";
						}
						continue;
					}
					
					if(s.equals(result))
					{
						Position finalState = states.get(states.size()-1);
						finalState.score = result;
						if(result.equals("1/2-1/2"))
						{
							finalState.gState = GameState.DRAW;
							finalState.result = "Draw by agreement";
						}
						else if(result.equals("1-0"))
						{
							finalState.gState = GameState.CHECKMATE;
							finalState.result = "White wins by resignation";
						}
						else if(result.equals("0-1"))
						{
							finalState.gState = GameState.CHECKMATE;
							finalState.result = "Black wins by resignation";
						}
						reader.close();
						return new GameData(states, event, site, date, round, white, black, result);
					}
						
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
		return new GameData(states, event, site, date, round, white, black, result);
	}
	
	public static Position generateStateFromPGN(ArrayList<Position> states, Tile[] board, String move)
	{
		Position previousState = states.get(states.size()-1);
		
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
		else if(Position.Repitition(states, board))
			gs = GameState.REPETITION;
		else
			gs = Position.EvaluateState(board, c);
			
		if(c == Color.BLACK)
			move = ((previousState.moveNumber+1)/2+1) + ". " + move;
		
		return new Position(board, move, gs, c, previousState.moveNumber+1, fiftyMoves);
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
		saveVariable("darkMode", Game.DARKMODE, path);
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
			Game.DARKMODE = Integer.parseInt(settings.getProperty("darkMode")) == 1;
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
