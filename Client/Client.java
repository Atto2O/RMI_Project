/**
 * Created by sbp5 on 24/10/18.
 */
package Client;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.rmi.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;

import CallBack.*;
import GUI.ClientGUI;
import Objects.FileObject;
import Objects.Type;
import RemoteObject.*;

import javax.print.DocFlavor;

public class Client {

	public String msg;
	static int RMIPort = 8001;
	static String hostName = "localhost";//AQUEST A DE SER EL DEL SERVIDOR!!"172.16.0.26";//
	static Garage h;
	public String state ="disconnected";
	private String userName = "";
	public int callbackid;
	public ClientCallbackInterface callbackObj;


	public String clientIP;
	public String clientPORT;
	public String serverIP;
	public String serverPORT;

	private Client(){}

	//region<MAIN>
	public static void main (String args[])
	{
		Client client = new Client();
		ClientGUI.client = client;
		ClientGUI.animation();
		Scanner scanner = new Scanner(System.in);
		int portNum = 8001;//AQUEST A DE SER EL DEL SERVIDOR!!
		client.state = "disconnected";
	}
	//endregion

    public ArrayList<FileObject> getFilesByText(Garage h,String text){
        try {
            return h.searchFile(text);
        }catch (Exception e)
        {
            System.out.println("Exception in Client-getFilesByText(): " + e.toString());
        }
        return new ArrayList<FileObject>();
    }

    public ArrayList<FileObject> getFilesByUser(){
		System.out.println(h.toString());
        ArrayList<FileObject> array = new ArrayList<>();
		try {
            array = h.searchFileByName(this.userName);
        }catch (Exception e)
        {
            System.out.println("Exception in Client-getFilesByUser(): " + e.toString());
        }
        return array;
    }
	public boolean changePassword(Garage h,String oldPassword,String newPassword1){
		try{
			return h.changePaswordOnServer(this.userName,oldPassword,newPassword1);
		}catch (Exception e)
		{
			System.out.println("Exception in Client-changePassword(): " + e.toString());
		}

		return false;
	}
	public void deslogear(Garage h){
		try{
		h.deleteCallback(this.callbackid);
		this.state = "disconnected";
		}catch (Exception e)
		{
			System.out.println("Exception in Client-deslogear(): " + e.toString());
		}
	}
	public void deleteCallbackFromClienth(Garage h){
		try{
			h.deleteCallback(this.callbackid);
		}catch (Exception e)
		{
			System.out.println("Exception in Client-deleteCallbackFromClient: " + e.toString());
		}

	}

