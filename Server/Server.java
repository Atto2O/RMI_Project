/**
 * Created by sbp5 on 24/10/18.
 */
package Server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.registry.Registry;
import java.util.Scanner;

import ServerUtils.ServerInfo;
import RemoteObject.*;
import ServerUtils.WS_manager.DataManager;

public class Server {

    public static void main(String args[]) throws UnknownHostException {

        int portNum;
        String address;

        InetAddress inetAddress = InetAddress.getLocalHost();
        address = inetAddress.getHostAddress();

        Scanner scanner = new Scanner(System.in);
        while (true){
            while (true){
                System.out.println("Address: " + address + "\nEnter a port number:");
                String port = scanner.next();
                try{
                    portNum = Integer.parseInt(port);
                    if(port.length()>0){
                        Server.saveInfo(portNum, address);
                        break;
                    }
                }catch(Exception e){
                    System.out.println("Incorrect port!");
                }
            }
            try
            {
                GarageImp exportedObj = new GarageImp();
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

    private static void saveInfo(int portNum, String address){
        ServerInfo serverInfo = new ServerInfo(portNum, address);
        serverInfo.saveInfo();
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
