package nahayi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JOptionPane;


public class ServerOnline {
	//names of players will be saved here.
	private static ArrayList<String> names = new ArrayList<>();
	//dataOutPutStream for client
	private static ArrayList<DataOutputStream> outs = new ArrayList<>();
	//row and column and proportion of trenches.
	private static int row=10, column=10, proportion=6;
	
	public static void main(String[] args) throws IOException {
		System.out.println("Server strated");
		ServerSocket serversocket = new ServerSocket(2020);
		try {
			//waiting for clients to connect
			while (true) {
				Socket socket = serversocket.accept();
				Handler hndlr = new Handler(socket);
				hndlr.start();
			}
		} finally {
			try {
				serversocket.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Coulden't close the server", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private static class Handler extends Thread {
		private Socket socket;
		//name of player(client)
		private String name;
		//for reading and writing
		private DataInputStream in;
		private DataOutputStream out;
		
		public Handler(Socket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() {
			try {
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				//Getting client's name. if it was repeated, server asks for another name.
				while (true) {
					//asking the server to send its name.
					out.writeUTF("NAME");
					//reading the name server sent.
					name = in.readUTF();
					//if its not empty and not repeated and clients are at last 2, then name is accepted.
					if (!names.contains(name) && name!="" && outs.size()<2) {
						names.add(name);
						outs.add(out);
						//writing to client that your name is accepted.
						out.writeUTF("UrName"+name);
						//if clients are 2, then game is started.
						if(outs.size()==2) {
							sendtoall("Trenches"+placeOfTrenches());
							sendtoall("Players"+placeOfPlayers());
							whoISWho();
						}
						break;
					}
					//if clients are 2, then server is busy.
					else {
						out.writeUTF("Busy");
					}
				}
//				while(true) {
//					if(in.readUTF().startsWith("PlaceMe")) {
//						
//					}
//					try {
//						Thread.sleep(50);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(e.toString());
			}
		}
		public void sendtoall(String s) {
			for(int i=0; i<outs.size(); i++) {
				try {
					outs.get(i).writeUTF(s);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		public void whoISWho() {
			int c = 1;
			for(int i=0; i<outs.size(); i++) {
				try {
					outs.get(i).writeUTF("Who"+String.valueOf(c));
				} catch (IOException e) {
					e.printStackTrace();
				}
				c++;
			}
		}
		public String placeOfTrenches() {
			float ftrenches = ((float)(10-proportion)/10)*(row-1)*(column-1);
			int trenches = (int) ftrenches;
			//saves the row and column of trenches in it like: 1257  xyxy
			String xandyOfTrenches="";
			for(int i=0; i<trenches; i++) {
				int x = randomNumber(1, row-2);
				xandyOfTrenches += String.valueOf(x);
				int y = randomNumber(1, column-2);
				xandyOfTrenches += String.valueOf(y);
			}
			return xandyOfTrenches;
		}
		//specifying the place of players.
		public String placeOfPlayers() {
			//saves the places of players like: rowcolumnrowcolumn
			String placeOfPlayers = "";
			int rowPlayer1 = randomNumber(1, row-2);
			int columnPlayer1 = randomNumber(1, column-2);
			int rowPlayer2 = randomNumber(1, row-2);
			placeOfPlayers += String.valueOf(rowPlayer1)+String.valueOf(columnPlayer1)+String.valueOf(rowPlayer2);
			int columnPlayer2 = randomNumber(1, column-2);
			//this loop checks that playesr don't place on each other.
			while(true) {
				if(columnPlayer2==columnPlayer1) {
					columnPlayer2 = randomNumber(1, column-2);
				}
				else {
					break;
				}
			}
			placeOfPlayers += columnPlayer2;
			return placeOfPlayers;
		}
		//makes a random number like: minimum <= ran <= maximum .
		int randomNumber(int min, int max) {
			Random r = new Random();
			return r.nextInt( (max-min)+1 ) + min;
		}
	}
}
