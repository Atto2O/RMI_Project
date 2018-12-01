/**
 * Created by sbp5 on 24/10/18.
 */
package RemoteObject;

import java.io.File;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import CallBack.*;
import Objects.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

import ServerUtils.*;

public class GarageImp extends UnicastRemoteObject implements Garage {

    private FilesArray files = new FilesArray();
    private UsersArray users = new UsersArray();
    Hashtable<Integer,User> connectUsers = new Hashtable<Integer,User>();
    Hashtable<Integer,ClientCallbackInterface> callbackObjects = new Hashtable<Integer,ClientCallbackInterface>();

    private int lastFileID = -1;
    private int lastUserID = -1;
    public static final Semaphore semaphore = new Semaphore(1, true);

    static int RMIPort;

    public GarageImp() throws RemoteException {
        super();
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

    public int user_login (String NomUsuari, String contrasenya, ClientCallbackInterface callbackObj) throws RemoteException
    {

        try{
            semaphore.acquire();
            int idConexio = -1;
            Iterator<User> iter = this.users.getUsers().iterator();

            while (iter.hasNext()) {
                User currentlyUser = iter.next();
                if(currentlyUser.getName().toLowerCase().equals(NomUsuari.toLowerCase())){

                    if(currentlyUser.getPassword().equals(contrasenya)){
                        semaphore.release();
                        return this.addCallback(callbackObj,currentlyUser);
                    }else{
                        semaphore.release();
                        return idConexio;
                    }
                }
            }
        }catch (Exception e) {
            semaphore.release();
            return  -1;
        }

        return -1;


    }

    // method for client to call to add itself to its callback
    //@Override
    public int addCallback (ClientCallbackInterface callbackObject,User currentlyUser)  throws RemoteException
    {
        int key=-1;
        try{
            semaphore.acquire();
            // store the callback object into the vector
            if(!(callbackObjects.contains(callbackObject)))
            {
                key =this.getKeyForCallBack();

                //GUARDEM EL HAMBOO
                this.callbackObjects.put(key,callbackObject);
                this.connectUsers.put(key,currentlyUser);

                System.out.println ("User: "+currentlyUser.getName()+" just connected with key: "+key+"\n");


                //callbackObjects.addElement (callbackObject);
            }
            semaphore.release();
            return key;
        }catch (Exception e) {
            e.printStackTrace();
            semaphore.release();
            return key;
        }
    }

    private  int getKeyForCallBack(){
        if(this.callbackObjects.size() == 0) {return 0;}

        for (int i = 0; i < this.callbackObjects.size(); i++) {
            if(!callbackObjects.containsKey(i)){
                return i;
            }
        }
        return this.callbackObjects.size();
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

    @Override
    public boolean addSubscriptionTag(String userName, String newTag) throws RemoteException {
        try {
            semaphore.acquire();
            Iterator<User> iter = this.users.getUsers().iterator();
            while (iter.hasNext()) {
                User curentlyUser = iter.next();
                if (curentlyUser.getName().toLowerCase().equals(userName.toLowerCase())) {
                    curentlyUser.addSubscriptions(newTag);
                    semaphore.release();
                    return true;
                }
            }
            semaphore.release();
            return false;

        } catch (Exception e) {
            semaphore.release();
            e.printStackTrace();
            return false;

        }

    }


    @Override
    public boolean user_signup(String newUserName, String password)
    {
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

    @Override
    public String uploadFile (FileObject file) {
        try {
            semaphore.acquire();
            this.lastFileID = generateId(this.lastFileID);
            file.setId(this.lastFileID);
            ServerUtils.saveFileID(this.lastFileID);
            this.files.addFile(file);

            ServerUtils.saveFiles(files.getFiles());
            this.notifyNewFile(file);
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
                    if((titleParsed.contains(keyWords[i].toString().toLowerCase())) || (currentlyFile.getTags().toString().toLowerCase().contains(keyWords[i].toLowerCase())) )
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

    //fica semafors a damun
    private void notifyNewFile(FileObject file)  throws RemoteException {
        ArrayList<Integer> usersToDelete= new ArrayList<Integer> ();
        ArrayList<String> tagArray= new ArrayList<String> ();
        try{
            for (int key : this.callbackObjects.keySet()) {
                //si esta public i els teus tags contennt algun del fitxer nou
                System.out.println("Avans for file.tags");
                tagArray= new ArrayList<String> ();
                for (String filetag:file.getTags()) {
                    if(this.connectUsers.get(key).getSubscriptions().contains(filetag)){
                        tagArray.add(filetag);
                    }

                }
                    if((file.getState() &&  !tagArray.isEmpty()) ||
                            (!file.getState() && this.connectUsers.get(key).getName().equals(file.getUser()))){
                        System.out.println("if tocho");
                        try {

                            ClientCallbackInterface client = (ClientCallbackInterface) callbackObjects.get(key);
                            String msg="";
                            msg +="Hey im the Server, User: "+file.getUser()+" just upload the file: "+file.getFileName()+"\nWith Tags:\n";
                            
                            for (String tag: tagArray) {
                               msg +="-"+tag+"\n";
                            }
                            client.callMe(msg);
                            System.out.println("We notified User:" + this.connectUsers.get(key).getName() +" about: User: "+file.getUser()+" just upload the file: "+file.getFileName()+"\n");
                        } catch (Exception e) {
                            //notifiedUsers.add(key);
                            System.out.println("Hem borrat el usuari desconectat: " + this.connectUsers.get(key).getName() + "\n\n\nWith ERROR: " + e.toString());
                            //this.callbackObjects.remove(key);
                            usersToDelete.add(key);
                            this.connectUsers.remove(key);

                            //notifyConnection(notifiedUsers,newConnection);
                            continue;
                        }

                    }

            }
            //BORREM ELS USUARIS QUE JA NO ESTAN CONNECTATS
            for (int idDelete:usersToDelete) {
                this.callbackObjects.remove(idDelete);
            }



        } catch (Exception e) {
            System.out.println("ERROR AL RECORRE ELS CALLBACKS \n" + e.toString());
        }
    }

    private FileObject getFileObject(int id){
        for (FileObject file: files.getFiles()) {
            if(file.getId()==id){return file;}
        }
        return new FileObject();
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
