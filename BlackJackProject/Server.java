import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;




public class Server {
	
	public static void main(String[]  args) throws IOException, ClassNotFoundException {
		


		try {
			
			ServerSocket serverSocket = new ServerSocket(777);
		
			while(true) 
			{
				Socket client=serverSocket.accept();
				ClientHandler clientSocket=new ClientHandler(client);
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