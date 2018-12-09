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

	public String msg;
	static int RMIPort = 8001;
	static String hostName = "localhost";//AQUEST A DE SER EL DEL SERVIDOR!!"172.16.0.26";//
	static Garage h;
	public String state ="disconnected";
	private static String userName = "";
	public int callbackid;
	public ClientCallbackInterface callbackObj;

	public String serverIP;
	public String serverPORT;

	private Client(){}

	//region<MAIN>
	public static void main (String args[])
	{
		Client client = new Client();
		ClientGUI.client = client;
		client.state = "disconnected";
		ClientGUI.animation();
	}
	//endregion

    public ArrayList<FileObject> getFilesByText(String text){
        ArrayList<FileObject> array = new ArrayList<>();
		try {
            array = this.h.searchFile(text.toLowerCase(), this.userName);
        }catch (Exception e)
        {
            System.out.println("Exception in Client-getFilesByText(): " + e.toString());
        }
        return array;
    }

    public ArrayList<FileObject> getFilesByUser(){
        ArrayList<FileObject> array = new ArrayList<>();
		try {
            array = this.h.searchFileByName(this.userName.toLowerCase());
        }catch (Exception e)
        {
            System.out.println("Exception in Client-getFilesByUser(): " + e.toString());
        }
        return array;
    }

    public boolean changePassword(String oldPassword,String newPassword1){
		try{
			return this.h.changePaswordOnServer(this.userName,oldPassword,newPassword1);
		}catch (Exception e)
		{
			System.out.println("Exception in Client-changePassword(): " + e.toString());
		}
		return false;
	}

	public void deslogear(){
		try{
			this.h.deleteCallback(this.callbackid);
			this.state = "disconnected";
			this.userName = "";
		}catch (Exception e)
		{
			System.out.println("Exception in Client-deslogear(): " + e.toString());
		}
	}
	public void deleteCallbackFromClienth(){
		try{
			this.h.deleteCallback(this.callbackid);
		}catch (Exception e)
		{
			System.out.println("Exception in Client-deleteCallbackFromClient: " + e.toString());
		}

	}

	public boolean setUpConnections(){
		String registryURL = "rmi://"+ this.serverIP +":" + this.serverPORT + "/some";
		try {

			this.h = (Garage)Naming.lookup(registryURL);


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
			return this.h.addSubscriptionTag(this.userName, newTag.toLowerCase());
		}
		catch (Exception e)
		{
			System.out.println("Exception in Client-subscribeToTag(): " + e.toString());
		}
		return false;
	}

	public boolean desSubscribeToTag(String oldTag){
		try {
			return this.h.deleteSubscriptionTag(this.userName, oldTag.toLowerCase());
		}
		catch (Exception e)
		{
			System.out.println("Exception in subscription: " + e.toString());
		}
		return false;
	}

	public boolean logear(String username, String password){
		this.userName=username.toLowerCase();
		String contrasenya=password;
		int callbackid = -1;
		try {
			if (!contrasenya.equals("") && !this.userName.equals("")){
				//les contrasenyes son iguals
				callbackid = this.h.user_login(this.userName, contrasenya,callbackObj);
				if (callbackid != -1) {
					this.callbackid = callbackid;
					System.out.print("T'as logeat correctamen!!!\n");
                    return true;
				} else {
					System.out.print("El nom de usuari o la contrasenya no coincideixen!\n");
				}
			} else {
				System.out.print("Un o mes camps son buit!!\n");
			}
		}
		catch (Exception e)
		{
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
				boolean server_response = this.h.user_signup(newUserName.toLowerCase(), password_1);
                System.out.println(server_response);
				if (server_response == true) {
					System.out.print("T'has registrat correctament!!!\n ja pots logear\n");
					this.userName = newUserName.toLowerCase();
					return true;
				} else {
					System.out.print("El nom de usuari no es valid! prova amb unaltre!\n");
					return false;
				}

			} else {
				System.out.print("Les contrasenyes son diferents!!\n");
				return false;
			}
		}
		catch (Exception e)
		{
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
		System.out.println(path);
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
		fileObject.setUser(this.userName);

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
			if(Client.h.deleteFile(file.getId(), Client.userName.toLowerCase())){
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

			return this.h.addModification(file);

		}catch (Exception e){
			System.out.println("Exception in Client-changeType(): " + e.toString());
			return false;
		}
	}

	//region<Tags>
	public ArrayList<String> getSubscriptionsClient()throws RemoteException{
		ArrayList<String> array = new ArrayList<>();
        try{
            array = h.getSubscriptionsList(this.userName.toLowerCase());
        }catch(Exception e){
            System.out.println("Error in Client-getSubscriptionsClient(): " + e.toString());
        }
        return array;
	}
	//endregion

	//endregion

	//region<Users>
	//endregion
	//endregion
}
