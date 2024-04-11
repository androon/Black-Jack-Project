import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;




public class Server {
	static LoadUserData loadData;
	static GameManager gameManager;
	
	public static void main(String[]  args) throws IOException, ClassNotFoundException {
		loadData = new LoadUserData();
		gameManager = new GameManager();
		
		
		try {
			ServerSocket serverSocket = new ServerSocket(777);
			InetAddress localhost = InetAddress.getLocalHost();
            System.out.println("Server IP Address: " + localhost.getHostAddress());
			while(true) 
			{
				Socket client=serverSocket.accept();
				ClientHandler clientSocket=new ClientHandler(client, loadData, gameManager);
				new Thread(clientSocket).start();
				
			}
		}
		
		
		catch(IOException e) 
			{
				e.printStackTrace();
			}
	
	
	}
}





/*finally {
	if(serverSocket!=null)
	{
		try {
			serverSocket.close();
			}
	
	catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}*/