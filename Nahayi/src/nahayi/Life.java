package nahayi;

import java.awt.Color;
import java.awt.Image;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Life extends Thread {
	private JButton[][] bt;
	private int row, column;
	//the image of heart.
	private Image heart = new ImageIcon(this.getClass().getResource("/img/heart.jpg")).getImage();
	//random x in board
	private int xRandom;
	//random y in the board
	private int yRandom;
	//is used in while(b) in run method.
	private boolean b;
	//object of random class. used to make a random number.
	private Random r = new Random();
	//Constructor
	public Life(JButton[][] bt, int row, int column) {
		this.bt = bt;
		this.row = row;
		this.column = column;
	}


	@Override
	public void run() {
		while(true) {
			b = true;
			//every 20 seconds a life should be made.
			try {
				sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			xRandom =  r.nextInt( ((row-1)-1)+1 ) + 1;
			yRandom = r.nextInt( ((column-1)-1)+1 ) + 1;
			while(b) {
				//if the button wasn't empty, then we try another random button
				if(!(bt[xRandom][yRandom].getBackground().equals(Color.LIGHT_GRAY))) {
					xRandom =  r.nextInt( ((row-1)-1)+1 ) + 1;
					yRandom = r.nextInt( ((column-1)-1)+1 ) + 1;
				}
				//if button was empty, then life is place there and we this while(b) should break by making b = false.
				if(bt[xRandom][yRandom].getBackground().equals(Color.LIGHT_GRAY)) {
					bt[xRandom][yRandom].setBackground(Color.red);
					bt[xRandom][yRandom].setIcon(new ImageIcon(heart.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
					b = false;
				}
			}
		}
	}
}
