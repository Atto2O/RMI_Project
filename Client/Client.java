/**
 * Created by sbp5 on 24/10/18.
 */
package Client;

import java.io.File;
import java.io.IOException;
import java.rmi.*;
import java.util.ArrayList;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileOutputStream;
import CallBack.*;
import GUI.ClientGUI;
import Objects.FileObject;
import Objects.Type;
import RemoteObject.*;

public class Client {

	private static Garage h;
	private String state ="disconnected";
	private static String userName = "";
	private int callbackid;
	private ClientCallbackInterface callbackObj;
	public String serverIP;
	public String serverPORT;

	private Client(){}

	//region<MAIN>
	public static void main (String args[]){
		Client client = new Client();
		ClientGUI.client = client;
		client.state = "disconnected";
		ClientGUI.animation();
	}
	//endregion

    public ArrayList<FileObject> getFilesByText(String text){
        ArrayList<FileObject> array = new ArrayList<>();
		try {
            array = Client.h.searchFile(text.toLowerCase(), Client.userName.toLowerCase().trim());
        }catch (Exception e){
            System.out.println("Exception in Client-getFilesByText(): " + e.toString());
        }
        return array;
    }

    public ArrayList<FileObject> getFilesByUser(){
        ArrayList<FileObject> array = new ArrayList<>();
		try {
            array = Client.h.searchFileByName(Client.userName.toLowerCase().trim());
        }catch (Exception e){
            System.out.println("Exception in Client-getFilesByUser(): " + e.toString());
        }
        return array;
    }

    public boolean changePassword(String oldPassword,String newPassword1){
		try{
			return Client.h.changePaswordOnServer(Client.userName.toLowerCase().trim(),oldPassword,newPassword1);
		}catch (Exception e){
			System.out.println("Exception in Client-changePassword(): " + e.toString());
		}
		return false;
	}

	public void deslogear(){
		try{
			Client.h.deleteCallback(this.callbackid);
			this.state = "disconnected";
            Client.userName = "";
		}catch (Exception e){
			System.out.println("Exception in Client-deslogear(): " + e.toString());
		}
	}

	public void deleteCallbackFromClienth(){
		try{
            Client.h.deleteCallback(this.callbackid);
		}catch (Exception e){
			System.out.println("Exception in Client-deleteCallbackFromClient: " + e.toString());
		}
	}

	public boolean setUpConnections(){
		String registryURL = "rmi://"+ this.serverIP +":" + this.serverPORT + "/some";
		try {
            Client.h = (Garage)Naming.lookup(registryURL);
		System.out.println("Garage created!");
        }catch (Exception e) {
            return false;
        }
		try {
			this.callbackObj = new CallbackImpl();
		} catch (Exception e) {
			e.printStackTrace();
            return false;
		}
		return true;
	}

	//region<LogIn>
	//region<SignUp>
	public boolean subscribeToTag(String newTag){
		try {
			return Client.h.addSubscriptionTag(Client.userName.toLowerCase().trim(), newTag.toLowerCase());
		}
		catch (Exception e)	{
			System.out.println("Exception in Client-subscribeToTag(): " + e.toString());
		}
		return false;
	}

	public boolean desSubscribeToTag(String oldTag){
		try {
			return Client.h.deleteSubscriptionTag(Client.userName.toLowerCase().trim(), oldTag.toLowerCase());
		}
		catch (Exception e)	{
			System.out.println("Exception in subscription: " + e.toString());
		}
		return false;
	}

	public boolean logear(String username, String password){
        Client.userName=username.toLowerCase().trim();
		int callbackid = -1;
		try {
			if (!password.equals("") && !Client.userName.equals("")){
				//les contrasenyes son iguals
				callbackid = Client.h.user_login(Client.userName.toLowerCase().trim(), password,callbackObj);
				if (callbackid != -1) {
					this.callbackid = callbackid;
					System.out.print("Correct log in.\n");
                    return true;
				} else {
					System.out.print("Username and password doesn't match.\n");
				}
			} else {
				System.out.print("One or more fields are empty.\n");
			}
		}
		catch (Exception e)	{
			System.out.println("Exception in Client-logear(): " + e.toString());
		}
		return false;
	}
	//endregion

	//region<SignIn>

	public boolean registrar(String newUserName, String password_1, String password_2){
		try {
			if (password_1.equals(password_2)) {
				//les contrasenyes son iguals
				boolean server_response = Client.h.user_signup(newUserName.toLowerCase().trim(), password_1);
                System.out.println(server_response);
				if (server_response) {
					System.out.print("Correct sign up\n");
                    Client.userName = newUserName.toLowerCase().trim();
                    this.logear(Client.userName, password_1);
					return true;
				} else {
					System.out.print("Invalid username. Try another one.\n");
					return false;
				}
			} else {
				System.out.print("Passwords doesn't match.\n");
				return false;
			}
		}
		catch (Exception e)	{
			System.out.println("Exception in Client-registrar(): " + e.toString());
			return false;
		}
	}
	//endregion
	//endregion

	//region<MODIFY Params>
	//region<Files>

	//region<Download>
	public boolean download(String path, FileObject file){
		try {
			FileOutputStream fos = new FileOutputStream(path+"/"+file.getFileName());
			fos.write(file.getFile());
			return true;
		}catch(Exception e){
			System.out.println("Error in Client-download(): "+e.toString());
		}
		return false;
	}

	//region<Upload>
	public boolean upload(File newFile, String filename, String type, String description, ArrayList<String> tags, boolean state){
		FileObject fileObject = new FileObject();
		fileObject.setUser(Client.userName.toLowerCase().trim());
		byte[] data;
		try {
			Path fileLocation = Paths.get(newFile.getAbsolutePath());
			data = Files.readAllBytes(fileLocation);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		fileObject.setFile(data);
		//FILE NAME
		fileObject.setFileName(filename.toLowerCase());
		//TYPE
		Type type1  = Type.fromString(type.toLowerCase());
		fileObject.setType(type1);
		//TAGS
		fileObject.setTags(tags);
        //DESCRIPTION TEXT
        fileObject.setDescription(description.toLowerCase());
		//IS PUBLIC
		fileObject.setState(state);
		try {
			return h.uploadFile(fileObject);
		} catch (IOException e) {
			return false;
		}

	}
	//endregion

	//region<Delete>
	public static boolean deleteFile(FileObject file){
		try{
			if(Client.h.deleteFile(file.getId(), Client.userName.toLowerCase().trim())){
				return true;
			}
		}catch (Exception e)
		{
			System.out.println("Exception in Client-deleteFile(): " + e.toString());
		}
		return false;
	}
	//endregion

	public boolean fileModified(FileObject file){
		try {
			return Client.h.addModification(file);
		}catch (Exception e){
			System.out.println("Exception in Client-changeType(): " + e.toString());
			return false;
		}
	}

	//region<Tags>
	public ArrayList<String> getSubscriptionsClient()throws RemoteException{
		ArrayList<String> array = new ArrayList<>();
        try{
            array = h.getSubscriptionsList(Client.userName.toLowerCase().trim());
        }catch(Exception e){
            System.out.println("Error in Client-getSubscriptionsClient(): " + e.toString());
        }
        return array;
	}
	//endregion
	//endregion
}