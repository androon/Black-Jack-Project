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
	OutputStream outputStream;
	ObjectInputStream objectInputStream;
	ObjectOutputStream objectOutputStream;
	
	public ClientHandler(Socket socket, LoadUserData userDataFile, GameManager gameManager){
			clientSocket=socket;
			this.userDataFile = userDataFile;
			this.gameManager = gameManager;
		}
	
	public void run() {
		try {
			outputStream = clientSocket.getOutputStream();
			objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			objectOutputStream = new ObjectOutputStream(outputStream);
			
			userData = userDataFile.getUserList();

			//Waiting for message type login
			while(true) {
				ClientMessage fromClient = (ClientMessage) objectInputStream.readObject();
				if(fromClient.getType() == MessageType.LOGIN) {
					boolean match = false;
					//Some validation
					for(int i = 0; i < userData.size(); i++) {
						UserData user = userData.get(i);
						if((fromClient.getUsername().compareTo(user.getUsername())== 0) && (fromClient.getPassword().compareTo(user.getPassword()) == 0)){
							response = new Response();
							response.setPlayerID(gameManager.getPlayerID());
							response.setType(ResponseType.LOGIN_SUCCESS);
							response.setWinAmount(user.getWinAmount());
							response.setLossAmount(user.getLossAmount());
							response.setBankRoll(user.getBankroll());
							response.setUsername(user.getUsername());
							objectOutputStream.writeObject(response);
							
							match = true;
							break;
						}
					}
					if(!match) {
						response = new Response();
						response.setType(ResponseType.LOGIN_FAIL);
						objectOutputStream.writeObject(response);
					}
				}else if(fromClient.getType() == MessageType.BET) {
					//go to game player data and add bet to player in game
					System.out.println("Player wants to make a bet");
				}
			}
			
			
		
		

	}
		catch(EOFException e){
			//System.out.println("whjen is it closeing this");	
			//e.printStackTrace();
		}
		catch (IOException e) {
				e.printStackTrace();
			} 
		catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	}
		
}
		
	

	

