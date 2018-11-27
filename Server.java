/**
 * Created by sbp5 on 24/10/18.
 */
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String args[])
    {
        int portNum = 8001;
        int RMIPortNum = 8001;
        try
        {
            HelloImpl exportedObj = new HelloImpl();
            startRegistry(RMIPortNum);
            String registryURL = "rmi://localhost:" + portNum + "/some";
            //String registryURL = "rmi://172.16.2.149:" + portNum + "/some";
            Naming.rebind(registryURL, exportedObj);
            System.out.println("Some Server ready.\n");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void startRegistry(int RMIPortNum) throws RemoteException
    {
        try
        {
            Registry registry = LocateRegistry.getRegistry(RMIPortNum);
            registry.list();
        }
        catch (RemoteException ex)
        {
            System.out.println("RMI registry cannot be located at port " + RMIPortNum+"\n");
            Registry registry = LocateRegistry.createRegistry(RMIPortNum);
            System.out.println("RMI registry created at port " + RMIPortNum+"\n");
        }
    }
}
