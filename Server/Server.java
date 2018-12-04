/**
 * Created by sbp5 on 24/10/18.
 */
package Server;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.registry.Registry;
import CallBack.*;
import Objects.*;
import RemoteObject.*;
import Client.*;

public class Server {

    public static void main(String args[])
    {
        int portNum = 8001;
        int RMIPort = 8001;
        try
        {
            GarageImp exportedObj = new GarageImp();//asar les ip's?
            startRegistry(RMIPort);
            String registryURL = "rmi://localhost:" + portNum + "/some";
            Naming.rebind(registryURL, exportedObj);
            System.out.println("Server ready.\n");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void startRegistry(int RMIPort) throws RemoteException
    {
        try
        {
            Registry registry = LocateRegistry.getRegistry(RMIPort);
            registry.list();
        }
        catch (RemoteException ex)
        {
            System.out.println("RMI registry cannot be located at port " + RMIPort+"\n");
            Registry registry = LocateRegistry.createRegistry(RMIPort);
            System.out.println("RMI registry created at port " + RMIPort+"\n");
        }
    }
}
