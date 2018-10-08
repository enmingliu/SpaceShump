import java.awt.*;

public class Alien 
{
	/*
	 * Alien Method
	 */
	
	//Define the shape of the alien
	final double[] originXPoints = {-10, 14, 10, 14}; //X coordinates for alien
	final double[] originYPoints = {0, -8, 0, 8}; //Y coordinates for alien
	
	final int radius = 12; //Radius of circle used to approximate alien
	
	//Variables used in movement
	double x;
	double y;
	double xVelocity;
	double yVelocity;
	
	boolean active;
	
	//Arrays used to store current locations of ship and flame
	int[] xPoints;
	int[] yPoints;
	
	//Used to determine rate of firing
	int shotDelay;
	int shotDelayLeft;
	
	//Used to determine rate of spawn
	int spawnDelay;
	int spawnDelayLeft;
	
	public Alien(double x, double y, double xVelocity, int shotDelay, int spawnDelay) //Alien constructor
	{
		//Set all variables
		this.x = x;
		this.y = y;
		
		//Initialize all variables
		this.xVelocity = xVelocity;
		this.yVelocity = 0;
		
		active = false; //Starting off paused
		
		//Allocate space for the arrays
		xPoints = new int[4]; 
		yPoints = new int[4];

		this.shotDelay = shotDelay; //Set # of frames between shots
		shotDelayLeft = 0; //Set ready to shoot
		
		this.spawnDelay = spawnDelay; //Set # of frames between spawns
		spawnDelayLeft = 0; //Set ready to spawn
	}
	
	public void draw(Graphics g) //Draws the alien
	{
		for (int i = 0; i < 4; i++) //For 4 times
		{
			//Rotating x and y points, translating them to the ships location by adding x or y, adding 0.5 and casting as integers, thus rounding them
			xPoints[i] = (int) (originXPoints[i] + x + 0.5);
			yPoints[i] = (int) (originYPoints[i] + y + 0.5);
		}
		
		if (active) //If active
		{
			g.setColor(Color.lightGray); //If game is running, draw white
		}
		else
		{
			g.setColor(Color.darkGray); //If not, draw gray
		}

		g.fillPolygon(xPoints, yPoints, 4); //Drawing ship
	}
	
	public void move(int screenWidth, int screenHeight) //To move the ship each frame
	{
		if(shotDelayLeft > 0) //For every frame, the shot delay is ticked down
		{
			shotDelayLeft--;
		}
		
		if(spawnDelayLeft > 0) //For every frame, the shot delay is ticked down
		{
			spawnDelayLeft--;
		}
		
		x -= xVelocity; //Adds the velocity to the position of the ship
		y -= yVelocity;
		
		if (x < 0) //If x position is out of the window, respawn at a random location
		{
			x = screenWidth;
			y = (Math.random() * 361) + 20;
		}
	}
	
	//Collision
	
	public boolean shipCollision(Ship ship) //Checks if alien has collided with the ship
	{
		//Checking collision:
		//If the sum of the radius of the circle circumscribing the ship and the alien is greater than the distance between the center of the two circles
		//The ships are colliding, otherwise, not
		if (Math.pow(radius + ship.getRadius(), 2) > Math.pow(ship.getX() - x, 2) + Math.pow(ship.getY() - y, 2) && ship.isActive())
		{
			return true;
		}
		
		return false;
	}
	
	public boolean shotCollision(Shot shot) //Checks if alien has collided with a shot
	{
		//Checking collision is same as with a ship, but shots don't have a radius
		if (Math.pow(radius, 2) > Math.pow(shot.getX() - x, 2) + Math.pow(shot.getY() - y, 2))
		{
			return true;
		}
		
		return false;
	}
	
	public double getX() //Returns the ships x location
	{
		return this.x;
	}
	
	public double getY() //Returns the ships y location
	{
		return this.y;
	}
	
	public double getRadius() //Returns radius of circle that approximates the ship
	{
		return this.radius; 
	}
	
	public void setActive(boolean active) //Sets if the ship is active or not
	{
		this.active = active;
	}
	
	public boolean isActive() //Returns if ship is active or not
	{
		return this.active;
	}
	
	public boolean canShoot() //Returns if the ship can shoot
	{
		if (shotDelayLeft > 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public boolean canSpawn() //Returns if the ship can spawn
	{
		if (spawnDelayLeft > 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public AlienShot shoot() //Method that creates an alien shot at the alien
	{
		shotDelayLeft = shotDelay; //Set delay until next shot can be fired
		
		return new AlienShot(x, y, 120); //Creates shot
	}
	
	public Alien spawn(int screenWidth, int screenHeight) //Method that spawns an alien
	{
		spawnDelayLeft = spawnDelay; //Set delay until next alien can be spawned
		
		return new Alien(screenWidth, (Math.random() * 361) + 20, xVelocity, 60, 60); //Creates alien
	}
	
}
