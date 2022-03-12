/*
 * Simple Snake Game
 * GamePanel class which runs the Snake Game
 * Created By: Carter Gamary
 */

// Import statements required
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

// GamePanel class, runs the snake game
public class GamePanel extends JPanel implements ActionListener{
	
	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 25;
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
	static final int DELAY = 75;
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyParts = 6;
	int applesEaten;
	int appleX;
	int appleY;
	char direction = 'R';
	boolean running = false;
	Timer timer;
	Random random;
	
	// Creates new instance of GamePanel, and calls startGame
	GamePanel() {
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGame();
	}
	
	// Starts the game
	public void startGame() {
		newApple();
		running = true;
		timer = new Timer(DELAY, this);
		timer.start();
	}
	
	// Calls for super
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	
	// Creates the visual for the game
	public void draw(Graphics g) {
		if (running) {
			/* Draws grid squares (if making changes)
			for (int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++) {
				g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
				g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
			} */
			
			// Create apple
			g.setColor(Color.red);
			g.fillOval(appleX, appleY, UNIT_SIZE - 2, UNIT_SIZE - 2);
			
			// Create the snake
			for (int i = 0; i < bodyParts; i++) {
				// First make snake head then body
				if (i == 0) {
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
				else {
					g.setColor(new Color(45, 180, 0));
					// Creates a fancy colored snake if wanting rainbow snake
					// g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			// Scoreboard
			g.setColor(Color.red);
			g.setFont(new Font("Ink Free", Font.BOLD, 40));
			FontMetrics metric = getFontMetrics(g.getFont());
			g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metric.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
		} else {
			// end game if not running
			gameOver(g);
		}
	}
	
	// Generates new apple coordinates whenever needed
	public void newApple() {
		// Creates initial coordinated for apple
		appleX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
		appleY = random.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
		
		// Checks coordinates and adjusts if apple lands on snake
		for (int i = 0; i < bodyParts; i++) {
			if ((x[i] == appleX) && (y[i] == appleY)) {
				appleX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
				appleY = random.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
				i = -1;
			}
		}
	}
	
	// Determines movement for the snake
	public void move() {
		// Moves all body parts (except the head) to their next location
		for (int i = bodyParts; i > 0; i--) {
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}
		
		// Moves the head to the direction it is facing
		switch(direction) {
		case 'U':
			y[0] = y[0] - UNIT_SIZE;
			break;
		case 'D':
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'L':
			x[0] = x[0] - UNIT_SIZE;
			break;
		case 'R':
			x[0] = x[0] + UNIT_SIZE;
			break;
		}
	}
	
	// Checks to see if an apple has been eaten by the snake
	public void checkApple() {
		if ((x[0] == appleX) && (y[0] == appleY)) {
			bodyParts++;
			applesEaten++;
			newApple();
		}
	}
	
	// Checks if snake has collided with itself or the walls
	public void checkCollisions() {
		// Checks head collide with body
		for (int i = bodyParts; i > 0; i--) {
			if ((x[0] == x[i]) && (y[0] == y[i])) {
				running = false;
			}
		}
		
		// Checks if head touches borders
		if ((x[0] < 0) || (x[0] > SCREEN_WIDTH - UNIT_SIZE) || (y[0] > SCREEN_HEIGHT - UNIT_SIZE) || (y[0] < 0)) {
			running = false;
		}
		
		if (!running) {
			timer.stop();
		}
	}
	
	// Displays the game over screen
	public void gameOver(Graphics g) {
		// Game Over text
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 75));
		FontMetrics metric = getFontMetrics(g.getFont());
		
		// Draws the Game Over String in center of the screen
		g.drawString("Game Over", (SCREEN_WIDTH - metric.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
		
		// Scoreboard
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 40));
		FontMetrics metric2 = getFontMetrics(g.getFont());
		g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metric2.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
	}
	
	// Runs the game
	@Override
	public void actionPerformed(ActionEvent e) {
		if (running) {
			move();
			checkApple();
			checkCollisions();
		}
		repaint();
	}
	
	// Performs the keypresses required
	public class MyKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			// Changes directions as key is pressed if able to do so.
			switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if (direction != 'R') {
					direction = 'L';
				}
				break;
			case KeyEvent.VK_RIGHT:
				if(direction != 'L') {
					direction = 'R';
				}
				break;
			case KeyEvent.VK_UP:
				if(direction != 'D') {
					direction = 'U';
				}
				break;
			case KeyEvent.VK_DOWN:
				if(direction != 'U') {
					direction = 'D';
				}
				break;
			}
		}
	}
}
