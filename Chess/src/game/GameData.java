package game;

public class GameData {

	//public ArrayList<Position> positions;
	public Position startPosition;
	
	public String event, site, date, round, white, black, result;
	
	public GameData(Position start, String event, String site, String date, String round, String white, String black, String result)
	{
		startPosition = start;
		this.event = event;
		this.site = site;
		this.date = date;
		this.round = round;
		this.white = white;
		this.black = black;
		this.result = result;
	}
	
}
