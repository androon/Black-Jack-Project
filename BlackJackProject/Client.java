package blackjack;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class Client {
	private GUIManager gui;
	Socket clientSocket;
	OutputStream outputStream;
	ObjectOutputStream objectOutputStream;
	ObjectInputStream objectInputStream;
	private String address;
	Client client;
	
	
	/*
	 * Client Class has the blueprint of what a single instance of Client object has
	 *  All Clients, dealer or player, have actions that they can perform in their respective GUI
	 *  The actions send bet request, send hit request, and others can be called upon by clients
	 *  All Clients utilize Socket Class to create a socket to connect to the Server Socket
	 *  All Clients also have instances of objectinputstream and objectoutputstream to read and write messages to and from server 
	 * */
	
	public static void main(String[]  args) throws  IOException, ClassNotFoundException 
	{
		Client client = new Client();
		client.setupClient();
	}
	
	public void setupClient() throws ClassNotFoundException, UnknownHostException, IOException{
				
				JFrame frame = new JFrame("GUI");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				JPanel panel = new JPanel();
				LayoutManager layout = new FlowLayout();
				panel.setLayout(layout);
				
				// The Client inputs the Server Address to Connect to the Server
				JLabel label = new JLabel("Enter server address");
				JTextField textField = new JTextField(20);
				
				JButton submitButton = new JButton("Submit");
		        // Add the ActionListener to the button
		        submitButton.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e) {
		                address = textField.getText();
		                System.out.println(address);
		                try {
		                	clientSocket = new Socket(address, 777);
		    				objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
		    				objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
		    				gui = new GUIManager(Client.this, address, clientSocket, objectOutputStream, objectInputStream);
		    				
		    				
		    				frame.dispose();
		    				gui.getLogin();
		    				
		    				

		                }catch (IOException ex) {
		                	
		                } catch (ClassNotFoundException e1) {
							e1.printStackTrace();
						}
		            }
		        });
				
				panel.add(label);
				panel.add(textField);
				panel.add(submitButton);
				
				frame.add(panel);
				
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
				

				
	}
	
	// All available actions that clients can perform
	public void sendBetRequest(int playerID, int betAmount) throws IOException {
		ClientMessage betMessage = new ClientMessage();
		betMessage.setPlayerID(playerID);
		betMessage.setBetAmount(betAmount);
		betMessage.setMessageType(MessageType.BET);
		objectOutputStream.writeObject(betMessage);
	}
	
	public void sendHitRequest(int playerID) throws IOException {
		ClientMessage hitMessage = new ClientMessage();
		hitMessage.setPlayerID(playerID);
		hitMessage.setMessageType(MessageType.HIT);
		objectOutputStream.writeObject(hitMessage);
	}
	
	public void sendStandRequest(int playerID) throws IOException{
		ClientMessage standMessage = new ClientMessage();
		standMessage.setPlayerID(playerID);
		standMessage.setMessageType(MessageType.STAND);
		objectOutputStream.writeObject(standMessage);
	}
	
	public void sendDoubleDownRequest(int playerID) throws IOException{
		ClientMessage doubledownMessage = new ClientMessage();
		doubledownMessage.setPlayerID(playerID);
		doubledownMessage.setMessageType(MessageType.DOUBLE_DOWN);
		objectOutputStream.writeObject(doubledownMessage);
	}
	
	public void sendStartRoundRequest() throws IOException {
		ClientMessage startRoundMessage = new ClientMessage();
		startRoundMessage.setMessageType(MessageType.START_ROUND);
		objectOutputStream.writeObject(startRoundMessage);
	}
	
	public void sendEndRoundRequest() throws IOException{
		ClientMessage endRoundMessage = new ClientMessage();
		endRoundMessage.setMessageType(MessageType.END_ROUND);
		objectOutputStream.writeObject(endRoundMessage);
	}
	
	
	public void debug() throws IOException{
		ClientMessage debug = new ClientMessage();
		debug.setMessageType(MessageType.DEBUG);
		objectOutputStream.writeObject(debug);
	}
	
}


