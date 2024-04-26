import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
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

        /*while(true)
        {
        	
        	Scanner scan=new Scanner(System.in);
            int choice;
            Response fromServer = (Response) objectInputStream.readObject();
        	if(fromServer.getType() == ResponseType.REQUEST_START_ROUND) {
        		System.out.println("1.Start Round");
	            choice = scan.nextInt();
		            if(choice == 1) {
			           start_Round();
			        }
        	}else if(fromServer.getType() == ResponseType.REQUEST_DEALER_HIT) {
	            System.out.println("1.Dealer Hit");
	            choice = scan.nextInt();
		            if(choice == 1) {
			           dealer_hit();
			        }
            }else if(fromServer.getType() == ResponseType.REQUEST_END_ROUND) {
	            System.out.println("1.Finish Round");
	            choice = scan.nextInt();
	            if(choice == 1) {
		           end_Round();
		        }
            }

        }*/
    	
    	frame = new JFrame("BlackJack: Dealer");
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setSize(800,800);
    	
    	gameInfoArea = new JTextArea();
    	gameInfoArea.setEditable(false);
    	JScrollPane scrollPane = new JScrollPane(gameInfoArea);
    	
    	JPanel buttons = new JPanel();
    	buttons.setLayout(new FlowLayout());
    	
    	startButton = new JButton("Start Round");
    	hitButton = new JButton("Hit");
    	endButton = new JButton("End Round");
    	
    	buttons.add(startButton);
    	buttons.add(hitButton);
    	buttons.add(endButton);
    	
    	startButton.addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			try {
    				start_Round();
    				startButton.setEnabled(false);
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
    				hitButton.setEnabled(false);
    			}catch (IOException e1) {
    				e1.printStackTrace();
    			}
    		}
    	});
    	
    	endButton.addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			try {
    				end_Round();
    				endButton.setEnabled(false);
    			}catch (IOException e1) {
    				e1.printStackTrace();
    			}
    		}
    	});
    	
    	frame.setLayout(new BorderLayout());
    	frame.add(scrollPane, BorderLayout.CENTER);
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
						System.out.println(fromServer.getType());
						processServerResponse(fromServer);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
    }
    
    public void processServerResponse(Response fromServer) {
    	if(fromServer.getType() == ResponseType.REQUEST_START_ROUND) {
    		startButton.setEnabled(true);
    	}else if(fromServer.getType() == ResponseType.REQUEST_DEALER_HIT) {
            hitButton.setEnabled(true);
        }else if(fromServer.getType() == ResponseType.REQUEST_END_ROUND) {
            endButton.setEnabled(true);
	    }
    }
 
    
    public void start_Round()throws IOException{
        client.sendStartRoundRequest();
    	
    }

    public void dealer_hit() throws IOException{
        client.sendHitRequest(0);
    	
    }
    public void end_Round() throws IOException{
       client.sendEndRoundRequest();
    }

}