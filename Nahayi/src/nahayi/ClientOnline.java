package nahayi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


public class ClientOnline extends JFrame{
	private static final long serialVersionUID = 1L;
	private static DataOutputStream out;
	private static DataInputStream in;
	private static Socket socket;
	//name of client.
	private static String name;
	//making my player.
	private static Player pl = new Player();
	//making enemy
	private static Player en = new Player();
 	//number of rows and columns of the game board.
	private static int row=10, column=10, proportion=6;
	//row of trenches
	private static ArrayList<Integer> rowOfTrenches;
	//column of trenches
	private static ArrayList<Integer> columnOfTrenches;
	//timer used for making 1 second delay after every shoot.
	private Timer timer = new Timer();
	//for delaying for shooting.
	private boolean shootDelay = true;
	//2D array of JButtons 
	private static JButton[][] bt = new JButton[row][column];
	private static ClientOnline frame;
	//panel that contains board
	private static JPanel panel;
//	//Defining the images
//	private Image redDown = new ImageIcon(this.getClass().getResource("/img/RedDown.jpg")).getImage();
//	private Image redLeft = new ImageIcon(this.getClass().getResource("/img/RedLeft.jpg")).getImage();
//	private Image redRight = new ImageIcon(this.getClass().getResource("/img/RedRight.jpg")).getImage();
//	private Image redUp = new ImageIcon(this.getClass().getResource("/img/RedUP.jpg")).getImage();
//	private Image blueDown = new ImageIcon(this.getClass().getResource("/img/BlueDown.jpg")).getImage();
//	private Image blueLeft = new ImageIcon(this.getClass().getResource("/img/BlueLeft.jpg")).getImage();
//	private Image blueRight = new ImageIcon(this.getClass().getResource("/img/BlueRight.jpg")).getImage();
//	private Image blueUp = new ImageIcon(this.getClass().getResource("/img/BlueUp.jpg")).getImage();

	private JPanel contentPane;

