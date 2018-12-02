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

    //We store the existing files of the server
    private FilesArray files = new FilesArray();
    //We store the existing users of the server
    private UsersArray users = new UsersArray();
    //We store the users that are currently connected
    Hashtable<Integer,User> connectUsers = new Hashtable<Integer,User>();
    //We store the callbacks associated to the connected users
    Hashtable<Integer,ClientCallbackInterface> callbackObjects = new Hashtable<Integer,ClientCallbackInterface>();
    //We use this 2 variables to provide a control id duplications
    private int lastFileID = -1;
    private int lastUserID = -1;
    //This one is the semaphore that we will use to avoid problems with concurrency
    public static final Semaphore semaphore = new Semaphore(1, true);


    /**
     *
     * @throws RemoteException
     */
    public GarageImp() throws RemoteException {
        super();
        this.getInfo();
    }

    /**
     * This method is the one that load all id's data from the backups
     */
    private void getInfo()
    {
        System.out.println("Getting existing files...");
        //Check for next id available for files
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
        //Check for next id available for users
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


    /**
     *
     * @param id Last id created
     * @return new user/file id
     */
    private int generateId(int id){
        id += 1;
        System.out.println("New ID: " + id);
        return id;
    }

    /**
     *
     * @param newUserName the name of new user account
     * @return true:if the name is available or false: if this name is used by another user
     */
    private boolean checkAvailableUser(String newUserName)
    {
        System.out.println("First user: "+this.users.isEmpty());
        if(!this.users.isEmpty()){
            Iterator<User> iter = this.users.getUsers().iterator();
            //For each user in the server we check if the name is currently used by another user
            while (iter.hasNext()) {
                if(iter.next().getName().toLowerCase().equals(newUserName.toLowerCase())){
                    return false;
                }
            }
        }
        return  true;
    }

    /**
     *
     * @param NomUsuari name of the user ho want to log in
     * @param contrasenya the pasword of the user
     * @param callbackObj his callbackobject
     * @return we return to the user his callback id. If something goes wrong we return the id "-1"
     * @throws RemoteException
     */
    public int user_login (String NomUsuari, String contrasenya, ClientCallbackInterface callbackObj) throws RemoteException
    {

        try{
            semaphore.acquire();
            int idConexio = -1;
            Iterator<User> iter = this.users.getUsers().iterator();

            while (iter.hasNext()) {
                User currentlyUser = iter.next();
                //Check the validity of the user name
                if(currentlyUser.getName().toLowerCase().equals(NomUsuari.toLowerCase())){
                    //Check if the username coincide with the password introduced
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

    /**
     *
     * @param callbackObject client callbackObject
     * @param currentlyUser currently user object information
     * @return the id of the callback related to the user.
     * @throws RemoteException
     */
    public int addCallback (ClientCallbackInterface callbackObject,User currentlyUser)  throws RemoteException
    {
        int key=-1;
        try{
            semaphore.acquire();
            //If the callback object is not still in the server
            if(!(callbackObjects.contains(callbackObject)))
            {
                //We generate him an id
                key =this.getKeyForCallBack();

                //We store the id in the server
                this.callbackObjects.put(key,callbackObject);
                this.connectUsers.put(key,currentlyUser);

                System.out.println ("User: "+currentlyUser.getName()+" just connected with key: "+key+"\n");

            }
            semaphore.release();
            //We return a valid id
            return key;
        }catch (Exception e) {
            e.printStackTrace();
            semaphore.release();
            //We return -1 as id
            return key;
        }
    }

    /**
     * We generate a key for callback according to the currently id's used by others callbacks
     * @return we return the id
     */
    private  int getKeyForCallBack(){
        //If is the first callback we return 0 as id
        if(this.callbackObjects.size() == 0) {return 0;}

        for (int i = 0; i < this.callbackObjects.size(); i++) {
            if(!callbackObjects.containsKey(i)){
                //If some user disconnects there is an available id we re asign it
                return i;
            }
        }
        //If nobody let a free id we generate a new one
        return this.callbackObjects.size();
    }

    /**
     *
     * @param id id related to the user connection
     * @throws RemoteException
     */
    @Override
    public void deleteCallback (int id) throws RemoteException
    {
        try{
            semaphore.acquire();
            System.out.println ("We delete the callback with id "+id+" from client:"+this.connectUsers.get(id).getName()+"\n");
            System.out.printf("id callback:"+id+"\n");
            connectUsers.remove(id);
            callbackObjects.remove(id);

            semaphore.release();
        }catch (Exception e) {
            e.printStackTrace();
            semaphore.release();
        }
    }

    /**
     *
     * @param userName the name of the user that want to add some tag
     * @param newTag the name of the tag that we will add to his subscriptions
     * @return true: If the tag is added correctly, false if not
     * @throws RemoteException
     */
    @Override
    public boolean addSubscriptionTag(String userName, String newTag) throws RemoteException {
        try {
            semaphore.acquire();
            Iterator<User> iter = this.users.getUsers().iterator();
            //We search for the currently user
            while (iter.hasNext()) {
                User curentlyUser = iter.next();
                if (curentlyUser.getName().toLowerCase().equals(userName.toLowerCase())) {
                    //We add the tag
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

    /**
     *
     * @param newUserName new user name
     * @param password password he want to link to his account
     * @return true if the user information is valid, false if not
     */
    @Override
    public boolean user_signup(String newUserName, String password)
    {

        try{
            semaphore.acquire();

            if(checkAvailableUser(newUserName)){
                this.lastUserID = generateId(lastUserID);
                //We create a new user
                User newUser = new User(newUserName,password,lastUserID);
                //We add the user to the server
                this.users.addUser(newUser);
                System.out.println("Usuari:"+newUserName+"registrat!!");
                //We save it to the back up
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

    /**
     *
     * @param file object that contains information about the file the client want to upload
     * @return true if upload method work fine, false if something goes wrong
     */
    @Override
    public boolean uploadFile (FileObject file) {
        try {
            semaphore.acquire();
            //We generate a if related to the new file
            this.lastFileID = generateId(this.lastFileID);
            file.setId(this.lastFileID);
            ServerUtils.saveFileID(this.lastFileID);
            this.files.addFile(file);

            //We save the file int the Server
            ServerUtils.saveFiles(files.getFiles());
            this.notifyNewFile(file);
            semaphore.release();
            return true;
        }catch(Exception e){
            System.out.println("Error uploading: " + e.toString());
            semaphore.release();
            return false;
        }
    }

    /**
     *
     * @param keyText the key words that will we use for partial search
     * @return the server return all the files that contain at least one of this key words in it's:title,topic tag
     * or description
     */
    @Override
    public  ArrayList<FileObject> searchFile(String keyText) {
        try{
            semaphore.acquire();
            ArrayList<FileObject> posibleFiles = new ArrayList<>();
            //We parse the key text and get all the words
            String[] keyWords = keyText.split("'., '");
            Iterator<FileObject> iter = this.files.getFiles().iterator();
            //For each key word we check all the files
            for (int i=0;i<keyWords.length;i++)
            {
                while (iter.hasNext())
                {
                    FileObject currentlyFile = iter.next();
                    String titleParsed = currentlyFile.getFileName().toLowerCase();//.split("'., '").toLowerCase();
                    //Check if title, description or tag contain the key word
                    if((titleParsed.contains(keyWords[i].toString().toLowerCase()))
                        ||(currentlyFile.getDescription().toLowerCase().contains(keyWords[i].toString().toLowerCase()))
                        ||(currentlyFile.getTags().toString().toLowerCase().contains(keyWords[i].toLowerCase())) )
                    {
                        //If we don't add the file previously we add it to the resultant possible files
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
            //If it goes wrong we return and empty list of files
            return new ArrayList<FileObject>();
        }
    }

    /**
     *
     * @param id this one is the id of the file u want to download
     * @return we return the file related to the id
     * @throws RemoteException
     */
    @Override
    public FileObject downloadFile(int id) throws RemoteException
    {
        try
        {
            semaphore.acquire();
            //We get the file object related to the id
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

    /**
     *
     * @param fileId id of the file we want to delete
     * @param user the name of the user who wants to delete
     * @return We return the message that confirm if the file was deleted or not and why
     * @throws RemoteException
     */
    @Override
    public String deleteFile(int fileId, String user) throws RemoteException
    {
        try
        {
            semaphore.acquire();
            Iterator<FileObject> iter = this.files.getFiles().iterator();
            //For each file in the server
            while (iter.hasNext()) {
                FileObject currentlyFile=  iter.next();
                //If we find the id
                if(currentlyFile.getId()==fileId){
                        //And the owner is the one who wants to delete it
                        if(currentlyFile.getUser().equals(user)){
                        //We remove it
                        this.files.removeFile(currentlyFile);
                        //And we update the information to the backup
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

    /**
     * This metod we notify all the users that at least one of his subscriptions match with the new file topics
     * @param file the new file added
     * @throws RemoteException
     */
    private void notifyNewFile(FileObject file)  throws RemoteException {
        //the Users that we detects that get a non friendly disconnection
        ArrayList<Integer> usersToDelete = new ArrayList<Integer> ();
        //In this array list for each user we will store the tags that matches with his subscriptions
        ArrayList<String> tagArray= new ArrayList<String> ();

        try{
            for (int key : this.callbackObjects.keySet()) {


                tagArray= new ArrayList<String> ();
                //We add the tags that matches
                for (String filetag:file.getTags()) {
                    if(this.connectUsers.get(key).getSubscriptions().contains(filetag)){
                        tagArray.add(filetag);
                    }

                }
                    //If the files is not private of user is the owner of the file
                    if((file.getState() &&  !tagArray.isEmpty())
                        ||(!file.getState() && this.connectUsers.get(key).getName().equals(file.getUser()))){

                        try {
                            //We notify the client with all the tag that matches and the description of the file
                            ClientCallbackInterface client = (ClientCallbackInterface) callbackObjects.get(key);
                            String msg="";
                            msg +="Hey im the Server, User: "+file.getUser()+" just upload the file: "+file.getFileName()+"\nWith Tags:\n";

                            for (String tag: tagArray) {
                               msg +="-"+tag+"\n";
                            }
                            msg +="Descripcio: "+file.getDescription()+"\n";
                            client.callMe(msg);
                            System.out.println("We notified User:" + this.connectUsers.get(key).getName() +" about: User: "+file.getUser()+" just upload the file: "+file.getFileName()+"\n");
                        } catch (Exception e) {
                            //If we fial connecting to the client add it to the remove list
                            System.out.println("Hem borrat el usuari desconectat: " + this.connectUsers.get(key).getName() + "\n\n\nWith ERROR: " + e.toString());
                            usersToDelete.add(key);
                            //notifyConnection(notifiedUsers,newConnection);
                            continue;
                        }

                    }

            }
            //We disconnect all the users that failed into the server connection
            for (int idDelete:usersToDelete) {
                this.callbackObjects.remove(idDelete);
                this.connectUsers.remove(idDelete);
            }


        } catch (Exception e) {
            System.out.println("Error at notifyNewFile method \n" + e.toString());
        }
    }

    /**
     * Search the id into the files array.If we find it, we return it,if not we return and empty one
     * @param id
     * @return return a
     */
    private FileObject getFileObject(int id){
        for (FileObject file: files.getFiles()) {
            if(file.getId()==id){return file;}
        }
        return new FileObject();
    }


    /**
     * We save the currently server file information into the json backup
     * @param file
     * @return
     */
    public String modifiedFile(FileObject file){
        ServerUtils.saveFiles(this.files.getFiles());
        return "Backup upload!(files)";
    }

    /**
     * We save the currently server file information into the json backup
     * @param user
     * @return
     */
    public String modifiedUser(User user){
        ServerUtils.saveUsers(this.users.getUsers());
        return "Backup upload!(users)";
    }
    //endregion
}
