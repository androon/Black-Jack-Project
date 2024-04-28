import java.awt.BorderLayout;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class Dealer {

	private int playerID;
    private Client client;
    private boolean isDealer;
    ObjectInputStream objectInputStream;
    private boolean roundStart = false;
    private JFrame frame;
    private JTextArea gameInfoArea; // Displays dealer and player hands
    private JButton hitButton;
    private JButton startButton;
    private JButton endButton;
	private JPanel dealerPanel;
    private JLabel dealerLabel;
    private JPanel playersPanel;
    
    private List<PlayerData> allGamePlayers = new LinkedList<>();
    
    public Dealer(Client client, int playerID, boolean isDealer, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException
    {
        this.playerID = playerID;
        this.client=client;
        this.isDealer=isDealer;
        this.objectInputStream = objectInputStream;
        
        displayDealerGUI();
        listenToServer();
    }



    private void displayDealerGUI() throws  IOException, ClassNotFoundException{
    	
    	frame = new JFrame("BlackJack: Dealer");
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setSize(800,600);
    	
    	gameInfoArea = new JTextArea();
    	gameInfoArea.setEditable(false);
    	Font customFont = new Font("Arial", Font.BOLD, 20);
		gameInfoArea.setFont(customFont);
    	
		JScrollPane scrollPane = new JScrollPane(gameInfoArea);
		
		String info = "Dealer\n"; 
		
		gameInfoArea.setText(info);
    	
    	JPanel buttons = new JPanel();
    	buttons.setLayout(new FlowLayout());
    	
    	startButton = new JButton("Start Round");
    	hitButton = new JButton("Hit");
    	endButton = new JButton("End Round");
    	
    	buttons.add(startButton);
    	buttons.add(hitButton);
    	buttons.add(endButton);
    	
    	dealerPanel = new JPanel(new FlowLayout());
    	dealerLabel = new JLabel("Dealer");
    	dealerPanel.add(dealerLabel);
    	
    	
    	startButton.addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			try {
    				start_Round();
    			}catch (IOException e1) {
    				e1.printStackTrace();
    			}
    		}
    	});
    	
    	hitButton.addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			try {
    				dealer_hit();
    			}catch (IOException e1) {
    				e1.printStackTrace();
    			} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    		}
    	});
    	
    	endButton.addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			try {
    				end_Round();
    			}catch (IOException e1) {
    				e1.printStackTrace();
    			}
    		}
    	});
    	
    	playersPanel = new JPanel(new GridLayout(1,3));
		for(int i = 1; i <= 10; i++) {
			JPanel playerSection = new JPanel(new FlowLayout());
			JLabel playerLabel = new JLabel("Player " + i);
			playerSection.add(playerLabel);
			playersPanel.add(playerSection);
		}
    	
    	
    	
    	JPanel mainPanel = new JPanel();
    	mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    	mainPanel.add(playersPanel);
    	mainPanel.add(dealerPanel);
    	
    	
    	
    	frame.setLayout(new BorderLayout());
    	frame.add(scrollPane, BorderLayout.NORTH);
    	frame.add(mainPanel, BorderLayout.CENTER);
    	frame.add(buttons, BorderLayout.SOUTH);
    	enableButtons(false);
    	
    	frame.setVisible(true);

    }
    
    public void enableButtons(boolean enabled) {
    	startButton.setEnabled(true);
    	hitButton.setEnabled(enabled);
    	endButton.setEnabled(enabled);
    }
    
    public void listenToServer() {
    	new Thread(new Runnable() {
			public void run() {
				try {
					while(true) {
						
						Response fromServer = (Response) objectInputStream.readObject();
						if(fromServer.getType() != null) {
							System.out.println(fromServer.getType());
							processServerResponse(fromServer);
						}
						
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
    }
    
    public void processServerResponse(Response fromServer) throws InterruptedException {
    	System.out.println(fromServer.getType());
    	if(fromServer.getType() == ResponseType.REQUEST_START_ROUND) {
    		startButton.setEnabled(true);
    	}else if(fromServer.getType() == ResponseType.REQUEST_DEALER_HIT) {
    		Thread.sleep(300);
            hitButton.setEnabled(true);
        }else if(fromServer.getType() == ResponseType.REQUEST_END_ROUND) {
            endButton.setEnabled(true);
	    }else if(fromServer.getType() == ResponseType.UPDATE) {
	    	updateGUI(fromServer);
	    }
    }
 
    public void updateGUI(Response fromServer) {
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
	    
	    
	    //Changing display
	    for(int i = 0; i < allGamePlayers.size(); i++) {
	    	PlayerData player = allGamePlayers.get(i);
	    	if(player.getPlayerID() == 0) {
	    		JTextArea dealerInfoArea = new JTextArea();
	    		dealerInfoArea.setEditable(false);
	    		dealerInfoArea.setOpaque(false);
	    		
	    		String dealerInfo = "Dealer\n" +
	    							"Hand Value: " + player.getHandValue() + "\n";
	    							if(player.getHandWithAce() != 0) {
	    								dealerInfo += "Ace Hand: " + player.getHandWithAce() + "\n";
	    							}
	    			  dealerInfo += "Cards: " + player.getHandString();
	    	    
	    		String gameInfoAreaText = "Dealer\n" +
	    							      "Dealer Hand: " + player.getHandValue();
	    								if(player.getHandWithAce() != 0) {
	    									dealerInfo += "Ace Hand: " + player.getHandWithAce();
	    								}
	    			  
	    	    gameInfoArea.setText(gameInfoAreaText);
	    			  
	    			  
	    	    Font customFont = new Font("Arial", Font.BOLD, 16);
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
				       playerInfo += "Cards: " + player.getHandString() + "\n" +
						    		 "Bet Amount: " + player.getBetAmount() + "\n";
 				       
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
    

    public void start_Round()throws IOException{
        client.sendStartRoundRequest();
		startButton.setEnabled(false);
    	
    }

    public void dealer_hit() throws IOException, InterruptedException{
        client.sendHitRequest(0);
    	hitButton.setEnabled(false);
    }
    public void end_Round() throws IOException{
       client.sendEndRoundRequest();
       endButton.setEnabled(false);
    }

}