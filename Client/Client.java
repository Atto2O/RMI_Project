/**
 * Created by sbp5 on 24/10/18.
 */
package Client;

import java.io.IOException;
import java.rmi.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileOutputStream;
import CallBack.*;
import Objects.FileObject;
import Objects.Type;
import RemoteObject.*;

import javax.print.DocFlavor;

public class Client {

	public String msg;
	static int RMIPort = 8997;
	static String hostName = "localhost";//AQUEST A DE SER EL DEL SERVIDOR!!"172.16.0.26";//
	static Garage h;
	public String state ="disconnected";
	private String userName = "";
	public int callbackid;
	public ClientCallbackInterface callbackObj;

	private Client(){}

	//region<MAIN>
	public static void main (String args[])
	{
		Client client = new Client();
		Scanner scanner = new Scanner(System.in);
		int portNum = 8001;//AQUEST A DE SER EL DEL SERVIDOR!!
		client.state = "disconnected";

        try
		{

			String registryURL = "rmi://"+ hostName +":" + portNum + "/some";
			Garage h = (Garage)Naming.lookup(registryURL);

			client.callbackObj = new CallbackImpl();

			while(true){
				if(client.state.equals("disconnected")){
					System.out.print("Si voleu fer registrar-vos escriviu: registrar. \n" +
							"Si voleu fer registrar-vos escriviu: logear.\n ");
					String resposta = scanner.next();

					if(resposta.toLowerCase().equals("registrar")){
						client.registrar(h);

					}else if(resposta.toLowerCase().equals("logear")){
						if(client.logear(h)){
							client.state = "connected";
						}
					}
				}else{

					System.out.print("Function over server? (deslogear,delete ,upload, search,download,subscribe) \n");
					String order = scanner.next();

					if(order.toLowerCase().equals("deslogear")){
						client.state = "disconnected";
					}

					//UPLOAD
					if(order.toLowerCase().equals("upload")){
						client.upload(h);
					}
					//SEARCH
					if(order.toLowerCase().equals("search")){
						System.out.print("Enter some key text: \n");
						String keyText = scanner.next();
						ArrayList<FileObject> files = h.searchFile(keyText);
						System.out.println("We found these files: ");
						for (FileObject f: files) {
							System.out.println(f.getId() + " - " + f.getFileName());
							System.out.printf("\tTags: ");
							for (String tag:f.getTags()) {
								System.out.printf(tag + " ");
							}
							System.out.printf("\n");
						}
					}

					//DOWLOAD
					if(order.toLowerCase().equals("download"))
					{
						System.out.println("Enter the file title:\n");
						int id = Integer.parseInt(scanner.next());
						FileObject file = h.downloadFile(id);

						String home = System.getProperty("user.home");
						try {
							FileOutputStream fos = new FileOutputStream(home+"/" + file.getFileName());
							fos.write(file.getFile());
							System.out.println("Downloaded!");
						}catch(Exception e){
							System.out.println("Error downloading: " + e.toString() + "\n");
						}
					}
					//DELETE
					if(order.toLowerCase().equals("delete"))
					{
						System.out.println(client.deleteFile(h));
					}
					//SUBSCRIBE
					if(order.toLowerCase().equals("subscribe"))
					{
						client.subscribeToTag(h);
					}
				}
			}
		}

		catch (Exception e)
		{
		    System.out.println("Exception in SomeClient: " + e.toString());
		}
	}
	//endregion

	//region<LogIn>
	//region<SignUp>
	public void subscribeToTag(Garage h){

		Scanner scanner = new Scanner(System.in);
		String newTag;
		System.out.print("Tag al que voleu subscriureus:\n");
		newTag = scanner.next().toLowerCase();
		boolean correcte=false;
		try {
			correcte=h.addSubscriptionTag(this.userName, newTag);
		}
		catch (Exception e)
		{
			System.out.println("Exception in subscription: " + e.toString());
		}

		if(correcte){
			System.out.println("Tas subscrit correctament");
		}else{
			System.out.println("No tas subscrit correctament");
		}


	}
	public boolean logear(Garage h){
		Scanner scanner = new Scanner(System.in);

		System.out.print("Nom de usuari:\n");
		userName = scanner.next().toLowerCase();
		System.out.print("Contrasenya:\n");
		String contrasenya = scanner.next();
		int callbackid = -1;
		try {
			if (!contrasenya.equals("") && !userName.equals("")){
				//les contrasenyes son iguals
				callbackid = h.user_login(userName, contrasenya,callbackObj);
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
	public void registrar(Garage h){
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter a user name:\n");
		String newUserName = scanner.next().toLowerCase();
		System.out.print("Enter a password:\n");
		String password_1 = scanner.next();
		System.out.print("Repeat password:\n");
		String password_2 = scanner.next();

		try {
			if (password_1.equals(password_2)) {
				//les contrasenyes son iguals
				boolean server_response = h.user_signup(newUserName, password_1);
                System.out.println(server_response);
				if (server_response == true) {
					System.out.print("T'has registrat correctament!!!\n ja pots logear\n");
				} else {
					System.out.print("El nom de usuari no es valid! prova amb unaltre!\n");
				}

			} else {
				System.out.print("Les contrasenyes son diferents!!\n");
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception in registrar: " + e.toString());
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
		System.out.print("Enter the file path (E.g.: /home/s/sbp5/Escritorio/image.png): \n");
		String filePath = scanner.next();
		Path fileLocation = Paths.get(filePath);
		byte[] data = new byte[0];

		try {
			data = Files.readAllBytes(fileLocation);


		fileObject.setFile(data);

		//FILE NAME
		System.out.print("Enter the file name (E.g.: Pug.jpg): \n");
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
			response = h.uploadFile(fileObject);
		} catch (RemoteException e) {
			e.printStackTrace();
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

	public String addTag(FileObject file){
		Scanner scanner = new Scanner(System.in);

		System.out.println("Type a tag to add: ");
		String tag = scanner.next();

		ArrayList<String> current_description = file.getTags();
		current_description.add(tag);
		file.setTags(current_description);
		return "added!";
	}

	public String removeTag(FileObject file){
		Scanner scanner = new Scanner(System.in);

		System.out.println("Type a tag to remove: ");
		String tag = scanner.next();

		ArrayList<String> current_description = file.getTags();
		if(current_description.contains(tag.toLowerCase())){
			current_description.remove(tag);
			file.setTags(current_description);
			return "added!";
		}
		else{
			return "not in current tags!";
		}
	}
	//endregion

	//endregion

	//region<Users>
	//endregion
	//endregion
}
