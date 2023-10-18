package nahayi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;

public class OfflineGame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	//time that game is started.
	static long startTime;
 	//number of rows and columns of the game board.
	private static int row=20, column=20, proportion=6;
	//these variables will keep x and y of previous clicked button(is used for removing the text and icon of the previous button).
	private int bx=0, by=0;
	//making my player.
	private Player pl = new Player();
	//array list of robots
	private ArrayList<Player> robot = new ArrayList<Player>();
	//2D array of JButtons 
	private JButton[][] bt = new JButton[row][column];
	//thread for each robot.
	private Robot rb0;
	private Robot rb1;
	private Robot rb2;
	//thread for the life that is visible on a random place every 20 seconds.
	private Life lf;
	//timer for starting the thread with delay, so robots won't move at the same time.
	private Timer timer = new Timer();
	//Defining the images
	private Image redLeft = new ImageIcon(this.getClass().getResource("/img/RedLeft.jpg")).getImage();
	private Image blueDown = new ImageIcon(this.getClass().getResource("/img/BlueDown.jpg")).getImage();
	private Image blueLeft = new ImageIcon(this.getClass().getResource("/img/BlueLeft.jpg")).getImage();
	private Image blueRight = new ImageIcon(this.getClass().getResource("/img/BlueRight.jpg")).getImage();
	private Image blueUp = new ImageIcon(this.getClass().getResource("/img/BlueUp.jpg")).getImage();
	//for delaying for shooting.
	private boolean shootDelay = true;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OfflineGame frame = new OfflineGame(row, column, proportion);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public OfflineGame(int row, int column, int proportion) {
		setTitle("Counter Strike - Offline");
		startTime = System.currentTimeMillis();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(300, 100, 600, 475);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		//when Back is clicked, this page is disposed and WelcomePage is opened.
		JButton btnNewButton = new JButton("Back");
		btnNewButton.setFont(new Font("Stencil", Font.PLAIN, 14));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backToWelcomePage(row, column, proportion);
			}
		});
		contentPane.add(btnNewButton, BorderLayout.SOUTH);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(row, column, 0, 0));
		
		//when shoot is clicked, method shoot is called. but you can shoot once a second. 
		JButton btnShoot = new JButton("Shoot");
		btnShoot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//if shoot delay is true, you can shoot.
				if(shootDelay) {
					playSound();
					shoot(pl, row, column, proportion);
					//after shooting, shootDelay becomes false.
					shootDelay=false;
					//after one second it'll be true.
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
						shootDelay=true;
						}
					}, 1000);
				}
			}
		});
		btnShoot.setFont(new Font("Stencil", Font.PLAIN, 14));
		contentPane.add(btnShoot, BorderLayout.NORTH);
		
		//Specifying the places of robots. put random numbers of rows in 'xPlace' array list.
		ArrayList<Integer> xPlace = new ArrayList<Integer>(3);
		//Specifying the places of robots. put random numbers of columns in 'yPlace' array list.
		ArrayList<Integer> yPlace = new ArrayList<Integer>(3);		
		//this 'for' will give 3 random number between rows. if random number wasn't repeated, then it's accepted.
		int ran = randomNumber(1, row-2);
		for(int a=0; a<3; a++) {
			//i is a counter and used to break while.
			int i = 0;
				while(i<=a) {
					if(xPlace.contains(ran)) {
						ran = randomNumber(1, row-2);
					}
					else{
						i++;
					}
				}
				xPlace.add(ran);			
		}
		//this 'for' will give 3 random number between columns. if random number wasn't repeated or wasn't 1
		//(in order to not place on my player) then it's accepted.
		ran = randomNumber(1, column-2);
		for(int a=0; a<3; a++) {
			int i = 0;
				while(i<=a) {
					if(yPlace.contains(ran) || ran==1) {
						ran = randomNumber(1, column-2);
					}
					else {
						i++;
					}
				}
				yPlace.add(ran);
		}
		//making the 3 robots and adding them to array list of robots.
		robot.add(new Player());
		robot.add(new Player());
		robot.add(new Player());
		//the number of robot. is from 0 to 2. 3 robots.
		int num = 0;
		//filling the board with Buttons and making it.
		for(int i=0; i<row; i++) {
			for(int j=0; j<column; j++) {
				bt[i][j] = new JButton();
				//Specifying border buttons
				if(i==0 || j==0 || i==row-1 || j==column-1) {
					bt[i][j].setBackground(Color.DARK_GRAY);
				}
				else {
					//other buttons of board have light gray background and white foreground(except player, robots, etc).
					bt[i][j].setBackground(Color.LIGHT_GRAY);
					bt[i][j].setForeground(Color.white);
					//place of trenches (Trench ~ Sangar). chance of a button to be rench is 0.4 .
					if((Math.random() * 10) > proportion) {
						bt[i][j].setBackground(Color.yellow);
					}					
					//adding action listener to buttons. except dark gray ones.
					bt[i][j].addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							int x=0,y=0;
							for (int a = 0; a < row; a++) {
								for (int b = 0; b < column; b++) {
									if(e.getSource().equals(bt[a][b])) {
										x = a;
										y = b;
									}
								} 
							}
							// HACK (Maybe!)
							/*codes written after the above ActionPerformed are executed over and over, after every click. so
							 * it's searched for button with white background(my Player)every time and when it's found, method move
							 * is called.
							 * when you click on a button, it'll check for the button with white background, then make valid
							 *buttons green by move method. if the clicked button is a valid button, in real-time it'll be green and
							 *cause it's green, you'll be moved there or turn there. it's so fast that we can't see when it became green.
							 *if you click on a not valid button, then valid buttons will be green.
							 */
z:							for (int a = 0; a < row; a++) {
								for (int b = 0; b < column; b++) {
									if(bt[a][b].getBackground().equals(Color.white)) {
										move(bt, a, b);
										bx = a;
										by = b;
										break z;
									}
								}
							}
							//if clicked button is green or red(life), then player moves to this button and all green buttons should be white.
							if(bt[x][y].getBackground().equals(Color.green) || (bt[x][y].getBackground().equals(Color.red) && bt[x][y].getForeground().equals(Color.green)) && pl.lives>0) {
								//if this new button is red and if player's lives are less than 4, then player gains another life.
								if(bt[x][y].getBackground().equals(Color.red) && (pl.lives<4)) {
										pl.lives++;
										lifeSound();
								}
								//previous button should be like an empty place of board.
								bt[bx][by].setBackground(Color.LIGHT_GRAY);
								bt[bx][by].setIcon(null);
								bt[bx][by].setText(null);
								bt[x][y].setBackground(Color.white);
								/*if this green or red button is on right side of my player, then player's shooting direction should be
								 * right and the proper image should be set on this button.
								 */
								if(y>by) {
									bt[x][y].setIcon(new ImageIcon(blueRight.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
									pl.shoot = 2;
									pl.x = x;
									pl.y = y;
								}
								else if(y<by) {
									bt[x][y].setIcon(new ImageIcon(blueLeft.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
									pl.shoot = 4;
									pl.x = x;
									pl.y = y;
								}
								else if(x>bx) {
									bt[x][y].setIcon(new ImageIcon(blueDown.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
									pl.shoot = 3;
									pl.x = x;
									pl.y = y;
								}
								else if(x<bx) {
									bt[x][y].setIcon(new ImageIcon(blueUp.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
									pl.shoot = 1;
									pl.x = x;
									pl.y = y;
								}
								//this button should be like player button. 
								bt[x][y].setText(String.valueOf(pl.lives));
								bt[x][y].setHorizontalTextPosition(JButton.CENTER);
								bt[x][y].setVerticalTextPosition(JButton.CENTER);
								//after moving green buttons or those with green foreground should be like before.
								clearGreen(row, column);
							}
							//if foreground was green and if button isn't a life button, then player should turn to the direction of this button.
							if(bt[x][y].getForeground().equals(Color.green) && !bt[x][y].getBackground().equals(Color.red) && pl.lives>0) {
								//if button with green foreground was on right side, then player should turn right.
								if(y>by) {
									bt[bx][by].setIcon(new ImageIcon(blueRight.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
									pl.shoot = 2;
								}
								else if(y<by) {
									bt[bx][by].setIcon(new ImageIcon(blueLeft.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
									pl.shoot = 4;
								}
								else if(x>bx) {
									bt[bx][by].setIcon(new ImageIcon(blueDown.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
									pl.shoot = 3;
								}
								else if(x<bx) {
									bt[bx][by].setIcon(new ImageIcon(blueUp.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
									pl.shoot = 1;
								}
								//after turning, green buttons or those with green foreground should be like before.
								clearGreen(row, column);
							}
							/*it may happen that before you got killed, you've clicked to move and valid buttons are green. but if you're dead, you can't
							*move, so green buttons will disappear after you click on other buttons and we go back to WelcomePage.
							*/
							if(pl.lives<1) {
								clearGreen(row, column);
								backToWelcomePage(row, column, proportion);
							}
						}
					});	
					
				}
				panel.add(bt[i][j]);
			}	
		}
		//place of my player.
		bt[1][1].setBackground(Color.white);
		//place of robots
		bt[xPlace.get(0)][yPlace.get(0)].setBackground(Color.black);
		bt[xPlace.get(1)][yPlace.get(1)].setBackground(Color.black);
		bt[xPlace.get(2)][yPlace.get(2)].setBackground(Color.black);
		//find player and robots in the board and give them their properties.
		for(int i=0; i<row; i++) {
			for(int j=0; j<column; j++) {
				if(bt[i][j].getBackground().equals(Color.white)) {
					bt[1][1].setIcon(new ImageIcon(blueRight.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
					pl.shoot = 2;
					pl.x = i;
					pl.y = j;
					bt[i][j].setText(String.valueOf(pl.lives));
					bt[i][j].setHorizontalTextPosition(JButton.CENTER);
					bt[i][j].setVerticalTextPosition(JButton.CENTER);
				}
				if(bt[i][j].getBackground().equals(Color.black)) {
					bt[i][j].setIcon(new ImageIcon(redLeft.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
					robot.get(num).shoot=4;
					robot.get(num).x = i;
					robot.get(num).y = j;
					bt[i][j].setText(String.valueOf(robot.get(num).lives));
					bt[i][j].setHorizontalTextPosition(JButton.CENTER);
					bt[i][j].setVerticalTextPosition(JButton.CENTER);
					num++;
				}
			}
		}
		//Making object of Life thread and start it.
		lf = new Life(bt, row, column);
		lf.start();
		/*Making objects of Robot Thread for each robot and then start the threads. but with timer, the start of each of them
		 *  begins in different time for others, just because it's more beautiful.
		 */
		rb0 = new Robot(bt, robot.get(0), robot, pl, row, column);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				rb0.start();
			}
		}, 600);
		rb1 = new Robot(bt, robot.get(1), robot, pl, row, column);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				rb1.start();
			}
		}, 800);
		rb2 = new Robot(bt, robot.get(2), robot, pl, row, column);
		rb2.start();
	}
	//method for specifying valid buttons for turning or moving.
	void move(JButton[][] bt, int x, int y ) {
		//if button above player button isn't light gray(isn't empty), then its foreground should be green.
		if(!(bt[x-1][y].getBackground().equals(Color.LIGHT_GRAY))) {
			bt[x-1][y].setForeground(Color.green);
		}
		if(!(bt[x][y-1].getBackground().equals(Color.LIGHT_GRAY))) {
			bt[x][y-1].setForeground(Color.green);
		}
		if(!(bt[x+1][y].getBackground().equals(Color.LIGHT_GRAY))) {
			bt[x+1][y].setForeground(Color.green);
		}
		if(!(bt[x][y+1].getBackground().equals(Color.LIGHT_GRAY))) {
			bt[x][y+1].setForeground(Color.green);
		}
		//if button above player button is light gray(is empty), then its background should be green.
		if(bt[x-1][y].getBackground().equals(Color.LIGHT_GRAY)) {
			bt[x-1][y].setBackground(Color.green);
		}
		if(bt[x][y-1].getBackground().equals(Color.LIGHT_GRAY)) {
			bt[x][y-1].setBackground(Color.green);
		}
		if(bt[x+1][y].getBackground().equals(Color.LIGHT_GRAY)) {
			bt[x+1][y].setBackground(Color.green);
		}
		if(bt[x][y+1].getBackground().equals(Color.LIGHT_GRAY)) {
			bt[x][y+1].setBackground(Color.green);
		}
	}
	//method for shooting. row and column and proportion are defined here to be sent to checkForDeletePlayer method.
	void shoot(Player pl, int row, int column, int proportion) {
		//checks the shooting direction of player.
		switch (pl.shoot) {
		//if it's up
		case 1:
			//above buttons will be checked
b:			for(int i=pl.x - 1 ; i>=0; i--) { 
				//until its light gray(empty), we move up. we move till a button which isn't light gray. 
				if(!(bt[i][pl.y].getBackground().equals(Color.LIGHT_GRAY))) {
					//if the button is yellow(Trench) or dark gray(border buttons), bullet stops.
					if(bt[i][pl.y].getBackground().equals(Color.DARK_GRAY) || bt[i][pl.y].getBackground().equals(Color.yellow)) {
						break b;
					}
					//if button is black(robot), then the robot in that button is found and its lives become one less. 
					else if(bt[i][pl.y].getBackground().equals(Color.black)) {
						Player r = findRobot(i, pl.y);
						//checking if robot should be destroyed or not.
						checkForDeletePlayer(r, row, column, proportion);
						break b;
					}
				}
			}
			break;
		case 2: 
b:			for(int i=pl.y + 1 ; i<column ; i++) {
				if(!(bt[pl.x][i].getBackground().equals(Color.LIGHT_GRAY))) {
					if(bt[pl.x][i].getBackground().equals(Color.DARK_GRAY) || bt[pl.x][i].getBackground().equals(Color.yellow)) {
						break b;
					}
					else if(bt[pl.x][i].getBackground().equals(Color.black)) {
						Player r = findRobot(pl.x, i);
						checkForDeletePlayer(r, row, column, proportion);
						break b;
					}
				}
			}
			break;
		case 3:
b:			for(int i=pl.x + 1 ; i<row ; i++) {
				if(!(bt[i][pl.y].getBackground().equals(Color.LIGHT_GRAY))) {
					if(bt[i][pl.y].getBackground().equals(Color.DARK_GRAY) || bt[i][pl.y].getBackground().equals(Color.yellow)) {
						break b;
					}
					else if(bt[i][pl.y].getBackground().equals(Color.black)) {
						Player r = findRobot(i, pl.y);
						checkForDeletePlayer(r, row, column, proportion);
						break b;
					}
				}
			}
			break;
		case 4:
b:			for(int i=pl.y - 1 ; i>=0 ; i--) {
				if(!(bt[pl.x][i].getBackground().equals(Color.LIGHT_GRAY))) {
					if(bt[pl.x][i].getBackground().equals(Color.DARK_GRAY) || bt[pl.x][i].getBackground().equals(Color.yellow)) {
						break b;
					}
					else if(bt[pl.x][i].getBackground().equals(Color.black)) {
						Player r = findRobot(pl.x, i);
						checkForDeletePlayer(r, row, column, proportion);
						break b;
					}
				}
			}
			break;
		default:
			break;
		}		
	}
	//Returns the robot in button[x][y] in order to shoot that robot or destroy it.
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
	/*makes the lives of the robot one less, and delete it if lives is 0. and checks if you won then shows the playing time in message.
	 * row and column and  proportion are defined to be sent to writeFile method.
	 */
	void checkForDeletePlayer(Player r, int row, int column, int proportion) {
		//if robot could ran away from bullet, then we come out of this method.
		if(r==null)
			return;
		//if robot's lives were more than zero, lives should be one less.
		if(r.lives>0) {
			r.lives--;
			bt[r.x][r.y].setText(String.valueOf(r.lives));
		}
		//if lives is zero, then robot should be deleted.
		if(r.lives<1) {
			bt[r.x][r.y].setBackground(Color.LIGHT_GRAY);
			bt[r.x][r.y].setText(null);
			bt[r.x][r.y].setIcon(null);
			r.shoot=0;
			robot.remove(r);
			r=null;
			/*if all robots are destroyed, then a message should show the time of playing and after 5 seconds disappears
			*and we go back to welcome page.
			*/
			if(robot.size()==0) {
				//time which game is finished in.
				long finishTime = System.currentTimeMillis();
				//calculating the gameTime, but in second, not in milliseconds.
				long gameTime = (long)(finishTime - startTime)/1000;
				writeFile(gameTime, row, column, proportion);
				//with codes below we make a message, but without any buttons or any actions for disposing message.
				JOptionPane pane = new JOptionPane("You won!"+"\nGame time = "+gameTime+" seconds", JOptionPane.INFORMATION_MESSAGE,
						JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
				JDialog dia = new JDialog();
				dia.setTitle("GAME FINISHED");
				dia.setModal(true);
				dia.setContentPane(pane);
				dia.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				dia.pack();
				//timer which after 5 seconds dispose the message and this page and opens welcome page. 
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
					dia.dispose();
					dispose();
					WelcomePage wp = new WelcomePage(row, column, proportion);
					wp.setVisible(true);
					}
				}, 5*1000);
				dia.setVisible(true);
			}
		}
	}
	//makes a random number like: min<=ran<=max .
	int randomNumber(int min, int max) {
		Random r = new Random();
		return r.nextInt( (max-min)+1 ) + min;
	}
	//sound of my player gun shot.
	void playSound() {
		try {
			AudioInputStream audio = AudioSystem.getAudioInputStream(this.getClass().getResource("/img/gunshot.wav"));
			Clip clip = AudioSystem.getClip();
			clip.open(audio);
			clip.start();
		} catch (Exception e) {
		}
	}
	//sound when player gain a new life.
	void lifeSound() {
		try {
			AudioInputStream audio = AudioSystem.getAudioInputStream(this.getClass().getResource("/img/newLife.wav"));
			Clip clip = AudioSystem.getClip();
			clip.open(audio);
			clip.start();
		} catch (Exception e) {
		}
	}

	//writes the time of playing and information about board in a file.
	void writeFile(long sec, int row, int column, int proportion) {
		String time = Long.toString(sec);
		try {
			File file = new File("Score.txt");
			if(!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
			buf.write(" \nBoard: "+row+"*"+column+"   Proportion of trenches: "+(10-proportion)+"   Time: "+time+" seconds");
			buf.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	//Disposing page
	void backToWelcomePage(int row, int column, int proportion) {
		//killing all the robots, so their threads will be interrupted.
		for(int i=0; i<robot.size(); i++) {
			robot.get(i).shoot=0;
			robot.get(i).lives=0;
		}
		dispose();
		WelcomePage wp = new WelcomePage(row, column, proportion);
		wp.setVisible(true);
	}
	//Clearing Green buttons
	void clearGreen(int row, int column) {
		for(int m=0; m<row; m++) {
			for(int n=0; n<column; n++) {
				if(bt[m][n].getBackground().equals(Color.green))
					bt[m][n].setBackground(Color.LIGHT_GRAY);
				if(bt[m][n].getForeground().equals(Color.green))
					bt[m][n].setForeground(Color.white);
			}
		}
	}
}
