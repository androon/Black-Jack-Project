import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;


public class Client {
	public static void main(String[]  args) throws  IOException, ClassNotFoundException 
	{
		
	
			try 
			{
				Type type=null;
				Socket clientSocket=new Socket("localhost", 777);
				Scanner scanner = new Scanner(System.in);
				OutputStream outputStream= clientSocket.getOutputStream();
				ObjectOutputStream objectOutputStream= new ObjectOutputStream(outputStream);
				ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			
		
				Message login=new Message(Type.LOGIN);
				System.out.println("Sending Message Objects");
				objectOutputStream.writeObject(login);
				Message fromServertoClient = (Message) objectInputStream.readObject();
				System.out.println("From Server:"+fromServertoClient.getStatus());
				while(fromServertoClient.getStatus().compareTo(Type.FAILED)!=0)
				{
					
					String m=scanner.nextLine();
					
					Message login12= new Message(m);
					objectOutputStream.writeObject(login12);
					
					Message fromServertoClient1 = (Message) objectInputStream.readObject();
					if(fromServertoClient1.getText()!=null)
					{
					System.out.println("Message from Server:"+fromServertoClient1.getText());
					
					fromServertoClient=fromServertoClient1;
					}
					else
					{
						fromServertoClient=fromServertoClient1;
					}
				
				}
		
				scanner.close();
				objectOutputStream.close();
				clientSocket.close();
			}
			
			catch(IOException e)
				{
					e.printStackTrace();
				}
		}
	}


