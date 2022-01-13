package engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Engine 
{
	public enum ENGINE {STOCKFISH};
	
	String path;
	
	private Process process = null;
    private BufferedReader reader = null;
    private OutputStreamWriter writer = null;

    public Engine(ENGINE engine)
    {
    	if(engine == ENGINE.STOCKFISH)
    		path = Engine.class.getResource("/engines/stockfish_14.1_win_x64_avx2.exe").getPath();
    		
    }
    
    public void start() 
    {
    	ProcessBuilder pb = new ProcessBuilder(path);
        try 
        {
            this.process = pb.start();
            this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            this.writer = new OutputStreamWriter(process.getOutputStream());
            
            command("uci", Function.identity(), (s) -> s.startsWith("uciok"), 2000l);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    public void stop() 
    {
        if (this.process.isAlive())
            this.process.destroy();
        try 
        {
            reader.close();
            writer.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    public void setPosition(String FEN)
    {
    	try 
    	{
			command("position fen " + FEN, Function.identity(), s -> s.startsWith("readyok"), 2000l);
		} 
    	catch (Exception ex) 
    	{
			ex.printStackTrace();
		}
    }
    
    public void setOption(String option, String value)
    {
    	try
    	{
    		command("setoption name " + option + " value " + value, Function.identity(), s->s.startsWith("readyok"), 2000l);
    	}
    	catch (Exception ex) 
    	{
			ex.printStackTrace();
		}
    }
    
    public class Line
    {
    	public float eval;
    	public int mate;
    	public String moves;
    	
    	public Line(int eval, String moves)
    	{
    		this.eval = (float)eval/100;
    		this.moves = moves;
    	}
    	
    	public String getEval()
    	{
    		if(eval == Integer.MAX_VALUE)
    			return "Mate in " + mate;
    		return eval + "";
    	}
    }
    
    public Map<Integer, Line> getAnalysis(int depth)
    {
    	String analysisLineRegex = "info depth ([\\w]*) seldepth [\\w]* multipv ([\\w]*) score (cp ([\\-\\w]*)|mate ([\\w*])) [\\s\\w]*pv ([\\w]*)\\s*([\\s\\w]*)";
    	final Pattern pattern = Pattern.compile(analysisLineRegex);
    	
    	try
    	{
    		Map<Integer, Line> moves =
        	        command(
    	                "go depth " + depth,
    	                lines -> {
    	                	Map<Integer, Line> result = new TreeMap<Integer, Line>();
    	                    for(String line : lines) {
    	                        Matcher matcher = pattern.matcher(line);
    	                        if (matcher.matches()) {
    	                        	Integer pv = Integer.parseInt(matcher.group(2));
    	                            int eval;
    	                            if(matcher.group(4).length() > 0)
    	                            	eval = Integer.parseInt(matcher.group(4));
    	                            else
    	                            	eval = Integer.MAX_VALUE;
    	                            String m = matcher.group(6) + " " + matcher.group(7);
    	                            result.put(pv, new Line(eval, m));
    	                            if(eval == Integer.MAX_VALUE)
    	                            	result.get(result.size()-1).mate = Integer.parseInt(matcher.group(5));
    	                        }
    	                    }
    	                    return result;
    	                },
    	                s -> s.startsWith("bestmove"),
    	                100000l);
        	
        	return moves;
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	return null;
    }
    
    private <T> T command(String cmd, Function<List<String>, T> commandProcessor, Predicate<String> breakCondition, long timeout)
            throws InterruptedException, ExecutionException, TimeoutException {

        // This completable future will send a command to the process
        // And gather all the output of the engine in the List<String>
        // At the end, the List<String> is translated to T through the
        // commandProcessor Function
        CompletableFuture<T> command = CompletableFuture.supplyAsync(() -> {
            final List<String> output = new ArrayList<>();
            try {
                writer.flush();
                writer.write(cmd + "\n");
                writer.write("isready\n");
                writer.flush();
                String line = "";
                while ((line = reader.readLine()) != null) 
                {
                    if (line.contains("Unknown command"))
                        throw new RuntimeException(line);
                    if (line.contains("Unexpected token"))
                        throw new RuntimeException("Unexpected token: " + line);
                    output.add(line);
                    if (breakCondition.test(line))
                    	 break;
                }
            }
            catch (IOException e) 
            {
                e.printStackTrace();
            }
            return commandProcessor.apply(output);
        });

        return command.get(timeout, TimeUnit.MILLISECONDS);
    }
	
}