	//launch the page
//	public static void main(String[] args) throws IOException {
//		ClientOnline client = new ClientOnline(row, column, proportion);
//		client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		client.setVisible(true);
//	}
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SecondThread secondThread = new SecondThread();
					frame = new ClientOnline(row, column, proportion);
					secondThread.start();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static class SecondThread extends Thread{
		//Defining the images
//		private Image redDown = new ImageIcon(this.getClass().getResource("/img/RedDown.jpg")).getImage();
		private Image redLeft = new ImageIcon(this.getClass().getResource("/img/RedLeft.jpg")).getImage();
//		private Image redRight = new ImageIcon(this.getClass().getResource("/img/RedRight.jpg")).getImage();
//		private Image redUp = new ImageIcon(this.getClass().getResource("/img/RedUP.jpg")).getImage();
//		private Image blueDown = new ImageIcon(this.getClass().getResource("/img/BlueDown.jpg")).getImage();
//		private Image blueLeft = new ImageIcon(this.getClass().getResource("/img/BlueLeft.jpg")).getImage();
		private Image blueRight = new ImageIcon(this.getClass().getResource("/img/BlueRight.jpg")).getImage();
//		private Image blueUp = new ImageIcon(this.getClass().getResource("/img/BlueUp.jpg")).getImage();

		@Override
		public void run() {
			while(true) {
				//string that comes from server.
				String get = null;
				try {
					//reading the string
					get = in.readUTF();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//if it starts with NAME, it means client should send its name.
				if(get.startsWith("NAME")) {
					name = nameGetter();
					try {
						//sending the name to server.
						out.writeUTF(name);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//if name was accepted, then server sends UrName+name.
				else if(get.startsWith("UrName")) {
					name = get.substring(6);
					System.out.println(name);
					frame.setTitle(name);
				}
				//if server sends a string that starts with Trenches, then it mean in client the places that server sent should be trench
				else if(get.startsWith("Trenches")) {
					String trenches = get.substring(8);
					rowOfTrenches = new ArrayList<>();
					columnOfTrenches = new ArrayList<>();
					for(int i=0; i<trenches.length(); i+=2) {
						rowOfTrenches.add(Integer.valueOf(trenches.substring(i, i+1)));
					}
					for(int i=1; i<trenches.length(); i+=2) {
						columnOfTrenches.add(Integer.valueOf(trenches.substring(i, i+1)));
					}
					for(int i=0; i<rowOfTrenches.size(); i++) {
						bt[rowOfTrenches.get(i)][columnOfTrenches.get(i)].setBackground(Color.yellow);
					}
				}
				//here server sends the place of players and client saves it in itself.
				else if(get.startsWith("Players")) {
					String player = get.substring(7);
					bt[Integer.valueOf(player.substring(0,1))][Integer.valueOf(player.substring(1,2))].setBackground(Color.white);
					bt[Integer.valueOf(player.substring(0,1))][Integer.valueOf(player.substring(1,2))].setIcon(new ImageIcon(blueRight.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
					bt[Integer.valueOf(player.substring(2,3))][Integer.valueOf(player.substring(3,4))].setBackground(Color.black);
					bt[Integer.valueOf(player.substring(2,3))][Integer.valueOf(player.substring(3,4))].setIcon(new ImageIcon(redLeft.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
				}
				//server sends who should be blue player, and who should be red.
				else if(get.startsWith("Who")) {
					String player= get.substring(3);
					if(player.equalsIgnoreCase("1")) {
f:						for(int i=1; i<row-1; i++) {
							for(int j=1; j<column-1; column++) {
								if(bt[i][j].getBackground().equals(Color.white)) {
									pl.shoot = 2;
									pl.x = i;
									pl.y = j;
									bt[i][j].setText(String.valueOf(pl.lives));
									bt[i][j].setHorizontalTextPosition(JButton.CENTER);
									bt[i][j].setVerticalTextPosition(JButton.CENTER);
									break f;
								}
							}
						}
					}
					if(player.equalsIgnoreCase("2")) {
f:						for(int i=1; i<row-1; i++) {
							for(int j=1; j<column-1; column++) {
								if(bt[i][j].getBackground().equals(Color.black)) {
									pl.shoot = 4;
									pl.x = i;
									pl.y = j;
									bt[i][j].setText(String.valueOf(pl.lives));
									bt[i][j].setHorizontalTextPosition(JButton.CENTER);
									bt[i][j].setVerticalTextPosition(JButton.CENTER);
									break f;
								}
							}
						}
					}
				}
				//if server is busy, client page should be disposed.
				else if(get.startsWith("Busy")) {
					frame.dispose();
					frame.dispose();
					WelcomePage wp = new WelcomePage(10, 10, 6);
					wp.setVisible(true);
				}
			}
		}
		//ask for client name
		private String nameGetter() {
			return JOptionPane.showInputDialog(null, "Your name:", "Name", JOptionPane.PLAIN_MESSAGE);
		}		
	}	
	/**
	 * Create the frame.
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public ClientOnline(int row, int column, int proportion) throws UnknownHostException, IOException {
		//making socket and in and out are for getting and sending data.
		socket = new Socket("localhost", 2020 );
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(300, 100, 600, 475);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(row, column, 0, 0));
		
		//when shoot is clicked, method shoot is called. but you can shoot once a second. 
		JButton btnShoot = new JButton("Shoot");
		btnShoot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//if shoot delay is true, you can shoot.
				if(shootDelay) {
					playSound();
					shoot(pl, row, column);
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
		//when Back is clicked, this page is disposed and WelcomePage is opened.
		JButton btnNewButton = new JButton("Back");
		btnNewButton.setFont(new Font("Stencil", Font.PLAIN, 14));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				frame.dispose();
				WelcomePage wp = new WelcomePage(10, 10, 6);
				wp.setVisible(true);
			}
		});
		contentPane.add(btnNewButton, BorderLayout.SOUTH);
		
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
				}
				panel.add(bt[i][j]);
			}
		}		
	}	
	//sending string to server.
	public void send(String str) {
		try {
			out.writeUTF(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//reads the string sent by server.
	public String read() {
		String str = null;
		try {
			str = (String) in.readUTF();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	//method for shooting. row and column and proportion are defined here to be sent to checkForDeletePlayer method.
	void shoot(Player pl, int row, int column) {
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
						//checking if enemy should be destroyed or not.
						checkForDeletePlayer(en);
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
						checkForDeletePlayer(en);
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
						checkForDeletePlayer(en);
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
						checkForDeletePlayer(en);
						break b;
					}
				}
			}
			break;
		default:
			break;
		}		
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
	//makes the lives of the enemy one less, and delete it if lives is 0.
	void checkForDeletePlayer(Player r) {
		//if enemy could ran away from bullet, then we come out of this method.
		if(r==null)
			return;
		//if enemy's lives were more than zero, lives should be one less.
		if(r.lives>0) {
			r.lives--;
			bt[r.x][r.y].setText(String.valueOf(r.lives));
		}
		//if lives is zero, then enemy should be deleted.
		if(r.lives<1) {
			bt[r.x][r.y].setBackground(Color.LIGHT_GRAY);
			bt[r.x][r.y].setText(null);
			bt[r.x][r.y].setIcon(null);
			r.shoot=0;
			r=null;
		}
	}


}
