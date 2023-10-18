package nahayi;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Dimension;

public class WelcomePage extends JFrame {
	private static int row=10, column=10 , proportion=6;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WelcomePage frame = new WelcomePage(row, column, proportion);
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
	public WelcomePage(int row, int column, int proportion) {
		setTitle("Counter Strike");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(300, 100, 500, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("");
		//adding an image to the welcome page.
		Image im = new ImageIcon(this.getClass().getResource("/img/Conter.jpg")).getImage();
		//closes this page and opens OfflineGame page. 
		JButton btnPlayOffline = new JButton("Play Offline");
		btnPlayOffline.setBackground(Color.BLACK);
		btnPlayOffline.setForeground(Color.WHITE);
		btnPlayOffline.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				OfflineGame offGame = new OfflineGame(row, column, proportion);
				offGame.setVisible(true);
			}
		});
		
		JButton btnBoardSetting = new JButton("Board Setting");
		btnBoardSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				Setting st = new Setting();
				st.setVisible(true);
			}
		});
		
		JButton btnOfflineScore = new JButton("Offline Score");
		btnOfflineScore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					File file = new File("Score.txt");
					if(file.exists()) {
						BufferedReader buf = new BufferedReader(new FileReader(file));
						String l;
						String sc= " ";
						while((l = buf.readLine()) != null) {
							sc+=l+"\n";
						}
						JTextArea txtArea = new JTextArea(sc);
						JScrollPane scrlPane = new JScrollPane(txtArea);
						scrlPane.setPreferredSize(new Dimension(330, 90));
						JOptionPane.showMessageDialog(null, scrlPane, "Score", 1);
						buf.close();
					}
				} catch (Exception e2) {
					 JOptionPane.showMessageDialog(null, e2.getMessage());
				} 
			}
		});
		btnOfflineScore.setForeground(Color.WHITE);
		btnOfflineScore.setBackground(Color.BLACK);
		btnOfflineScore.setBounds(25, 377, 119, 23);
		contentPane.add(btnOfflineScore);
		btnBoardSetting.setForeground(Color.WHITE);
		btnBoardSetting.setBackground(Color.BLACK);
		btnBoardSetting.setBounds(335, 377, 119, 23);
		contentPane.add(btnBoardSetting);
		
		JButton btnClientOnline = new JButton("Client Online");
		btnClientOnline.setForeground(Color.WHITE);
		btnClientOnline.setBackground(Color.BLACK);
		btnClientOnline.setBounds(180, 340, 119, 23);
		contentPane.add(btnClientOnline);
		
		JLabel lblNewLabel_1 = new JLabel("Simple version");
		lblNewLabel_1.setFont(new Font("Urdu Typesetting", Font.PLAIN, 11));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setForeground(Color.WHITE);
		lblNewLabel_1.setBounds(211, 204, 80, 23);
		contentPane.add(lblNewLabel_1);
		btnPlayOffline.setBounds(335, 340, 119, 23);
		contentPane.add(btnPlayOffline);
		
		JButton btnServerOnline = new JButton("Server Online");
		btnServerOnline.setBackground(Color.BLACK);
		btnServerOnline.setForeground(Color.WHITE);
		btnServerOnline.setBounds(25, 340, 119, 23);
		contentPane.add(btnServerOnline);
		
		lblNewLabel.setIcon(new ImageIcon(im.getScaledInstance(484, 411, Image.SCALE_SMOOTH)));
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel.setBounds(0, 0, 484, 411);
		contentPane.add(lblNewLabel);
		
	
	}
}
