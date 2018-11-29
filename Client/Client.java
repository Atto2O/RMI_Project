/**
 * Created by sbp5 on 24/10/18.
 */
package Client;

import java.rmi.*;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileOutputStream;

import CallBack.*;
import RemoteObject.*;

public class Client {

	public String msg;
	static int RMIPort = 8999;
	static String hostName = "localhost";
	static Garage h;
	public String state ="disconnected";

	private Client()
	{
	}

	public static void main (String args[])
	{
		Client client = new Client();
		Scanner scanner = new Scanner(System.in);
		int portNum = 8001;
		client.state = "disconnected";

        try
		{
			ClientCallbackInterface callbackObj = new CallbackImpl();
			String registryURL = "rmi://"+ hostName +":" + portNum + "/some";
			Garage h = (Garage)Naming.lookup(registryURL);

			h.addCallback (callbackObj);

			while(true){
				if(client.state.equals("disconnected")){
					System.out.print("Si voleu fer registrar-vos escriviu: registrar. \n" +
							"Si voleu fer registrar-vos escriviu: logear.\n ");
					String resposta = scanner.next();

					if(resposta.toLowerCase().equals("registrar")){
						registrar(h);

					}else if(resposta.toLowerCase().equals("logear")){
						if(logear(h)){
							client.state = "connected";
						}
					}
				}else{

                System.out.print("Function over server? (deslogear ,upload, search,download) \n");
                String order = scanner.next();

				if(order.toLowerCase().equals("deslogear")){
					client.state = "disconnected";
				}

                //UPLOAD
                if(order.toLowerCase().equals("upload")){
					System.out.print("Enter the file name (E.g.: example.txt): \n");
					String filename = scanner.next();

					Path fileLocation = Paths.get("./clientBase/"+filename);
					byte[] data = Files.readAllBytes(fileLocation);
					System.out.println(h.uploadFile(data,filename));
                }
                //SEARCH
				if(order.toLowerCase().equals("search")){
					System.out.print("Enter some key text: \n");
					String keyText = scanner.next();
					System.out.println(h.searchFile(keyText));
				}

                //DOWLOAD
                if(order.toLowerCase().equals("download"))
                {
					System.out.println("Enter the file title:\n");
					String filename = scanner.next();
					byte[] arraybytes=h.downloadFile(filename);

					try (FileOutputStream fos = new FileOutputStream("./clientBase/"+filename)) {
						fos.write(arraybytes);
					}catch(Exception e){
						System.out.println("Error downloading: " + e.toString() + "\n");
					}
				}
                //DELETE
				}
                

			}
		}

		catch (Exception e)
		{
		    System.out.println("Exception in SomeClient: " + e.toString());
		}
	}
	public static boolean logear(Garage h){
		Scanner scanner = new Scanner(System.in);
		System.out.print("Nom de usuari:\n");
		String NomUsuari = scanner.next();
		System.out.print("Contrasenya:\n");
		String contrasenya = scanner.next();

		try {


			if (!contrasenya.equals("") && !NomUsuari.equals("")){
				//les contrasenyes son iguals
				boolean resposta_servidor = h.user_login(NomUsuari, contrasenya);
				if (resposta_servidor == true) {
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
	public static void registrar(Garage h){
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter a user name:\n");
		String newUserName = scanner.next();
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
}
