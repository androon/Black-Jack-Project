import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Player {
	private String username;
	private int bankRoll;
	private int playerID;
	private int numWin;
	private int numLoss;
	private Client client;
	ObjectInputStream objectInputStream;
	
	
	private JFrame frame;
    private JTextArea gameInfoArea; // Displays dealer and player hands
    private JButton hitButton;
    private JButton standButton;
    private JButton doubleDownButton;
    private JPanel playersPanel;
    private JPanel dealerPanel;
    private JLabel dealerLabel;
    
    private List<PlayerData> allGamePlayers = new LinkedList<>();
    
	public Player(String username, int playerID, int bankRoll, int numWin, int numLoss, Client client, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
		this.username = username;
		this.playerID = playerID;
		this.numWin = numWin;
		this.numLoss = numLoss;
		this.client = client;
		this.bankRoll = bankRoll;
		this.objectInputStream = objectInputStream;
		displayGUI();
		listenToServer();
	}
	
	private void displayGUI() throws IOException, ClassNotFoundException {
			frame = new JFrame("BlackJack");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(800,600);
			
			gameInfoArea = new JTextArea();
			gameInfoArea.setEditable(false);
			JScrollPane scrollPane = new JScrollPane(gameInfoArea);
			
			JPanel buttons = new JPanel();
			buttons.setLayout(new FlowLayout());
		
			hitButton = new JButton("Hit");
			standButton = new JButton("Stand");
			doubleDownButton = new JButton("Double Down");
			
			buttons.add(hitButton);
			buttons.add(standButton);
			buttons.add(doubleDownButton);
			
			dealerPanel = new JPanel(new FlowLayout());
			dealerLabel = new JLabel("Dealer");
			dealerPanel.add(dealerLabel);
			
			hitButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						hit();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			
			
			standButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						stand();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			
			
			doubleDownButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						doubleDown();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
		
			
			frame.setLayout(new BorderLayout());
			frame.add(scrollPane,BorderLayout.CENTER);
			//frame.add(buttons, BorderLayout.SOUTH);
			
			playersPanel = new JPanel(new GridLayout(1,3));
			for(int i = 1; i <= 10; i++) {
				JPanel playerSection = new JPanel(new FlowLayout());
				JLabel playerLabel = new JLabel("Player " + i);
				playerSection.add(playerLabel);
				playersPanel.add(playerSection);
			}
			
			JPanel southPanel = new JPanel(new BorderLayout());
			
			dealerPanel.setPreferredSize(new Dimension(800,250));
			
			southPanel.setPreferredSize(new Dimension(800,250));
			
			southPanel.add(playersPanel, BorderLayout.NORTH);
			southPanel.add(buttons, BorderLayout.SOUTH);
			
			frame.add(dealerPanel, BorderLayout.NORTH);
			frame.add(southPanel, BorderLayout.SOUTH);
			enableButtons(false);
			
			frame.setVisible(true);
			
		}
			
	public void listenToServer() throws ClassNotFoundException, IOException {
		new Thread(new Runnable() {
			public void run() {
				try {
					while(true) {
						Response fromServer = (Response) objectInputStream.readObject();
						System.out.println(fromServer.getType());
						processServerResponse(fromServer);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void enableButtons(boolean enabled) {
		hitButton.setEnabled(enabled);
		standButton.setEnabled(enabled);
		doubleDownButton.setEnabled(enabled);
	}
	
	public void processServerResponse(Response fromServer) throws InterruptedException {
	    // Process different response types
	    switch (fromServer.getType()) {
	        case ALL_BETS:
	            System.out.println("Requesting for bet");
	            betGUI(); // Handle bet GUI
	            break;
	        case PLAYER_TURN:
	            enableButtons(true); // Enable player buttons
	            break;
	        case PLAYER_TURN_END:
	            enableButtons(false); // Disable player buttons
	            break;
	        case UPDATE:
	            updateGUI(fromServer); // Update GUI with new data
	            break;
	        default:
	            System.out.println("Unknown response type: " + fromServer.getType());
	            break;
	    }
	}

	public void updateGUI(Response fromServer) throws InterruptedException {
	

	    System.out.println("Processing server response");
	    
	    // Check the data in the response
	    System.out.println("Player ID: " + fromServer.getPlayerID());
	    System.out.println("Players Hand Value: " + fromServer.getHandValue());
	    System.out.println("Players individual cards: " + fromServer.getHandString());
	    PlayerData data = new PlayerData();
	    playersPanel.removeAll(); 
	    
	    
	    //If first player to add add player
	    if(allGamePlayers.size() == 0) {
	    	data.setPlayerID(fromServer.getPlayerID());
		    data.setHandValue(fromServer.getHandValue());
		    data.setHandString(fromServer.getHandString());
		    allGamePlayers.add(data);
	    }else { //If not - check if player already exists else add the player to the game
	    	for(int i = 0; i < allGamePlayers.size(); i++) {
	    		PlayerData checkPlayers = allGamePlayers.get(i);
	    		if(checkPlayers.getPlayerID() == fromServer.getPlayerID()) {
	    			checkPlayers.setHandValue(fromServer.getHandValue());
	    			checkPlayers.setHandString(fromServer.getHandString());
	    			break;
	    		}else if (i == allGamePlayers.size() - 1) {
	    			data.setPlayerID(fromServer.getPlayerID());
	    			data.setHandValue(fromServer.getHandValue());
	    			data.setHandString(fromServer.getHandString());
	    			allGamePlayers.add(data);
	    		}
	    	}
	    }
	    
	    
	    playersPanel.removeAll(); // Clear existing content
	    dealerPanel.removeAll();
	    JPanel dealerPanel = new JPanel(new FlowLayout());
	    for (int i = 0; i < allGamePlayers.size(); i++) {
	        PlayerData player = allGamePlayers.get(i);
	        if(player.getPlayerID() == 0) {
	        	JTextArea dealerInfoArea = new JTextArea();
	        	dealerInfoArea.setEditable(false);
	        	
	        	String dealerInfo = "Dealer\n" +
	        						"Hand Value: " + player.getHandValue() + "\n"+
	        						"Cards: " + player.getHandString();
	        	dealerInfoArea.setText(dealerInfo);
	        	dealerPanel.add(dealerInfoArea);
	        }else {
		        JPanel playerSection = new JPanel(new FlowLayout());
	
		        JTextArea playerInfoArea = new JTextArea();
		        playerInfoArea.setEditable(false); 
		        
		        String playerInfo = "Player ID: " + player.getPlayerID() + "\n" +
		                            "Hand Value: " + player.getHandValue() + "\n" +
		                            "Cards: " + player.getHandString();
	
		        playerInfoArea.setText(playerInfo);
		        playerSection.add(playerInfoArea);
		        playersPanel.add(playerSection); 
	        }
	    }

	    
	    dealerPanel.setPreferredSize(new Dimension(800, 250));
	    playersPanel.revalidate();
	    playersPanel.repaint();
	    dealerPanel.revalidate();
	    dealerPanel.repaint();
	    

	    frame.getContentPane().remove(dealerPanel); 
	    frame.getContentPane().add(dealerPanel, BorderLayout.NORTH); 
	    
	}
	
	
	public void betGUI() {
		System.out.println("INSIDE BET GUI");
		JFrame betFrame = new JFrame("Place your bet");
		betFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		betFrame.setSize(300,300);
		
		JPanel betPanel = new JPanel(new FlowLayout());
		JLabel betLabel = new JLabel("Enter your bet: ");
		JTextField betField = new JTextField(10);
		JButton submitButton = new JButton("Confirm Bet");
		
		submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int betAmount = Integer.parseInt(betField.getText());
                    // Send the bet amount, then close the bet window
                    placeBet(betAmount);
                    betFrame.dispose(); // Close the bet window
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(betFrame, "Please enter a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
		
		betPanel.add(betLabel);
		betPanel.add(betField);
		betPanel.add(submitButton);
		
		betFrame.add(betPanel);
		
		
		//Centering the bet window to main window
		int mainX = frame.getX();
		int mainY = frame.getY();
		int mainWidth = frame.getWidth();
		int mainHeight = frame.getHeight();
		
		int betFrameX = mainX + (mainWidth - betFrame.getWidth()) /2;
		int betFrameY = mainY + (mainHeight - betFrame.getHeight()) /2;
		
		betFrame.setLocation(betFrameX,betFrameY);
		
		
		betFrame.setVisible(true);
		
		
		
	}
	
	
	public void placeBet(int betAmount) throws IOException {
		client.sendBetRequest(playerID, betAmount);
	}
	
	public void hit() throws IOException{
		client.sendHitRequest(playerID);
	}
	
	public void stand() throws IOException{
		client.sendStandRequest(playerID);
	}
	
	public void doubleDown() throws IOException{
		client.sendDoubleDownRequest(playerID);
	}
	
	public void debug() throws IOException{
		client.debug();
	}
}
