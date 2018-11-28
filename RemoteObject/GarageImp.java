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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
//import ServerUtils.*;

public class GarageImp extends UnicastRemoteObject implements Garage {

    private ArrayList<FileObject> files = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();

    private int lastFileID = -1;
    private int lastUserID = -1;

    static int RMIPort;
    // vector for store list of callback objects
    private static Vector callbackObjects;

    public GarageImp() throws RemoteException {
        super();
        // instantiate a Vector object for storing callback objects
        callbackObjects = new Vector();
        //this.getInfo();
    }

    /*private void getInfo()
    {
        System.out.println("Getting existing files...\n");
        try {
            this.files = ServerUtils.getFiles();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Getting last file ID available...\n");
        this.lastFileID = ServerUtils.getFileID();
        System.out.println("Getting existing users...\n");
        try {
            this.users = ServerUtils.getUsers();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Getting last user ID available...\n");
        this.lastUserID = ServerUtils.getUserID();
    }*/

    @Override
    public boolean registrar_usuari (String NewNomUsuari, String contrasenya)  throws RemoteException
    {
        //SI EL NOM ES VALID RETORNA TRUE

        if(check_usuari_available(NewNomUsuari)){
            User newUser = new User(NewNomUsuari,contrasenya,generateId());
            this.users.add(newUser);
            System.out.println("Usuari:"+NewNomUsuari+"registrat!!");
            return true;
        }else{
            return false;
        }

    }
    //WE GENERATE ID FOR THE USER
    private int generateId(){
        this.lastUserID +=+ 1;
        return this.lastUserID;
    }

    private boolean check_usuari_available (String NewNomUsuari)  throws RemoteException
    {


        Iterator<User> iter = this.users.iterator();

        while (iter.hasNext()) {

            if(iter.next().getName().equals(NewNomUsuari)){

                return false;
            }
        }

        return  true;
    }

    public boolean user_login (String NomUsuari, String contrasenya)  throws RemoteException

    {

        Iterator<User> iter = this.users.iterator();

        boolean correct = false;
        while (iter.hasNext()) {

            User currentlyUser = iter.next();
            if(currentlyUser.getName().equals(NomUsuari)){

                if(currentlyUser.getPassword().equals(contrasenya)){

                    correct = true;
                }else{
                    return correct;
                }
            }
        }

        return  correct;
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
