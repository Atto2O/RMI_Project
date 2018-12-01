/**
 * Created by sbp5 on 24/10/18.
 */
package RemoteObject;

import java.io.File;
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
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import ServerUtils.*;

public class GarageImp extends UnicastRemoteObject implements Garage {

    private FilesArray files = new FilesArray();
    private UsersArray users = new UsersArray();

    private int lastFileID = -1;
    private int lastUserID = -1;
    public static final Semaphore semaphore = new Semaphore(1, true);

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
        System.out.println("Getting existing files...");
        try {
            this.files = ServerUtils.getFiles();
            System.out.printf("\tFiles id: ");
            for (FileObject f: this.files.getFiles()) {
                System.out.printf(f.getId() + " ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("\nGetting last file ID available...");
        this.lastFileID = ServerUtils.getFileID();
        System.out.println("\tLast file ID: " + this.lastFileID);
        System.out.println("Getting existing users...");
        try {
            this.users = ServerUtils.getUsers();
            System.out.printf("\tUsers id: ");
            for (User f: this.users.getUsers()) {
                System.out.printf(f.getId() + " ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("\nGetting last user ID available...");
        this.lastUserID = ServerUtils.getUserID();
        System.out.println("\tLast user ID: " + this.lastUserID + "\n");
    }

    @Override
    public boolean user_signup(String newUserName, String password)
    {
        System.out.println("Check username: " + newUserName);
        //SI EL NOM ES VALID RETORNA TRUE
        try{
            semaphore.acquire();

            if(checkAvailableUser(newUserName)){
                this.lastUserID = generateId(lastUserID);
                User newUser = new User(newUserName,password,lastUserID);
                this.users.addUser(newUser);
                System.out.println("Usuari:"+newUserName+"registrat!!");
                ServerUtils.saveUsers(this.users.getUsers());
                ServerUtils.saveUserID(this.lastUserID);
                semaphore.release();
                return true;
            }else{

                semaphore.release();
                return false;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        semaphore.release();
        return false;
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
                if(iter.next().getName().toLowerCase().equals(newUserName.toLowerCase())){
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
            if(currentlyUser.getName().toLowerCase().equals(NomUsuari.toLowerCase())){

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
        try{
            semaphore.acquire();
            // store the callback object into the vector
            if(!(callbackObjects.contains(callbackObject)))
            {
                callbackObjects.addElement (callbackObject);
                System.out.println ("Server got an 'addCallback' call.");
            }
            callback();
            semaphore.release();

        }catch (Exception e) {
            e.printStackTrace();
            semaphore.release();
        }
    }

    @Override
    public void deleteCallback (ClientCallbackInterface callbackObject) throws RemoteException
    {
        try{
            semaphore.acquire();
            System.out.println ("Server got an 'deleteCallback' call.");
            callbackObjects.remove (callbackObject);
            semaphore.release();
        }catch (Exception e) {
            e.printStackTrace();
            semaphore.release();
        }
    }

    private static void callback()  throws RemoteException {
        System.out.println("*******************************************************\n" + "Callbacks initiated ---");
        for (int i = 0; i < callbackObjects.size(); i++) {
            System.out.println("Now performing the " + i + "-th callback");
            // convert the vector object to a callback object
            ClientCallbackInterface client = (ClientCallbackInterface) callbackObjects.elementAt(i);
            try
            {
                client.callMe("Server calling back to client " + i);
            }
            catch(Exception e){
                System.out.println("Error: "+e.toString());
            }
            System.out.println("Server completed callbacks ---"+"\n*******************************************************\n");

            //...
        }
        //...
    }

    @Override
    public String uploadFile (FileObject file) {
        try {
            semaphore.acquire();
            this.lastFileID = generateId(this.lastFileID);
            file.setId(this.lastFileID);
            ServerUtils.saveFileID(this.lastFileID);
            this.files.addFile(file);
            ServerUtils.saveFiles(files.getFiles());
            semaphore.release();
            return "Saved!";
        }catch(Exception e){
            System.out.println("Error uploading: " + e.toString());
            semaphore.release();
            return "Error uploading: " + e.toString();
        }
    }

    @Override
    public  ArrayList<FileObject> searchFile(String keyText) {
        try{
            semaphore.acquire();
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
            semaphore.release();
            return posibleFiles;

        }catch (Exception e) {
            e.printStackTrace();
            semaphore.release();
            return new ArrayList<FileObject>();
        }
    }

    @Override
    public FileObject downloadFile(int id) throws RemoteException
    {
        try
        {
            semaphore.acquire();
            FileObject file = getFileObject(id);
            semaphore.release();
            return file;
        }
        catch(Exception e)
        {
            System.out.println("Download Error: " + e.toString());
            semaphore.release();
            return null;
        }
    }

    private FileObject getFileObject(int id){
        for (FileObject file: files.getFiles()) {
            if(file.getId()==id){return file;}
        }
        return new FileObject();
    }

    @Override
    public String deleteFile(int fileId, String user) throws RemoteException
    {
        try
        {
            semaphore.acquire();
            Iterator<FileObject> iter = this.files.getFiles().iterator();
            while (iter.hasNext()) {
                FileObject currentlyFile=  iter.next();

                if(currentlyFile.getId()==fileId){
                        if(currentlyFile.getUser().equals(user)){

                        //iter.remove();
                        this.files.removeFile(currentlyFile);
                        ServerUtils.saveFiles(this.files.getFiles());
                        semaphore.release();
                        return "Fitxer amb id: "+fileId+", eliminat correctament";
                    }

                }


            }
            semaphore.release();
            return "No ets el propietari de aquest fitxer o no existeix :(";
        }
        catch(Exception e)
        {
            System.out.println("delete file Error: " + e.toString());
            semaphore.release();
            return "Error al intentar borra el fitxer";
        }

    }

    public static boolean SetAvailableFile(String path){
        try{

        File tmpDir = new File(path);
        if(!tmpDir.exists()){
            File f = new File(path);

            f.getParentFile().mkdirs();
            f.createNewFile();
        }
        }catch(Exception e){
            System.out.println("erro pelotudo: "+e+"");
        }

        return true;
    }

    public static boolean SetAvailableDirectory(String path){
        try{

            File tmpDir = new File(path);
            if(!tmpDir.exists()){
                File f = new File(path);

                f.getParentFile().mkdirs();
                f.createNewFile();
            }
        }catch(Exception e){
            System.out.println("erro pelotudo: "+e+"");
        }
        return true;
    }

    //region<SAVE .json>
    public String modifiedFile(FileObject file){
        ServerUtils.saveFiles(this.files.getFiles());
        return "changed!";
    }

    public String modifiedUser(User user){
        ServerUtils.saveUsers(this.users.getUsers());
        return "changed!";
    }
    //endregion
}
