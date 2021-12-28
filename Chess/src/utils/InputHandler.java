package utils;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import game.Game;

public class InputHandler implements KeyListener, MouseMotionListener, MouseListener 
{

	public static int MOUSEX, MOUSEY;
	private static boolean[] key = new boolean[68836];
	private static boolean MOUSE1CLICKED = false, MOUSE2CLICKED = false;
	public static boolean DRAGGING = false;

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
		MOUSEX = (int) (e.getX()/Game.SCALE);
		MOUSEY = (int) (e.getY()/Game.SCALE);
	}

	public void mouseMoved(MouseEvent e) 
	{
		MOUSEX = (int) (e.getX()/Game.SCALE);
		MOUSEY = (int) (e.getY()/Game.SCALE);
	}

	public void keyPressed(KeyEvent e) 
	{
		key[e.getExtendedKeyCode()] = true;
	}

	public void keyReleased(KeyEvent e) 
	{
		key[e.getExtendedKeyCode()] = false;
	}

	public void keyTyped(KeyEvent e) {}
	
}
