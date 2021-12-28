package game;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import scenes.Scene;
import scenes.Settings;
import scenes.MainMenu;
import scenes.Chess;
import utils.InputHandler;

public class Game implements Runnable
{
	public static final int WIDTH = 800, HEIGHT = 600;
	
	public static final float SCALE = 1.0f;

	public static boolean SHOWFPS = false;
	
	private boolean running = false;
	
	private String fps = "";
	
	private Thread gameThread;
	
	private JFrame frame;
	
	private Canvas canvas;
	
	private BufferedImage image;
	
	private BufferStrategy bufferStrat;
	
	private int[] pixels;
	
	private InputHandler input;
	
	private Scene[] scenes;
	
	private int currentScene;
	
	private float sceneFade, fadeSpeed;
	
	public Game()
	{
		//Set up Window Properties
		frame = new JFrame("Game");
		//+32 compensates window border
		frame.setSize((int)(WIDTH * SCALE), (int)(HEIGHT * SCALE)+32);
		canvas = new Canvas();
		frame.add(canvas);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
		
		input = new InputHandler();
		canvas.addKeyListener(input);
		canvas.addMouseListener(input);
		canvas.addMouseMotionListener(input);
		
		//Link pixels in image to int[] pixels
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		pixels = ((DataBufferInt)(image.getRaster().getDataBuffer())).getData();
		
		scenes = new Scene[3];
		scenes[0] = new MainMenu();
		scenes[1] = new Chess();
		scenes[2] = new Settings();
		
		currentScene = 0;
		sceneFade = 1.0f;
		fadeSpeed = .05f;
		
		canvas.requestFocus();
		start();
	}
	
	public void start()
	{
		running = true;
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	public void stop()
	{
		running = false;
	}
	
	public void SetScene(int scene)
	{
		if(scene < 0 || scene > scenes.length - 1)
		{
			System.err.println("Error invalid scene: " + scene);
			return;
		}
		
		scenes[currentScene].stop();
		scenes[scene].start();
		currentScene = scene;
		sceneFade = 1.0f;
	}
	
	@Override
	public void run() 
	{
		int frames = 0;
		int updates = 0;
		
		long previousTime = System.nanoTime();
		long currentTime;
		long timeBank = 0;
		double delta = 0;
		
		//Game Loop
		while(running) {
			
			currentTime = System.nanoTime();
			timeBank += currentTime - previousTime;
			delta += (currentTime - previousTime)/(1000000000.0/60.0);
			previousTime = currentTime;
			
			if(delta > 1) {
				update();
				delta--;
				updates++;
			}
			
			render();
			frames++;
			
			if(timeBank > 1000000000) {
				fps = "FPS:" + frames + " | UPS:" + updates;
				timeBank = 0;
				frames = 0;
				updates = 0;
			}
		}
		
		frame.dispose();
	}
	
	private void update()
	{
		scenes[currentScene].update(this);
		
		if(sceneFade > fadeSpeed)
			sceneFade -= fadeSpeed;
	}
	
	private void render()
	{
		bufferStrat = canvas.getBufferStrategy();
		if(bufferStrat == null)
		{
			canvas.createBufferStrategy(3);
			return;
		}
		
		//Draw all images to pixel array
		for(int i = 0; i < pixels.length; i++)
			pixels[i] = 0xff000000;
		
		scenes[currentScene].render(pixels);
		
		//Render pixel array and all text
		Graphics g = bufferStrat.getDrawGraphics();
		
		g.drawImage(image, 0, 0, (int)(WIDTH*SCALE), (int)(HEIGHT*SCALE), null);
		
		scenes[currentScene].renderText(g);
		
		if(SHOWFPS)
		{
			g.setColor(Color.WHITE);
			g.setFont(new Font("Courier", 1, (int)(20*SCALE)));
			g.drawString(fps, (int) (10*SCALE), (int) (25*SCALE));
		}
		
		//Scene Fade in
		if(sceneFade > fadeSpeed)
		{
			g.setColor(new Color(0.0f, 0.0f, 0.0f, sceneFade));
			g.fillRect(0, 0, (int)(WIDTH*SCALE), (int)(HEIGHT*SCALE));
		}

		g.dispose();
		bufferStrat.show();
	}
	
	public static void main(String args[])
	{
		new Game();
	}

}
