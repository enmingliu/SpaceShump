import java.awt.*;

public class AlienShot 
{
	/*
	 * Alien Shot Method
	 */
	
	final double shotSpeed = -6; //The speed at which the shots move, in pixels per frame
	
	//Variables for movement
	double x;
	double y;
	double xVelocity;
	double yVelocity;
	
	int lifeLeft; //Life to cause shot to disappear if it does not hit anything
	
	public AlienShot(double x, double y, int lifeLeft) //Alien shot constructor
	{
		//Set all variables
		this.x = x; 
		this.y = y;
		
		xVelocity = shotSpeed; 
		yVelocity = 0;
		
		this.lifeLeft = lifeLeft; //Setting life left value
	}
	
	public void move(int screenWidth, int screenHeight) //To move the alien shot each frame
	{
		lifeLeft--; //Decrease life left per frame
		
		x += xVelocity; //Moving the shot in the x and y direction
		y += yVelocity;
	}
	
	public void draw(Graphics g) //Draws the shot
	{
		g.setColor(Color.lightGray);
		g.fillOval((int) (x - 0.5), (int) (y - 0.5), 3, 3);
	}
	
	public double getX() //Returns x position
	{
		return x;
	}
	
	public double getY() //Returns y position
	{
		return y;
	}
	
	public int getLifeLeft() //Returns life left values
	{
		return lifeLeft;
	}

	//Collision
	
	public boolean shipCollision(Ship ship) //Checks if the alien shot has collided with the ship
	{
		//Checking collision:
		//If the sum of the radius of the circle circumscribing the ship and the alien is greater than the distance between the center of the two circles
		//The ships are colliding, otherwise, not
		if (Math.pow(ship.getRadius(), 2) > Math.pow(ship.getX() - x, 2) + Math.pow(ship.getY() - y, 2) && ship.isActive())
		{
			return true;
		}
		
		return false;
	}
	
}
