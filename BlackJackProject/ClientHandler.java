import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.io.EOFException;

public class ClientHandler implements Runnable{
	private final Socket clientSocket;
	
	public ClientHandler(Socket socket)
		{
			clientSocket=socket;
		}
	
	public void run()
	
	{
		try 
		{
		
			OutputStream outputStream= clientSocket.getOutputStream();
			ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			ObjectOutputStream objectOutputStream= new ObjectOutputStream(outputStream);
			
			
			Message fromClient = (Message) objectInputStream.readObject();
			
			
			if(fromClient.getType().compareTo(Type.LOGIN)==0)
				
			{
				fromClient.setStatus(Type.SUCCESS);
				objectOutputStream.writeObject(fromClient);
								
				while(true) {
					Message m=(Message) objectInputStream.readObject();
					
					if(m.getType().compareTo(Type.TEXT)==0)
					{
						String capitalizer= m.getText().toUpperCase();
						m.setText(capitalizer);
						objectOutputStream.writeObject(m);
					}
					else if(m.getType().compareTo(Type.LOGOUT)==0)
					{

						m.setStatus(Type.FAILED);
						objectOutputStream.writeObject(m);
					
					}
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
		
	

	

