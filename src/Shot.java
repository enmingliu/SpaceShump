import java.awt.*;

public class Shot 
{
	/*
	 * Shot Method
	 */
	
	final double shotSpeed = 12; //The speed at which the shots move, in pixels per frame
	
	//Variables for movement
	double x;
	double y;
	double xVelocity;
	double yVelocity;
	
	int lifeLeft; //Life to cause shot to disappear if it does not hit anything
	
	public Shot(double x, double y, double shipXVelocity, double shipYVelocity, int lifeLeft) //Shot constructor
	{
		//Set all variables
		this.x = x; 
		this.y = y;
		
		xVelocity = shotSpeed + (shipXVelocity / 4); //Adding velocity of ship to velocity of shot
		yVelocity = shipYVelocity / 4;
		
		this.lifeLeft = lifeLeft; //Setting life left value
	}
	
	public void move(int screenWidth, int screenHeight) //To move the shot each frame
	{
		lifeLeft--; //Decrease life left per frame
		
		x += xVelocity; //Moving the shot in the x and y direction
		y += yVelocity;
		
		if (y < 0) //Wrapping shot across screen if needed
		{
			y += screenHeight;
		}
		else if (y > screenHeight)
		{
			y -= screenHeight;
		}
	}
	
	public void draw(Graphics g) //Draws the shot
	{
		g.setColor(Color.white);
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
	
}
