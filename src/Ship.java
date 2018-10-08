import java.awt.*;

public class Ship 
{
	/*
	 * Ship Method
	 */
	
	//Define the shape of the ship and its flame
	final double[] originXPoints = {14, -10, -6, -10}; //X coordinates for ship
	final double[] originYPoints = {0, -8, 0, 8}; //Y coordinates for ship
	final double[] originFlameXPoints = {-6, -23, -6}; //X coordinates for flame
	final double[] originFlameYPoints = {-3, 0, 3}; //Y coordinates for flame
	
	final int radius = 6; //Radius of circle used to approximate ship
	
	//Variables used in movement
	double x;
	double y;
	double xVelocity;
	double yVelocity;
	double xAcceleration;
	double yAcceleration;
	double velocityDecay;
	
	boolean xAccelerating;
	boolean yAccelerating;
	boolean xDecelerating;
	boolean yDecelerating;
	boolean active;
	
	//Arrays used to store current locations of ship and flame
	int[] xPoints;
	int[] yPoints;
	int[] flameXPoints;
	int[] flameYPoints;
	
	//Used to determine rate of firing
	int shotDelay;
	int shotDelayLeft;

	public Ship(double x, double y, double xAcceleration, double yAcceleration, double velocityDecay, double rotationalSpeed, int shotDelay) //Ship constructor
	{
		//Set all variables
		this.x = x;
		this.y = y;
		this.xAcceleration = xAcceleration;
		this.yAcceleration = yAcceleration;
		this.velocityDecay = velocityDecay;
		
		//Initialize all variables
		xVelocity = 0; //Starting off not moving
		yVelocity = 0;
		
		xAccelerating = false; //Starting off not accelerating
		yAccelerating = false;
		
		active = false; //Starting off paused
		
		//Allocate space for the arrays
		xPoints = new int[4]; 
		yPoints = new int[4];
		flameXPoints = new int[3];
		flameYPoints = new int[3];
		
		this.shotDelay = shotDelay; //Set # of frames between shots
		shotDelayLeft = 0; //Set ready to shoot
	}
	
	public void draw(Graphics g) //Draws the ship
	{
		if (xAccelerating && active) //If the ship is accelerating and active
		{
			for (int i = 0; i < 3; i++) //For 3 times
			{
				//Rotating x and y points, translating them to the ships location by adding x or y, adding 0.5 and casting as integers, thus rounding them
				flameXPoints[i] = (int) (originFlameXPoints[i] + x + 0.5);
				flameYPoints[i] = (int) (originFlameYPoints[i] + y + 0.5);
			}
			g.setColor(Color.red); //Setting color to white
			g.drawPolygon(flameXPoints, flameYPoints, 3); //Drawing flame polygon
		}
		
		for (int i = 0; i < 4; i++) //For 4 times
		{
			//Rotating x and y points, translating them to the ships location by adding x or y, adding 0.5 and casting as integers, thus rounding them
			xPoints[i] = (int) (originXPoints[i] + x + 0.5);
			yPoints[i] = (int) (originYPoints[i] + y + 0.5);
		}
		
		if (active) //If active
		{
			g.setColor(Color.white); //If game is running, draw white
		}
		else
		{
			g.setColor(Color.gray); //If not, draw gray
		}
		
		g.fillPolygon(xPoints, yPoints, 4); //Drawing ship
	}
	
	public void move(int screenWidth, int screenHeight) //To move the ship each frame
	{
		if(shotDelayLeft > 0) //For every frame, the shot delay is ticked down
		{
			shotDelayLeft--;
		}
		
		//If accelerating, add acceleration to velocity
		if (xAccelerating) 
		{
			xVelocity += xAcceleration; 
		}
		
		if (xDecelerating)
		{
			xVelocity -= xAcceleration;
		}
		
		if (yAccelerating) 
		{
			yVelocity += yAcceleration; 
		}
		
		if (yDecelerating)
		{
			yVelocity -= yAcceleration;
		}
		
		x += xVelocity; //Adds the velocity to the position of the ship
		y += yVelocity;
		
		xVelocity *= velocityDecay; //Slows down ship by a percentage to limit maximum velocity
		yVelocity *= velocityDecay;
		
		if (x < 0) //If x position is out of the window, wrap around
		{
			x = 0;
		}
		else if (x > screenWidth)
		{
			x = screenWidth;
		}
		
		if (y < 0) //If y position is out of window, wrap around
		{
			y += screenHeight;
		}
		else if (y > screenHeight)
		{
			y -= screenHeight;
		}
	}
	
	public void setXAccelerating(boolean xAccelerating) //Set if the ship is accelerating in x direction
	{
		this.xAccelerating = xAccelerating; 
	}
	
	public void setYAccelerating(boolean yAccelerating) //Set if the ship is accelerating in y direction
	{
		this.yAccelerating = yAccelerating; 
	}
	
	public void setXDecelerating(boolean xDecelerating) //Set if the ship is decelerating in x direction
	{
		this.xDecelerating = xDecelerating; 
	}
	
	public void setYDecelerating(boolean yDecelerating) //Set if the ship is decelerating in y direction
	{
		this.yDecelerating = yDecelerating; 
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
	
	public Shot shoot() //Method that creates a shot from the ship
	{
		shotDelayLeft = shotDelay; //Set delay until next shot can be fired
		
		return new Shot(x + 8, y, xVelocity, yVelocity, 60); //Creates shot
	}
	
}
