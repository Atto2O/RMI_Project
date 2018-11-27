/**
 * Created by sbp5 on 24/10/18.
 */
import java.rmi.*;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Client {
	public String msg;
	private Client(){}
	
	
	public static void main (String args[])
	{


		
		Scanner scanner = new Scanner(System.in);
        	int portNum = 8001;
        try
		{
		    String registryURL = "rmi://localhost:" + portNum + "/some";
		    Hello h = (Hello)Naming.lookup(registryURL);
			while(true){

                System.out.print("Function over server?: You can use: $upload, $search \n");
                String order = scanner.next();




                //UPLOAD
                if(order.equals("upload")){
					System.out.print("Fine, now give me the file name like: exemple.txt\n");
					String filename = scanner.next();

					Path fileLocation = Paths.get("./clientBase/"+filename);
					byte[] data = Files.readAllBytes(fileLocation);
					message=h.saveFile(data,filename);
                }
                //SEARCH
				if(order.equals("search")){
					System.out.print("Fine, now give me the title of the movie\n");
					String title = scanner.next();
					message=h.searchTitle("tile");
				}
                //DOWLOAD
                
                //DELETE
                
                
                
			    //String message = h.sayHello();
			    System.out.println(message);			    
			}
		}
		catch (Exception e)
		{
		    System.out.println("Exception in SomeClient: " + e.toString());
		}
	}
}
