/**
 * Created by sbp5 on 24/10/18.
 */
package RemoteObject;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import CallBack.*;
import Objects.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

import ServerUtils.*;
import ServerUtils.WS_manager.DataManager;


public class GarageImp extends UnicastRemoteObject implements Garage {

    //We store the existing files of the server
    public FilesArray files = new FilesArray();
    //We store the users that are currently connected
    Hashtable<Integer, User> connectUsers = new Hashtable<Integer, User>();
    //We store the callbacks associated to the connected users
    Hashtable<Integer, ClientCallbackInterface> callbackObjects = new Hashtable<Integer, ClientCallbackInterface>();
    //This one is the semaphore that we will use to avoid problems with concurrency
    public static final Semaphore semaphore = new Semaphore(1, true);

    public static final Semaphore Internationalsemaphore = new Semaphore(1, true);

    /**
     * @throws RemoteException
     */
    public GarageImp() throws RemoteException {
        super();
        this.getInfo();
    }

    /**
     * This method is the one that load all id's data from the backups
     */
    private void getInfo() {
        System.out.println("\nGetting existing files...");
        //Check for next id available for files
        try {
            this.files = ServerUtils.getFiles();
            System.out.printf("\tFiles id: ");
            for (FileObject f : this.files.getFiles()) {
                System.out.printf(f.getId() + " ");
            }
            System.out.println("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Saving Server info...");
        System.out.println("\tAddress: "+ ServerUtils.getServerInfo().getIp() + "\n\tPort:\t " + ServerUtils.getServerInfo().getPort()+"\n");
    }

    /**
     * @param username name of the user ho want to log in
     * @param password the pasword of the user
     * @param callbackObj his callbackobject
     * @return we return to the user his callback id. If something goes wrong we return the id "-1"
     * @throws RemoteException
     */
    public int user_login(String username, String password, ClientCallbackInterface callbackObj) throws RemoteException {
        try {
            semaphore.acquire();
            int idConexio = -1;
            User currentUser = DataManager.userGET(username);
            if(currentUser != null){
                if (password.equals(currentUser.getPassword())){
                    semaphore.release();
                    return this.addCallback(callbackObj, currentUser);
                }else {
                    semaphore.release();
                    return idConexio;
                }
            }
        } catch (Exception e) {
            semaphore.release();
            return -1;
        }
        return -1;
    }

    /**
     * @param userName     currenlty user name
     * @param oldPassword  old password
     * @param newPassword1 password that will replace the old one
     * @return return true if all goes fine,false if not
     * @throws java.rmi.RemoteException
     */
    public boolean changePaswordOnServer(String userName, String oldPassword, String newPassword1) throws java.rmi.RemoteException {
        try {
            semaphore.acquire();

            User currentUser = DataManager.userGET(userName);
            if (currentUser.getPassword().equals(oldPassword)) {
                currentUser.setPassword(newPassword1);
                DataManager.userPUT(currentUser);
                semaphore.release();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        semaphore.release();
        return false;
    }

    //region<CALLBACK>
    // method for client to call to add itself to its callback
    //@Override

    /**
     * @param callbackObject client callbackObject
     * @param currentlyUser  current user object information
     * @return the id of the callback related to the user.
     * @throws RemoteException
     */
    public int addCallback(ClientCallbackInterface callbackObject, User currentlyUser) throws RemoteException {
        int key = -1;
        try {
            semaphore.acquire();
            //If the callback object is not still in the server
            if (!(callbackObjects.contains(callbackObject))) {
                //We generate him an id
                key = this.getKeyForCallBack();

                //We store the id in the server
                this.callbackObjects.put(key, callbackObject);
                this.connectUsers.put(key, currentlyUser);
                System.out.println("User: " + currentlyUser.getName() + " just connected with key: " + key + "\n");
            }
            semaphore.release();
            //We return a valid id
            return key;
        } catch (Exception e) {
            e.printStackTrace();
            semaphore.release();
            //We return -1 as id
            return key;
        }
    }

    /**
     * We generate a key for callback according to the currently id's used by others callbacks
     *
     * @return we return the id
     */
    private int getKeyForCallBack() {
        //If is the first callback we return 0 as id
        if (this.callbackObjects.size() == 0) {
            return 0;
        }
        for (int i = 0; i < this.callbackObjects.size(); i++) {
            if (!callbackObjects.containsKey(i)) {
                //If some user disconnects there is an available id we re asign it
                return i;
            }
        }
        //If nobody let a free id we generate a new one
        return this.callbackObjects.size();
    }

    /**
     * @param id id related to the user connection
     * @throws RemoteException
     */
    @Override
    public void deleteCallback(int id) throws RemoteException {
        try {
            semaphore.acquire();
            System.out.println("We delete the callback with id " + id + " from client:" + this.connectUsers.get(id).getName() + "\n");
            connectUsers.remove(id);
            callbackObjects.remove(id);
            semaphore.release();
        } catch (Exception e) {
            e.printStackTrace();
            semaphore.release();
        }
    }

    /**
     * @param userName the name of the user that want to add some tag
     * @param newTag   the name of the tag that we will add to his subscriptions
     * @return true: If the tag is added correctly, false if not
     * @throws RemoteException
     */
    @Override
    public boolean addSubscriptionTag(String userName, String newTag) throws RemoteException {
        try {
            semaphore.acquire();

            User currentUser = DataManager.userGET(userName);
            if (currentUser.addSubscriptions(newTag)) {
                DataManager.userPUT(currentUser);
                semaphore.release();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        semaphore.release();
        return false;
    }

    public boolean deleteSubscriptionTag(String userName, String oldTag) throws RemoteException {
        try {
            semaphore.acquire();

            User currentUser = DataManager.userGET(userName);
            if (currentUser.deleteSubscriptions(oldTag)) {
                DataManager.userPUT(currentUser);
                semaphore.release();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        semaphore.release();
        return false;
    }

    /**
     * @param newUserName new user name
     * @param password    password he want to link to his account
     * @return true if the user information is valid, false if not
     */
    @Override
    public boolean user_signup(String newUserName, String password) {
        int id = 0;
        try {
            semaphore.acquire();
            id = DataManager.userPOST(newUserName, password);
            if(id!=0){
                System.out.println("Usuari: " + newUserName + " registrat!!");
                semaphore.release();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        semaphore.release();
        return false;
    }
    //endregion

    /**
     * @param file object that contains information about the file the client want to upload
     * @return true if upload method work fine, false if something goes wrong
     */
    @Override
    public boolean uploadFile(FileObject file) {
        try {
            semaphore.acquire();
            System.out.println("BEFORE POST\n");
            file.setId(DataManager.filePOST(file));
            System.out.println("----Despres deque el servidor li doni el id: "+file.getId());
            this.files.addFile(file);
            System.out.println("AFTEF POST & BEFORE NOTIFY\n");
            ServerUtils.saveFiles(files.getFiles());
            this.notifyNewFile(file,true);
            System.out.println("AFTER NOTIFY\n");
            semaphore.release();
            return true;
        } catch (Exception e) {
            System.out.println("Error uploading: " + e.toString());
            semaphore.release();
            return false;
        }
    }

    /**
     * @param keyText the key words that will we use for partial search
     * @return the server return all the files that contain at least one of this key words in it's:title,topic tag
     * or description
     */
    @Override
    public ArrayList<FileObject> searchFile(String keyText, String username) {
        try {
            semaphore.acquire();
            ArrayList<FileObject> posibleFiles = new ArrayList<>();
            //We parse the key text and get all the words
            String[] keyWords = keyText.split("'., '");
            Iterator<FileObject> iter = this.files.getFiles().iterator();
            //For each key word we check all the files
            for (int i = 0; i < keyWords.length; i++) {
                while (iter.hasNext()) {
                    FileObject currentlyFile = iter.next();
                    String titleParsed = currentlyFile.getFileName().toLowerCase();//.split("'., '").toLowerCase();
                    //Check if owner name,title, description or tag contain the key word
                    if(currentlyFile.getState()==true || currentlyFile.getUser().equals(username)){
                    if ((currentlyFile.getUser().equals(keyWords[i]))
                            || (titleParsed.contains(keyWords[i].toString().toLowerCase()))
                            || (currentlyFile.getDescription().toLowerCase().contains(keyWords[i].toString().toLowerCase()))
                            || (currentlyFile.getTags().toString().toLowerCase().contains(keyWords[i].toLowerCase()))) {
                        //If we don't add the file previously we add it to the resultant possible files
                        if (!posibleFiles.contains(currentlyFile)) {
                            posibleFiles.add(currentlyFile);
                        }
                    }
                    }
                }
            }
            semaphore.release();
            return posibleFiles;

        } catch (Exception e) {
            e.printStackTrace();
            semaphore.release();
            //If it goes wrong we return and empty list of files
            return new ArrayList<FileObject>();
        }
    }

/**
 * @param name user name of the files
 * @return the server return all the files that contain at least one of this key words in it's:title,topic tag
 * or description
 */
    @Override
    public ArrayList<FileObject> searchFileByName(String name) {
        try {
            semaphore.acquire();
            ArrayList<FileObject> Files = new ArrayList<>();

            Iterator<FileObject> iter = this.files.getFiles().iterator();

                while (iter.hasNext()) {
                    FileObject currentlyFile = iter.next();
                    if ((currentlyFile.getUser().toLowerCase().equals(name))){

                        Files.add(currentlyFile);
                    }
                }
            semaphore.release();
            return Files;
        } catch (Exception e) {
            e.printStackTrace();
            semaphore.release();
            //If it goes wrong we return and empty list of files
            return new ArrayList<FileObject>();
        }
    }

    public FileObject searchFileByID(int id) {
        for (FileObject f: files.getFiles()) {
            if (f.getId() == id) return f;
        }
        return null;
    }

    /**
     * @param fileId id of the file we want to delete
     * @param user   the name of the user who wants to delete
     * @return We return the message that confirm if the file was deleted or not and why
     * @throws RemoteException
     */
    @Override
    public boolean deleteFile(int fileId, String user) throws RemoteException {
        try {
            semaphore.acquire();
            Iterator<FileObject> iter = this.files.getFiles().iterator();
            //For each file in the server
            while (iter.hasNext()) {
                FileObject currentlyFile = iter.next();
                //If we find the id
                if (currentlyFile.getId() == fileId) {
                    //And the owner is the one who wants to delete it
                    if (currentlyFile.getUser().equals(user)) {
                        //We remove it
                        DataManager.fileDELETE(fileId);
                        this.files.removeFile(currentlyFile);
                        //And we update the information to the backup
                        ServerUtils.saveFiles(this.files.getFiles());
                        semaphore.release();
                        return true;//"Fitxer amb id: " + fileId + ", eliminat correctament";
                    }
                }
            }
            semaphore.release();
            return false;//"No ets el propietari de aquest fitxer o no existeix :(";
        } catch (Exception e) {
            System.out.println("delete file Error: " + e.toString());
            semaphore.release();
            return false;//"Error al intentar borra el fitxer";
        }
    }

    /**
     * This metod we notify all the users that at least one of his subscriptions match with the new file topics
     *
     * @param file the new file added
     * @throws RemoteException
     */
    @Override
    public void notifyNewFile(FileObject file,boolean thisServer) throws RemoteException {
        try{
            //Internationalsemaphore.acquire();

        //the Users that we detects that get a non friendly disconnection
        if(thisServer){
            notifyOtherServer(file);
            System.out.println("Le penjat jo");
        }else{
            System.out.println("No le penjat jo");
        }

        System.out.println("id:"+file.getId()+" nom:"+file.getFileName()+" tag 1:"+file.getTags().get(0)+" user: sserver:"+thisServer+"\n");

        ArrayList<Integer> usersToDelete = new ArrayList<Integer>();
        //In this array list for each user we will store the tags that matches with his subscriptions
        ArrayList<String> tagArray = new ArrayList<String>();
        try {
            for (int key : this.callbackObjects.keySet()) {
                tagArray = new ArrayList<String>();
                //We add the tags that matches
                for (String filetag : file.getTags()) {
                    if (this.connectUsers.get(key).getSubscriptions().contains(filetag)) {
                        System.out.println("Notifico a user :"+this.connectUsers.get(key).getId()+"\n");
                        tagArray.add(filetag);
                    }
                }
                //If the files is not private of user is the owner of the file
                if ((file.getState() && !tagArray.isEmpty())
                        || (!file.getState() && this.connectUsers.get(key).getName().equals(file.getUser()))) {

                    try {
                        //We notify the client with all the tag that matches and the description of the file
                        ClientCallbackInterface client = (ClientCallbackInterface) callbackObjects.get(key);
                        String msg = "";
                        msg += "User " + file.getUser().toUpperCase() + " upload the file {" + file.getFileName() + "}\nType: "+file.getType().toString()+"\nTags:\n";
                        for (String tag : tagArray) {
                            msg += "\t- " + tag + "\n";
                        }
                        msg += "Description: " + file.getDescription() + "\n * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n";
                        client.callMe(msg);
                        System.out.println("We notified User:" + this.connectUsers.get(key).getName() + " about: User: " + file.getUser() + " just upload the file: " + file.getFileName() + "\n");
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
            for (int idDelete : usersToDelete) {
                this.callbackObjects.remove(idDelete);
                this.connectUsers.remove(idDelete);
            }
        } catch (Exception e) {
            System.out.println("Error at notifyNewFile method \n" + e.toString());
            //Internationalsemaphore.release();
        }
        } catch (Exception e) {
            System.out.println("Update file Error: " + e.toString());
            //Internationalsemaphore.release();
        }
        //Internationalsemaphore.release();
    }

    public void notifyOtherServer(FileObject file){

        ArrayList<ServerInfo> serverList = DataManager.serverGET_all();

        Iterator<ServerInfo> iter = serverList.iterator();
        //For each file in the server
        while (iter.hasNext()) {

            ServerInfo server= iter.next();
            System.out.println("befor if the notify el serv amb id:"+server.getId());
            if(server.getId()!= ServerUtils.getServerInfo().getId()) {
                System.out.println("dins if the notify el serv amb id:"+server.getId());
                connectAndNotify(file, server.getIp(), server.getPort());
            }
        }
        System.out.println("\nDespres del while\n");
    }

    public void connectAndNotify(FileObject file, String serverIP, int serverPORT){
        String registryURL = "rmi://"+ serverIP +":" + serverPORT + "/some";
        try {
            Garage h = (Garage)Naming.lookup(registryURL);

            h.notifyNewFile(file,false);
        }catch (Exception e) {
        }
    }
    /**
     * Search the id into the files array.If we find it, we return it,if not we return and empty one
     *
     * @param id
     * @return return a
     */
    public FileObject getFileObject(int id) {
        for (FileObject file : files.getFiles()) {
            if (file.getId() == id) {
                return file;
            }
        }
        return new FileObject();
    }

    public ArrayList<FileObject> getFileObjects(ArrayList<Integer> ids) {
        ArrayList<FileObject> files = new ArrayList<FileObject>();
        for (Integer id:ids) {
            for (FileObject file:this.files.getFiles()) {
                if(file.getId() == id){
                    files.add(file);
                }
            }
        }
        return files;
    }

    @Override
    public boolean addModification(FileObject file) {
        try {
            semaphore.acquire();
            System.out.println("----dins de modify id:"+file.getId());
            DataManager.filePUT(file);
            Iterator<FileObject> iter = this.files.getFiles().iterator();
            //For each file in the server
            while (iter.hasNext()) {
                FileObject currentlyFile = iter.next();
                //If we find the id
                if (currentlyFile.getId() == file.getId()) {
                    this.files.removeFile(currentlyFile);
                    this.files.addFile(file);
                    ServerUtils.saveFiles(this.files.getFiles());
                    semaphore.release();
                    return true;
                }
            }
            semaphore.release();
            return false;
        } catch (Exception e) {
            System.out.println("delete file Error: " + e.toString());
            semaphore.release();
            return false;
        }
    }
    //endregion

    @Override
    public ArrayList<String> getSubscriptionsList(String userName)throws RemoteException {
        ArrayList<String> array = new ArrayList<>();
        try {
            semaphore.acquire();
            User currentUser = DataManager.userGET(userName);
            array = currentUser.getSubscriptions();
            semaphore.release();
        }catch(Exception e){
            System.out.println("Error at notifyNewFile method \n" + e.toString());
        }
        semaphore.release();
        return array;
    }
}