import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.swing.BoxLayout;
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
	private int betAmount = 0;
	private int currPlayer = 0;
	private int actionNum = 0;
	
	private JFrame frame;
    private JTextArea gameInfoArea; // Displays dealer and player hands
    private JButton hitButton;
    private JButton standButton;
    private JButton doubleDownButton;
    private JButton depositButton;
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
			frame = new JFrame("BlackJack Player" + playerID);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(800,450);
			
			gameInfoArea = new JTextArea();
			gameInfoArea.setEditable(false);
			Font customFont = new Font("Arial", Font.BOLD, 20);
			gameInfoArea.setFont(customFont);
			JScrollPane scrollPane = new JScrollPane(gameInfoArea);
			
			
			
			String info = "Your ID: " + playerID + "\n" +
						  "Your BankRoll: " + bankRoll + "\n" +
						  "Your Bet: " + betAmount;
			
			gameInfoArea.setText(info);
			
			JPanel buttons = new JPanel();
			buttons.setLayout(new FlowLayout());
			hitButton = new JButton("Hit");
			standButton = new JButton("Stand");
			doubleDownButton = new JButton("Double Down");
			depositButton = new JButton("Deposit");
			buttons.add(hitButton);
			buttons.add(standButton);
			buttons.add(doubleDownButton);
			buttons.add(depositButton);
			
			dealerPanel = new JPanel(new FlowLayout());
			dealerLabel = new JLabel("Dealer");
			dealerPanel.add(dealerLabel);
			frame.setLayout(new BorderLayout());
			frame.add(scrollPane,BorderLayout.NORTH);
			
			playersPanel = new JPanel(new GridLayout(1,3));
			for(int i = 1; i <= 10; i++) {
				JPanel playerSection = new JPanel(new FlowLayout());
				JLabel playerLabel = new JLabel("Player " + i);
				playerSection.add(playerLabel);
				playersPanel.add(playerSection);
			}
			
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
			mainPanel.add(dealerPanel);
			mainPanel.add(playersPanel);
			
			frame.add(mainPanel, BorderLayout.CENTER);
			frame.add(buttons, BorderLayout.SOUTH);
			
			hitButton.setEnabled(false);
            standButton.setEnabled(false);
            doubleDownButton.setEnabled(false);
			depositButton.setEnabled(true);
			
			frame.setVisible(true);
			
			
			hitButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						hit();
						doubleDownButton.setEnabled(false);
					} catch (IOException e1) {
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
						e1.printStackTrace();
					}
				}
			});
			
			depositButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					depositGUI();
				}
			});
		
			
			
			
		}
			
	public void listenToServer() throws ClassNotFoundException, IOException {
		new Thread(new Runnable() {
			public void run() {
				try {
					while(true) {
						Response fromServer = (Response) objectInputStream.readObject();
						processServerResponse(fromServer);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	
	public void processServerResponse(Response fromServer) throws InterruptedException {

		switch (fromServer.getType()) {
	        case ALL_BETS:
	            betGUI(); 
	            break;
	        
	        case PLAYER_TURN:
	            hitButton.setEnabled(true);
	            standButton.setEnabled(true);
	            depositButton.setEnabled(false);
	            if(actionNum == 0) {
	            	doubleDownButton.setEnabled(true);
	            }else {
	            	doubleDownButton.setEnabled(false);
	            }
	            
	            actionNum++;
	            break;
	        
	        case PLAYER_TURN_END:
	        	hitButton.setEnabled(false);
	            standButton.setEnabled(false);
	            doubleDownButton.setEnabled(false);
	            depositButton.setEnabled(true);
	            actionNum = 0;
	            break;
	        
	        case UPDATE:
	            updateGUI(fromServer); 
	            break;
	        
	        case UPDATE_BANKROLL:
	        	System.out.println("UPDATING BANKROLL");
	        	updateBankRoll(fromServer);
	        	break;
	        
	        default:
	            System.out.println("Unknown response type: " + fromServer.getType());
	            break;
	    }
	}

	public void updateGUI(Response fromServer) throws InterruptedException {
		if(fromServer.getInitialDraw() == true) {
			for(int i = 0; i < allGamePlayers.size(); i++) {
				PlayerData clear = allGamePlayers.get(i);
				clear.setHandWithAce(0);
			}
		}

	    
	    PlayerData data = new PlayerData();
	    
	    //If first player to add add player
	    //Code Block to add players
	    if(allGamePlayers.size() == 0) {
	    	data.setPlayerID(fromServer.getPlayerID());
		    data.setHandValue(fromServer.getHandValue());
		    data.setHandString(fromServer.getHandString());
		    if(fromServer.getHandWithAce() != 0) {
		    	data.setHandWithAce(fromServer.getHandWithAce());
		    }
		    allGamePlayers.add(data);
	    }else { //If not - check if player already exists else add the player to the game
	    	for(int i = 0; i < allGamePlayers.size(); i++) {
	    		PlayerData checkPlayers = allGamePlayers.get(i);
	    		if(checkPlayers.getPlayerID() == fromServer.getPlayerID()) {
	    			checkPlayers.setHandValue(fromServer.getHandValue());
	    			checkPlayers.setHandString(fromServer.getHandString());
	    			checkPlayers.setBetAmount(fromServer.getBetAmount());
	    			checkPlayers.setBankRoll(fromServer.getBankroll());
	    			if(fromServer.getHandWithAce() != 0) {
	    				checkPlayers.setHandWithAce(fromServer.getHandWithAce());
	    			}
	    			break;
	    		}else if (i == allGamePlayers.size() - 1) {
	    			data.setPlayerID(fromServer.getPlayerID());
	    			data.setHandValue(fromServer.getHandValue());
	    			data.setHandString(fromServer.getHandString());
	    			data.setBetAmount(fromServer.getBetAmount());
	    			data.setBankRoll(fromServer.getBankroll());
	    			if(fromServer.getHandWithAce() != 0) {
	    				data.setHandWithAce(fromServer.getHandWithAce());
	    			}
	    			allGamePlayers.add(data);
	    		}
	    	}
	    }
	    
	    
	    playersPanel.removeAll(); // Clear existing content
	    dealerPanel.removeAll();
	    
	    //Updating panels
	    for (int i = 0; i < allGamePlayers.size(); i++) {
	        PlayerData player = allGamePlayers.get(i);
	        if(player.getPlayerID() == 0) {
	        	JTextArea dealerInfoArea = new JTextArea();
	        	dealerInfoArea.setEditable(false);
	        	dealerInfoArea.setOpaque(false);
	        	System.out.println(player.getHandWithAce());
	        	String dealerInfo = "Dealer\n" +
	        						"Hand Value: " + player.getHandValue() + "\n";
	        						if(player.getHandWithAce()!= 0 && fromServer.getInitialDraw() == false) {
	        							dealerInfo += "Ace Hand: " + player.getHandWithAce() + "\n";
	        						}
	        						
	        			dealerInfo += "Cards: " + player.getHandString();
	        	Font customFont = new Font("Arial", Font.BOLD, 14);
	        	dealerInfoArea.setFont(customFont);
	        	dealerInfoArea.setText(dealerInfo);
	        	dealerPanel.add(dealerInfoArea);
	        }else {
		        JPanel playerSection = new JPanel(new FlowLayout());
	
		        JTextArea playerInfoArea = new JTextArea();
		        playerInfoArea.setEditable(false); 
		        playerInfoArea.setOpaque(false);
		        String playerInfo = "Player ID: " + player.getPlayerID() + "\n" +
		                            "Hand Value: " + player.getHandValue() + "\n";
		                            if(player.getHandWithAce() != 0 && player.getHandValue() <= player.getHandWithAce()) {
		                            	playerInfo += "Ace Hand: " + player.getHandWithAce() + "\n";
		                            }
		                            
		                            
		               playerInfo +="Cards: " + player.getHandString() + "\n" +
		                            "Bet Amount: " + player.getBetAmount() + "\n";
		        
		        if(playerID == player.getPlayerID()) {
		        	bankRoll = player.getBankRoll();
		        	betAmount = player.getBetAmount();
		        	gameInfoArea.setText("Your ID: " + playerID + "\n" + 
		        						 "Your BankRoll: " + bankRoll + "\n" +
		        						 "Your Bet: " + betAmount);
		        	
		        }
		        Font customFont = new Font("Arial", Font.PLAIN, 14);
				playerInfoArea.setFont(customFont);
	
		        playerInfoArea.setText(playerInfo);
		        playerSection.add(playerInfoArea);
		        playersPanel.add(playerSection); 
	        }
	    }

	    playersPanel.revalidate();
	    playersPanel.repaint();
	    dealerPanel.revalidate();
	    dealerPanel.repaint();
	    
	    
	}
	
	public void depositGUI() {
		JFrame depositFrame = new JFrame("Deposit amount");
		depositFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		depositFrame.setSize(300,150);
		JPanel depositPanel = new JPanel(new FlowLayout());
		JLabel depositLabel = new JLabel("Enter amount to deposit: ");
		JTextField depositField = new JTextField(10);
		JButton submitButton = new JButton("Confirm Deposit");
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int depositAmount = Integer.parseInt(depositField.getText());
					if(depositAmount > 1000 || bankRoll > 10000) {
						JOptionPane.showMessageDialog(depositFrame, "Deposit Failed", "Invalid Deposit", JOptionPane.ERROR_MESSAGE);
						depositGUI();
					}else {
						deposit(depositAmount);
					}
					depositFrame.dispose();
				}catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(depositFrame, "Please enter a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                } catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
			}
		});
		
		depositPanel.add(depositLabel);
		depositPanel.add(depositField);
		depositPanel.add(submitButton);
		depositFrame.add(depositPanel);
		int mainX = frame.getX();
		int mainY = frame.getY();
		int mainWidth = frame.getWidth();
		int mainHeight = frame.getHeight();
		
		int betFrameX = mainX + (mainWidth - depositFrame.getWidth()) /2;
		int betFrameY = mainY + (mainHeight - depositFrame.getHeight()) /2;
		
		depositFrame.setLocation(betFrameX,betFrameY);
		
		
		depositFrame.setVisible(true);
		
		
		
	}
	
	public void betGUI() {
		JFrame betFrame = new JFrame("Place your bet");
		betFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		betFrame.setSize(300,150);
		
		JPanel betPanel = new JPanel(new FlowLayout());
		JLabel betLabel = new JLabel("Enter your bet: ");
		JTextField betField = new JTextField(10);
		JButton submitButton = new JButton("Confirm Bet");
		
		submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    betAmount = Integer.parseInt(betField.getText());
                    // Send the bet amount, then close the bet window
                    if(betAmount > bankRoll) {
                    	JOptionPane.showMessageDialog(betFrame, "You do not have enough money", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    	betGUI();
                    }else{
                    	placeBet(betAmount);
                    }
                    
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
	
	public void updateBankRoll(Response fromServer) {
		bankRoll = fromServer.getBankroll();
		gameInfoArea.setText("Your ID: " + playerID + "\n" + 
				 			 "Your BankRoll: " + bankRoll + "\n" +
				 			 "Your Bet: " + betAmount);
		
		gameInfoArea.revalidate();
		gameInfoArea.repaint();
	}
	
	public void deposit(int depositAmount) throws IOException {
		client.sendDepositRequest(playerID, depositAmount);
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
		client.sendDoubleDownRequest(playerID, betAmount);
	}
	
	public void debug() throws IOException{
		client.debug();
	}
}
