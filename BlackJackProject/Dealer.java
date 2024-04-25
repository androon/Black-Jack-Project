import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;


public class Dealer {

	private int playerID;
    private Client client;
    private boolean isDealer;
    ObjectInputStream objectInputStream;
    private boolean roundStart = false;

    public Dealer(Client client, int playerID, boolean isDealer, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException
    {
        this.playerID = playerID;
        this.client=client;
        this.isDealer=isDealer;
        this.objectInputStream = objectInputStream;
        displayDealerGUI();
    }



    private void displayDealerGUI() throws  IOException, ClassNotFoundException{

        while(true)
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