	public void setUpConnections(){
		String registryURL = "rmi://"+ hostName +":" + this.serverPORT + "/some";
		Garage h = null;
		try {
			h = (Garage)Naming.lookup(registryURL);
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.out.println("Garage created!");
		ClientGUI.h = h;

		try {
			this.callbackObj = new CallbackImpl();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	//region<LogIn>
	//region<SignUp>
	public boolean subscribeToTag(Garage h, String newTag){




		try {

			return h.addSubscriptionTag(this.userName, newTag.toLowerCase());
		}
		catch (Exception e)
		{
			System.out.println("Exception in subscription: " + e.toString());
			return false;
		}

	}

	public boolean desSubscribeToTag(Garage h, String oldTag){



		try {

			return h.deleteSubscriptionTag(this.userName, oldTag.toLowerCase());
		}
		catch (Exception e)
		{
			System.out.println("Exception in subscription: " + e.toString());
			return false;
		}

	}
	public boolean logear(Garage h,String username, String password){
		/*Scanner scanner = new Scanner(System.in);

		System.out.print("Nom de usuari:\n");
		userName = scanner.next().toLowerCase();
		System.out.print("Contrasenya:\n");
		String contrasenya = scanner.next();
		*/
		this.userName=username;
		String contrasenya=password;
		int callbackid = -1;
		try {
			if (!contrasenya.equals("") && !this.userName.equals("")){
				//les contrasenyes son iguals
				callbackid = h.user_login(this.userName, contrasenya,callbackObj);
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
			System.out.println("Exception in logear: " + e.toString());
		}
		return false;

	}
	//endregion

	//region<SignIn>
	public void signin(Garage h){

	}

	public boolean registrar(Garage h, String newUserName, String password_1, String password_2){
		/*Scanner scanner = new Scanner(System.in);
		System.out.print("Enter a user name:\n");
		String newUserName = scanner.next().toLowerCase();
		System.out.print("Enter a password:\n");
		String password_1 = scanner.next();
		System.out.print("Repeat password:\n");
		String password_2 = scanner.next();*/

		try {
			if (password_1.equals(password_2)) {
				//les contrasenyes son iguals
				boolean server_response = h.user_signup(newUserName, password_1);
                System.out.println(server_response);
				if (server_response == true) {
					System.out.print("T'has registrat correctament!!!\n ja pots logear\n");
					this.userName = newUserName;
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
			System.out.println("Exception in registrar: " + e.toString());
			return false;
		}
	}
	//endregion
	//endregion

	//region<MODIFY Params>
	//region<Files>

	//region<Upload>
	public void upload(Garage h){
		///home/s/sbp5/Escritorio/exemple.txt
		Scanner scanner = new Scanner(System.in);
		FileObject fileObject = new FileObject();
		fileObject.setUser(this.userName);

		//FILE
		while(true) {
			System.out.print("Enter the file path (E.g.: /home/s/sbp5/Escritorio/image.png): \n");
			String filePath = scanner.next();
			Path fileLocation = Paths.get(filePath);
			byte[] data = new byte[0];

			try {
				data = Files.readAllBytes(fileLocation);
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
			fileObject.setFile(data);
		}

		//FILE NAME
		System.out.print("Enter the file name (E.g.: Pug): \n");
		String fileName = scanner.next();
		fileObject.setFileName(fileName);

		//TYPE
		System.out.print("Enter a Type MOVIE, IMAGE, TEXT, PDF or AUDIO (E.g.: image): \n");
		String t = scanner.next();
		Type type  = Type.fromString(t);
		fileObject.setType(type);

		//DESCRIPTION
		System.out.print("Enter topic tags (E.g.: animal,cute,dog,love,pug) (Without spaces): \n");
		String desc = scanner.next();
		String[] keyWords = desc.split(",");
		ArrayList<String> description = new ArrayList<>();
		for(int i =0; i<keyWords.length; i++){
			description.add(keyWords[i]);
		}
		fileObject.setTags(description);

        //DESCRIPTION TEXT
        System.out.print("Enter description: \n");
        char[] descript = scanner.next().toCharArray();


        fileObject.setDescription(descript);

		//IS PUBLIC
		System.out.print("You will make it public? (YES/NO): \n");
		String resp = scanner.next().toLowerCase();
		if(resp.equals("yes")){
			fileObject.setState(true);
		}else{fileObject.setState(false);}

		String response = "Error";

		try {
			if(h.uploadFile(fileObject)){
				System.out.printf("Fitxer pujat i guardat correctament\n");
			}else{
				System.out.printf("Error al pujar o guarda el fitxer\n");
			}
		System.out.println(response);
        } catch (IOException e) {
            System.out.printf("Aquest no es un fitxer valid\n");
            //e.printStackTrace();
        }
	}
	//endregion

	//region<Delete>
	public String deleteFile(Garage h){
		Scanner scanner = new Scanner(System.in);
		try{
			System.out.println("Give me the id file:\n");
			int fileId = Integer.parseInt(scanner.next());
			return h.deleteFile(fileId, this.userName);
		}catch (Exception e)
		{
			System.out.println("Exception in SomeClient: " + e.toString());
			return "Error en el Client al intentar borra fitxer: " + e.toString();
		}
	}
	//endregion

	public String changeName(Garage h, FileObject file, String newName){
		try {
			file.setFileName(newName);
			return "Name " + h.modifiedFile(file);
		}catch (Exception e){
			return "Error changing file name: " + e.toString();
		}
	}

	public String changeType(FileObject file){
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter new Type MOVIE, IMAGE, TEXT, PDF or AUDIO: \n");
		try {
			String t = scanner.next();
			Type type  = Type.fromString(t);
			file.setType(type);
			return "Type changed!";
		}catch (Exception e){
			return "Error changing file type: " + e.toString();
		}
	}

	public String changeState(FileObject file){
		Scanner scanner = new Scanner(System.in);
		String response;

		System.out.printf("Current file state is ");
		if(file.getState()){
			System.out.printf("PUBLIC\nWill you change it to PRIVATE? (YES/NO)");
			response = scanner.next();
			if(response.toLowerCase().equals("yes")){
				file.setState(false);
				return "State changed, now this file is PRIVATE.";
			}else{return "State not changed.";}
		} else {
			System.out.printf("PRIVATE\nWill you change it to PUBLIC? (YES/NO)");
			response = scanner.next();
			if(response.toLowerCase().equals("yes")){
				file.setState(true);
				return "State changed, now this file is PUBLIC.";
			}else{return "State not changed.";}
		}
	}

	//region<Tags>
	public String changeTags(FileObject file){
		String state = "Tag ";
		Scanner scanner = new Scanner(System.in);

		System.out.println("Add or delete tags?");
		while (true){
			String response = scanner.next();
			if(response.toLowerCase().equals("add")){
				state += addTag(file);
				break;
			}
			if(response.toLowerCase().equals("delete")){
				state += removeTag(file);
				break;
			}
		}
		return state;
	}
	public ArrayList<String> getSubscriptionsClient()throws RemoteException{
		System.out.println(h.toString());
		ArrayList<String> array = new ArrayList<>();
        try{
            array = h.getSubscriptionsList(this.userName);
        }catch(Exception e){
            System.out.println("Error getSubscriptionsClient: " + e.toString() + "\n");
        }
        return array;
	}

	public String addTag(FileObject file){
		Scanner scanner = new Scanner(System.in);

		System.out.println("Type a tag to add: ");
		String tag = scanner.next();

		ArrayList<String> current_description = file.getTags();
		current_description.add(tag);
		file.setTags(current_description);
		return "added!";
	}

	public String removeTag(FileObject file) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Type a tag to remove: ");
        String tag = scanner.next();

        ArrayList<String> current_description = file.getTags();
        if (current_description.contains(tag.toLowerCase())) {
            current_description.remove(tag);
            file.setTags(current_description);
            return "removed!";
        } else {
            return "not in current tags!";
        }
    }
	//endregion

	//endregion

	//region<Users>
	//endregion
	//endregion

	public boolean checkUsername(Garage h, String userName) throws RemoteException{
		return !h.checkAvailableUser(userName);
	}

	public boolean logIn(Garage h, String userName, String password) throws RemoteException{
		int callbackid = -1;
		if (!password.equals("") && !userName.equals("")){
			callbackid = h.user_login(userName, password, callbackObj);
			if (callbackid != -1) {
				this.callbackid = callbackid;
				return true;
			}
		}
		return false;
	}
}
