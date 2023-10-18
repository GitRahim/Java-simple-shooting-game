package nahayi;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class Setting extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtRows;
	private JTextField txtColumns;
	private JTextField txtProportion;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Setting frame = new Setting();
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
	public Setting() {
		setTitle("Setting");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(300, 100, 500, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("");
		//adding an image to the welcome page.
		Image im = new ImageIcon(this.getClass().getResource("/img/Conter.jpg")).getImage();
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int row = Integer.valueOf(txtRows.getText());
					int column = Integer.valueOf(txtColumns.getText());
					int proportion = Integer.valueOf(txtProportion.getText());
					if(row>=5 && row<=20 && column>=5 && column<=20 && proportion<=4 && proportion>=0) {
						dispose();
						WelcomePage wp = new WelcomePage(row, column, (10-proportion));
						wp.setVisible(true);
					}
					else {
						JOptionPane.showMessageDialog(null, "Wrong input");
					}
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "Wrong input");
				}
			}
		});
		btnSave.setForeground(Color.WHITE);
		btnSave.setBackground(Color.BLACK);
		btnSave.setBounds(182, 336, 119, 23);
		contentPane.add(btnSave);
		
		txtProportion = new JTextField();
		txtProportion.setHorizontalAlignment(SwingConstants.CENTER);
		txtProportion.setText("4");
		txtProportion.setColumns(10);
		txtProportion.setBounds(333, 289, 40, 20);
		contentPane.add(txtProportion);
		
		txtColumns = new JTextField();
		txtColumns.setHorizontalAlignment(SwingConstants.CENTER);
		txtColumns.setText("10");
		txtColumns.setColumns(10);
		txtColumns.setBounds(333, 254, 40, 20);
		contentPane.add(txtColumns);
		
		txtRows = new JTextField();
		txtRows.setHorizontalAlignment(SwingConstants.CENTER);
		txtRows.setText("10");
		txtRows.setBounds(333, 219, 40, 20);
		contentPane.add(txtRows);
		txtRows.setColumns(10);
		
		JLabel lblProportionOfTrenches = new JLabel("Proportion of trenches (0 - 4)");
		lblProportionOfTrenches.setForeground(Color.WHITE);
		lblProportionOfTrenches.setBounds(98, 292, 172, 14);
		contentPane.add(lblProportionOfTrenches);
		
		JLabel lblColumnsOfThe = new JLabel("Columns of the board (5 - 20)");
		lblColumnsOfThe.setForeground(Color.WHITE);
		lblColumnsOfThe.setBounds(98, 257, 172, 14);
		contentPane.add(lblColumnsOfThe);
		
		JLabel lblNewLabel_1 = new JLabel("Rows of the board (5 - 20)");
		lblNewLabel_1.setForeground(Color.WHITE);
		lblNewLabel_1.setBounds(98, 222, 172, 14);
		contentPane.add(lblNewLabel_1);
		lblNewLabel.setIcon(new ImageIcon(im.getScaledInstance(484, 411, Image.SCALE_SMOOTH)));
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel.setBounds(0, 0, 484, 411);
		contentPane.add(lblNewLabel);
		
	}
}
