import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

public class Game extends Applet implements Runnable
{	
	/*
	 * SpaceShump
	 * Java Applet
	 * By: Bill Liu
	 * 2017-06-13
	 */
	
	//Declaring variables
	
	Thread thread; //Thread of tasks to execute
	
	long startTime; //Start time of a single frame, in milliseconds
	long endTime; //End time of a single frame , in milliseconds
	long framePeriod; //Total time of a single frame, in milliseconds
	
	Dimension dim; //Dimension of back buffer used to double buffer
	
	Image img; //Image of the back buffer
	
	Graphics g; //Graphics object used to draw on image img
	
	Ship ship; //Ship variable
	
	boolean paused; //If the game is paused
	
	Shot[] shots; //Array of shot objects
	int numShots; //Number of shots on screen
	
	AlienShot[] aShots; //Array of alien shot objects
	int numAShots; //Number of alien shots on screen
	
	boolean shooting; //To determine if ship is shooting
	
	Alien[] aliens; //Array of aliens
	int numAliens; //Number of aliens on screen
	int aliensLeft; //Number of aliens left
	int maxAliens; //Maximum number of aliens
	
	Alien a1; //First alien
	int alienSpawn; //Alien spawn rate
	
	static JLabel obj1 = new JLabel(); //Label to place key bindings into
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW; //JComponent to reference to in key bindings
    
    int level; //Game level
    
    boolean debug; //Press space to enter debug mode
    
    public static void main(String[] args) {
    	//Empty main
    }
	
