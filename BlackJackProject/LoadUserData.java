import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class LoadUserData {
	List<UserData> userData = new LinkedList<UserData>();
	
	public LoadUserData() {
		startLoad();
	}
	
	public void startLoad() {
		File userFile = new File("userData.txt");	
		try{
			Scanner fileScanner = new Scanner(userFile);
		while(fileScanner.hasNextLine()) {
					String wholeLine = fileScanner.nextLine();
					String[] data = wholeLine.split(",");
					if(data.length >= 6) {
						String username = data[0];
						String password = data[1];
						boolean isDealer = Boolean.parseBoolean(data[2]);
						int winAmount = Integer.parseInt(data[3]);
						int lossAmount = Integer.parseInt(data[4]);
						int bankRoll = Integer.parseInt(data[5]);
						UserData newUser = new UserData(username, password, isDealer, winAmount, lossAmount, bankRoll);
						userData.add(newUser);
					}
				}
				fileScanner.close();
			}catch(Exception e) {
				System.out.println(e);
			}
	}
	
	public void saveData() throws IOException {
		File userFile = new File("userData.txt");
		FileWriter writer = new FileWriter(userFile);
		try {
            writer = new FileWriter(userFile);

            for (int i = 0; i < userData.size(); i++) {
                UserData user = userData.get(i);
                String writeLine = user.getUsername() + "," +
                              user.getPassword() + "," +
                              user.getIsDealer() + "," +
                              user.getWinAmount() + "," +
                              user.getLossAmount() + "," +
                              user.getBankroll();

                writer.write(writeLine);
                writer.write("\n");
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
		
    }
	
	public List<UserData> getUserList(){
		return userData;
	}
}
