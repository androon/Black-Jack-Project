import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
		//If message recieved = player turn - enable buttons
		
		/*Scanner myScanner = new Scanner(System.in);
		while(true) {
			
			
			Response fromServer = (Response) objectInputStream.readObject();
			if(fromServer.getType() == ResponseType.ALL_BETS) {
				placeBet();
			}
			if(fromServer.getType() == ResponseType.PLAYER_TURN) {
				
				System.out.println("1. hit");
				System.out.println("2. stand");
				System.out.println("3. double down");
				int choice = myScanner.nextInt();
				switch(choice) {
					case 1:
						hit();
						break;
						
					case 2:
						stand();
						break;
							
					case 3: 
						doubleDown();
						break;
						
					case 9:
						debug();
						break;
						
					}
				}else if(fromServer.getType() == ResponseType.PLAYER_TURN_END) {
					
				}
			}
			*/
			frame = new JFrame("BlackJack");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(800,800);
			
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
			
			/*hitButton.addActionListener(e -> {
	            try {
	                hit();
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        });

	        standButton.addActionListener(e -> {
	            try {
	                stand();
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        });

	        doubleDownButton.addActionListener(e -> {
	            try {
	                doubleDown();
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        });*/
	    
			
			frame.setLayout(new BorderLayout());
			frame.add(scrollPane,BorderLayout.CENTER);
			frame.add(buttons, BorderLayout.SOUTH);
			enableButtons(false);
			
			frame.setVisible(true);
			
			//waitForServer();
			
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
	
	public void processServerResponse(Response fromServer) {
		if(fromServer.getType() == ResponseType.ALL_BETS) {
			System.out.println("Requesting for bet");
			betGUI();
		}else if(fromServer.getType() == ResponseType.PLAYER_TURN) {
			enableButtons(true);
		}else if(fromServer.getType() == ResponseType.PLAYER_TURN_END) {
			enableButtons(false);
		}
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
		
		
		//Centering the bet window
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
