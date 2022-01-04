package utils;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import game.Game;

public class InputHandler implements KeyListener, MouseMotionListener, MouseListener, 
MouseWheelListener, ComponentListener
{

	public static int MOUSEX, MOUSEY;
	private static int MOUSESCROLL;
	private static boolean[] key = new boolean[68836];
	public static Character KEYPRESSED = null;
	private static boolean MOUSE1CLICKED = false, MOUSE2CLICKED = false;
	public static boolean DRAGGING = false, RESIZED = false;

	public static boolean KeyPressed(int k)
	{
		return key[k];
	}
	
	public static boolean KeyPressedAndSetFalse(int k)
	{
		boolean pressed = key[k];
		key[k] = false;
		return pressed;
	}
	
	public static boolean MouseClicked(int button) {
		if(button == 1)
			return MOUSE1CLICKED;
		else if(button == 2)
			return MOUSE2CLICKED;
		else
			return false;
	}
	
	public static boolean MouseClickedAndSetFalse(int button)
	{
		if(button == 1) 
		{
			boolean clicked = MOUSE1CLICKED;
			MOUSE1CLICKED = false;
			return clicked;
		}
		else if(button == 2)
		{
			boolean clicked = MOUSE2CLICKED;
			MOUSE2CLICKED = false;
			return clicked;
		}
		else
			return false;
	}
	
	public void mouseClicked(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) 
	{
		if(e.getButton() == MouseEvent.BUTTON1)
			MOUSE1CLICKED = true;
		if(e.getButton() == MouseEvent.BUTTON3)
			MOUSE2CLICKED = true;
	}

	public void mouseReleased(MouseEvent e) 
	{
		if(e.getButton() == MouseEvent.BUTTON1)
			MOUSE1CLICKED = false;
		if(e.getButton() == MouseEvent.BUTTON3) {
			MOUSE2CLICKED = false;
		}
	}

	public void mouseDragged(MouseEvent e) 
	{
		MOUSEX = (int) ((e.getX() - Game.XOFF)/Game.SCALE);
		MOUSEY = (int) ((e.getY() - Game.YOFF)/Game.SCALE);
	}

	public void mouseMoved(MouseEvent e) 
	{
		MOUSEX = (int) ((e.getX() - Game.XOFF)/Game.SCALE);
		MOUSEY = (int) ((e.getY() - Game.YOFF)/Game.SCALE);
	}

	public void keyPressed(KeyEvent e) 
	{
		key[e.getExtendedKeyCode()] = true;
	}

	public void keyReleased(KeyEvent e) 
	{
		key[e.getExtendedKeyCode()] = false;
	}

	public void keyTyped(KeyEvent e) 
	{
		KEYPRESSED = new Character(e.getKeyChar());
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		MOUSESCROLL = e.getWheelRotation();
	}
	
	public static int getMouseScroll()
	{
		int scroll = MOUSESCROLL;
		MOUSESCROLL = 0;
		return scroll;
	}

	@Override
	public void componentHidden(ComponentEvent e) {}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentResized(ComponentEvent e) {
		int w = e.getComponent().getWidth();
		int h = e.getComponent().getHeight();
		
		float wRatio = w/(float)(Game.WIDTH);
		float hRatio = h/((float)Game.HEIGHT+32.0f);
		
		if(wRatio > hRatio)
		{
			Game.SCALE = hRatio;
			Game.YOFF = 0;
			Game.XOFF = (int)(w - Game.WIDTH*Game.SCALE - 16)/2;
		}
		else
		{
			Game.SCALE = wRatio;
			Game.YOFF = (int)(h - Game.HEIGHT*Game.SCALE - 38)/2;
			Game.XOFF = 0;
		}
		RESIZED = true;
	}

	@Override
	public void componentShown(ComponentEvent e) {}
	
}
