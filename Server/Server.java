/**
 * Created by sbp5 on 24/10/18.
 */
package Server;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.registry.Registry;
import java.util.Scanner;

import RemoteObject.*;

public class Server {

    public static void main(String args[])
    {
        int portNum;
        Scanner scanner = new Scanner(System.in);
        while (true){
            while (true){
                System.out.println("Enter a port number:");
                String port = scanner.next();
                try{
                    portNum = Integer.parseInt(port);
                    if(port.length()>0){
                        break;
                    }
                }catch(Exception e){
                    System.out.println("Incorrect port!");
                }
            }
            try
            {
                GarageImp exportedObj = new GarageImp();//asar les ip's?
                startRegistry(portNum);
                String registryURL = "rmi://localhost:" + portNum + "/some";
                Naming.rebind(registryURL, exportedObj);
                System.out.println("Server ready.\n");
                break;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private static void startRegistry(int RMIPort) throws RemoteException
    {
        try
        {
            Registry registry = LocateRegistry.getRegistry(RMIPort);
            registry.list();
        }
        catch (Exception ex)
        {
            System.out.println("RMI registry cannot be located at port " + RMIPort);
            try{
                Registry registry = LocateRegistry.createRegistry(RMIPort);
                System.out.println("RMI registry created at port " + RMIPort);
            }catch(Exception e){
                System.out.println("RMI registry cannot be created at port "+ RMIPort + "\n");
            }
        }
    }
}
