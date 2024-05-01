package blackjack;


/*
 * Class is called to authenticate user credentials by comparing client username and password to the text file
 * After a successful login, either a player class object is instantiated or a dealer class object is instantiated based on the text file settings
 * The server either sends a message of successful login or failed login 
 * */


import java.awt.GridBagConstraints;

import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
//Purpose: Request login and determine GUI
public class GUIManager {
	Client client;
	String address;
	Socket clientSocket;
	ObjectOutputStream objectOutputStream;
	ObjectInputStream objectInputStream;

	private JTextField usernameField;
	private JTextField passwordField;
	
	public GUIManager(Client client, String address, Socket clientSocket, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
		this.client = client;
		this.address = address;
		this.clientSocket = clientSocket;
		this.objectOutputStream = objectOutputStream;
		this.objectInputStream = objectInputStream;

	}
	
	public void getLogin() throws UnknownHostException, IOException, ClassNotFoundException {
			loginGUI();
	}
	
	public void loginGUI() {
		JFrame frame = new JFrame("Login");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(5,5,5,5);
		
		JLabel usernameLabel = new JLabel("Username: ");
		usernameField = new JTextField(20);
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		panel.add(usernameLabel, constraints);
		
		constraints.gridx = 1;
		panel.add(usernameField, constraints);
		
		JLabel passwordLabel = new JLabel("Password: ");
		passwordField = new JTextField(20);
		constraints.gridx = 0;
		constraints.gridy = 1;
		panel.add(passwordLabel, constraints);
		
		constraints.gridx = 1;
		panel.add(passwordField, constraints);
		
		
		
		JButton submitButton = new JButton("Submit");
		constraints.gridx = 1;
		constraints.gridy = 2;
		
		panel.add(submitButton, constraints);
		
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendLogin(frame);
				frame.dispose();
			}
		});
		
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
}


	public void sendLogin(JFrame frame) {
		try {
			String username = usernameField.getText();
			String password = passwordField.getText();
			
			ClientMessage loginMessage = new ClientMessage();
			loginMessage.setUsername(username);
			loginMessage.setPassword(password);
			loginMessage.setMessageType(MessageType.LOGIN);
			
			objectOutputStream.writeObject(loginMessage);
			
			Response serverResponse = (Response) objectInputStream.readObject();
			
			if(serverResponse.getType() == ResponseType.LOGIN_SUCCESS && serverResponse.getIsDealer() == false) {
				frame.dispose();

				username = serverResponse.getUsername();
				int playerID = serverResponse.getPlayerID();
				int bankRoll = serverResponse.getBankroll();
				int winAmount = serverResponse.getWinAmount();
				int lossAmount = serverResponse.getLossAmount();
				//Use to break out of loop
				
				Player player = new Player(username, playerID, bankRoll, winAmount, lossAmount, client, objectInputStream);
				
			}else if(serverResponse.getType() == ResponseType.LOGIN_SUCCESS && serverResponse.getIsDealer() == true) {
				frame.dispose();
				
				Dealer dealer = new Dealer(client, serverResponse.getPlayerID(), serverResponse.getIsDealer(), objectInputStream);
				
			}else if(serverResponse.getType() == ResponseType.LOGIN_FAIL){
				JOptionPane.showMessageDialog(frame, "LOGIN FAILED");
				frame.dispose();
				loginGUI();
			}
			
			
			
		}catch(Exception ex) {
			loginGUI();
		}
	}
	


}


