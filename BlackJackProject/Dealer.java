import java.io.IOException;
import java.util.Scanner;


public class Dealer {

	private int playerID;
    private Client client;
    private boolean isDealer;


    public Dealer(Client client, int playerID, boolean isDealer) throws IOException
    {
        this.playerID = playerID;
        this.client=client;
        this.isDealer=isDealer;
        displayDealerGUI();
    }



    private void displayDealerGUI() throws  IOException{

        while(true)
        {
            System.out.println("1.Start Round");
            System.out.println("2.Dealer Hit");
            System.out.println("3.Finish Round");
            Scanner scan=new Scanner(System.in);
            int choice=scan.nextInt();

            switch(choice) {
            case 1:
                start_Round();
                break;
            case 2:
                dealer_hit();
                break;
            case 3:
                end_Round();
                break;
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