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
                for (int i:this.connectUsers.keySet()) {
                    if(this.connectUsers.get(i).getId() == currentUser.getId()){
                        this.connectUsers.replace(i, currentUser);
                    }
                }
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
                for (int i:this.connectUsers.keySet()) {
                    if(this.connectUsers.get(i).getId() == currentUser.getId()){
                        this.connectUsers.replace(i, currentUser);
                    }
                }
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
        try {
            semaphore.acquire();
            int id = DataManager.userPOST(newUserName, password);
            if(id!=0){
                System.out.println("Usuari: " + newUserName + " registered.");
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
            file.setId(DataManager.filePOST(file));
            this.files.addFile(file);
            ServerUtils.saveFiles(files.getFiles());
            this.notifyNewFile(file,true);
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
        ArrayList<FileObject> posibleFiles = new ArrayList<FileObject>();
        try {
            semaphore.acquire();
            if(!keyText.isEmpty()){
                posibleFiles = DataManager.fileGET_Array(keyText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        semaphore.release();
        return posibleFiles;
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
        } catch (Exception e) {
            System.out.println("delete file Error: " + e.toString());
        }
        semaphore.release();
        return false;
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
            //the Users that we detects that get a non friendly disconnection
            if(thisServer){
                notifyOtherServer(file);
            }
            ArrayList<Integer> usersToDelete = new ArrayList<Integer>();
            //In this array list for each user we will store the tags that matches with his subscriptions
            ArrayList<String> tagArray = new ArrayList<String>();
            try {
                for (int key : this.callbackObjects.keySet()) {
                    tagArray = new ArrayList<String>();
                    //We add the tags that matches
                    System.out.println("Callback num:"+key+"\n");
                    for (String filetag : file.getTags()) {
                        if (this.connectUsers.get(key).getSubscriptions().contains(filetag)) {
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
            }
        } catch (Exception e) {
            System.out.println("Update file Error: " + e.toString());
        }
    }

    public void notifyOtherServer(FileObject file){
        ArrayList<ServerInfo> serverList = DataManager.serverGET_all();
        Iterator<ServerInfo> iter = serverList.iterator();
        //For each file in the server
        while (iter.hasNext()) {
            ServerInfo server= iter.next();
            if(server.getId()!= ServerUtils.getServerInfo().getId()) {
                connectAndNotify(file, server.getIp(), server.getPort());
            }
        }
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

    @Override
    public ArrayList<FileObject> getFileObjects(ArrayList<Integer> ids) throws RemoteException {
        ArrayList<FileObject> files = new ArrayList<FileObject>();
        for (FileObject file:this.files.getFiles()) {
            if(ids.contains(file.getId())){
                files.add(file);
            }

        }
        return files;
    }

    @Override
    public boolean addModification(FileObject file) {
        try {
            semaphore.acquire();
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
        } catch (Exception e) {
            System.out.println("delete file Error: " + e.toString());
        }
        semaphore.release();
        return false;
    }
    //endregion

    @Override
    public ArrayList<String> getSubscriptionsList(String userName)throws RemoteException {
        ArrayList<String> array = new ArrayList<>();
        try {
            semaphore.acquire();
            User currentUser = DataManager.userGET(userName);
            array = currentUser.getSubscriptions();
        }catch(Exception e){
            System.out.println("Error at notifyNewFile method \n" + e.toString());
        }
        semaphore.release();
        return array;
    }
}