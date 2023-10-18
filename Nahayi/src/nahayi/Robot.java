package nahayi;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

//this class is a thread
public class Robot extends Thread {
	//in the thread of robots we need the game board information, so we define them here and initialize them in constructor.
	private JButton[][] bt;
	private int row , column;
	private Player rb;
	private ArrayList<Player> robot;
	private Player pl;
	//getting the images of robots.
	private Image redDown = new ImageIcon(this.getClass().getResource("/img/RedDown.jpg")).getImage();
	private Image redLeft = new ImageIcon(this.getClass().getResource("/img/RedLeft.jpg")).getImage();
	private Image redRight = new ImageIcon(this.getClass().getResource("/img/RedRight.jpg")).getImage();
	private Image redUp = new ImageIcon(this.getClass().getResource("/img/RedUP.jpg")).getImage();
	//constructor
	public Robot(JButton[][] bt, Player rb, ArrayList<Player> robot , Player pl , int row, int column) {
		this.bt = bt;
		this.rb = rb;
		this.robot = robot;
		this.pl = pl;
		this.row = row;
		this.column = column;
	}



	@Override
	public void run() {
		//while robot's lives is more than 0, it will play.
		while(rb.lives>0) {
			//after any action, robot will sleep for one second.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//for shooting, shooting direction of robot must be checked.
			switch (rb.shoot) {
			//if it is up.
			case 1:
				//move toward above buttons.
b:				for(int i=rb.x - 1 ; i>=0; i-- ) {
					if((bt[i][rb.y].getBackground().equals(Color.LIGHT_GRAY))) {
						continue;
					}
					else {
						//if it was Player, then shoot.
						if(bt[i][rb.y].getBackground().equals(Color.white)) {
							checkForDeletePlayer(pl);
							isGameOver(pl);
							break b;
						}
						//if it was Robot, find which one is it and then shoot.
						else if(bt[i][rb.y].getBackground().equals(Color.black)) {
							Player robot = findRobot(i, rb.y);
							checkForDeletePlayer(robot);
							break b;
						}
						//if it was Trench or border of board, robot should move.
						else if(bt[i][rb.y].getBackground().equals(Color.yellow) || bt[i][rb.y].getBackground().equals(Color.DARK_GRAY)) {
							move(rb);
							break b;
						}
					}
				}
				break;
			case 2:
b:				for(int i=rb.y + 1 ; i<column ; i++) {
					if((bt[rb.x][i].getBackground().equals(Color.LIGHT_GRAY))) {
						continue;
					}
					else {
						if(bt[rb.x][i].getBackground().equals(Color.white)) {
							checkForDeletePlayer(pl);
							isGameOver(pl);
							break b;
						}
						else if(bt[rb.x][i].getBackground().equals(Color.black)) {
							Player robot = findRobot(rb.x, i);
							checkForDeletePlayer(robot);
							break b;
						}
						else if(bt[rb.x][i].getBackground().equals(Color.yellow) || bt[rb.x][i].getBackground().equals(Color.DARK_GRAY)) {
							move(rb);
							break b;
						}
					}
				}
				break;
			case 3:
b:				for(int i=rb.x + 1 ; i<row ; i++) {
					if((bt[i][rb.y].getBackground().equals(Color.LIGHT_GRAY))) {
						continue;
					}
					else {
						if(bt[i][rb.y].getBackground().equals(Color.white)) {
							checkForDeletePlayer(pl);
							isGameOver(pl);
							break b;
						}
						else if(bt[i][rb.y].getBackground().equals(Color.black)) {
							Player robot = findRobot(i, rb.y);
							checkForDeletePlayer(robot);
							break b;
						}
						else if(bt[i][rb.y].getBackground().equals(Color.yellow) || bt[i][rb.y].getBackground().equals(Color.DARK_GRAY)) {
							move(rb);
							break b;
						}
					}
				}
				break;
			case 4:
b:				for(int i=rb.y - 1 ; i>=0 ; i--) {
					if((bt[rb.x][i].getBackground().equals(Color.LIGHT_GRAY))) {
						continue;
					}
					else {
						if(bt[rb.x][i].getBackground().equals(Color.white)) {
							checkForDeletePlayer(pl);
							isGameOver(pl);
							break b;
						}
						else if(bt[rb.x][i].getBackground().equals(Color.black)) {
							Player robot = findRobot(rb.x, i);
							checkForDeletePlayer(robot);
							break b;
						}
						else if(bt[rb.x][i].getBackground().equals(Color.yellow) || bt[rb.x][i].getBackground().equals(Color.DARK_GRAY)) {
							move(rb);
							break b;
						}
					}
				}
				break;
			default:
				break;
			}
		}
	}
	//like in OfflineGame class
	Player findRobot(int x , int y) {
		Player plyr = null;
		for(int i=0; i<robot.size(); i++) {
			if(robot.get(i).x == x && robot.get(i).y == y) {
				plyr = robot.get(i);
				break;
			}
		}
		return plyr;
	}
	//like in OfflineGame class
	synchronized void checkForDeletePlayer(Player r) {
		if(r==null)
			return;
		if(r.lives>0) {
			r.lives--;
			bt[r.x][r.y].setText(String.valueOf(r.lives));
		}
		if(r.lives<1) {
			bt[r.x][r.y].setBackground(Color.LIGHT_GRAY);
			bt[r.x][r.y].setText(null);
			bt[r.x][r.y].setIcon(null);
			r.shoot = 0;
			robot.remove(r);
			r = null;
		}
		playSound();
	}
	//Method for moving the robot.
	synchronized void move(Player robot) {
		//put the x and y of the robot in variables with shorter name to be used more easily.
		int x = robot.x;
		int y = robot.y;
		//an integer variable for saving a random number.
		int ran;
		//an ArrayList of valid directions for moving.
		ArrayList<Integer> validWay = new ArrayList<Integer>();
		//if the above button was empty(light gray) or life(red), then it'll be added 1 to "validWay" ArrayList.
		if(bt[x-1][y].getBackground().equals(Color.LIGHT_GRAY) || bt[x-1][y].getBackground().equals(Color.red)) {
			validWay.add(1);
		}
		if(bt[x][y+1].getBackground().equals(Color.LIGHT_GRAY) || bt[x][y+1].getBackground().equals(Color.red)) {
			validWay.add(2);
		}
		if(bt[x+1][y].getBackground().equals(Color.LIGHT_GRAY) || bt[x+1][y].getBackground().equals(Color.red)) {
			validWay.add(3);
		}
		if(bt[x][y-1].getBackground().equals(Color.LIGHT_GRAY) || bt[x][y-1].getBackground().equals(Color.red)) {
			validWay.add(4);
		}
		//if there is no way and robot is alive, it does't move.
		//like ( * is blocked way. > and < are robots ):			*
		/*														*	<	>
		 * 															*
		 */				
		if(validWay.size()==0) {
			if(robot.lives>0)
			return;
		}
		//a random number in range of validWay ArrayList.
		ran = Randomnumber(0, validWay.size()-1);
		//for moving, current button should be like an empty place of board and properties of robot move to new button.
		bt[x][y].setBackground(Color.LIGHT_GRAY);
		bt[x][y].setText(null);
		bt[x][y].setIcon(null);
		//switch on the index with random value.
		switch (validWay.get(ran)) {
		//if valid direction is up
		case 1:
			//if this button is life button and robot's lives is less than 4, then robot gains another life.
			if(bt[x-1][y].getBackground().equals(Color.red) && rb.lives<4) {
				rb.lives++;
			}
			//Changing x and y and shooting direction of robot and adding properties of robot to this button.
			bt[x-1][y].setBackground(Color.black);
			bt[x-1][y].setText(String.valueOf(robot.lives));
			bt[x-1][y].setIcon(new ImageIcon(redUp.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
			bt[x-1][y].setHorizontalTextPosition(JButton.CENTER);
			bt[x-1][y].setVerticalTextPosition(JButton.CENTER);
			robot.x = x-1;
			robot.y = y;
			robot.shoot = 1;
			break;
		case 2:
			if(bt[x][y+1].getBackground().equals(Color.red) && rb.lives<4) {
				rb.lives++;
			}
			bt[x][y+1].setBackground(Color.black);
			bt[x][y+1].setText(String.valueOf(robot.lives));
			bt[x][y+1].setIcon(new ImageIcon(redRight.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
			bt[x][y+1].setHorizontalTextPosition(JButton.CENTER);
			bt[x][y+1].setVerticalTextPosition(JButton.CENTER);
			robot.x = x;
			robot.y = y+1;
			robot.shoot = 2;
			break;
		case 3:
			if(bt[x+1][y].getBackground().equals(Color.red) && rb.lives<4) {
				rb.lives++;
			}
			bt[x+1][y].setBackground(Color.black);
			bt[x+1][y].setText(String.valueOf(robot.lives));
			bt[x+1][y].setIcon(new ImageIcon(redDown.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
			bt[x+1][y].setHorizontalTextPosition(JButton.CENTER);
			bt[x+1][y].setVerticalTextPosition(JButton.CENTER);
			robot.x = x+1;
			robot.y = y;
			robot.shoot = 3;
			break;
		case 4:
			if(bt[x][y-1].getBackground().equals(Color.red) && rb.lives<4) {
				rb.lives++;
			}
			bt[x][y-1].setBackground(Color.black);
			bt[x][y-1].setText(String.valueOf(robot.lives));
			bt[x][y-1].setIcon(new ImageIcon(redLeft.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
			bt[x][y-1].setHorizontalTextPosition(JButton.CENTER);
			bt[x][y-1].setVerticalTextPosition(JButton.CENTER);
			robot.x = x;
			robot.y = y-1;
			robot.shoot = 4;
			break;
		default:
			break;
		}
	}
	//like in OfflineGame class
	int Randomnumber(int min, int max) {
		Random r = new Random();
		return r.nextInt( (max-min)+1 ) + min;
	}
	//sound of robot's gun.
	void playSound() {
		try {
			AudioInputStream audio = AudioSystem.getAudioInputStream(this.getClass().getResource("/img/lasergun.wav"));
			Clip clip = AudioSystem.getClip();
			clip.open(audio);
			clip.start();
		} catch (Exception e) {
		}
	}
	//checks if the player got killed by robots and if yes, then shows the message "Game Over" with time of playing for 5 seconds.
	void isGameOver(Player pl) {
		if(pl.shoot==0) {
			long finishTime = System.currentTimeMillis();
			long gameTime = (long)(finishTime - OfflineGame.startTime)/1000;
			JOptionPane pane = new JOptionPane("You got killed!\nIf you want to exit game, click on Back or any button in board"+"\nPlaying time = "+gameTime+" sec", JOptionPane.INFORMATION_MESSAGE,
					JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
			JDialog dia = new JDialog();
			dia.setTitle("Game Over!");
			dia.setModal(true);
			dia.setContentPane(pane);
			dia.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
			dia.pack();
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				
				@Override
				public void run() {
				dia.dispose();
				}
			}, 7*1000);
			dia.setVisible(true);
		}
	}
	

}
