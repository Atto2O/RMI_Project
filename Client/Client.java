/**
 * Created by sbp5 on 24/10/18.
 */
package Client;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import CallBack.*;
import Objects.*;
import RemoteObject.*;
import Server.*;

public class Client {

	public String msg;
	static int RMIPort = 8999;
	static String hostName = "localhost";
	static Garage h;

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
		Scanner scanner = new Scanner(System.in);
		int portNum = 8001;

        try
		{
			ClientCallbackInterface callbackObj = new CallbackImpl();
		    String registryURL = "rmi://"+ hostName +":" + portNum + "/some";
		    Garage h = (Garage)Naming.lookup(registryURL);

			h.addCallback (callbackObj);

			while(true){

                System.out.print("Function over server? (upload, search or download) \n");
                String order = scanner.next();

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
					System.out.print("Enter the title: \n");
					String title = scanner.next();
					System.out.println(h.searchFile(title));
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

		catch (Exception e)
		{
		    System.out.println("Exception in SomeClient: " + e.toString());
		}
	}
}
