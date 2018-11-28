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
		/*try {
			System.setSecurityManager (new RMISecurityManager());
			Registry registry = LocateRegistry.getRegistry ("localhost", RMIPort);
			h = (Garage) registry.lookup("hello");
			callbackObj = new CallbackImpl();
			h.addCallback (callbackObj);
			System.out.println("Registered for callback.");
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException ex){ // sleep over
			}
			h.deleteCallback (callbackObj);
			System.out.println("Unregistered for callback.");
		} // end try
		catch (Exception e) {
			System.out.println("Exception in CallbackClient: " + e);
		} // end catch*/
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

					if(resposta.equals("registrar")){
						registrar(h);

					}else if(resposta.equals("logear")){
						if(logear(h)){
							client.state = "connected";
						}

					}




				}else{


                System.out.print("Function over server? (Deslogear ,upload, search,download) \n");
                String order = scanner.next();

				if(order.equals("Deslogear")){
					client.state = "disconnected";
				}

                //UPLOAD
                if(order.equals("upload")){
					System.out.print("Enter the file name (E.g.: example.txt): \n");
					String filename = scanner.next();

					Path fileLocation = Paths.get("./clientBase/"+filename);
					byte[] data = Files.readAllBytes(fileLocation);
					System.out.println(h.uploadFile(data,filename));
                }
                //SEARCH
				if(order.equals("search")){
					System.out.print("Enter some key text: \n");
					String keyText = scanner.next();
					System.out.println(h.searchFile(keyText));
				}

                //DOWLOAD
                if(order.equals("download"))
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
		System.out.print("Donam el nom de usuari\n");
		String NewNomUsuari = scanner.next();
		System.out.print("Introduix una contrasenya\n");
		String contrasenya_1 = scanner.next();
		System.out.print("Introduix la contrasenya un altre cop\n");
		String contrasenya_2 = scanner.next();

		try {


			if (contrasenya_1.equals(contrasenya_2)) {
				//les contrasenyes son iguals
				boolean resposta_servidor = h.user_signup(NewNomUsuari, contrasenya_1);
				if (resposta_servidor = true) {
					System.out.print("T'as registrat correctamen!!!\n ja pots logear\n");
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