	public void init() //Thread will be started and created in this method
	{
		resize(800, 400); //Making sure applet is right size
		
		//Starting variables are initialized here
		
		shots = new Shot[61]; //Allocating 61 shots in the array; this is max is shots only last for 60 frames 
		
		shooting = false; //Ship is not shooting
		
		level = 0; //Start at level 0, will be level 1 at game start
		
		debug = false; //Not starting in debug mode
		
		startTime = 0; //Start time of code execution in a frame, in milliseconds
		endTime = 0; //End time of code execution in a frame, in milliseconds
		framePeriod = 25; //How long a frame should be, in milliseconds
		
		dim = getSize(); //Set dim equal to size of applet
		img = createImage(dim.width, dim.height); //Create back buffer equal to size of applet
		g = img.getGraphics(); //Get graphics object for back buffer
		
		//Key bindings
		//Mapping input key strokes to action map keys
		obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "PAUSE");
		obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "UPPRESS");
		obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "DOWNPRESS");
		obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "LEFTPRESS");
		obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "RIGHTPRESS");
		obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "DEBUG");
		
		obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "UPRELEASE");
		obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "DOWNRELEASE");
		obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "LEFTRELEASE");
		obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "RIGHTRELEASE");
		
		//Mapping action map keys to actions
		obj1.getActionMap().put("PAUSE", new PauseAction(1));
		obj1.getActionMap().put("UPPRESS", new UpPressAction(1));
		obj1.getActionMap().put("DOWNPRESS", new DownPressAction(1));
		obj1.getActionMap().put("LEFTPRESS", new LeftPressAction(1));
		obj1.getActionMap().put("RIGHTPRESS", new RightPressAction(1));
		obj1.getActionMap().put("DEBUG", new DebugAction(1));
		
		obj1.getActionMap().put("UPRELEASE", new UpReleaseAction(1));
		obj1.getActionMap().put("DOWNRELEASE", new DownReleaseAction(1));
		obj1.getActionMap().put("LEFTRELEASE", new LeftReleaseAction(1));
		obj1.getActionMap().put("RIGHTRELEASE", new RightReleaseAction(1));
		
		add(obj1); //Adding JLabel into applet
		
		thread = new Thread(this); //Initializing thread
		thread.start(); //Starting thread
	}
	
	public void nextLevel() //Method to increment level
	{
		//Rest of variables are initialized here
		
		level++; //Incrementing level
		
		ship = new Ship(200, 200, 0.75, 2, 0.90, 0.1, 3); //Creating ship
		paused = true; //Starting the game paused
		shooting = false; //Ship is not shooting
		
		numShots = 0; //No shots start on screen
		
		aliens = new Alien[((int) Math.pow(level, 2)) + 19]; //Allocating aliens
		numAliens = 1; //1 alien starts on screen
		maxAliens = 1; //Max number of aliens in a level
		aliensLeft = ((int) Math.pow(level, 2)) + 20; //Aliens left to destroy
		
		a1 = new Alien(800, (Math.random() * 361) + 20, 2, 60, 100000); //Initialization of first alien
		aliens[0] = a1; //Setting first alien
		alienSpawn = 60 / (level + 1); //Initializing alien spawn rate
		
		aShots = new AlienShot[120 * (((int) Math.pow(level, 2)) + 20)]; //Initializing array of shots
		
		numAShots = 0; //No alien shots start on screen
	}
	
	public void paint(Graphics gfx) //Graphics painting method
	{
		//Draw to back buffer first in order to prevent flickering
		//g is back buffer, gfx is the actual graphics
		
		g.setColor(Color.black); 
		g.fillRect(0,0,800,400); //Fill the screen black
		
		if (!ship.isActive() && !paused) //If the ship isn't active and isn't paused
		{
			g.setColor(Color.white);
			g.drawString("Press ENTER to Pause/Unpause", 303, 204); //Write pause message
		}
		else if (!ship.isActive() && paused) //If the ship isn't active and it is paused
		{
			g.setColor(Color.white);
			g.drawString("Press ENTER to Pause/Unpause", 303, 204); //Write pause message
		}
		else 
		{
			g.setColor(Color.black); 
			g.fillRect(0,0,800,400); //Otherwise fill the screen black
		}
		
		ship.draw(g); //Drawing the ship in the buffer
		
		for (int i = 0; i < numShots; i++) //Loop that calls draw for each shot
		{
			shots[i].draw(g);
		}
		
		for (int i = 0; i < numAliens; i++) //Loop that calls draw for each alien
		{
			aliens[i].draw(g);
		}
		
		for (int i = 0; i < numAShots; i++) //Loop that calls draw for each alien shot
		{
			aShots[i].draw(g);
		}
		
		g.setColor(Color.white);
		g.drawString("Level: " + level, 10, 20); //Drawing score
		g.drawString("Enemies Left: " + aliensLeft, 690, 20); //Drawing score
		
		//Press space to toggle debug data
		if (debug)
		{
			g.drawString("x: " + (int) ship.getX(), 10, 310);
			g.drawString("y: " + (int) ship.getY(), 10, 330);
			g.drawString("xVelocity: " + (int) ship.xVelocity, 10, 350);
			g.drawString("yVelocity: " + (int) ship.yVelocity, 10, 370);
			g.drawString("numShots: " + numShots, 10, 390);
			
			g.drawString("numAliens: " + numAliens, 703, 350);
			g.drawString("alienSpawnDelay: " + alienSpawn, 665, 370);
			g.drawString("numAlienShots: " + numAShots, 675, 390);
		}
		
		gfx.drawImage(img, 0, 0, this); //Copies the back buffer to the applet
	}
	
	public void update(Graphics gfx) //Update paint graphics method
	{
		paint(gfx); //Call paint without clearing the screen
	}
	
	public void start() //Overriding in order to ensure focus on the JLabel with the key bindings
	{
		obj1.setFocusable(true);
		obj1.requestFocusInWindow();
	}
	
	public void run() //This method, implemented by runnable, will contain code to run one frame of game per iteration
	{
		for (;;) //Infinite loop, ends when web page is updated
		{
				startTime = System.currentTimeMillis(); //Marking the start of frame code loop in milliseconds
				
				if (aliensLeft <= 0) //If there are no aliens left to destroy
				{
					nextLevel(); //Increment level
				}
				
				if (!paused) //Moves the ship if the ship is not paused
				{
					ship.move(dim.width, dim.height); //Moves ship
					
					for (int i = 0; i < numShots; i++) 
					{
						shots[i].move(dim.width, dim.height); //Moves each shot
						
						if (shots[i].getLifeLeft() <= 0) //If shot has no more life
						{
							deleteShot(i); //Delete the shot
							i--; //Move the loop back one
						}
					}
					
					for (int i = 0; i < numAShots; i++)
					{
						aShots[i].move(dim.width, dim.height); //Moves each alien shot
						
						if (aShots[i].getLifeLeft() <= 0) //If alien shot has no more life
						{
							deleteAShot(i); //Delete the shot
							i--; //Move the loop back one
						}
					}
					
					updateAliens(); //Update and move all aliens on screen
					
					if (shooting && ship.canShoot()) //If the ship is shooting and it can shoot
					{
						shots[numShots] = ship.shoot(); //Add a shot to the array
						numShots++;
					}
					
					for (int i = 0; i < numAliens; i++)
					{
						if (aliens[i].canShoot()) //If the alien can shoot
						{
							aShots[numAShots] = aliens[i].shoot(); //Add an alien shot to the array
							numAShots++;
						}
					}
					
					if (alienSpawn > 0) //If the alien spawn delay is not 0
					{
						alienSpawn--; //Decrease the delay
					}
					else
					{
						alienSpawn = 60 / (level + 1); //Otherwise reset the delay
					}
					
					if (alienSpawn == 60 / (level + 1) && maxAliens < ((int) Math.pow(level, 2)) + 20)  //If the spawn rate is reset and there less total aliens on screen than max aliens 
					{
						if (numAliens == 0)
						{
							aliens[numAliens] = new Alien(800, (Math.random() * 361) + 20, 2, 60, 60); //Create a new alien from scratch
						}
						else
						{
							aliens[numAliens] = aliens[numAliens - 1].spawn(dim.width, dim.height); //Create a new alien by calling the previous alien
						}
						numAliens++;
						maxAliens++;
					}
					
					for (int i = 0; i < numAliens; i++) //Set all aliens to be active
					{
						aliens[i].setActive(true);
					}
				}
				
				repaint(); //Updating circle
				
				endTime = System.currentTimeMillis(); //Marking the end of frame code loop in milliseconds
				
				if (framePeriod - (endTime - startTime) > 0) //If the time remaining in the allotted frame time is more than zero, sleep for that remaining time
				{
					try //Since Thread.sleep may throw exception, we must put it in a try catch block
					{
						Thread.sleep(framePeriod - (endTime - startTime)); //Sleep for remaining time
					}
					catch (InterruptedException e) 
					{
						//Do nothing if exception is thrown
					}
				}
		}
	}
	
	private void deleteShot(int index) //Deletes a shot and moves entire array down
	{
		numShots--; 
		for (int i = index; i < numShots; i++) //Sets every shot past the index to be the same as the one ahead of it
		{
			shots[i] = shots[i + 1];
		}
		
		shots[numShots] = null; //Sets the index shot to be null
	}
	
	private void deleteAlien(int index) //Deletes an alien and moves entire array down
	{
		numAliens--;
		aliensLeft--;
		for (int i = index; i < numAliens; i++) //Sets every alien past the index to be the same as the one ahead of it
		{
			aliens[i] = aliens[i + 1];
		}
		
		aliens[numAliens] = null; //Sets the index alien to be null
	}
	
	private void deleteAShot(int index) //Deletes an alien shot and moves entire array down
	{
		numAShots--;
		for (int i = index; i < numAShots; i++) //Sets every alien shot past the index to be the same as the one ahead of it
		{
			aShots[i] = aShots[i + 1];
		}
		
		aShots[numAShots] = null; //Sets the index alien shot to be null
	}
	
	private void updateAliens() //Method to update and move all aliens
	{
		for (int i = 0; i < numAliens; i++)
		{
			aliens[i].move(dim.width, dim.height); //Move aliens
			
			if (aliens[i].shipCollision(ship)) //If an alien has collided with the ship
			{
				level--; //Reset level
				numAliens = 0;
				aliensLeft = ((int) Math.pow(level, 2)) + 20;
				nextLevel(); //Reinitialize values
				return; //Break out of code
			}
			
			for (int j = 0; j < numAShots; j++) 
			{
				if (aShots[j].shipCollision(ship)) //If an alien shot has collided with the ship
				{
					level--; //Reset level
					numAliens = 0;
					aliensLeft = ((int) Math.pow(level, 2)) + 20;
					nextLevel(); //Reinitialize values
					return; //Break out of code
				}
			}
			
			for (int j = 0; j < numShots; j++)
			{
				if (aliens[i].shotCollision(shots[j])) //If a shot has collided with an alien
				{
					deleteShot(j); //Delete the shot
					
					deleteAlien(i); //Delete the alien
					
					j = numShots; 
					i--;
				}
			}
		}
	}
	
	//Key binding actions
	
	private class PauseAction extends AbstractAction
	{
		int player; //Currently this variable has no use, but could be used in the future to implement multiple players
		
		PauseAction(int player)
		{
			this.player = player;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (!ship.isActive() && !paused) //If the ship isn't active and isn't paused
			{
				ship.setActive(true); //Set the ship to be active
				for (int i = 0; i < numAliens; i++)
				{
					aliens[i].setActive(true);
				}
				shooting = true;
			}
			else
			{
				paused = !paused; //Unpause
				
				if (paused) //Deactivate the ship if game is paused
				{
					ship.setActive(false);
					for (int i = 0; i < numAliens; i++)
					{
						aliens[i].setActive(false);
					}
					shooting = false;
				}
				else
				{
					ship.setActive(true);
					for (int i = 0; i < numAliens; i++)
					{
						aliens[i].setActive(true);
					}
					shooting = true;
				}
			}
		}
	}
	
	private class UpPressAction extends AbstractAction
	{
		int player;
		
		UpPressAction(int player)
		{
			this.player = player;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (!(paused || !ship.isActive()))
			{
				ship.setYDecelerating(true);
			}
		}
	}
	
	private class DownPressAction extends AbstractAction
	{
		int player;
		
		DownPressAction(int player)
		{
			this.player = player;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (!(paused || !ship.isActive()))
			{
				ship.setYAccelerating(true);
			}
		}
	}

	private class LeftPressAction extends AbstractAction
	{
		int player;
		
		LeftPressAction(int player)
		{
			this.player = player;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (!(paused || !ship.isActive()))
			{
				ship.setXDecelerating(true);
			}
		}
	}

	private class RightPressAction extends AbstractAction
	{
		int player;
		
		RightPressAction(int player)
		{
			this.player = player;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (!(paused || !ship.isActive()))
			{
				ship.setXAccelerating(true);
			}
		}
	}
	
	private class DebugAction extends AbstractAction
	{
		int player;
		
		DebugAction(int player)
		{
			this.player = player;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (debug) //Toggle debug
			{
				debug = false;
			}
			else
			{
				debug = true;
			}
		}
	}

	private class UpReleaseAction extends AbstractAction
	{
		int player;
		
		UpReleaseAction(int player)
		{
			this.player = player;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			ship.setYDecelerating(false);
		}
	}
	
	private class DownReleaseAction extends AbstractAction
	{
		int player;
		
		DownReleaseAction(int player)
		{
			this.player = player;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			ship.setYAccelerating(false);
		}
	}
	
	private class LeftReleaseAction extends AbstractAction
	{
		int player;
		
		LeftReleaseAction(int player)
		{
			this.player = player;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			ship.setXDecelerating(false);
		}
	}

	private class RightReleaseAction extends AbstractAction
	{
		int player;
		
		RightReleaseAction(int player)
		{
			this.player = player;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			ship.setXAccelerating(false);
		}
	}
	
}
