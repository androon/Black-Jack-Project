import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Client {
	private GUIManager gui;
	
	public static void main(String[]  args) throws  IOException, ClassNotFoundException 
	{
		Client client = new Client();
		client.setupClient();
	}
	
	public void setupClient() throws ClassNotFoundException, UnknownHostException, IOException{

				Scanner myScanner = new Scanner(System.in);
				System.out.println("Enter Server Address");
				
				String address = myScanner.nextLine();
				gui = new GUIManager(this, address);
				gui.getLogin();

				
	}
}


