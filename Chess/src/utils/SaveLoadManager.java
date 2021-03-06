package utils;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.Stack;

import game.Game;
import game.GameData;
import game.Position;
import objects.MoveArrow;
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
			
			saveLine(gd.startPosition, 0, writer);
			
			if(gd.result.length() > 0)
				writer.write(gd.result);
			
			writer.close();
		}
		catch(Exception ex) {}
	}
	
	//Saves all move and comment data for all moves within line
	public static void saveLine(Position p, int numberOfOpenBrackets, FileWriter writer) throws IOException
	{
		if(p.getChildren().size() == 0)
			return;
		
		if(p.getChildren().size() == 1)
		{
			saveMove(p.getChildren().get(0), numberOfOpenBrackets, false, writer);
			saveLine(p.getChildren().get(0), numberOfOpenBrackets, writer);
		}
		else if(p.getChildren().get(0).getChildren().size() > 0)
		{
			saveMove(p.getChildren().get(0), numberOfOpenBrackets, false, writer);
			for(int i = 1; i < p.getChildren().size(); i++)
			{
				saveMove(p.getChildren().get(i), 1, true, writer);
				saveLine(p.getChildren().get(i), 1, writer);
			}
			saveLine(p.getChildren().get(0), numberOfOpenBrackets, writer);
		}
		else
		{
			saveMove(p.getChildren().get(0), 0, false, writer);
			for(int i = 1; i < p.getChildren().size(); i++)
			{
				if(i == p.getChildren().size()-1)
				{
					saveMove(p.getChildren().get(i), numberOfOpenBrackets+1, true, writer);
					saveLine(p.getChildren().get(i), numberOfOpenBrackets+1, writer);
				}
				else
				{
					saveMove(p.getChildren().get(i), 1, true, writer);
					saveLine(p.getChildren().get(i), 1, writer);
				}
			}
		}
	}

	//Helper function for saveLine. Saves move data of a specific move.
	//Must be split from saveLine since with variations a move will be saved, then variations, then the line from the move
	public static void saveMove(Position p, int numberOfOpenBrackets, boolean startOfLine, FileWriter writer) throws IOException
	{
		if(startOfLine)
		{
			if(p.turn == Color.BLACK)
				writer.write("(");
			else
				writer.write("(" + p.moveNumber/2 + "... ");
		}
		writer.write(p.rawText);
		String moveComments = p.comments;
		if(moveComments.length() > 0)
			writer.write(" {" + moveComments + "}");
		if(p.getChildren().size() == 0)
			for(int i  = 0; i < numberOfOpenBrackets; i++)
				writer.write(")");
		writer.write(" ");
	}
	
	public static GameData loadGame(String path)
	{
		String event = "", site = "", date = "", round = "", white = "", black = "", result = "";
		Position current = null;
		
		try
		{
			Scanner reader = new Scanner(new File(path));
			
			Tile[] board = Tile.getDefaultBoard();
			Stack<Position> returnPositions = new Stack<Position>();
			Pawn.enPassantTile = -1;
			Pawn.epPawn = -1;
			
			current = new Position(board, "", GameState.ONGOING, Color.WHITE, 0, 0, null);
			
			boolean readingComment = false, branching = false;
			String comment = "";
			while(reader.hasNextLine())
			{
				String data = reader.nextLine();
				//skip empty lines
				if(data.length() < 1)
					continue;
				
				//Read in game attributes
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
				
				//read in move data
				for(String s : data.split(" "))
				{
					//skip empty spaces
					if(s.length() == 0)
						continue;
					
					if(s.charAt(0) == '{')
						readingComment = true;
					
					if(readingComment)
					{
						comment += s + " ";
						if(s.charAt(s.length()-1) == '}')
						{
							readingComment = false;
							comment = comment.substring(1, comment.length()-2);
							current.comments = comment;
							comment = "";
						}
						continue;
					}
					
					//Check for result tag at end of game
					if(s.equals(result))
					{
						current.score = result;
						if(result.equals("1/2-1/2"))
						{
							current.gState = GameState.DRAW;
							current.result = "Draw by agreement";
						}
						else if(result.equals("1-0"))
						{
							current.gState = GameState.CHECKMATE;
							current.result = "White wins by resignation";
						}
						else if(result.equals("0-1"))
						{
							current.gState = GameState.CHECKMATE;
							current.result = "Black wins by resignation";
						}
						reader.close();
						return new GameData(current.getHead(), event, site, date, round, white, black, result);
					}
						
					if(s.contains("("))
					{
						branching = true;
						continue;
					}
					
					//Skip reading move number
					if(s.contains("."))
						continue;
					//Move data
					else
					{
						if(branching)
						{
							returnPositions.add(current);
							current = current.getParent();
							current.loadPieces(board);
							Position newPosition = generatePositionFromPGN(current, board, s);
							current.addChild(newPosition);
							current = newPosition;
							branching = false;
						}
						else
						{
							Position newPosition = generatePositionFromPGN(current, board, s);
							current.addChild(newPosition);
							current = newPosition;
						}
						
						//go back to start of branch
						int num = s.length() - s.replace(")", "").length();//Number of branches being closed
						for(int i = 0; i < num; i++)
							current = returnPositions.pop();
						if(num > 0)
							current.loadPieces(board);
					}
				}
			}
			
			reader.close();
		}
		catch(Exception ex) { ex.printStackTrace(); }
		if(current != null)
			return new GameData(current.getHead(), event, site, date, round, white, black, result);
		return null;
	}
	
	public static Position generatePositionFromPGN(Position current, Tile[] board, String move)
	{
		makeMove(board, move, current.turn);
		
		Color c = Color.WHITE;
		if(current.turn == Color.WHITE)
			c = Color.BLACK;
		
		int fiftyMoves = current.fiftyMoves+1;
		
		if(move.contains("x") || !"NBRQK0".contains(move.charAt(0)+""))
			fiftyMoves = 0;
			
		if(c == Color.BLACK)
			move = ((current.moveNumber+1)/2+1) + ". " + move;
		
		return new Position(board, move, GameState.ONGOING, c, current.moveNumber+1, fiftyMoves, current);
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
				start.getPiece().move(start, end, board);
			}
			else
			{
				Tile start = board[4+0*8];
				Tile end = board[6+0*8];
				start.getPiece().move(start, end, board);
			}
			return;
		}
		else if(move.equals("O-O-O"))
		{
			if(turn == Color.WHITE)
			{
				Tile start = board[4+7*8];
				Tile end = board[2+7*8];
				start.getPiece().move(start, end, board);
			}
			else
			{
				Tile start = board[4+0*8];
				Tile end = board[2+0*8];
				start.getPiece().move(start, end, board);
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
			Piece p = board[i].getPiece();
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
			start.getPiece().move(start, end, board);
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
		saveVariable("arrowColor", (int)MoveArrow.MOVEARROWCOLOR, path);
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
			MoveArrow.MOVEARROWCOLOR = Integer.parseInt(settings.getProperty("arrowColor"));
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
