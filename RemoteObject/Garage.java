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

public interface Garage extends Remote{
    public boolean user_signup(String NewNomUsuari, String contrasenya)  throws RemoteException;
    public int user_login (String NomUsuari, String contrasenya, ClientCallbackInterface callbackObj)  throws RemoteException;
    public boolean uploadFile(FileObject file)  throws java.rmi.RemoteException;
    public  ArrayList<FileObject> searchFile (String keyText,String username) throws java.rmi.RemoteException;
    public  ArrayList<FileObject> searchFileByName (String userName) throws java.rmi.RemoteException;
    public void deleteCallback (int id) throws java.rmi.RemoteException;
    public boolean deleteFile(int fileId, String user) throws java.rmi.RemoteException;
    public boolean addModification(FileObject file) throws java.rmi.RemoteException;
    public boolean addSubscriptionTag(String userName, String newTag)throws java.rmi.RemoteException;
    public boolean deleteSubscriptionTag(String userName, String newTag)throws java.rmi.RemoteException;
    public ArrayList<String> getSubscriptionsList(String userName)throws java.rmi.RemoteException;
    public boolean changePaswordOnServer(String userName,String oldPassword,String newPassword1)throws java.rmi.RemoteException;
    public void notifyNewFile(FileObject file,boolean thisServer) throws RemoteException ;
    public ArrayList<FileObject> getFileObjects(ArrayList<Integer> ids) throws RemoteException;
}
