/**
 * Created by sbp5 on 24/10/18.
 */
package RemoteObject;

import java.rmi.*;
import java.rmi.server.*;
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
import java.util.Vector;

public class GarageImp extends UnicastRemoteObject implements Garage {

    private ArrayList<FileObject> files = null;
    private ArrayList<User> users = null;

    private int lastFileID = -1;
    private int lastUserID = -1;

    static int RMIPort;
    // vector for store list of callback objects
    private static Vector callbackObjects;

    public GarageImp() throws RemoteException {
        super();
        // instantiate a Vector object for storing callback objects
        callbackObjects = new Vector();
        getInfo();
    }

    @Override
    public boolean registrar_usuari (String NewNomUsuari, String contrasenya)  throws RemoteException
    {
        //SI EL NOM ES VALID RETORNA TRUE
        return true;
        /*if(){
            return true;
        }else{
            return false;
        }*/


    }



    // method for client to call to add itself to its callback
    @Override
    public void addCallback (ClientCallbackInterface callbackObject)  throws RemoteException
    {
    // store the callback object into the vector
        if(!(callbackObjects.contains(callbackObject)))
        {
            callbackObjects.addElement (callbackObject);
            System.out.println ("Server got an 'addCallback' call.");
        }
        callback();
    }

    @Override
    public void deleteCallback (ClientCallbackInterface callbackObject) throws RemoteException
    {
        System.out.println ("Server got an 'deleteCallback' call.");
        callbackObjects.remove (callbackObject);
    }

    private static void callback()  throws RemoteException {
        System.out.println("*******************************************************\n" + "Callbacks initiated ---");
        for (int i = 0; i < callbackObjects.size(); i++) {
            System.out.println("Now performing the " + i + "-th callback\n");
            // convert the vector object to a callback object
            ClientCallbackInterface client = (ClientCallbackInterface) callbackObjects.elementAt(i);
            try
            {
                client.callMe("Server calling back to client " + i);
            }
            catch(Exception e){
                System.out.println("Error: "+e.toString());
            }
            System.out.println("--- Server completed callbacks"+"*******************************************************\n");

            //...
        }
        //...
    }

    @Override
    public String uploadFile (byte[] myByteArray, String filename) throws RemoteException {
        try (FileOutputStream fos = new FileOutputStream("./garage/"+filename)) {
            System.out.println(fos);
            fos.write(myByteArray);
        }catch(Exception e){
            System.out.println("Error uploading: " + e.toString());
            return "Error uploading: " + e.toString();
        }
        return "Saved!";
    }

    @Override
    public String searchFile(String file) throws RemoteException {

        return "We find it: "+file+" !!";
    }

    @Override
    public byte[] downloadFile(String title) throws RemoteException
    {
        try
        {
            Path fileLocation = Paths.get("./garage/"+title);
            byte[] data = Files.readAllBytes(fileLocation);
            return data;
        }
        catch(Exception e)
        {
            System.out.println("Download Error: " + e.toString());
            return null;
        }
    }
}
