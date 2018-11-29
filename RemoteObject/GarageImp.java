/**
 * Created by sbp5 on 24/10/18.
 */
package RemoteObject;

import java.rmi.*;
import java.rmi.server.*;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileOutputStream;

import CallBack.*;
import Objects.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import ServerUtils.*;

public class GarageImp extends UnicastRemoteObject implements Garage {

    private FilesArray files = new FilesArray();
    private UsersArray users = new UsersArray();

    private int lastFileID = -1;
    private int lastUserID = -1;

    static int RMIPort;
    // vector for store list of callback objects
    private static Vector callbackObjects;

    public GarageImp() throws RemoteException {
        super();
        // instantiate a Vector object for storing callback objects
        callbackObjects = new Vector();
        this.getInfo();
    }

    private void getInfo()
    {
        System.out.println("Getting existing files...\n");
        try {
            this.files = ServerUtils.getFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Getting last file ID available...\n");
        this.lastFileID = ServerUtils.getFileID();
        System.out.println("Getting existing users...\n");
        try {
            this.users = ServerUtils.getUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Getting last user ID available...\n");
        this.lastUserID = ServerUtils.getUserID();
    }

    @Override
    public boolean user_signup(String newUserName, String password)
    {
        System.out.println("Check username: " + newUserName);
        //SI EL NOM ES VALID RETORNA TRUE
        if(checkAvailableUser(newUserName)){
            this.lastUserID = generateId(lastUserID);
            User newUser = new User(newUserName,password,lastUserID);
            this.users.addUser(newUser);
            System.out.println("Usuari:"+newUserName+"registrat!!");
            ServerUtils.saveUsers(this.users.getUsers());
            ServerUtils.saveUserID(this.lastUserID);
            return true;
        }else{
            return false;
        }
    }

    //WE GENERATE ID
    private int generateId(int id){
        id += 1;
        System.out.println("New ID: " + id);
        return id;
    }


    private boolean checkAvailableUser(String newUserName)
    {
        System.out.println("First user: "+this.users.isEmpty());
        if(!this.users.isEmpty()){
            Iterator<User> iter = this.users.getUsers().iterator();
            while (iter.hasNext()) {
                if(iter.next().getName().equals(newUserName)){
                    return false;
                }
            }
        }
        return  true;
    }

    public boolean user_login (String NomUsuari, String contrasenya)
    {
        Iterator<User> iter = this.users.getUsers().iterator();
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
    public  ArrayList<FileObject> searchFile(String keyText) throws RemoteException {

        ArrayList<FileObject> posibleFiles = new ArrayList<>();
        String[] keyWords = keyText.split("'., '");
        Iterator<FileObject> iter = this.files.getFiles().iterator();

        for (int i=0;i<keyWords.length;i++)
        {

            while (iter.hasNext())
            {

                FileObject currentlyFile = iter.next();
                String titleParsed = currentlyFile.getFileName().toLowerCase();//.split("'., '").toLowerCase();
                if((titleParsed.contains(keyWords[i].toString().toLowerCase())) || (currentlyFile.getDescription().toString().toLowerCase().contains(keyWords[i].toLowerCase())) )
                {

                    if(!posibleFiles.contains(currentlyFile))
                    {
                        posibleFiles.add(currentlyFile);
                    }
                }
            }
        }

        return posibleFiles;

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
