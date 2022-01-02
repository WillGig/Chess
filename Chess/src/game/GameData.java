package game;

import java.util.ArrayList;

public class GameData {

	public ArrayList<Position> positions;
	
	public String event, site, date, round, white, black, result;
	
	public GameData(ArrayList<Position> positions, String event, String site, String date, String round, String white, String black, String result)
	{
		this.positions = positions;
		this.event = event;
		this.site = site;
		this.date = date;
		this.round = round;
		this.white = white;
		this.black = black;
		this.result = result;
	}
	
}
