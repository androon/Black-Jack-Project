import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.io.EOFException;

public class ClientHandler implements Runnable{
	private final Socket clientSocket;
	Response response;
	LoadUserData userDataFile;
	List<UserData> userData;
	private int playerID;
	GameManager gameManager;
	public ClientHandler(Socket socket, LoadUserData userDataFile, GameManager gameManager)
		{
			clientSocket=socket;
			this.userDataFile = userDataFile;
			this.gameManager = gameManager;
		}
	
	public void run()
	
	{
		try 
		{
			OutputStream outputStream= clientSocket.getOutputStream();
			ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			ObjectOutputStream objectOutputStream= new ObjectOutputStream(outputStream);
			
			userData = userDataFile.getUserList();
			
			while(true) {
				ClientMessage fromClient = (ClientMessage) objectInputStream.readObject();
				if(fromClient.getType() == MessageType.LOGIN) {
					//Some validation
					for(int i = 0; i < userData.size(); i++) {
						UserData user = userData.get(i);
						if((fromClient.getUsername().compareTo(user.getUsername())== 0) && (fromClient.getPassword().compareTo(user.getPassword()) == 0)){
							response = new Response();
							response.setPlayerID(gameManager.getPlayerID());
							response.setType(ResponseType.LOGIN_SUCCESS);
							objectOutputStream.writeObject(response);
						}
						
						
						/*System.out.println(user.getUsername());
						System.out.println(user.getPassword());
						System.out.println(user.getIsDealer());
						System.out.println(user.getWinAmount());
						System.out.println(user.getLossAmount());
						System.out.println(user.getBankroll());*/
					}
						
					
					
					/*System.out.println(fromClient.getUsername());
					if(fromClient.getUsername().equals("bob")) {
						response = new Response();
						response.setType(ResponseType.LOGIN_SUCCESS);
						response.setValidated(true);
						response.setDealer(true);
						response.setPlayerID(1);
						objectOutputStream.writeObject(response);
						break;
					}else {
						response = new Response();
						response.setType(ResponseType.LOGIN_FAIL);
						objectOutputStream.writeObject(response);
					}*/
					//send success with player information
				}
			}
			
			
		
		

	}
		catch(EOFException e)
		{
			//System.out.println("whjen is it closeing this");	
			//e.printStackTrace();
		}
		catch (IOException e) 
			{
				e.printStackTrace();
			} 
		catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			}
	}
		
}
		
	

	

